package mocks;

import entities.Stock;
import persistence.interfaces.StockDAO;

import java.util.List;

public class MockStockDAO implements StockDAO
{
  private Stock stockToReturn;

  public void setStockToReturn(Stock stock)
  {
    this.stockToReturn = stock;
  }

  @Override public void create(Stock stock)
  {

  }

  @Override public Stock getBySymbol(String symbol)
  {
    if (stockToReturn != null && stockToReturn.getSymbol().equals(symbol))
      return stockToReturn;
    return null;
  }

  @Override public List<Stock> getAll()
  {
    return List.of();
  }

  @Override public void update(Stock stock)
  {

  }

  @Override public void delete(String symbol)
  {

  }
}
