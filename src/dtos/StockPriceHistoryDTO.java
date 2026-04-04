package dtos;

import entities.StockPriceHistory;
import business.services.queries.PriceHistoryRange;

import java.util.List;

public record StockPriceHistoryDTO(String symbol, PriceHistoryRange range, List<StockPriceHistory> dataPoints)
{
  public StockPriceHistoryDTO
  {
    dataPoints = List.copyOf(dataPoints);   // immutable, consistent with PortfolioSummaryDTO
  }
}