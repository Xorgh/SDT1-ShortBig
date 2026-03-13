package business.stockmarket.simulation;

import java.util.concurrent.ThreadLocalRandom;

public class SteadyState implements LiveStockState
{
  @Override public double calculateNewPrice(double currentPrice)
  {
    double change = (ThreadLocalRandom.current().nextDouble() * 2 - 1) / 100; // +-1%
    return currentPrice + currentPrice * change;
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
