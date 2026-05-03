package shared.logging;

import provided.FileLogOutputter;
import shared.configuration.AppConfig;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileLogOutputAdapter implements LogOutput
{
  FileLogOutputter adaptee;
  String logFilePath = AppConfig.INSTANCE.getLogFileDirectory() + "app-"
      + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
  String minimumLogLevel = AppConfig.INSTANCE.getMinimumFileLogLevel();

  public FileLogOutputAdapter()
  {
    adaptee = new FileLogOutputter(logFilePath, minimumLogLevel);
  }

  @Override public void log(LogLevel logLevel, String message)
  {
    switch (logLevel)
    {
      case DEBUG -> {} // Unsupported by provided library
      case INFO -> adaptee.logInfo(message);
      case WARNING -> adaptee.logWarning(message);
      case ERROR -> adaptee.logError(message);
    }
  }
}
