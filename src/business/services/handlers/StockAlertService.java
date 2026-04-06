package business.services.handlers;

import business.events.StockBankruptcyEvent;
import business.events.StockResetEvent;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.function.Consumer;

public class StockAlertService
{
  private final Logger logger = Logger.getInstance();
  private final Consumer<String> notifyError;
  private final Consumer<String> notifyInfo;

  public StockAlertService(Consumer<String> notifyError, Consumer<String> notifyInfo)
  {
    this.notifyError = notifyError;
    this.notifyInfo = notifyInfo;
  }

  public void handleBankruptcyAlert(StockBankruptcyEvent event)
  {
    String message = "⚠ Stock [" + event.stockSymbol() + "] went BANKRUPT!";
    logger.log(LogLevel.WARNING, message);
    notifyError.accept(message);   // bankruptcy = error
  }

  public void handleStockResetAlert(StockResetEvent event)
  {
    String message = "🔄 Stock [" + event.stockSymbol() + "] has been RESET.";
    logger.log(LogLevel.INFO, message);
    notifyInfo.accept(message);    // reset = info
  }
}
