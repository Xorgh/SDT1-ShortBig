# Class Diagram Update — TODO

## LiveStockState (interface)
- Change visibility from **public** to **package-private**
    - No `+` prefix
    - Use `~` or no modifier (depending on notation)

## LiveStock
- Remove method:
    - `getCurrentState(): LiveStockState`
- Add method:
    - `+getStockState(): StockState`
- Update method visibility:
    - `~setState(state: LiveStockState)`

## StockStateUpdateEvent (record)
- Remove fields:
    - `oldLiveStockState: LiveStockState`
    - `newLiveStockState: LiveStockState`
- Add fields:
    - `oldState: StockState`
    - `newState: StockState`
- Remove dependency arrow to `LiveStockState`
- Add / update dependency arrow to `StockState`

## StockMarket
- Remove method:
    - `getAllLiveStocks(): List<LiveStock>`
    - _(already removed from code)_

## StockListenerService
- Remove dependency on `StockStateMapper`
    - No longer needed — `event.newState()` returns `StockState` directly
- All fields must be shown as `final`

## RunApp
- Update method signature:
    - `testRealTimeMarket()` now takes `StockDAO` as a parameter
- Remove `LiveStock` import / dependency
    - `RunApp` no longer references `LiveStock` directly