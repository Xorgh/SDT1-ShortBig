package business.stockmarket.simulation;

import shared.configuration.AppConfig;

import java.util.Random;

public class GrowingState implements LiveStockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange()
  {
    double roll = random.nextDouble();

    if (roll < AppConfig.INSTANCE.getReversePriceChangeChance()) {
      // 5% chance: decline between 0% and -0.5%
      return -random.nextDouble() * 0.5 / 100;
    } else if (roll < (1 - AppConfig.INSTANCE.getRapidPriceChangeChance())) {
      // 90% chance: moderate growth between 0.1% and 1.5%
      return (0.1 + random.nextDouble() * 1.4) / 100;
    } else {
      // 5% chance: strong growth between 1.5% and 5%
      return (1.5 + random.nextDouble() * 3.5) / 100;
    }
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}