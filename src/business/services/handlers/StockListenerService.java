package business.services.handlers;

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

    try
    {
      Stock stock = stockDAO.getBySymbol(event.stockSymbol());

      if (stock == null)
      {
        throw new IllegalArgumentException("No such stock found. StockSymbol: " + event.stockSymbol());
      }

      stock.setCurrentState(event.newState());
      stockDAO.update(stock);

      uow.commit();
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "State change failed: " + e.getMessage());
    }
  }

  public void handlePriceChange(StockPriceUpdateEvent event)
  {
    uow.begin();

    try
    {
      Stock stock = stockDAO.getBySymbol(event.stockSymbol());

      if (stock == null)
      {
        throw new IllegalArgumentException("No such stock found. StockSymbol: " + event.stockSymbol());
      }

      double newPrice = event.newPrice();
      stock.setCurrentPrice(newPrice);

      stockDAO.update(stock);
      savePriceHistory(event);

      uow.commit();
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Price change failed: " + e.getMessage());
    }
  }

  private void savePriceHistory(StockPriceUpdateEvent event)
  {
    StockPriceHistory priceHistory = new StockPriceHistory(event.stockSymbol(), event.newPrice());

    priceHistoryDAO.create(priceHistory);
  }
}
