package persistence.fileimplementation;

import entities.StockPriceHistory;
import persistence.interfaces.StockPriceHistoryDAO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class StockPriceHistoryFileDAO implements StockPriceHistoryDAO
{
  private final FileUnitOfWork uow;

  public StockPriceHistoryFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(StockPriceHistory stockPriceHistory)
  {
    uow.getStockPriceHistoryList().add(stockPriceHistory);
  }

  @Override public StockPriceHistory getById(UUID id)
  {
    return uow.getStockPriceHistoryList().stream().filter(history -> history.getId().equals(id)).findFirst().orElse(null);
  }

  @Override public List<StockPriceHistory> getAll()
  {
    return uow.getStockPriceHistoryList();
  }

  @Override public List<StockPriceHistory> getBySymbolSince(String symbol, LocalDateTime since)
  {
    return uow.getStockPriceHistoryList().stream()
        .filter(h -> h.getStockSymbol().equalsIgnoreCase(symbol))
        .filter(h -> h.getTimestamp().isAfter(since))
        .sorted(Comparator.comparing(StockPriceHistory::getTimestamp))
        .toList();
  }

  @Override public void delete(UUID id)
  {
    uow.getStockPriceHistoryList().remove(getById(id));
  }
}
