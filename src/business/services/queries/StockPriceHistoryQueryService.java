package business.services.queries;

import dtos.StockPriceHistoryDTO;
import entities.StockPriceHistory;
import persistence.interfaces.StockPriceHistoryDAO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class StockPriceHistoryQueryService
{
  private final StockPriceHistoryDAO dao;

  public StockPriceHistoryQueryService(StockPriceHistoryDAO dao)
  {
    this.dao = dao;
  }

  public StockPriceHistoryDTO getHistory(String symbol, PriceHistoryRange range)
  {
    LocalDateTime cutoff = range.getCutoff();

    List<StockPriceHistory> points = dao.getAll().stream()
        .filter(h -> h.getStockSymbol().equalsIgnoreCase(symbol))
        .filter(h -> h.getTimestamp().isAfter(cutoff))
        .sorted(Comparator.comparing(StockPriceHistory::getTimestamp))
        .toList();

    return new StockPriceHistoryDTO(symbol, range, points);
  }


}