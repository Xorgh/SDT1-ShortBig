package business.fees;

import shared.configuration.AppConfig;

public class FlatFeeStrategy implements IFeeStrategy
{
  @Override public double calculate(double pricePerShare, int numberOfShares)
  {
    return AppConfig.INSTANCE.getTransactionFeeFlat();
  }
}
