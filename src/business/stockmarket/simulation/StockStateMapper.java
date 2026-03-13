package business.stockmarket.simulation;

import entities.StockState;

public class StockStateMapper
{/**
 * Converts a LiveStockState instance to a StockState enum.
 * Used when persisting simulation state to database/files.
 */
public static StockState toStockState(LiveStockState liveState)
{
  if (liveState instanceof SteadyState) return StockState.STEADY;
  if (liveState instanceof GrowingState) return StockState.GROWING;
  if (liveState instanceof DecliningState) return StockState.DECLINING;
  if (liveState instanceof BankruptState) return StockState.BANKRUPT;
  if (liveState instanceof ResetState) return StockState.RESET;

  throw new IllegalArgumentException("Unknown LiveStockState: " + liveState.getClass().getName());
}

  /**
   * Converts a StockState enum to a LiveStockState instance.
   * Used when loading persisted stocks into the simulation.
   */
  public static LiveStockState toLiveStockState(StockState stockState)
  {
    switch (stockState)
    {
      case STEADY: return new SteadyState();
      case GROWING: return new GrowingState();
      case DECLINING: return new DecliningState();
      case BANKRUPT: return new BankruptState();
      case RESET: return new ResetState();
      default:
        throw new IllegalArgumentException("Unknown StockState: " + stockState);
    }
  }
}
