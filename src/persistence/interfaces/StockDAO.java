package persistence.interfaces;

import entities.Stock;
import java.util.List;

public interface StockDAO
{
  void create(Stock stock);
  Stock getBySymbol(String symbol);
  List<Stock> getAll();
  void update(Stock stock);
  void delete(String symbol);
}
