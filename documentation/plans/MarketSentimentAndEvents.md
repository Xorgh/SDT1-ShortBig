# Plan: Market Sentiment & Random Market Events

## Overview

Two new game features that add depth to the stock simulation:

1. **Market Sentiment** — A slow-moving global mood (bullish / neutral / bearish) that biases all stock price changes and state transitions.
2. **Random Market Events** — Time-limited dramatic events ("Trade War", "Bull Market", "Tech Boom") that temporarily impact the entire market.

Both systems produce a `double bias` that gets **summed** and passed into the existing `calculateNewPrice()` and `TransitionManager`. The individual stock state machine stays untouched — it just receives an extra nudge from the market-wide systems.

---

## Architecture

```
StockMarket (existing enum singleton)
├── MarketSentiment         (NEW — slow-moving mood: bullish/neutral/bearish)
├── MarketEventManager      (NEW — random dramatic events with duration)
│   └── MarketEvent         (NEW — record: name, biases, duration)
├── List<LiveStock>         (existing — individual stocks)
│   └── LiveStockState      (existing — gets bias parameter added)
└── Observer lists          (existing + new onMarketEvent)
```

---

## Feature 1: Market Sentiment

### Concept

A global market mood that shifts slowly over time. It biases:
- **Price changes** — a small additive % bias to every stock's price calculation
- **State transitions** — shifts the probability of stocks entering Growing vs Declining

### New class: `MarketSentiment`

**Location:** `business/stockmarket/simulation/MarketSentiment.java`

```java
package business.stockmarket.simulation;

import java.util.concurrent.ThreadLocalRandom;

public class MarketSentiment
{
  public enum Mood { BULLISH, NEUTRAL, BEARISH }

  private Mood currentMood = Mood.NEUTRAL;
  private int ticksInMood = 0;

  // How much the market mood biases individual stock price changes
  // e.g. +0.3% bias when BULLISH, -0.3% when BEARISH, 0 when NEUTRAL
  private static final double MOOD_BIAS = 0.003;

  // How much the mood shifts transition probabilities
  // e.g. BULLISH makes stocks more likely to enter/stay in GrowingState
  private static final double TRANSITION_BIAS = 0.05;

  public double getPriceBias()
  {
    return switch (currentMood)
    {
      case BULLISH -> +MOOD_BIAS;
      case BEARISH -> -MOOD_BIAS;
      case NEUTRAL -> 0.0;
    };
  }

  public double getTransitionBias()
  {
    return switch (currentMood)
    {
      case BULLISH -> +TRANSITION_BIAS;  // favour growing
      case BEARISH -> -TRANSITION_BIAS;  // favour declining
      case NEUTRAL -> 0.0;
    };
  }

  public void tick()
  {
    ticksInMood++;
    // Random chance to shift mood every tick, increasing over time
    double shiftChance = Math.min(0.02 + ticksInMood * 0.002, 0.15);
    if (ThreadLocalRandom.current().nextDouble() < shiftChance)
    {
      currentMood = rollNewMood();
      ticksInMood = 0;
    }
  }

  private Mood rollNewMood()
  {
    double roll = ThreadLocalRandom.current().nextDouble();
    if (roll < 0.33) return Mood.BULLISH;
    else if (roll < 0.66) return Mood.BEARISH;
    else return Mood.NEUTRAL;
  }

  public Mood getCurrentMood() { return currentMood; }
}
```

### Integration points

**1. `StockMarket` owns the sentiment:**

```java
public enum StockMarket
{
  INSTANCE;
  private final MarketSentiment sentiment = new MarketSentiment();

  public MarketSentiment getSentiment() { return sentiment; }

  public void updateAllLiveStocks()
  {
    sentiment.tick();  // update mood each tick
    for (LiveStock liveStock : liveStocks) { ... }
  }
}
```

**2. `LiveStockState.calculateNewPrice` receives the bias:**

Change the interface to accept a market bias parameter:

```java
interface LiveStockState
{
  double calculateNewPrice(double currentPrice, double marketBias);
  String getName();
}
```

