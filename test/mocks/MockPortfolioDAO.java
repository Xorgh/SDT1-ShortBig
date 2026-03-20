package mocks;

import entities.Portfolio;
import persistence.interfaces.PortfolioDAO;

import java.util.List;
import java.util.UUID;

public class MockPortfolioDAO implements PortfolioDAO
{
  private Portfolio portfolioToReturn;

  public void setPortfolioToReturn(Portfolio portfolioToReturn)
  {
    this.portfolioToReturn = portfolioToReturn;
  }

  @Override public void create(Portfolio portfolio)
  {

  }

  @Override public Portfolio getById(UUID id)
  {
    return portfolioToReturn;
  }

  @Override public List<Portfolio> getAll()
  {
    return List.of();
  }

  @Override public void update(Portfolio portfolio)
  {

  }

  @Override public void delete(UUID id)
  {

  }
}
