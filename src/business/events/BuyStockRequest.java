package business.events;

import java.util.UUID;

public record BuyStockRequest(String stockSymbol, int numberOfShares, UUID portfolioId){}

