package business.stockmarket;

import business.stockmarket.simulation.LiveStock;
import business.stockmarket.simulation.LiveStockState;
import business.utility.StockStateMapper;
import entities.Stock;
import entities.StockState;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum StockMarket
{
  INSTANCE;

  private final Logger logger = Logger.getInstance();

  private List<LiveStock> liveStocks;


  StockMarket()
  {
    liveStocks = new ArrayList<>();
  }


  public void addNewLiveStock(String stockSymbol)
  {
    String upperStockSymbol = stockSymbol.toUpperCase();
    liveStocks.add(new LiveStock(upperStockSymbol));
    logger.log(LogLevel.INFO, "Added new stock: [" + upperStockSymbol + "] to StockMarket\n");
  }

  public void addExistingLiveStock(Stock stock)
  {
    String symbol = stock.getSymbol().toUpperCase();
    StockState stockState = stock.getCurrentState();
    LiveStockState liveStockState = StockStateMapper.toLiveStockState(stockState);
    double currentPrice = stock.getCurrentPrice();

    liveStocks.add(new LiveStock(symbol, currentPrice, liveStockState));
    logger.log(LogLevel.INFO, "Added existing stock: [" + symbol + "] to StockMarket\n");
  }

  public void updateAllLiveStocks()
  {
    for (LiveStock liveStock : liveStocks)
    {
      liveStock.updatePrice();
    }
  }

  public List<LiveStock> getAllLiveStocks()
  {
    return Collections.unmodifiableList(liveStocks);
  }
}
