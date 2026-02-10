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

////  loaded object
//  public OwnedStock(UUID id)
//  {
//    this.id = id;
////    TODO how to load objects
//  }

//  TODO Getters/Setters og andre metoder
}
