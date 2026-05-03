import business.services.*;
import entities.*;
import persistence.fileimplementation.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.FileLogOutputAdapter;
import shared.logging.Logger;

public class RunApp
{
  public static void main(String[] args)
  {
    // Switch logger to file output
    Logger.getInstance().setOutput(new FileLogOutputAdapter());

    FileUnitOfWork uow = new FileUnitOfWork(AppConfig.INSTANCE.getTestDataDirectory());

    // Initialize DAOs
    StockDAO stockDAO = new StockFileDAO(uow);
    PortfolioDAO portfolioDAO = new PortfolioFileDAO(uow);
    OwnedStockDAO ownedStockDAO = new OwnedStockFileDAO(uow);
    TransactionDAO transactionDAO = new TransactionFileDAO(uow);
    StockPriceHistoryDAO historyDAO = new StockPriceHistoryFileDAO(uow);

    GameService gameService = new GameService(uow, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO, historyDAO);

  }
}