Then in each state (e.g. `GrowingState`):

```java
@Override public double calculateNewPrice(double currentPrice, double marketBias)
{
  // ... existing logic to calculate `change` ...
  change += marketBias;  // shift by market sentiment
  return currentPrice + currentPrice * change;
}
```

**3. `LiveStock.updatePrice()` passes the bias:**

```java
double bias = StockMarket.INSTANCE.getSentiment().getPriceBias();
currentPrice = MathUtil.Round(currentState.calculateNewPrice(currentPrice, bias));
```

**4. `TransitionManager` uses the transition bias:**

Pass it into `getNextState()` and use it to shift the growing-vs-declining probability. For example, in the Steady handler:

```java
double sentimentBias = StockMarket.INSTANCE.getSentiment().getTransitionBias();
// BULLISH → more likely to go Growing, BEARISH → more likely to go Declining
AppConfig.INSTANCE.getSteadyToGrowingBase() + halfAdj + sentimentBias
```

---

## Feature 2: Random Market Events

### Concept

Time-limited events that dramatically affect the market. Each event has a name, description, price bias modifier, transition bias modifier, and a duration in ticks.

### New record: `MarketEvent`

**Location:** `business/stockmarket/simulation/MarketEvent.java`

```java
package business.stockmarket.simulation;

public record MarketEvent(
    String name,
    String description,
    double priceBiasModifier,      // added to price bias (e.g. -0.01 for crash)
    double transitionBiasModifier, // shift transition probabilities
    int durationTicks              // how long it lasts
) {}
```

### New class: `MarketEventManager`

**Location:** `business/stockmarket/simulation/MarketEventManager.java`

```java
package business.stockmarket.simulation;

import java.util.concurrent.ThreadLocalRandom;

public class MarketEventManager
{
  private MarketEvent activeEvent = null;
  private int ticksRemaining = 0;

  // Predefined event pool
  private static final MarketEvent[] EVENT_POOL = {
      new MarketEvent("Bull Market",    "Investors are optimistic",   +0.005, +0.10, 30),
      new MarketEvent("Bear Market",    "Fear grips the market",      -0.005, -0.10, 30),
      new MarketEvent("Trade War",      "Tariffs announced!",         -0.008, -0.15, 20),
      new MarketEvent("Tech Boom",      "AI hype drives stocks up",   +0.010, +0.15, 15),
      new MarketEvent("Rate Cut",       "Fed cuts interest rates",    +0.003, +0.05, 25),
      new MarketEvent("Market Panic",   "Flash crash incoming",       -0.015, -0.20, 10),
      new MarketEvent("Earnings Season","Mixed earnings reports",      0.000,  0.00, 20),
  };

  private static final double EVENT_CHANCE_PER_TICK = 0.005; // ~0.5% per tick

  public void tick()
  {
    if (activeEvent != null)
    {
      ticksRemaining--;
      if (ticksRemaining <= 0)
      {
        activeEvent = null;
      }
      return; // don't roll new event while one is active
    }

    if (ThreadLocalRandom.current().nextDouble() < EVENT_CHANCE_PER_TICK)
    {
      activeEvent = EVENT_POOL[ThreadLocalRandom.current().nextInt(EVENT_POOL.length)];
      ticksRemaining = activeEvent.durationTicks();
    }
  }

  public double getPriceBias()
  {
    return activeEvent != null ? activeEvent.priceBiasModifier() : 0.0;
  }

  public double getTransitionBias()
  {
    return activeEvent != null ? activeEvent.transitionBiasModifier() : 0.0;
  }

  public MarketEvent getActiveEvent() { return activeEvent; }
  public boolean hasActiveEvent()     { return activeEvent != null; }
}
```

### Integration points

**1. `StockMarket` owns the event manager and fires events to the UI:**

