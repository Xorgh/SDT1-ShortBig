package business.services;

import business.events.StockBankruptcyEvent;
import business.events.StockStateUpdateEvent;
import entities.OwnedStock;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.List;

public class StockBankruptService
{
  private OwnedStockDAO ownedStockDAO;
  private Logger logger = Logger.getInstance();

  public StockBankruptService(OwnedStockDAO ownedStockDAO)
  {
    this.ownedStockDAO = ownedStockDAO;
  }


  public void handleBankruptcy(StockBankruptcyEvent event)
  {
    // TODO wip implement bankruptcy
    List<OwnedStock> ownedStockList = ownedStockDAO.getAllByStockSymbol(event.stockSymbol());

    if(ownedStockList.isEmpty())
    {
      logger.log(LogLevel.ERROR, "No owned stocks found. StockSymbol: " + event.stockSymbol());
      return;
    }
    ownedStockList.forEach(ownedStock -> ownedStock.setNumberOfShares(0));

  }

  // TODO update class diagram
}
