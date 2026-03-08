package business.services;

import business.events.StockBankruptcyEvent;
import business.events.StockResetEvent;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;

public class StockAlertService
{
  private Logger logger = Logger.getInstance();
  // TODO Implement
  // TODO update class diagram

  public StockAlertService()
  {
  }

  public void handleBankruptcyAlert(StockBankruptcyEvent event)
  {
    // TODO implement
  }

  public void handleStockResetAlert(StockResetEvent event)
  {
    // TODO implement
  }

}
