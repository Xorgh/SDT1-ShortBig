package shared.configuration;

// Singleton enum
// JVM initializes at runtime == no need for manual creation.
// And enums constants are immutable by default

// Thread safety
//Is it important to make this thread-safe?
// Why or why not? Maybe we will talk about this at the exam.

// We have no setters and do not create the instance manually. Fields are final. All in all this is thread safe.

// Why thread safety matters here:
// Multiple threads may access AppConfig.INSTANCE simultaneously in a multi-threaded application
// Without proper synchronization, you could have visibility issues or partial initialization
// The enum pattern solves this automatically - no manual synchronization needed
// For the exam: You could mention that alternative singleton patterns (lazy initialization, double-checked locking) require explicit synchronization, but enums handle this inherently.
// This makes enums the preferred singleton implementation in Java.

public enum AppConfig
{
  INSTANCE;

  // General configuration
  private final String dataDirectory;
  private final String testDataDirectory;
  private final int startingBalance;
  private final double transactionFeePercent;
  private final double transactionFeeFlat;
  private final int updateFrequencyInMs;
  private final double stockResetValue;

  // Price volatility probabilities
  private final double reversePriceChangeChance;
  private final double rapidPriceChangeChance;
  private final double defaultPriceChangeChance;

  // Bankruptcy
  private final int bankruptStateTimeoutTicks;

  // State transition configuration
  private final double stateTransitionIncrementPerTick;
  private final int maxConsecutiveTicksBeforeForceTransition;

  // Base transition probabilities for Steady state
  private final double steadyToSteadyBase;
  private final double steadyToGrowingBase;
  private final double steadyToDecliningBase;

  // Base transition probabilities for Growing state
  private final double growingToGrowingBase;
  private final double growingToSteadyBase;
  private final double growingToDecliningBase;

  // Base transition probabilities for Declining state
  private final double decliningToDecliningBase;
  private final double decliningToSteadyBase;
  private final double decliningToGrowingBase;

  public String getDataDirectory()
  {
    return dataDirectory;
  }

  public String getTestDataDirectory()
  {
    return testDataDirectory;
  }

  AppConfig()
  {
    // General configuration
    this.dataDirectory = "data/prd/";
    this.testDataDirectory = "data/test/";
    this.startingBalance = 10000;
    this.transactionFeePercent = 0.05;
    this.transactionFeeFlat = 10;
    if (this.transactionFeePercent < 0)
      throw new IllegalStateException("Transaction fee cannot be negative");


    this.updateFrequencyInMs = 1000;
    this.stockResetValue = 100.0;

    // Price volatility probabilities
    this.reversePriceChangeChance = 0.05;
    this.rapidPriceChangeChance = 0.05;
    this.defaultPriceChangeChance = 1 - ( reversePriceChangeChance + rapidPriceChangeChance);

    // Bankruptcy configuration
    this.bankruptStateTimeoutTicks = 20;

    // State transition configuration
    this.stateTransitionIncrementPerTick = 0.05;
    this.maxConsecutiveTicksBeforeForceTransition = 10;

    // Base probabilities for Steady state (80% stay, 10% growing, 10% declining)
    this.steadyToSteadyBase = 0.80;
    this.steadyToGrowingBase = 0.10;
    this.steadyToDecliningBase = 0.10;

    // Base probabilities for Growing state
    this.growingToGrowingBase = 0.70;
    this.growingToSteadyBase = 0.20;
    this.growingToDecliningBase = 0.10;

    // Base probabilities for Declining state
    this.decliningToDecliningBase = 0.70;
    this.decliningToSteadyBase = 0.20;
    this.decliningToGrowingBase = 0.10;
  }

  public int getStartingBalance()
  {
    return startingBalance;
  }

  public double getTransactionFeePercent()
  {
    return transactionFeePercent;
  }

  public int getUpdateFrequencyInMs()
  {
    return updateFrequencyInMs;
  }

  public double getReversePriceChangeChance()
  {
    return reversePriceChangeChance;
  }

  public double getRapidPriceChangeChance()
  {
    return rapidPriceChangeChance;
  }

  public double getDefaultPriceChangeChance()
  {
    return defaultPriceChangeChance;
  }

  public double getStockResetValue()
  {
    return stockResetValue;
  }

  public double getStateTransitionIncrementPerTick()
  {
    return stateTransitionIncrementPerTick;
  }

  public int getMaxConsecutiveTicksBeforeForceTransition()
  {
    return maxConsecutiveTicksBeforeForceTransition;
  }

  public double getSteadyToSteadyBase()
  {
    return steadyToSteadyBase;
  }

  public double getSteadyToGrowingBase()
  {
    return steadyToGrowingBase;
  }

  public double getSteadyToDecliningBase()
  {
    return steadyToDecliningBase;
  }

  public double getGrowingToGrowingBase()
  {
    return growingToGrowingBase;
  }

  public double getGrowingToSteadyBase()
  {
    return growingToSteadyBase;
  }

  public double getGrowingToDecliningBase()
  {
    return growingToDecliningBase;
  }

  public double getDecliningToDecliningBase()
  {
    return decliningToDecliningBase;
  }

  public double getDecliningToSteadyBase()
  {
    return decliningToSteadyBase;
  }

  public double getDecliningToGrowingBase()
  {
    return decliningToGrowingBase;
  }

  public int getBankruptStateTimeoutTicks()
  {
    return bankruptStateTimeoutTicks;
  }

  public double getTransactionFeeFlat() { return transactionFeeFlat; }
}
