package shared.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLogOutput implements LogOutput
{
  @Override public void log(LogLevel logLevel, String message)
  {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String timestamp = LocalDateTime.now().format(formatter);
    String level = logLevel.name();

    String line = String.format("[%s][%s]%s %s", timestamp, level, " ".repeat(7 - level.length()), message);
    System.out.println(line);
  }
}
