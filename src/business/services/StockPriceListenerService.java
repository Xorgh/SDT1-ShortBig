package business.services;

import business.events.StockPriceUpdateEvent;
import business.events.StockStateUpdateEvent;
import business.utility.StockStateMapper;
import entities.Stock;
import entities.StockPriceHistory;
import persistence.interfaces.StockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StockPriceListenerService
{
  private StockDAO stockDAO;
  private StockPriceHistoryDAO priceHistoryDAO;
  private Logger logger = Logger.getInstance();

  public StockPriceListenerService(StockDAO stockDAO, StockPriceHistoryDAO priceHistoryDAO)
  {
    this.stockDAO = stockDAO;
    this.priceHistoryDAO = priceHistoryDAO;
  }

  public void handleStateChange(StockStateUpdateEvent event)
  {
    Stock stock = stockDAO.getBySymbol(event.stockSymbol());

    if(stock == null)
    {
      logger.log(LogLevel.ERROR, "No such stock found. StockSymbol: " + event.stockSymbol());
      return;
    }

    stock.setCurrentState(StockStateMapper.toStockState(event.newLiveStockState()));
    stockDAO.update(stock);
  }

  public void handlePriceChange(StockPriceUpdateEvent event)
  {
    Stock stock = stockDAO.getBySymbol(event.stockSymbol());

    if(stock == null)
    {
      logger.log(LogLevel.ERROR, "No such stock found. StockSymbol: " + event.stockSymbol());
      return;
    }

    stock.setCurrentPrice(event.newPrice());

    stockDAO.update(stock);
    logStockPriceHistory(event);
  }

  private void logStockPriceHistory(StockPriceUpdateEvent event)
  {
    StockPriceHistory priceHistory = new StockPriceHistory(event.stockSymbol(), event.newPrice());

    priceHistoryDAO.create(priceHistory);
  }
}
