package business.stockmarket.simulation;

import java.util.Random;

public class SteadyState implements LiveStockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange()
  {
    double change = (random.nextDouble() * 2-1) / 100; // +- 0.01
    return change;
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
