package business.services;

import business.events.StockResetEvent;
import shared.logging.Logger;

public class StockResetService
{
  private Logger logger = Logger.getInstance();

  public StockResetService()
  {
  }

  public void handleStockReset(StockResetEvent event)
  {
    // TODO implement
  }
}
