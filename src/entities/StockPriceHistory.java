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

  // created from a price update
  // TODO update class diagram with new constructor.

  public StockPriceHistory(String stockSymbol, double price)
  {
    this.id = UUID.randomUUID();
    this.stockSymbol = stockSymbol;
    this.price = price;
    this.timestamp = LocalDateTime.now();
  }

  public UUID getId()
  {
    return id;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public double getPrice()
  {
    return price;
  }

  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }

  @Override public String toString()
  {
    return "StockPriceHistory{" + "id=" + id + ", stockSymbol='" + stockSymbol + '\'' + ", price=" + price
        + ", timestamp=" + timestamp + '}';
  }
}
