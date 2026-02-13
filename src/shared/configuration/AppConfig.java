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

  private final int startingBalance;
  private final double transactionFee;
  private final int updateFrequencyInMs;
  private final double stockResetValue;

  AppConfig()
  {
    this.startingBalance = 10000;
    this.transactionFee = 0.05;
    this.updateFrequencyInMs = 1000;
    this.stockResetValue = 100.0;
  }

  public int getStartingBalance()
  {
    return startingBalance;
  }

  public double getTransactionFee()
  {
    return transactionFee;
  }

  public int getUpdateFrequencyInMs()
  {
    return updateFrequencyInMs;
  }

  public double getStockResetValue()
  {
    return stockResetValue;
  }
}
