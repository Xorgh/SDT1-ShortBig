package business.stockmarket;

import business.events.StockPriceUpdateEvent;
import business.events.StockStateUpdateEvent;
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
import java.util.function.Consumer;

public enum StockMarket
{
  INSTANCE;

  private final Logger logger = Logger.getInstance();

  // Observer Pattern implementation - Lambdas only
  public final List<Consumer<StockPriceUpdateEvent>> onStockPriceChange = new ArrayList<>();
  public final List<Consumer<StockStateUpdateEvent>> onStockStateChange = new ArrayList<>();

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
      String stockSymbol = liveStock.getSymbol();
      
      LiveStockState oldLiveStockState = liveStock.getCurrentState();
      Double oldPrice = liveStock.getCurrentPrice();
      
      liveStock.updatePrice();
      
      Double newPrice = liveStock.getCurrentPrice();
      LiveStockState newState = liveStock.getCurrentState();
      
      // create new price event(DTO)
      StockPriceUpdateEvent newPriceUpdateEvent =
          new StockPriceUpdateEvent(stockSymbol, oldPrice, newPrice);
      

      // Compare old and new state, if changed notify listeners.
      if(!oldLiveStockState.equals(newState))
      {
        StockStateUpdateEvent newStateUpdateEvent =
            new StockStateUpdateEvent(stockSymbol, oldLiveStockState, newState);
        onStockStateChange.forEach((listener -> listener.accept(newStateUpdateEvent)));
      }

      // Notify price listeners
      onStockPriceChange.forEach(listener -> listener.accept(newPriceUpdateEvent));

    }
  }

  public List<LiveStock> getAllLiveStocks()
  {
    return Collections.unmodifiableList(liveStocks);
  }
}
