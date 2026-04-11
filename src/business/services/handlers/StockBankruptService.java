package business.services.handlers;

import business.events.StockBankruptcyEvent;
import entities.OwnedStock;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.UnitOfWork;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.List;

public class StockBankruptService
{
  private UnitOfWork uow;
  private OwnedStockDAO ownedStockDAO;
  private Logger logger = Logger.getInstance();

  public StockBankruptService(UnitOfWork uow, OwnedStockDAO ownedStockDAO)
  {
    this.uow = uow;
    this.ownedStockDAO = ownedStockDAO;
  }


  public void handleBankruptcy(StockBankruptcyEvent event)
  {
    uow.begin();

    try
    {
      List<OwnedStock> ownedStockList = ownedStockDAO.getAllByStockSymbol(event.stockSymbol());

      if (ownedStockList.isEmpty())
      {
        uow.rollback();
        logger.log(LogLevel.INFO, "No owned stocks found for bankrupt stock: " + event.stockSymbol());
        return;
      }

      for (OwnedStock ownedStock : ownedStockList)
      {
        ownedStockDAO.delete(ownedStock.getId());
      }

      uow.commit();
      logger.log(LogLevel.INFO, "Bankruptcy handled for stock: " + event.stockSymbol());
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LogLevel.ERROR, "Bankruptcy handling failed: " + e.getMessage());
    }
  }
}
