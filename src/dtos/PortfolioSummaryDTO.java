package dtos;

import entities.OwnedStock;
import entities.Transaction;

import java.util.List;
import java.util.UUID;

public record PortfolioSummaryDTO(
    UUID portfolioId,
    double balance,
    List<OwnedStock> ownedStocks,
    List<Transaction> transactionHistory
) {
  // Compact constructor — no parameters, no this. assignments
  // You modify the parameters BEFORE the record assigns them
  public PortfolioSummaryDTO {
    ownedStocks = List.copyOf(ownedStocks);
    transactionHistory = List.copyOf(transactionHistory);
  }
}
