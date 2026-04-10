package business.services.queries;

import dtos.PortfolioSummaryDTO;
import entities.OwnedStock;
import entities.Portfolio;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import shared.logging.LogLevel;
import shared.logging.Logger;

import java.util.List;
import java.util.UUID;

public class PortfolioQueryService
{
  private final Logger logger = Logger.getInstance();
  private final PortfolioDAO portfolioDAO;
  private final OwnedStockDAO ownedStockDAO;

  public PortfolioQueryService(PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO)
  {
    this.portfolioDAO = portfolioDAO;
    this.ownedStockDAO = ownedStockDAO;
  }

  public PortfolioSummaryDTO getPortfolioSummary(UUID portfolioId)
  {
    if (portfolioId == null)
      throw new IllegalArgumentException("Portfolio ID cannot be null");

    Portfolio portfolio = portfolioDAO.getById(portfolioId);

    if (portfolio == null)
    {
      logger.log(LogLevel.ERROR, "Portfolio not found: " + portfolioId);
      throw new IllegalArgumentException("Portfolio not found: " + portfolioId);
    }

    List<OwnedStock> owned = ownedStockDAO.getAll().stream()
        .filter(os -> os.getPortfolioId().equals(portfolioId))
        .toList();

    return new PortfolioSummaryDTO(portfolioId, portfolio.getCurrentBalance(), owned);
  }

  public UUID getDefaultPortfolioId()
  {
    List<Portfolio> all = portfolioDAO.getAll();
    if (all.isEmpty()) return null;
    return all.getFirst().getId();
  }
}