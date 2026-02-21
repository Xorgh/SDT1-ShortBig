package persistence.interfaces;

import entities.StockPriceHistory;
import java.util.List;
import java.util.UUID;

public interface StockPriceHistoryDAO
{
  void create(StockPriceHistory stockPriceHistory);
  StockPriceHistory getById(UUID id);
  List<StockPriceHistory> getAll();
  void delete(UUID id);
}

