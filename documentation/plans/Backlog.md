# Product Backlog

> ShortBig — Stock Game  
> Last updated: 2026-04-06

---

## 🔴 High Priority

### Code TODOs

- [ ] **Implement `StockResetService.handleStockReset()`** — currently empty stub  
  `src/business/services/handlers/StockResetService.java`

- [ ] **Finish `StockBankruptService.handleBankruptcy()`** — marked WIP, sets shares to 0 but doesn't handle portfolio balance or create a transaction  
  `src/business/services/handlers/StockBankruptService.java`

- [ ] **Remove test notification on stock click** — `// TODO remove after verifying`  
  `src/presentation/views/stockmarket/MarketViewController.java`

### Documentation

- [ ] **Update class diagram** — multiple changes pending (see `documentation/assignment6/Class diagram TODO.md`)
  - `LiveStockState` visibility
  - `LiveStock` method changes
  - `StockStateUpdateEvent` field changes
  - `StockMarket` removed methods
  - `StockListenerService` dependencies
  - `RunApp` signature changes
  - New: `StockAlertService` with `Consumer<String>` fields

- [ ] **Update class diagram for `StockPriceHistory`** — new constructor added  
  `src/entities/StockPriceHistory.java`

---

## 🟡 Medium Priority

### Bugs & Tech Debt

- [ ] **Consecutive tick counter lost on reload** — `LiveStock` resets `consecutiveTicksInState` to 0 when loaded from file. Decide whether to persist it.  
  `src/business/stockmarket/simulation/LiveStock.java`

- [ ] **Remove unused fields in `GameService`** — `notifyError` and `notifyInfo` are declared but never used or injected  
  `src/business/services/GameService.java`

---

## 🟢 Features — Planned

### Market Sentiment & Events

> Full plan: [MarketSentimentAndEvents.md](MarketSentimentAndEvents.md)

**Feature 1: Market Sentiment**

- [ ] Create `MarketSentiment.java` — slow-moving global mood (bullish/neutral/bearish)
- [ ] Add `double marketBias` parameter to `LiveStockState.calculateNewPrice()`
- [ ] Update all state implementations (`SteadyState`, `GrowingState`, `DecliningState`, `BankruptState`, `ResetState`)
- [ ] Wire `MarketSentiment` into `StockMarket.updateAllLiveStocks()`
- [ ] Apply transition bias in `TransitionManager`
- [ ] Move magic numbers to `AppConfig`

**Feature 2: Random Market Events**

- [ ] Create `MarketEvent.java` record
- [ ] Create `MarketEventManager.java` with predefined event pool
- [ ] Add `onMarketEvent` observer list to `StockMarket`
- [ ] Wire event notifications to UI via `AppContext.registerObservers()`
- [ ] Combine sentiment + event biases in `LiveStock.updatePrice()`

---

## 🔵 Ideas — Not Yet Planned

- [ ] Display current market sentiment in the UI (e.g. badge on MarketView)
- [ ] Display active market event with countdown in the UI
- [ ] Log file output for debug lines (~2 MB/hour with 6 stocks)
- [ ] Configurable log level filter (suppress DEBUG in production)
- [ ] Persist `consecutiveTicksInState` to file so reloads don't reset state timers

---

## ✅ Done

_Move completed items here._

