package entities;

public class Stock
{
  private final String symbol;
  private final String name;
  private double currentPrice;
  private StockState currentState;

  public Stock(String symbol, String name)
  {
    this.symbol = symbol;
    this.name = name;
  }

//  TODO How do we initialize currentPrice and state
}
