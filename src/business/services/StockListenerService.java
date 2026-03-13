package business.services;

import business.events.StockPriceUpdateEvent;
import business.events.StockStateUpdateEvent;
import entities.Stock;
import entities.StockPriceHistory;
import persistence.interfaces.StockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import persistence.interfaces.UnitOfWork;
import shared.logging.LogLevel;
import shared.logging.Logger;

public class StockListenerService
{
  private final UnitOfWork uow;
  private final Logger logger = Logger.getInstance();
  private final StockDAO stockDAO;
  private final StockPriceHistoryDAO priceHistoryDAO;


  public StockListenerService(UnitOfWork uow, StockDAO stockDAO, StockPriceHistoryDAO priceHistoryDAO)
  {
    this.uow = uow;
    this.stockDAO = stockDAO;
    this.priceHistoryDAO = priceHistoryDAO;
  }

  public void handleStateChange(StockStateUpdateEvent event)
  {
    uow.begin();

    Stock stock = stockDAO.getBySymbol(event.stockSymbol());

    if(stock == null)
    {
      logger.log(LogLevel.ERROR, "No such stock found. StockSymbol: " + event.stockSymbol());
      uow.rollback();
      return;
    }

    stock.setCurrentState(event.newState());
    stockDAO.update(stock);

    uow.commit();
  }

  public void handlePriceChange(StockPriceUpdateEvent event)
  {
    uow.begin();
    Stock stock = stockDAO.getBySymbol(event.stockSymbol());
    double newPrice = event.newPrice();

    if(stock == null)
    {
      logger.log(LogLevel.ERROR, "No such stock found. StockSymbol: " + event.stockSymbol());
      uow.rollback();
      return;
    }

    stock.setCurrentPrice(newPrice);

    stockDAO.update(stock);
    logStockPriceHistory(event);

    uow.commit();
  }

  private void logStockPriceHistory(StockPriceUpdateEvent event)
  {
    StockPriceHistory priceHistory = new StockPriceHistory(event.stockSymbol(), event.newPrice());

    priceHistoryDAO.create(priceHistory);
  }
}
