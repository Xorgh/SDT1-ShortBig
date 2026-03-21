package business.services;

import business.events.SellStockRequest;
import entities.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.LogLevel;
import shared.logging.Logger;

public class SellStockService
{
  private final UnitOfWork uow;
  private final Logger logger = Logger.getInstance();
  private final StockDAO stockDAO;
  private final PortfolioDAO portfolioDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final double transactionFee = AppConfig.INSTANCE.getTransactionFee();

  public SellStockService(UnitOfWork uow, StockDAO stockDAO, PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO,
      TransactionDAO transactionDAO)
  {
    this.uow = uow;
    this.stockDAO = stockDAO;
    this.portfolioDAO = portfolioDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
  }

  public void handleSellStockRequest(SellStockRequest request)
  {
    uow.begin();

    try
    {
      // Fetch and null checks
      Stock stock = stockDAO.getBySymbol(request.stockSymbol());
      if (stock == null)
      {
        throw new IllegalArgumentException("Stock not found: " + request.stockSymbol());
      }

      Portfolio portfolio = portfolioDAO.getById(request.portfolioId());
      if (portfolio == null)
      {
        throw new IllegalArgumentException("Portfolio not found: " + request.portfolioId());
      }

      int numberOfShares = request.numberOfShares();
      double totalCost = (stock.getCurrentPrice() * numberOfShares) - transactionFee;
      StockState currentStockState = stock.getCurrentState();

      // Business rules
      if (numberOfShares <= 0)
        throw new IllegalArgumentException("Number of shares must be positive");

      if (currentStockState == StockState.BANKRUPT)
      {
        throw new IllegalArgumentException("Stock is bankrupt: " + request.stockSymbol());
      }

      // Verify seller actually owns this stock in this portfolio
      OwnedStock existing = ownedStockDAO.getAllByStockSymbol(request.stockSymbol()).stream()
          .filter(ownedStock -> ownedStock.getPortfolioId().equals(request.portfolioId())).findFirst().orElse(null);

      if (existing == null)
      {
        throw new IllegalArgumentException("Stock not found in portfolio: " + request.stockSymbol());
      }

      // Verify seller isn't selling more than he owns.
      if (numberOfShares > existing.getNumberOfShares())
      {
        throw new IllegalArgumentException(
            "Selling more shares than owned. Owned: " + existing.getNumberOfShares() + " Selling: " + numberOfShares);
      }

      // Apply changes to ownedstock object, check if seller sold his entire holdings
      existing.setNumberOfShares(existing.getNumberOfShares() - numberOfShares);
      if (existing.getNumberOfShares() == 0) {
        ownedStockDAO.delete(existing.getId());
      } else {
        ownedStockDAO.update(existing);
      }

      // Apply balance change to portfolio object
      portfolio.setCurrentBalance(portfolio.getCurrentBalance() + totalCost);
      portfolioDAO.update(portfolio);

      // Create a transaction
      transactionDAO.create(
          new Transaction(request.portfolioId(), request.stockSymbol(), TransactionType.SELL, numberOfShares,
              stock.getCurrentPrice()));

      uow.commit();
      logger.log(LogLevel.INFO, "Sold " + numberOfShares + " of " + request.stockSymbol());

    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Sell stock failed: " + e.getMessage());
      throw e;
    }
  }
}
