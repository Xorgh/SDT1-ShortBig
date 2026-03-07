import business.services.StockPriceListenerService;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import entities.*;
import persistence.fileimplementation.*;
import persistence.interfaces.*;
import shared.logging.LogLevel;
import shared.logging.Logger;
import business.stockmarket.simulation.LiveStock;

import java.time.LocalDateTime;
import java.util.UUID;

public class RunApp
{
  public static void main(String[] args)
  {
    Logger logger = Logger.getInstance();

    // Test observer pattern

    FileUnitOfWork uow = new FileUnitOfWork("data/test/");

    // Initialize DAOs
    StockDAO stockDAO = new StockFileDAO(uow);
    PortfolioDAO portfolioDAO = new PortfolioFileDAO(uow);
    OwnedStockDAO ownedStockDAO = new OwnedStockFileDAO(uow);
    TransactionDAO transactionDAO = new TransactionFileDAO(uow);
    StockPriceHistoryDAO historyDAO = new StockPriceHistoryFileDAO(uow);

    // Create test stocks
    stockDAO.create(new Stock("AAPL", "Apple", 100, StockState.STEADY));
    stockDAO.create(new Stock("GOOG", "Google", 100, StockState.STEADY));
    stockDAO.create(new Stock("MSFT", "Microsoft", 100, StockState.STEADY));

    // Commit to File
    uow.commit();

    // Create StockPriceListenerService and inject DAOs
    StockPriceListenerService stockPriceListener = new StockPriceListenerService(stockDAO, historyDAO);

    // Create StockMarket
    StockMarket stockMarket = StockMarket.INSTANCE;

    // Register listeners
    stockMarket.onStockPriceChange.add(stockPriceListener::handlePriceChange);
    stockMarket.onStockStateChange.add(stockPriceListener::handleStateChange);

    // Test Real-Time Threaded Market Ticker
    testRealTimeMarket(uow, logger, stockMarket, 10);

    //    // Test LiveStock State Machine
    //    testStateMachine(logger, 100);
    //
    //    // Existing DAO tests
    //    testDAOOperations(logger);
  }


  private static void testRealTimeMarket(UnitOfWork uow, Logger logger, StockMarket stockMarket, int secondsToRun)
  {
    logger.log(LogLevel.INFO, "=== Testing Real-Time Market Ticker ===");
    int msTorun = secondsToRun * 1000;

    // Add stocks
    stockMarket.addNewLiveStock("AAPL");
    stockMarket.addNewLiveStock("GOOG");
    stockMarket.addNewLiveStock("MSFT");

    // Create and start ticker thread
    MarketTickerThread ticker = new MarketTickerThread(uow);
    ticker.start();

    logger.log(LogLevel.INFO, "Market ticker running. Press Ctrl+C to stop.\n");

    // Let it run for 30 seconds for testing
    try
    {
      Thread.sleep(msTorun);
    }
    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
    }

    // Summary
    logger.log(LogLevel.INFO, "\n=== Final Stock States ===");
    for (LiveStock stock : stockMarket.getAllLiveStocks())
    {
      logger.log(LogLevel.INFO,
          String.format("%s: $%.2f (%s, %d ticks)", stock.getSymbol(), stock.getCurrentPrice(), stock.getStateName(),
              stock.getConsecutiveTicksInState()));
    }

    // Stop the ticker
    ticker.stopTicker();

