package entities;

import java.util.UUID;

public class OwnedStock
{
    private final UUID id;
    private final UUID portfolioId;
    private final String stockSymbol;
    private int numberOfShares;

  public OwnedStock(UUID portfolioId, String stockSymbol, int numberOfShares)
  {
    id = UUID.randomUUID();
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.numberOfShares = numberOfShares;
  }

  public OwnedStock(UUID id, UUID portfolioId, String stockSymbol, int numberOfShares)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.numberOfShares = numberOfShares;
  }

//  TODO Getters/Setters og andre metoder
}
