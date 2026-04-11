package persistence.interfaces;

import entities.StockPriceHistory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StockPriceHistoryDAO
{
  void create(StockPriceHistory stockPriceHistory);
  StockPriceHistory getById(UUID id);
  List<StockPriceHistory> getAll();
  List<StockPriceHistory> getBySymbolSince(String symbol, LocalDateTime since);
  void delete(UUID id);
}