    logger.log(LogLevel.INFO, "Test completed");
  }

  private static void testStateMachine(Logger logger, int numberOfTestTicks)
  {
    logger.log(LogLevel.INFO, "=== Testing LiveStock State Machine ===");

    // Create test stocks
    LiveStock[] stocks = {new LiveStock("AAPL"), new LiveStock("GOOG"), new LiveStock("MSFT")};

    // Run simulation
    logger.log(LogLevel.INFO, "Running 50-tick simulation with 3 stocks...\n");

    for (int tick = 1; tick <= numberOfTestTicks; tick++)
    {
      logger.log(LogLevel.DEBUG, String.format("--- Tick %d ---", tick));

      for (LiveStock stock : stocks)
      {
        stock.updatePrice();
      }

      // Add small delay to make output readable (optional)
      try
      {
        Thread.sleep(50);
      }
      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }
    }

    // Summary
    logger.log(LogLevel.INFO, "\n=== Final Stock States ===");
    for (LiveStock stock : stocks)
    {
      logger.log(LogLevel.INFO,
          String.format("%s: $%.2f (%s, %d ticks)", stock.getSymbol(), stock.getCurrentPrice(), stock.getStateName(),
              stock.getConsecutiveTicksInState()));
    }

    logger.log(LogLevel.INFO, "\n");
  }

  private static void testDAOOperations(Logger logger)
  {
    FileUnitOfWork uow = new FileUnitOfWork("data/test/");

    // Initialize DAOs
    StockDAO stockDAO = new StockFileDAO(uow);
    PortfolioDAO portfolioDAO = new PortfolioFileDAO(uow);
    OwnedStockDAO ownedStockDAO = new OwnedStockFileDAO(uow);
    TransactionDAO transactionDAO = new TransactionFileDAO(uow);
    StockPriceHistoryDAO historyDAO = new StockPriceHistoryFileDAO(uow);

    // Begin transaction
    uow.begin();

    // Test DAO create operations
    stockDAO.create(new Stock("DAO1", "DAO Test Stock", 50.0, StockState.STEADY));

    UUID portfolioId = UUID.randomUUID();
    portfolioDAO.create(new Portfolio(portfolioId, 10000.0));

    UUID ownedStockId = UUID.randomUUID();
    ownedStockDAO.create(new OwnedStock(ownedStockId, portfolioId, "DAO1", 20));

    UUID transactionId = UUID.randomUUID();
    transactionDAO.create(
        new Transaction(transactionId, portfolioId, "DAO1", TransactionType.BUY, 20, 50.0, 1000.0, 0.10,
            LocalDateTime.now()));

    UUID historyId = UUID.randomUUID();
    historyDAO.create(new StockPriceHistory(historyId, "DAO1", 50.0, LocalDateTime.now()));

    // Test DAO read operations
    logger.log(LogLevel.INFO, "--- Testing DAO reads before commit ---");
    System.out.println("Stock by symbol: " + stockDAO.getBySymbol("DAO1"));
    System.out.println("Portfolio by id: " + portfolioDAO.getById(portfolioId));
    System.out.println("OwnedStock by id: " + ownedStockDAO.getById(ownedStockId));
    System.out.println("Transaction by id: " + transactionDAO.getById(transactionId));
    System.out.println("History by id: " + historyDAO.getById(historyId));

    // Commit to files
    uow.commit();
    logger.log(LogLevel.INFO, "Data committed to files");

    // Reload and verify persistence
    FileUnitOfWork verifyUow = new FileUnitOfWork("data/test/");
    StockDAO verifyStockDAO = new StockFileDAO(verifyUow);
    PortfolioDAO verifyPortfolioDAO = new PortfolioFileDAO(verifyUow);
    OwnedStockDAO verifyOwnedStockDAO = new OwnedStockFileDAO(verifyUow);
    TransactionDAO verifyTransactionDAO = new TransactionFileDAO(verifyUow);
    StockPriceHistoryDAO verifyHistoryDAO = new StockPriceHistoryFileDAO(verifyUow);

    logger.log(LogLevel.INFO, "--- Verifying persisted data via DAOs ---");
    System.out.println("All Stocks: " + verifyStockDAO.getAll());
    System.out.println("All Portfolios: " + verifyPortfolioDAO.getAll());
    System.out.println("All OwnedStocks: " + verifyOwnedStockDAO.getAll());
    System.out.println("All Transactions: " + verifyTransactionDAO.getAll());
    System.out.println("All History: " + verifyHistoryDAO.getAll());
  }

  //  TODO Assignment 3 feedback: Generelt kan du godt udelade private metoder fra klasse diagrammet. Det er ofte bare støj, og interne impl detaljer. Det kan være du kigger på koden igen og refakturerer noget fælles kode ud i en private hjælpe metode. Du har ikke ændret klassens "public interface", i.e. public metoder, men klassediagrammet skal alligevel opdateres.
  //  TODO Assignment 3 feedback: I sjældne tilfælde kan private metoder være relevant, men jeg har ikke lige et godt eksempel.
  //  TODO Assignment 3 feedback: Du må gerne beholde dem. Du kan også spare lidt tid fremover.

}