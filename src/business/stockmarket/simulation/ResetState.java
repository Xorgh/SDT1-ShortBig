package business.stockmarket.simulation;

public class ResetState implements LiveStockState
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
