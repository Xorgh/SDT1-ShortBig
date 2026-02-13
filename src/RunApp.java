import shared.logging.LogLevel;
import shared.logging.Logger;

public class RunApp
{
  static void main(String[] args)
  {
    Logger logger = Logger.getInstance();
    logger.log(LogLevel.INFO, "Test123");
    logger.log(LogLevel.WARNING, "Test123");
    logger.log(LogLevel.ERROR, "Test123");
  }
}
