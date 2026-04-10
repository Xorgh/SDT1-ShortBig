package business.services;

import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import entities.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.function.Consumer;

public class GameService
{
  private final UnitOfWork uow;
  private final StockDAO stockDAO;
  private final PortfolioDAO portfolioDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final StockPriceHistoryDAO historyDAO;
  private final StockMarket stockMarket = StockMarket.INSTANCE;
  private final Logger logger = Logger.getInstance();
  private final AppConfig config = AppConfig.INSTANCE;

  private MarketTickerThread ticker;

  public GameService(UnitOfWork uow, StockDAO stockDAO, PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO,
      TransactionDAO transactionDAO, StockPriceHistoryDAO historyDAO)
  {
    this.uow = uow;
    this.stockDAO = stockDAO;
    this.portfolioDAO = portfolioDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
    this.historyDAO = historyDAO;
  }

  public void startTicker()
  {
    ticker = new MarketTickerThread();
    ticker.start();
  }

  public void stopTicker()
  {
    if (ticker != null)
      ticker.stopTicker();
  }

  public void startNewGame()
  {
    uow.begin();
    try
    {
      // Don't overwrite an existing game
      if (!stockDAO.getAll().isEmpty())
      {
        logger.log(LogLevel.INFO, "Game already exists — use loadGame() or resetGame()");
        uow.rollback();
        return;
      }

      stockDAO.create(new Stock("AAPL", "Apple", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("GOOG", "Google", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("MSFT", "Microsoft", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("NVDA", "Nvidia", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("AMZN", "Amazon", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("MU", "Micron Technology", config.getStockResetValue(), StockState.STEADY));

      portfolioDAO.create(new Portfolio(config.getStartingBalance()));
      uow.commit();

      for (Stock stock : stockDAO.getAll())
      {
        stockMarket.addExistingLiveStock(stock);
      }

      logger.log(LogLevel.INFO, "New game started");
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Failed to start new game: " + e.getMessage());
    }
  }

  public void loadGame()
  {
    stockMarket.clearLiveStocks();
    uow.begin();
    try
    {
      java.util.List<Stock> stocks = stockDAO.getAll();
      if (stocks.isEmpty())
      {
        logger.log(LogLevel.INFO, "No saved game found, starting new game");
        uow.rollback();
        startNewGame();
        return;
      }

      for (Stock stock : stocks)
      {
        stockMarket.addExistingLiveStock(stock);
      }

      uow.rollback();
      logger.log(LogLevel.INFO, "Game loaded — " + stocks.size() + " stocks restored");
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Failed to load game: " + e.getMessage());
    }
  }

  public void resetGame()
  {
    stockMarket.clearLiveStocks();
    uow.begin();
    try
    {
      ownedStockDAO.getAll().clear();
      transactionDAO.getAll().clear();
      historyDAO.getAll().clear();
      stockDAO.getAll().clear();
      portfolioDAO.getAll().clear();

      uow.commit();
      logger.log(LogLevel.INFO, "Game reset — all data wiped");
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Failed to reset game: " + e.getMessage());
    }
  }

  public void loadTestGame()
  {
    resetGame();
    stockMarket.clearLiveStocks();
    uow.begin();
    try
    {

      stockDAO.create(new Stock("AAPL", "Apple", 10, StockState.STEADY));
      stockDAO.create(new Stock("GOOG", "Google", 0, StockState.STEADY));
      stockDAO.create(new Stock("MSFT", "Microsoft", 10, StockState.GROWING));
      stockDAO.create(new Stock("NVDA", "Nvidia", 10, StockState.DECLINING));
      stockDAO.create(new Stock("AMZN", "Amazon", 10, StockState.RESET));
      stockDAO.create(new Stock("MU", "Micron Technology", 10, StockState.BANKRUPT));

      portfolioDAO.create(new Portfolio(config.getStartingBalance()));
      uow.commit();

      for (Stock stock : stockDAO.getAll())
      {
        stockMarket.addExistingLiveStock(stock);
      }

      logger.log(LogLevel.INFO, "New game started");
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Failed to start new game: " + e.getMessage());
    }
  }
}