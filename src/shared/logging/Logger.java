package shared.logging;

// Singleton

public class Logger
{
  // Without volatile, the JVM can reorder instructions. Thread A might see
  // a partially constructed Logger instance.
  // Volatile ensures that when Thread B reads instance,
  // it sees the fully initialized object that Thread A created
  private static volatile Logger instance;
  private LogOutput output;

  private Logger()
  {
    output = new ConsoleLogOutput();
  }

  public static Logger getInstance()
  {
    // Double-checked locking is a lazy initialization pattern that avoids synchronizing every call by checking the instance twice:
    // first without a lock, then inside a synchronized block.
    // The second check prevents creating multiple instances when multiple threads race.
    // In Java, the instance must be volatile to ensure correct visibility and prevent reordering issues.

    if (instance == null)
    {
      synchronized (Logger.class)
      {
        if (instance == null)
        {
          instance = new Logger();
        }
      }
    }
    return instance;
  }

  // Synched to avoid race issues
  public synchronized void setOutput(LogOutput output)
  {
    this.output = output;
  }

  // Synched to avoid race issues
  public synchronized void log(LogLevel logLevel, String message)
  {
    output.log(logLevel, message);
  }

}
