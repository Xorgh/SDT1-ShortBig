package persistence.fileimplementation;

import entities.Stock;
import persistence.interfaces.StockDAO;
import java.util.List;

public class StockFileDAO implements StockDAO
{
  private final FileUnitOfWork uow;

  public StockFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Stock stock)
  {
    uow.getStocks().add(stock);
  }

  @Override public Stock getBySymbol(String symbol)
  {
    return uow.getStocks().stream().filter(stock -> stock.getSymbol().equals(symbol)).findFirst().orElse(null);
  }

  @Override public List<Stock> getAll()
  {
    return uow.getStocks();
  }

  @Override public void update(Stock stock)
  {
    Stock existing = getBySymbol(stock.getSymbol());
    if (existing != null)
    {
      existing.setCurrentPrice(stock.getCurrentPrice());
      existing.setCurrentState(stock.getCurrentState());
    }
  }

  @Override public void delete(String symbol)
  {
    uow.getStocks().remove(getBySymbol(symbol));
  }
}
