package shared.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLogOutput implements LogOutput
{
  @Override public void log(LogLevel logLevel, String message)
  {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String dt = LocalDateTime.now().format(formatter);
    System.out.println("[" + dt + "]" + "[" + logLevel.name() + "] " + message);
  }
}
