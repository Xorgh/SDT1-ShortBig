package dtos;

import entities.OwnedStock;

import java.util.List;
import java.util.UUID;

public record PortfolioSummaryDTO(
    UUID portfolioId,
    double balance,
    List<OwnedStock> ownedStocks
) {
  public PortfolioSummaryDTO {
    ownedStocks = List.copyOf(ownedStocks);
  }
}