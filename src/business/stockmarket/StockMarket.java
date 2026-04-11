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
  public final List<Consumer<StockBankruptcyEvent>> onStockBankruptcy = new ArrayList<>();
  public final List<Consumer<StockResetEvent>> onStockReset = new ArrayList<>();

  private final List<LiveStock> liveStocks;

  StockMarket()
  {
    liveStocks = new ArrayList<>();
  }

  public void clearLiveStocks()
  {
    liveStocks.clear();
    logger.log(LogLevel.INFO, "All live stocks cleared from StockMarket");
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
      StockState oldState = liveStock.getStockState();
      double oldPrice = liveStock.getCurrentPrice();

      liveStock.updatePrice();

      String stockSymbol = liveStock.getSymbol();
      double newPrice = liveStock.getCurrentPrice();
      StockState newState = liveStock.getStockState();

      tryFireStateChangeEvents(stockSymbol, oldState, newState);
      firePriceUpdateEvent(stockSymbol, oldPrice, newPrice);
    }
  }

  private void tryFireStateChangeEvents(String stockSymbol, StockState oldState, StockState newState)
  {
    if (oldState.equals(newState))
    {
      return;
    }

    tryFireBankruptcyEvent(stockSymbol, newState);
    tryFireResetEvent(stockSymbol, newState);

    StockStateUpdateEvent event = new StockStateUpdateEvent(stockSymbol, oldState, newState);
    onStockStateChange.forEach(listener -> listener.accept(event));
  }

  private void tryFireBankruptcyEvent(String stockSymbol, StockState newState)
  {
    if (newState == StockState.BANKRUPT)
    {
      StockBankruptcyEvent event = new StockBankruptcyEvent(stockSymbol);
      onStockBankruptcy.forEach(listener -> listener.accept(event));
    }
  }

  private void tryFireResetEvent(String stockSymbol, StockState newState)
  {
    if (newState == StockState.RESET)
    {
      StockResetEvent event = new StockResetEvent(stockSymbol);
      onStockReset.forEach(listener -> listener.accept(event));
    }
  }

  private void firePriceUpdateEvent(String stockSymbol, double oldPrice, double newPrice)
  {
    StockPriceUpdateEvent event = new StockPriceUpdateEvent(stockSymbol, oldPrice, newPrice);
    onStockPriceChange.forEach(listener -> listener.accept(event));
  }
}
