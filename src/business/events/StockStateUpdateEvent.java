package business.events;

import entities.StockState;

public record StockStateUpdateEvent(String stockSymbol, StockState oldState, StockState newState)
{
}
