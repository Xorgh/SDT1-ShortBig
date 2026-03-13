package business.events;

import entities.StockState;

public record StockStateUpdateEvent(String stockSymbol, StockState oldLiveStockState, StockState newLiveStockState)
{
}
