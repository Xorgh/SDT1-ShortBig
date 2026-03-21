package mocks;

import entities.Portfolio;
import persistence.interfaces.PortfolioDAO;

import java.util.List;
import java.util.UUID;

public class MockPortfolioDAO implements PortfolioDAO
{
  private int updateCounter;
  private Portfolio portfolioToReturn;
  private Portfolio lastCreated;

  public int getUpdateCounter()
  {
    return updateCounter;
  }

  public void setPortfolioToReturn(Portfolio portfolioToReturn)
  {
    this.portfolioToReturn = portfolioToReturn;
  }

  public Portfolio getPortfolioToReturn()
  {
    return portfolioToReturn;
  }

  @Override public void create(Portfolio portfolio)
  {
    lastCreated = portfolio;
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
    updateCounter++;
  }

  @Override public void delete(UUID id)
  {

  }
}
