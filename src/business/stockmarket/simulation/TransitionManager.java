package business.stockmarket.simulation;

import shared.configuration.AppConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TransitionManager
{
  private final Map<Class<? extends LiveStockState>, BiFunction<Integer, Double, LiveStockState>> transitionHandlers;
  private final Map<Class<? extends LiveStockState>, Supplier<LiveStockState>> forceTransitionHandlers;

  public TransitionManager()
  {
    transitionHandlers = new HashMap<>();
    transitionHandlers.put(BankruptState.class,  this::handleBankruptTransition);
    transitionHandlers.put(ResetState.class,     (ticks, adj) -> new SteadyState());

    transitionHandlers.put(SteadyState.class, (ticks, adj) -> {
      double halfAdj = adj / 2;
      return rollTransition(
          Math.max(0, AppConfig.INSTANCE.getSteadyToSteadyBase() - adj),
          AppConfig.INSTANCE.getSteadyToGrowingBase() + halfAdj,
          SteadyState::new, GrowingState::new, DecliningState::new);
    });

    transitionHandlers.put(GrowingState.class, (ticks, adj) -> rollTransition(
        Math.max(0, AppConfig.INSTANCE.getGrowingToGrowingBase() - adj),
        AppConfig.INSTANCE.getGrowingToSteadyBase() + adj,
        GrowingState::new, SteadyState::new, DecliningState::new));

    transitionHandlers.put(DecliningState.class, (ticks, adj) -> rollTransition(
        Math.max(0, AppConfig.INSTANCE.getDecliningToDecliningBase() - adj),
        AppConfig.INSTANCE.getDecliningToSteadyBase() + adj,
        DecliningState::new, SteadyState::new, GrowingState::new));

    forceTransitionHandlers = new HashMap<>();
    forceTransitionHandlers.put(SteadyState.class,
        () -> ThreadLocalRandom.current().nextDouble() < 0.5 ? new GrowingState() : new DecliningState());
    forceTransitionHandlers.put(GrowingState.class,   SteadyState::new);
    forceTransitionHandlers.put(DecliningState.class, SteadyState::new);
  }

  LiveStockState getNextState(LiveStockState currentState, int consecutiveTicks)
  {
    double adjustment = consecutiveTicks * AppConfig.INSTANCE.getStateTransitionIncrementPerTick();

    if (consecutiveTicks >= AppConfig.INSTANCE.getMaxConsecutiveTicksBeforeForceTransition()
        && forceTransitionHandlers.containsKey(currentState.getClass()))
    {
      return forceTransition(currentState);
    }

    BiFunction<Integer, Double, LiveStockState> handler = transitionHandlers.get(currentState.getClass());

    if (handler == null)
    {
      return currentState;
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

  private LiveStockState rollTransition(double stayChance, double firstChance,
      Supplier<LiveStockState> stay, Supplier<LiveStockState> first, Supplier<LiveStockState> second)
  {
    double roll = ThreadLocalRandom.current().nextDouble();
    if (roll < stayChance)                    return stay.get();
    else if (roll < stayChance + firstChance) return first.get();
    else                                      return second.get();
  }
}