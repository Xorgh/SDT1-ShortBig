package business.services.queries;

import dtos.BalanceHistoryDTO;
import dtos.TransactionDTO;
import entities.Transaction;
import entities.TransactionType;
import persistence.interfaces.TransactionDAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class TransactionQueryService
{
  private final TransactionDAO transactionDAO;

  public TransactionQueryService(TransactionDAO transactionDAO)
  {
    this.transactionDAO = transactionDAO;
  }

  public List<TransactionDTO> getTransactionsByPortfolio(UUID portfolioId)
  {
    if (portfolioId == null)
      throw new IllegalArgumentException("Portfolio ID cannot be null");

    return transactionDAO.getAll().stream()
        .filter(t -> t.getPortfolioId().equals(portfolioId))
        .sorted(Comparator.comparing(Transaction::getTimestamp))
        .map(t -> new TransactionDTO(
            t.getId(),
            t.getStockSymbol(),
            t.getType(),
            t.getQuantity(),
            t.getPricePerShare(),
            t.getTotalAmount(),
            t.getFee(),
            t.getTimestamp()))
        .toList();
  }

  public List<BalanceHistoryDTO> getBalanceHistory(UUID portfolioId, double startingBalance)
  {
    if (portfolioId == null)
      throw new IllegalArgumentException("Portfolio ID cannot be null");

    List<Transaction> history = transactionDAO.getAll().stream()
        .filter(t -> t.getPortfolioId().equals(portfolioId))
        .sorted(Comparator.comparing(Transaction::getTimestamp))
        .toList();

    List<BalanceHistoryDTO> result = new ArrayList<>();
    double runningBalance = startingBalance;

    for (Transaction t : history)
    {
      if (t.getType() == TransactionType.BUY)
        runningBalance -= t.getTotalAmount();
      else
        runningBalance += t.getTotalAmount();

      result.add(new BalanceHistoryDTO(t.getTimestamp(), t.getType(), t.getTotalAmount(), runningBalance));
    }
    return result;
  }
}