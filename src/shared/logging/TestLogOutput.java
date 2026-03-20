package shared.logging;

public class TestLogOutput implements LogOutput
{
  @Override public void log(LogLevel logLevel, String message)
  {
    // Logging effectively disabled while testing
  }
}
