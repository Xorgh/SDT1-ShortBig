package business.events;

import entities.Portfolio;

public record BuyStockEvent(String stockSymbol, int numberOfShares, Portfolio portfolio){}

