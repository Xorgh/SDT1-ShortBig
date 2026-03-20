package mocks;

import shared.logging.LogLevel;
import shared.logging.LogOutput;

public class TestLogOutput implements LogOutput
{
  @Override public void log(LogLevel logLevel, String message)
  {
    // Logging silenced for tests
  }
}
