package business.stockmarket.simulation;

import shared.configuration.AppConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TransitionManager
{
  // Maps state class → transition handler (consecutiveTicks, adjustment) → nextState
  private final Map<Class<? extends LiveStockState>, BiFunction<Integer, Double, LiveStockState>> transitionHandlers;

  // Maps state class → forced transition handler (only states that can be force-transitioned)
  private final Map<Class<? extends LiveStockState>, Supplier<LiveStockState>> forceTransitionHandlers;

  public TransitionManager()
  {
    transitionHandlers = new HashMap<>();
    transitionHandlers.put(BankruptState.class,  this::handleBankruptTransition);
    transitionHandlers.put(ResetState.class,     (ticks, adj) -> new SteadyState());
    transitionHandlers.put(SteadyState.class,    (ticks, adj) -> handleSteadyStateTransition(adj));
    transitionHandlers.put(GrowingState.class,   (ticks, adj) -> handleGrowingStateTransition(adj));
    transitionHandlers.put(DecliningState.class, (ticks, adj) -> handleDecliningStateTransition(adj));

    forceTransitionHandlers = new HashMap<>();
    forceTransitionHandlers.put(SteadyState.class,
        () -> ThreadLocalRandom.current().nextDouble() < 0.5 ? new GrowingState() : new DecliningState());
    forceTransitionHandlers.put(GrowingState.class,   SteadyState::new);
    forceTransitionHandlers.put(DecliningState.class, SteadyState::new);
  }

  LiveStockState getNextState(LiveStockState currentState, int consecutiveTicks)
  {
    double adjustment = consecutiveTicks * AppConfig.INSTANCE.getStateTransitionIncrementPerTick();

    // Force transition if max ticks reached — only applies to states that have a force handler
    if (consecutiveTicks >= AppConfig.INSTANCE.getMaxConsecutiveTicksBeforeForceTransition()
        && forceTransitionHandlers.containsKey(currentState.getClass()))
    {
      return forceTransition(currentState);
    }

    BiFunction<Integer, Double, LiveStockState> handler = transitionHandlers.get(currentState.getClass());

    if (handler == null)
    {
      return currentState; // Unknown state, fallback — stay in current
    }

    return handler.apply(consecutiveTicks, adjustment);
  }

  private LiveStockState handleBankruptTransition(int consecutiveTicks, double adjustment)
  {
    return consecutiveTicks >= AppConfig.INSTANCE.getBankruptStateTimeoutTicks()
        ? new ResetState()
        : new BankruptState();
  }

  private LiveStockState forceTransition(LiveStockState currentState)
  {
    Supplier<LiveStockState> handler = forceTransitionHandlers.get(currentState.getClass());
    return handler != null ? handler.get() : currentState;
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

    double roll = ThreadLocalRandom.current().nextDouble();

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

    double roll = ThreadLocalRandom.current().nextDouble();

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

    double roll = ThreadLocalRandom.current().nextDouble();

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


// TODO Improve Stock simulation - Skewed towards growing it seems at the moment.