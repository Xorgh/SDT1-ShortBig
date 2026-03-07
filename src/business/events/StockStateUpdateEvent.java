package business.events;

import business.stockmarket.simulation.LiveStockState;

public record StockStateUpdateEvent(String stockSymbol, LiveStockState oldLiveStockState, LiveStockState newLiveStockState)
{
}
