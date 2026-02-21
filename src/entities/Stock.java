package entities;

public class Stock
{
  private final String symbol;
  private final String name;
  private double currentPrice;
  private StockState currentState;

  //  new object
  public Stock(String symbol, String name, double currentPrice, StockState currentState)
  {
    this.symbol = symbol;
    this.name = name;
    this.currentPrice = currentPrice;
    this.currentState = currentState;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public String getName()
  {
    return name;
  }

  public double getCurrentPrice()
  {
    return currentPrice;
  }

  public void setCurrentPrice(double currentPrice)
  {
    this.currentPrice = currentPrice;
  }

  public StockState getCurrentState()
  {
    return currentState;
  }

  public void setCurrentState(StockState currentState)
  {
    this.currentState = currentState;
  }

  @Override
  public String toString() {
    return "Stock{" +
        "symbol='" + symbol + '\'' +
        ", name='" + name + '\'' +
        ", currentPrice=" + currentPrice +
        ", currentState=" + currentState +
        '}';
  }
}
