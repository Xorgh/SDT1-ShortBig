package business.stockmarket;

import business.events.StockBankruptcyEvent;
import business.events.StockPriceUpdateEvent;
import business.events.StockResetEvent;
import business.events.StockStateUpdateEvent;
import business.stockmarket.simulation.LiveStock;
import entities.Stock;
import entities.StockState;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.ArrayList;

import java.util.List;
import java.util.function.Consumer;

public enum StockMarket
{
  INSTANCE;

  private final Logger logger = Logger.getInstance();

  // Observer Pattern implementation - Lambdas only
  public final List<Consumer<StockPriceUpdateEvent>> onStockPriceChange = new ArrayList<>();
  public final List<Consumer<StockStateUpdateEvent>> onStockStateChange = new ArrayList<>();
  // TODO update class diagram
  public final List<Consumer<StockBankruptcyEvent>> onStockBankruptcy = new ArrayList<>();
  public final List<Consumer<StockResetEvent>> onStockReset = new ArrayList<>();

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
    double currentPrice = stock.getCurrentPrice();

    liveStocks.add(new LiveStock(symbol, currentPrice, stockState));
    logger.log(LogLevel.INFO, "Added existing stock: [" + symbol + "] to StockMarket\n");
  }

  public void updateAllLiveStocks()
  {
    for (LiveStock liveStock : liveStocks)
    {
      String stockSymbol = liveStock.getSymbol();

      StockState oldStockState = liveStock.getStockState();
      double oldPrice = liveStock.getCurrentPrice();

      liveStock.updatePrice();

      double newPrice = liveStock.getCurrentPrice();
      StockState newState = liveStock.getStockState();

      // Check for StockReset and fire event
      if (newState == StockState.RESET)
      {
        StockResetEvent newResetEvent = new StockResetEvent(stockSymbol);
        onStockReset.forEach(listener -> listener.accept(newResetEvent));
      }

      // Check for bankruptcy, and fire event
      // TODO Should bankruptcy events be fired every time we hit an instanceof 'BankruptState' or only when it's changed to this state?
      //
      if (newState == StockState.BANKRUPT)
      {
        StockBankruptcyEvent newBankruptcyEvent = new StockBankruptcyEvent(stockSymbol);
        onStockBankruptcy.forEach(listener -> listener.accept(newBankruptcyEvent));
      }

      // create new price event(DTO)
      StockPriceUpdateEvent newPriceUpdateEvent = new StockPriceUpdateEvent(stockSymbol, oldPrice, newPrice);

      // Compare old and new state, if changed notify listeners.
      if (!oldStockState.equals(newState))
      {
        StockStateUpdateEvent newStateUpdateEvent = new StockStateUpdateEvent(stockSymbol, oldStockState, newState);
        onStockStateChange.forEach(listener -> listener.accept(newStateUpdateEvent));
      }

      // Notify price listeners
      onStockPriceChange.forEach(listener -> listener.accept(newPriceUpdateEvent));

    }
  }
}
