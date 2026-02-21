package entities;

import java.util.UUID;

public class OwnedStock
{
    private final UUID id;
    private final UUID portfolioId;
    private final String stockSymbol;
    private int numberOfShares;

//    new object
  public OwnedStock(UUID portfolioId, String stockSymbol, int numberOfShares)
  {
    id = UUID.randomUUID();
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.numberOfShares = numberOfShares;
  }

// loaded object
  public OwnedStock(UUID id, UUID portfolioId, String stockSymbol, int numberOfShares)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.numberOfShares = numberOfShares;
  }

  @Override
  public String toString() {
    return "OwnedStock{" +
        "id=" + id +
        ", portfolioId=" + portfolioId +
        ", stockSymbol='" + stockSymbol + '\'' +
        ", numberOfShares=" + numberOfShares +
        '}';
  }

  public UUID getId()
  {
    return id;
  }

  public UUID getPortfolioId()
  {
    return portfolioId;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public int getNumberOfShares()
  {
    return numberOfShares;
  }

  public void setNumberOfShares(int numberOfShares)
  {
    this.numberOfShares = numberOfShares;
  }
}
