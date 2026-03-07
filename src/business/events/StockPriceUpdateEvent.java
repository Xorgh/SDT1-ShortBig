package business.events;

public record StockPriceUpdateEvent(String stockSymbol, double oldPrice, double newPrice){}
