package business.stockmarket.simulation;

import shared.configuration.AppConfig;

public class LiveStock
{
  private final String symbol;
  private LiveStockState currentState;
  private double currentPrice;
  private int consecutiveTicksInState;
  private final TransitionManager transitionManager;

  public LiveStock(String symbol)
  {
    this.symbol = symbol;
    this.currentPrice = AppConfig.INSTANCE.getStockResetValue();
    this.currentState = new SteadyState();
    this.consecutiveTicksInState = 0;
    this.transitionManager = new TransitionManager();
  }

  public void updatePrice()
  {
    // Calculate and apply price change
    double priceChange = currentState.calculatePriceChange();
    currentPrice += currentPrice * priceChange;

    // Check for bankruptcy
    if(currentPrice <= 0)
    {
      currentPrice = 0;
      setState(new BankruptState());
      // Don't return early - let state transition logic handle timeout
    }

    // Increment consecutive ticks counter
    consecutiveTicksInState++;

    // Check for state transition
    LiveStockState newState = transitionManager.getNextState(currentState, consecutiveTicksInState);

    // If state changed, update and reset counter
    if (newState.getClass() != currentState.getClass())
    {
      setState(newState);

      // Reset price when transitioning to ResetState
      if (newState instanceof ResetState)
      {
        currentPrice = AppConfig.INSTANCE.getStockResetValue();
      }
    }
  }

  // default access modifier for package privacy
  void setState(LiveStockState state)
  {
    currentState = state;
    consecutiveTicksInState = 0; // Reset counter when state changes
  }

  public String getStateName()
  {
    return currentState.getName();
  }

  public String getSymbol()
  {
    return symbol;
  }

  public double getCurrentPrice()
  {
    return currentPrice;
  }

  public int getConsecutiveTicksInState()
  {
    return consecutiveTicksInState;
  }
}
