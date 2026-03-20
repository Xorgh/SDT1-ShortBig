package business.services;

import business.events.BuyStockRequest;
import entities.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.LogLevel;
import shared.logging.Logger;

public class BuyStockService
{
  private final UnitOfWork uow;
  private final Logger logger = Logger.getInstance();
  private final StockDAO stockDAO;
  private final PortfolioDAO portfolioDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final double transactionFee;

  public BuyStockService(UnitOfWork uow, StockDAO stockDAO, PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO,
      TransactionDAO transactionDAO)
  {
    this.uow = uow;
    this.stockDAO = stockDAO;
    this.portfolioDAO = portfolioDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
    this.transactionFee = AppConfig.INSTANCE.getTransactionFee();
  }

  public BuyStockService(UnitOfWork uow, StockDAO stockDAO, PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO,
      TransactionDAO transactionDAO, double transactionFee)
  {
    this.uow = uow;
    this.stockDAO = stockDAO;
    this.portfolioDAO = portfolioDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
    this.transactionFee = transactionFee;
  }

  public void handleBuyStockRequest(BuyStockRequest request)
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
      double totalCost = (stock.getCurrentPrice() * numberOfShares) + transactionFee;
      double currentBalance = portfolio.getCurrentBalance();
      StockState currentStockState = stock.getCurrentState();

      // Business rules
      if (numberOfShares <= 0)
        throw new IllegalArgumentException("Number of shares must be positive");

      if (currentStockState == StockState.BANKRUPT)
      {
        throw new IllegalArgumentException("Stock is bankrupt: " + request.stockSymbol());
      }

      if (currentBalance < totalCost)
      {
        throw new IllegalArgumentException("Insufficient balance. Required: " + totalCost);
      }

      // Apply balance change to portfolio object
      portfolio.setCurrentBalance(portfolio.getCurrentBalance() - totalCost);
      portfolioDAO.update(portfolio);

      // Check if buyer already owns this stock in this portfolio
      OwnedStock existing = ownedStockDAO.getAllByStockSymbol(request.stockSymbol()).stream()
          .filter(ownedStock -> ownedStock.getPortfolioId().equals(request.portfolioId())).findFirst().orElse(null);

      if (existing == null)
      {
        ownedStockDAO.create(new OwnedStock(request.portfolioId(), request.stockSymbol(), numberOfShares));
      }
      else
      {
        existing.setNumberOfShares(existing.getNumberOfShares() + numberOfShares);
        ownedStockDAO.update(existing);
      }

      // Create a transaction
      transactionDAO.create(
          new Transaction(request.portfolioId(), request.stockSymbol(), TransactionType.BUY, numberOfShares,
              stock.getCurrentPrice()));

      uow.commit();
      logger.log(LogLevel.INFO, "Bought " + numberOfShares + " of " + request.stockSymbol());

    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Buy stock failed: " + e.getMessage());
      throw e;
    }
  }
}
