package business.requests;

import java.util.UUID;

public record SellStockRequest(String stockSymbol, int numberOfShares, UUID portfolioId){}

