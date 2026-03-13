package business.stockmarket.simulation;

import shared.configuration.AppConfig;

public class ResetState implements LiveStockState
{
  @Override public double calculateNewPrice(double currentPrice)
  {
    return AppConfig.INSTANCE.getStockResetValue();
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
