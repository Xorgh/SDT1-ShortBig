package business.stockmarket.simulation;

public class BankruptState implements LiveStockState
{
  @Override public double calculateNewPrice(double currentPrice)
  {
    return 0;
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
