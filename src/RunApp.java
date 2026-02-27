import entities.*;
import persistence.fileimplementation.FileUnitOfWork;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.time.LocalDateTime;
import java.util.UUID;

public class RunApp
{
  public static void main(String[] args)
  {
    Logger logger = Logger.getInstance();
    FileUnitOfWork fileUnitOfWork = new FileUnitOfWork("data/test/");

    // Test: Modify data and commit
    fileUnitOfWork.begin();

    // Add a new stock
    fileUnitOfWork.getStocks().add(new Stock("TEST", "Test Company", 99.99, StockState.STABLE));

    // Add a new portfolio
    UUID portfolioId = UUID.randomUUID();
    fileUnitOfWork.getPortfolios().add(new Portfolio(portfolioId, 5000.0));

    // Add an owned stock
    fileUnitOfWork.getOwnedStocks().add(new OwnedStock(UUID.randomUUID(), portfolioId, "TEST", 10));

    // Add a transaction
    fileUnitOfWork.getTransactions().add(new Transaction(
        UUID.randomUUID(),
        portfolioId,
        "TEST",
        TransactionType.BUY,
        10,
        99.99,
        999.95,
        0.05,
        LocalDateTime.now()
    ));

    // Add stock price history
    fileUnitOfWork.getStockPriceHistoryList().add(new StockPriceHistory(
        UUID.randomUUID(),
        "TEST",
        99.99,
        LocalDateTime.now()
    ));

    // Commit changes to files
    fileUnitOfWork.commit();
    logger.log(LogLevel.INFO, "Data saved successfully");

    // Reload and verify
    FileUnitOfWork verifyUow = new FileUnitOfWork("data/test/");
    logger.log(LogLevel.INFO, "--- Verifying saved data ---");

    for (Stock stock : verifyUow.getStocks())
    {
      System.out.println("Stock: " + stock);
    }
    for (Portfolio portfolio : verifyUow.getPortfolios())
    {
      System.out.println("Portfolio: " + portfolio);
    }
    for (OwnedStock ownedStock : verifyUow.getOwnedStocks())
    {
      System.out.println("OwnedStock: " + ownedStock);
    }
    for (Transaction transaction : verifyUow.getTransactions())
    {
      System.out.println("Transaction: " + transaction);
    }
    for (StockPriceHistory history : verifyUow.getStockPriceHistoryList())
    {
      System.out.println("StockPriceHistory: " + history);
    }
  }
}