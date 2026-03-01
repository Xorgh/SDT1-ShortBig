package business.stockmarket.simulation;

import shared.configuration.AppConfig;
import java.util.concurrent.ThreadLocalRandom;

public class GrowingState implements LiveStockState
{
  @Override public double calculatePriceChange()
  {
    double roll = ThreadLocalRandom.current().nextDouble();

    if (roll < AppConfig.INSTANCE.getReversePriceChangeChance()) {
      // Chance to decline 0% and 0.5%
      return -ThreadLocalRandom.current().nextDouble() * 0.5 / 100;
    } else if (roll < (1 - AppConfig.INSTANCE.getRapidPriceChangeChance())) {
      // Chance for moderate growth between 0.1% and 1.5%
      return (0.1 + ThreadLocalRandom.current().nextDouble() * 1.4) / 100;
    } else {
      // Chance for strong growth between 1.5% and 5%
      return (1.5 + ThreadLocalRandom.current().nextDouble() * 3.5) / 100;
    }
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}