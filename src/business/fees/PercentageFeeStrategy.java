package business.fees;

import shared.configuration.AppConfig;

public class PercentageFeeStrategy implements IFeeStrategy
{
  @Override public double calculate(double pricePerShare, int numberOfShares)
  {
    double grossAmount = pricePerShare * numberOfShares;

    double feeAmount = grossAmount * AppConfig.INSTANCE.getTransactionFeePercent();

    return feeAmount;
  }
}

