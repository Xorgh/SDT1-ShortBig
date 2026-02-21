package persistence.fileimplementation;

import entities.Portfolio;
import entities.Stock;
import persistence.interfaces.PortfolioDAO;

import java.util.List;
import java.util.UUID;

public class PortfolioFileDAO implements PortfolioDAO
{
  private final FileUnitOfWork uow;

  public PortfolioFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Portfolio portfolio)
  {
    uow.getPortfolios().add(portfolio);
  }

  @Override public Portfolio getById(UUID id)
  {
    return uow.getPortfolios().stream().filter(portfolio -> portfolio.getId().equals(id)).findFirst().orElse(null);
  }

  @Override public List<Portfolio> getAll()
  {
    return uow.getPortfolios();
  }

  @Override public void update(Portfolio portfolio)
  {
    Portfolio existing = getById(portfolio.getId());
    if (existing != null)
    {
      existing.setCurrentBalance(portfolio.getCurrentBalance());
    }
  }

  @Override public void delete(UUID id)
  {
    uow.getPortfolios().remove(getById(id));
  }
}
