## Current Architecture Review

Let me explain the object management and flow in your stock market simulation:

### Object Creation & Dependencies

**No Dependency Injection Framework** — You're using **constructor initialization** and **direct instantiation**. This is perfectly fine for your application size.

```java
// LiveStock creates its own dependencies
public LiveStock(String symbol) {
  this.transitionManager = new TransitionManager();  // Direct instantiation
  this.currentState = new SteadyState();              // Direct instantiation
}
```

### Flow Explanation

#### 1. **Initialization** (When `LiveStock` is created)
```
LiveStock constructor├─> Creates TransitionManager (no dependencies, just Random)
  ├─> Creates initial SteadyState
  └─> Sets price to 100.0, consecutiveTicks to 0
```

#### 2. **Each Tick** (Every 1000ms via `updatePrice()`)

```
LiveStock.updatePrice()
  │
  ├─> 1. currentState.calculatePriceChange()
  │      └─> State uses Random + AppConfig for probabilities
  │
  ├─> 2. Apply price change to currentPrice
  │
  ├─> 3. Check if price <= 0
  │      └─> If yes: setState(new BankruptState())
  │
  ├─> 4. Increment consecutiveTicksInState
  │
  ├─> 5. transitionManager.getNextState(currentState, consecutiveTicks)
  │      ├─> Uses AppConfig for probabilities/thresholds
  │      ├─> Uses Random for probability rolls
  │      └─> Returns new state instance or same state
  │
  └─> 6. If state changed:
         ├─> setState(newState) — resets consecutiveTicks
         └─> If ResetState: reset price to 100.0
```

#### 3. **State Transition Logic** (Inside `TransitionManager`)

```
TransitionManager.getNextState()
  │
  ├─> Is BankruptState? 
  │   └─> Yes: Check if consecutiveTicks >= 20 → ResetState
  │
  ├─> Is ResetState?
  │   └─> Yes: Immediately return SteadyState
  │
  ├─> Is SteadyState/GrowingState/DecliningState?
  │   ├─> Calculate adjustment (consecutiveTicks × 0.10)
  │   ├─> Check if consecutiveTicks >= 5 (forced transition)
  │   └─> Roll dice with adjusted probabilities
  │
  └─> Return new state instance (or current if staying)
```

### Object Lifecycle

| Object | Lifecycle | Ownership |
|--------|-----------|-----------|
| `LiveStock` | Created once, lives forever | Your business layer |
| `TransitionManager` | Created once per `LiveStock` | Owned by `LiveStock` |
| `LiveStockState` implementations | Created on every state change | Short-lived, replaced frequently |
| `AppConfig.INSTANCE` | Singleton, exists for entire JVM lifetime | Global |

### Key Design Patterns Used

1. **State Pattern** — `LiveStockState` interface with concrete states
2. **Singleton Pattern** — `AppConfig` enum singleton
3. **Strategy Pattern** — `TransitionManager` encapsulates transition logic
4. **Composition** — `LiveStock` has-a `TransitionManager` and has-a `LiveStockState`

### What's Being Passed vs Stored

**Passed as parameters:**
- `currentState` → to `TransitionManager.getNextState()`
- `consecutiveTicks` → to `TransitionManager.getNextState()`

**Stored as fields:**
- `LiveStock` stores: `currentState`, `transitionManager`, `consecutiveTicksInState`
- `TransitionManager` stores: `Random` instance (static)
- State classes store: `Random` instance (static)

**Accessed globally:**
- `AppConfig.INSTANCE` — all classes read configuration from it

### Summary

You're **not using dependency injection** in the framework sense (no Spring, Guice, etc.). You're using **manual composition** where `LiveStock` creates and owns its dependencies. This is appropriate for your application size and keeps things simple.

The flow is:
1. `LiveStock` owns `TransitionManager` (created once)
2. `LiveStock` owns current `LiveStockState` (replaced frequently)
3. Each tick: calculate price change → check bankruptcy → ask `TransitionManager` for next state → transition if needed
4. `BankruptState` waits 20 ticks → `ResetState` resets price → immediately moves to `SteadyState`