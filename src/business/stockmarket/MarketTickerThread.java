package business.stockmarket;

import persistence.interfaces.UnitOfWork;
import shared.configuration.AppConfig;
import shared.logging.LogLevel;
import shared.logging.Logger;

public class MarketTickerThread extends Thread
{
  private final StockMarket stockMarket;
  private final Logger logger;
  private final int updateFrequency;
  private volatile boolean running;
  private int currentTick;
  private UnitOfWork uow;

  public MarketTickerThread(UnitOfWork uow)
  {
    this.uow = uow;
    this.stockMarket = StockMarket.INSTANCE;
    this.logger = Logger.getInstance();
    this.updateFrequency = AppConfig.INSTANCE.getUpdateFrequencyInMs();
    this.running = false;
    this.currentTick = 0;

    setDaemon(true);
    setName("MarketTicker");
  }

  @Override
  public void run()
  {
    running = true;
    currentTick++;
    logger.log(LogLevel.INFO, "Market ticker thread started (update frequency: " + updateFrequency + "ms)");

    while (running)
    {
      try
      {
        // Update all stocks in the market
        uow.begin();
        logger.log(LogLevel.DEBUG, String.format("--- Tick %d ---", currentTick));
        stockMarket.updateAllLiveStocks();
        currentTick++;
        uow.commit();

        // Sleep for the configured update frequency
        Thread.sleep(updateFrequency);
      }
      catch (InterruptedException e)
      {
        logger.log(LogLevel.WARNING, "Market ticker thread interrupted");
        uow.rollback();
        Thread.currentThread().interrupt();
        break;
      }
      catch (Exception e)
      {
        logger.log(LogLevel.ERROR, "Error in market ticker: " + e.getMessage());
      }
    }

    logger.log(LogLevel.INFO, "Market ticker thread stopped");
  }

  public void stopTicker()
  {
    running = false;
    interrupt();
  }

  public boolean isRunning()
  {
    return running;
  }
}
