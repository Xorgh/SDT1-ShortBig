package business.services;

import business.stockmarket.StockMarket;
import entities.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.List;

public class GameService {
  private final UnitOfWork uow;
  private final StockDAO stockDAO;
  private final PortfolioDAO portfolioDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final StockPriceHistoryDAO historyDAO;
  private final StockMarket stockMarket = StockMarket.INSTANCE;
  private final Logger logger = Logger.getInstance();
  private final AppConfig config = AppConfig.INSTANCE;

  public GameService(UnitOfWork uow, StockDAO stockDAO, PortfolioDAO portfolioDAO,
      OwnedStockDAO ownedStockDAO, TransactionDAO transactionDAO, StockPriceHistoryDAO historyDAO) {
    this.uow = uow;
    this.stockDAO = stockDAO;
    this.portfolioDAO = portfolioDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
    this.historyDAO = historyDAO;
  }

  public void startNewGame() {
    uow.begin();
    try {
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
      for (Stock stock : stockDAO.getAll()) {
        stockMarket.addExistingLiveStock(stock);
      }

      logger.log(LogLevel.INFO, "New game started");
    } catch (Exception e) {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Failed to start new game: " + e.getMessage());
    }
  }

  public void loadGame() {
    uow.begin();
    try {
      List<Stock> stocks = stockDAO.getAll();
      if (stocks.isEmpty()) {
        logger.log(LogLevel.INFO, "No saved game found, starting new game");
        uow.rollback();
        startNewGame();
        return;
      }

      for (Stock stock : stocks) {
        stockMarket.addExistingLiveStock(stock);
      }

      uow.rollback(); // read-only, nothing to commit
      logger.log(LogLevel.INFO, "Game loaded — " + stocks.size() + " stocks restored");
    } catch (Exception e) {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Failed to load game: " + e.getMessage());
    }
  }

  public void resetGame() {
    uow.begin();
    try {
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
    } catch (Exception e) {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Failed to reset game: " + e.getMessage());
    }
  }
}