```java
public enum StockMarket
{
  INSTANCE;

  private final MarketSentiment sentiment = new MarketSentiment();
  private final MarketEventManager eventManager = new MarketEventManager();

  // New observer list for market events
  public final List<Consumer<MarketEvent>> onMarketEvent = new ArrayList<>();

  public void updateAllLiveStocks()
  {
    sentiment.tick();

    MarketEvent before = eventManager.getActiveEvent();
    eventManager.tick();
    MarketEvent after = eventManager.getActiveEvent();

    // Fire event only when a NEW event starts
    if (after != null && after != before)
    {
      onMarketEvent.forEach(listener -> listener.accept(after));
    }

    for (LiveStock liveStock : liveStocks) { ... }
  }
}
```

**2. Combined bias passed to price calculation:**

```java
double totalBias = sentiment.getPriceBias() + eventManager.getPriceBias();
currentPrice = MathUtil.Round(currentState.calculateNewPrice(currentPrice, totalBias));
```

**3. Wiring to UI (same pattern as existing stock alerts):**

In `AppContext.registerObservers()`:

```java
StockMarket market = StockMarket.INSTANCE;
market.onMarketEvent.add(event -> {
  NotificationManager alertManager = ViewManager.getAlertNotificationManager();
  if (alertManager != null)
    alertManager.notify("📰 " + event.name() + ": " + event.description(),
        NotificationType.WARNING);
});
```

This uses the existing `Consumer<>` observer pattern — the business layer fires events, `AppContext` (composition root) bridges to the presentation layer's `NotificationManager` via a lambda.

---

## Data flow

```
MarketTickerThread (each tick)
  → StockMarket.updateAllLiveStocks()
    → MarketSentiment.tick()              — may shift mood
    → MarketEventManager.tick()           — may start/end event
    → fire onMarketEvent if new event     — observer pattern → UI alert
    → for each LiveStock:
        totalBias = sentiment + event biases
        → LiveStockState.calculateNewPrice(price, totalBias)
        → TransitionManager.getNextState(state, ticks, totalTransitionBias)
```

---

## Files to create

| File | Type | Location |
|---|---|---|
| `MarketSentiment.java` | New class | `business/stockmarket/simulation/` |
| `MarketEvent.java` | New record | `business/stockmarket/simulation/` |
| `MarketEventManager.java` | New class | `business/stockmarket/simulation/` |

## Files to modify

| File | Change |
|---|---|
| `LiveStockState.java` | Add `double marketBias` parameter to `calculateNewPrice` |
| `SteadyState.java` | Accept and apply `marketBias` |
| `GrowingState.java` | Accept and apply `marketBias` |
| `DecliningState.java` | Accept and apply `marketBias` |
| `BankruptState.java` | Accept parameter (ignore it — price stays 0) |
| `ResetState.java` | Accept parameter (ignore it — price resets to $100) |
| `LiveStock.java` | Pass combined bias to `calculateNewPrice()` |
| `StockMarket.java` | Own `MarketSentiment` + `MarketEventManager`, tick them, fire events |
| `TransitionManager.java` | Accept and apply transition bias |
| `AppContext.java` | Wire `onMarketEvent` observer to `NotificationManager` |

---

## Design principles maintained

| Principle | How |
|---|---|
| **State pattern** | Individual stock states unchanged — just receive an extra parameter |
| **Observer pattern** | `onMarketEvent` list of `Consumer<MarketEvent>` — same as existing stock events |
| **Separation of concerns** | Market-wide logic in its own classes, not mixed into stock states |
| **MVVM** | UI notified via observer → `AppContext` bridge → `NotificationManager` |
| **Open/Closed** | New events added to `EVENT_POOL` array without modifying any other code |
| **Single Responsibility** | `MarketSentiment` handles mood, `MarketEventManager` handles events, `StockMarket` orchestrates |

---

## Configuration candidates for `AppConfig`

If implemented, these magic numbers could move to `AppConfig`:

- `MOOD_BIAS` (0.003)
- `TRANSITION_BIAS` (0.05)
- `EVENT_CHANCE_PER_TICK` (0.005)
- Individual event durations and bias values
- Mood shift chance parameters (0.02 base, 0.002 increment, 0.15 cap)

