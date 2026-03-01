package business.stockmarket.simulation;

import shared.configuration.AppConfig;

import java.util.Random;

public class TransitionManager
{
  private static final Random random = new Random();

  /**
   * Determines the next state based on current state and consecutive ticks.
   * Probabilities increase for transitioning out as consecutive ticks accumulate.
   * After max consecutive ticks, transition is guaranteed.
   *
   * @param currentState The current state of the stock
   * @param consecutiveTicks Number of consecutive ticks in the current state
   * @return The next state (could be same or different)
   */
  public LiveStockState getNextState(LiveStockState currentState, int consecutiveTicks)
  {
    // Bankrupt and Reset states don't transition
    if (currentState instanceof BankruptState || currentState instanceof ResetState)
    {
      return currentState;
    }

    // Calculate adjustment based on consecutive ticks
    double adjustment = consecutiveTicks * AppConfig.INSTANCE.getStateTransitionIncrementPerTick();

    // Check if we've reached max consecutive ticks (force transition)
    if (consecutiveTicks >= AppConfig.INSTANCE.getMaxConsecutiveTicksBeforeForceTransition())
    {
      return forceTransition(currentState);
    }

    // Handle each state type
    if (currentState instanceof SteadyState)
    {
      return handleSteadyStateTransition(adjustment);
    }
    else if (currentState instanceof GrowingState)
    {
      return handleGrowingStateTransition(adjustment);
    }
    else if (currentState instanceof DecliningState)
    {
      return handleDecliningStateTransition(adjustment);
    }

    return currentState; // Fallback
  }

  /**
   * Force a transition when max consecutive ticks is reached.
   * SteadyState: 50/50 to Growing or Declining
   * GrowingState/DecliningState: 100% to SteadyState
   */
  private LiveStockState forceTransition(LiveStockState currentState)
  {
    if (currentState instanceof SteadyState)
    {
      // 50/50 split between Growing and Declining
      return random.nextDouble() < 0.5 ? new GrowingState() : new DecliningState();
    }
    else if (currentState instanceof GrowingState || currentState instanceof DecliningState)
    {
      // Always transition to Steady
      return new SteadyState();
    }

    return currentState;
  }

  /**
   * Handle SteadyState transitions.
   * Adjustment is split equally between Growing and Declining.
   * Base: 80% stay, 10% Growing, 10% Declining
   */
  private LiveStockState handleSteadyStateTransition(double adjustment)
  {
    // Calculate adjusted probabilities
    // Split adjustment equally between Growing and Declining
    double halfAdjustment = adjustment / 2;
    double stayProbability = Math.max(0, AppConfig.INSTANCE.getSteadyToSteadyBase() - adjustment);
    double growingProbability = AppConfig.INSTANCE.getSteadyToGrowingBase() + halfAdjustment;
    double decliningProbability = AppConfig.INSTANCE.getSteadyToDecliningBase() + halfAdjustment;

    double roll = random.nextDouble();

    if (roll < stayProbability)
    {
      return new SteadyState();
    }
    else if (roll < stayProbability + growingProbability)
    {
      return new GrowingState();
    }
    else
    {
      return new DecliningState();
    }
  }

  /**
   * Handle GrowingState transitions.
   * Adjustment only increases probability to SteadyState.
   * Base: 75% stay, 20% Steady, 5% Declining
   */
  private LiveStockState handleGrowingStateTransition(double adjustment)
  {
    // Adjustment only goes to Steady, Declining probability remains unchanged
    double stayProbability = Math.max(0, AppConfig.INSTANCE.getGrowingToGrowingBase() - adjustment);
    double steadyProbability = AppConfig.INSTANCE.getGrowingToSteadyBase() + adjustment;
    double decliningProbability = AppConfig.INSTANCE.getGrowingToDecliningBase();

    double roll = random.nextDouble();

    if (roll < stayProbability)
    {
      return new GrowingState();
    }
    else if (roll < stayProbability + steadyProbability)
    {
      return new SteadyState();
    }
    else
    {
      return new DecliningState();
    }
  }

  /**
   * Handle DecliningState transitions.
   * Adjustment only increases probability to SteadyState.
   * Base: 65% stay, 25% Steady, 10% Growing
   */
  private LiveStockState handleDecliningStateTransition(double adjustment)
  {
    // Adjustment only goes to Steady, Growing probability remains unchanged
    double stayProbability = Math.max(0, AppConfig.INSTANCE.getDecliningToDecliningBase() - adjustment);
    double steadyProbability = AppConfig.INSTANCE.getDecliningToSteadyBase() + adjustment;
    double growingProbability = AppConfig.INSTANCE.getDecliningToGrowingBase();

    double roll = random.nextDouble();

    if (roll < stayProbability)
    {
      return new DecliningState();
    }
    else if (roll < stayProbability + steadyProbability)
    {
      return new SteadyState();
    }
    else
    {
      return new GrowingState();
    }
  }
}


