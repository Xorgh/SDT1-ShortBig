package business.services;

import business.events.BuyStockRequest;
import business.events.SellStockRequest;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import dtos.PortfolioSummaryDTO;
import entities.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.List;
import java.util.UUID;

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

  // Services created internally — GameService owns them
  private final StockListenerService stockListenerService;
  private final StockBankruptService stockBankruptService;
  private final StockAlertService stockAlertService;
  private final StockResetService stockResetService;
  private final BuyStockService buyStockService;
  private final SellStockService sellStockService;
  private final PortfolioQueryService portfolioQueryService;
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

    // Create services
    this.stockListenerService = new StockListenerService(uow, stockDAO, historyDAO);
    this.stockBankruptService = new StockBankruptService(uow, ownedStockDAO);
    this.stockAlertService = new StockAlertService();
    this.stockResetService = new StockResetService();
    this.buyStockService = new BuyStockService(uow, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO);
    this.sellStockService = new SellStockService(uow, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO);
    this.portfolioQueryService = new PortfolioQueryService(portfolioDAO, ownedStockDAO, transactionDAO);

    // Register observers
    registerObservers();
  }

  private void registerObservers()
  {
    stockMarket.onStockPriceChange.add(stockListenerService::handlePriceChange);
    stockMarket.onStockStateChange.add(stockListenerService::handleStateChange);
    stockMarket.onStockBankruptcy.add(stockBankruptService::handleBankruptcy);
    stockMarket.onStockBankruptcy.add(stockAlertService::handleBankruptcyAlert);
    stockMarket.onStockReset.add(stockResetService::handleStockReset);
    stockMarket.onStockReset.add(stockAlertService::handleStockResetAlert);
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

  // ... startNewGame(), loadGame(), resetGame()
  public void startNewGame()
  {
    uow.begin();
    try
    {
      // Create default stocks
      stockDAO.create(new Stock("AAPL", "Apple", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("GOOG", "Google", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("MSFT", "Microsoft", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("NVDA", "Nvidia", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("AMZN", "Amazon", config.getStockResetValue(), StockState.STEADY));
      stockDAO.create(new Stock("MU", "Micron Technology", config.getStockResetValue(), StockState.STEADY));

      // Create portfolio with starting balance
      portfolioDAO.create(new Portfolio(config.getStartingBalance()));

      uow.commit();

      // Load into StockMarket
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
    uow.begin();
    try
    {
      List<Stock> stocks = stockDAO.getAll();
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

      uow.rollback(); // read-only, nothing to commit
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
    uow.begin();
    try
    {
      // Clear all persisted data
      for (OwnedStock os : ownedStockDAO.getAll())
        ownedStockDAO.delete(os.getId());
      for (Transaction t : transactionDAO.getAll())
        transactionDAO.delete(t.getId());
      for (StockPriceHistory h : historyDAO.getAll())
        historyDAO.delete(h.getId());
      for (Stock s : stockDAO.getAll())
        stockDAO.delete(s.getSymbol());
      for (Portfolio p : portfolioDAO.getAll())
        portfolioDAO.delete(p.getId());

      uow.commit();
      logger.log(LogLevel.INFO, "Game reset");
      startNewGame();
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Failed to reset game: " + e.getMessage());
    }
  }

  public void testRealTimeMarket(int secondsToRun)
  {
    logger.log(LogLevel.INFO, "=== Testing Real-Time Market Ticker ===");
    int msTorun = secondsToRun * 1000;

    try
    {
      Thread.sleep(msTorun);
    }
    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
    }

    // Summary - read from persistence, not from StockMarket internals
    logger.log(LogLevel.INFO, "\n=== Final Stock States ===");
    uow.commit(); // make sure everything is flushed
    for (Stock stock : stockDAO.getAll())
    {
      logger.log(LogLevel.INFO,
          String.format("%s: $%.2f (%s)", stock.getSymbol(), stock.getCurrentPrice(), stock.getCurrentState()));
    }

    logger.log(LogLevel.INFO, "Test completed");
  }

  // Expose trading services to presentation layer
  public void buyStock(BuyStockRequest request)
  {
    buyStockService.handleBuyStockRequest(request);
  }

  public void sellStock(SellStockRequest request)
  {
    sellStockService.handleSellStockRequest(request);
  }

  public PortfolioSummaryDTO getPortfolioSummary(UUID portfolioId)
  {
    return portfolioQueryService.getPortfolioSummary(portfolioId);
  }
}