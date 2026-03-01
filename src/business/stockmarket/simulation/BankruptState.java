package business.stockmarket.simulation;

public class BankruptState implements LiveStockState
{
  @Override public double calculatePriceChange()
  {
    return 0;
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
