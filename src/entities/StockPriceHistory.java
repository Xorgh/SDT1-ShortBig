package entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockPriceHistory
{
  private final UUID id;
  private final String stockSymbol;
  private final double price;
  private final LocalDateTime timestamp;

  //  new object
  public StockPriceHistory(UUID id, String stockSymbol, double price, LocalDateTime timestamp)
  {
    this.id = id;
    this.stockSymbol = stockSymbol;
    this.price = price;
    this.timestamp = timestamp;
  }

  ////  loaded object
  //  public StockPriceHistory(UUID id)
  //  {
  //    this.id = id;
  //  TODO how to load objects.
  //  }
}
