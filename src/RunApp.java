import entities.*;
import persistence.fileimplementation.*;
import persistence.interfaces.*;
import shared.logging.LogLevel;
import shared.logging.Logger;
import business.stockmarket.simulation.LiveStock;

import java.time.LocalDateTime;
import java.util.UUID;

public class RunApp
{public static void main(String[] args)
{
  Logger logger = Logger.getInstance();
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
  transactionDAO.create(new Transaction(
      transactionId, portfolioId, "DAO1", TransactionType.BUY,
      20, 50.0, 1000.0, 0.10, LocalDateTime.now()
  ));

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

  // Test TransitionManager and LiveStock state transitions
  logger.log(LogLevel.INFO, "--- Testing TransitionManager ---");
  LiveStock testStock = new LiveStock("TEST");

  System.out.println("\nSimulating 20 price updates to observe state transitions:");
  for (int i = 0; i < 20; i++)
  {
    String beforeState = testStock.getStateName();
    int ticksBefore = testStock.getConsecutiveTicksInState();
    double priceBefore = testStock.getCurrentPrice();

    testStock.updatePrice();

    String afterState = testStock.getStateName();
    int ticksAfter = testStock.getConsecutiveTicksInState();
    double priceAfter = testStock.getCurrentPrice();

    String transition = beforeState.equals(afterState) ? "" : " -> TRANSITIONED to " + afterState;
    System.out.printf("Tick %2d: %-15s | Ticks: %2d | Price: $%.2f -> $%.2f%s%n",
        i + 1, beforeState, ticksBefore, priceBefore, priceAfter, transition);
  }
}
}