package business.stockmarket.simulation;

import shared.configuration.AppConfig;

public class LiveStock
{
  private String symbol;
//  private someStateObject currentState;
  private double currentPrice;

  public LiveStock(String symbol)
  {
    this.symbol = symbol;
    this.currentPrice = AppConfig.INSTANCE.getStockResetValue();
//    this.currentState = someDefaultState;
  }

  public void updatePrice()
  {
    double priceChange = currentState.calculatePriceChange(this);

    currentPrice += priceChange;

    if(currentPrice <= 0)
    {
      currentPrice = 0;
      setState(SomeStateObject bankrup);
    }
  }

  // default access modifier for package privacy
  void setState(SomeStateObject object)
  {
    currentState = object;
  }

  public String getStateName()
  {
    return currentState.toString();
  }

  public String getSymbol()
  {
    return symbol;
  }

  public double getCurrentPrice()
  {
    return currentPrice;
  }
}
