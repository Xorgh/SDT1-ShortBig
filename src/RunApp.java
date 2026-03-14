import business.services.*;
import entities.*;
import persistence.fileimplementation.*;
import persistence.interfaces.*;

public class RunApp
{
  public static void main(String[] args)
  {

    FileUnitOfWork uow = new FileUnitOfWork("data/test/");

    // Initialize DAOs
    StockDAO stockDAO = new StockFileDAO(uow);
    PortfolioDAO portfolioDAO = new PortfolioFileDAO(uow);
    OwnedStockDAO ownedStockDAO = new OwnedStockFileDAO(uow);
    TransactionDAO transactionDAO = new TransactionFileDAO(uow);
    StockPriceHistoryDAO historyDAO = new StockPriceHistoryFileDAO(uow);

    GameService gameService = new GameService(uow, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO, historyDAO);

//    Transaction test
    gameService.startNewGame();
    gameService.testTransactions();

//    // Basic real time market test
//    gameService.startNewGame();
//    gameService.startTicker();
//    gameService.testRealTimeMarket(10);
//    gameService.stopTicker();
  }
}