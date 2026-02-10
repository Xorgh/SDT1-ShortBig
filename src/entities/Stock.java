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

//  //  loaded object
//  public Stock(String symbol)
//  {
//    this.symbol = symbol;
//    //  TODO how to load objects
//  }

  //  TODO How do we initialize currentPrice and state
}
