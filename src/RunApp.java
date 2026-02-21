import entities.*;
import persistence.fileimplementation.FileUnitOfWork;
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

    FileUnitOfWork fileUnitOfWork = new FileUnitOfWork("data/test/");

    // Quick test:
    for (OwnedStock stock : fileUnitOfWork.getOwnedStocks())
    {
      System.out.println(stock);
    }
    for (Stock stock : fileUnitOfWork.getStocks())
    {
      System.out.println(stock);
    }
    for (Portfolio portfolio : fileUnitOfWork.getPortfolios())
    {
      System.out.println(portfolio);
    }
    for (StockPriceHistory history : fileUnitOfWork.getStockPriceHistoryList())
    {
      System.out.println(history);
    }
    for (Transaction transactions : fileUnitOfWork.getTransactions())
    {
      System.out.println(transactions);
    }
  }
}