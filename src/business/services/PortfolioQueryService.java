package business.services;

import dtos.BalanceHistoryDTO;
import dtos.PortfolioSummaryDTO;
import entities.OwnedStock;
import entities.Portfolio;
import entities.Transaction;
import entities.TransactionType;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.TransactionDAO;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PortfolioQueryService
{
  private final Logger logger = Logger.getInstance();
  private final PortfolioDAO portfolioDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;

  public PortfolioQueryService(PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO, TransactionDAO transactionDAO)
  {
    this.portfolioDAO = portfolioDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
  }

  public PortfolioSummaryDTO getPortfolioSummary(UUID portfolioId)
  {
    if (portfolioId == null)
    {
      throw new IllegalArgumentException("Portfolio ID cannot be null");
    }

    Portfolio portfolio = portfolioDAO.getById(portfolioId);

    if (portfolio == null)
    {
      logger.log(LogLevel.ERROR, "Portfolio not found: " + portfolioId);
      throw new IllegalArgumentException("Portfolio not found: " + portfolioId);
    }

    List<OwnedStock> owned = ownedStockDAO.getAll().stream().filter(os -> os.getPortfolioId().equals(portfolioId))
        .toList();

    List<Transaction> history = transactionDAO.getAll().stream().filter(t -> t.getPortfolioId().equals(portfolioId))
        .toList();

    return new PortfolioSummaryDTO(portfolioId, portfolio.getCurrentBalance(), owned, history);
  }

  public List<BalanceHistoryDTO> getBalanceHistory(UUID portfolioId)
  {
    if (portfolioId == null)
    {
      throw new IllegalArgumentException("Portfolio ID cannot be null");
    }

    List<Transaction> history = transactionDAO.getAll().stream().filter(t -> t.getPortfolioId().equals(portfolioId))
        .sorted(Comparator.comparing(Transaction::getTimestamp)).toList();

    List<BalanceHistoryDTO> result = new ArrayList<>();
    double runningBalance = 0;

    for (Transaction t : history)
    {
      if (t.getType() == TransactionType.BUY)
      {
        runningBalance -= t.getTotalAmount();
      }
      else
      {
        runningBalance += t.getTotalAmount();
      }
      result.add(new BalanceHistoryDTO(t.getTimestamp(), t.getType(), t.getTotalAmount(), runningBalance));
    }
    return result;
  }
}

