package business.stockmarket.simulation;

import entities.StockState;
import shared.configuration.AppConfig;
import shared.logging.LogLevel;
import shared.logging.Logger;
import shared.utility.MathUtil;

public class LiveStock
{
  private static final Logger logger = Logger.getInstance();

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

    logger.log(LogLevel.INFO, String.format("[%s] Initialized at %.2f", symbol, currentPrice));
  }

  public LiveStock(String symbol, double currentPrice, StockState currentState)
  {
    this.symbol = symbol;
    this.currentPrice = MathUtil.Round(currentPrice);
    this.currentState = StockStateMapper.toLiveStockState(currentState);;
    this.consecutiveTicksInState = 0;
    // TODO last current tick lost when reloading, do I care?
    this.transitionManager = new TransitionManager();

    logger.log(LogLevel.INFO, String.format("[%s] Loaded at %.2f", symbol, currentPrice));
  }

  public void updatePrice()
  {
    double previousPrice = currentPrice;

    // Calculate and apply price change
    double priceChange = currentState.calculatePriceChange();
    currentPrice += currentPrice * priceChange;
    currentPrice = MathUtil.Round(currentPrice);

    // Log price change
    logger.log(LogLevel.DEBUG, String.format("[%s] %.2f → %.2f (%+.2f%%) | %s (tick %d)",
        symbol,
        previousPrice,
        currentPrice,
        priceChange * 100,
        currentState.getName(),
        consecutiveTicksInState + 1
    ));

    // Check for bankruptcy
    if(currentPrice <= 0)
    {
      currentPrice = 0;
      logger.log(LogLevel.WARNING, String.format("[%s] BANKRUPT - Price reached 0", symbol));
      setState(new BankruptState());
    }

    // Increment consecutive ticks counter
    consecutiveTicksInState++;

    // Check for state transition
    LiveStockState newState = transitionManager.getNextState(currentState, consecutiveTicksInState);

    // If state changed, update and reset counter
    if (newState.getClass() != currentState.getClass())
    {
      String oldState = currentState.getName();
      setState(newState);

      logger.log(LogLevel.INFO, String.format("[%s] %s → %s",
          symbol, oldState, newState.getName()));

      // Reset price when transitioning to ResetState
      if (newState instanceof ResetState)
      {
        // TODO rework price calculation.
//        newState.calculatePriceChange();
        logger.log(LogLevel.INFO, String.format("[%s] RESET - Price restored to %.2f",
            symbol, currentPrice));
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

  public StockState getStockState() { return StockStateMapper.toStockState(currentState) ; }

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
