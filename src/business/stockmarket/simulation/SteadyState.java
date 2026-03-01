package business.stockmarket.simulation;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SteadyState implements LiveStockState
{
  @Override public double calculatePriceChange()
  {
    double change = (ThreadLocalRandom.current().nextDouble() * 2-1) / 100; // +- 0.01
    return change;
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
