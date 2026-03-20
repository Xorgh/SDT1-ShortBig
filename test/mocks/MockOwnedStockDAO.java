package mocks;

import entities.OwnedStock;
import persistence.interfaces.OwnedStockDAO;

import java.util.List;
import java.util.UUID;

public class MockOwnedStockDAO implements OwnedStockDAO
{
  // INPUT — configure what getAllByStockSymbol returns
  private List<OwnedStock> ownedStocksBySymbol = List.of();

  // OUTPUT — capture what create/update received
  private OwnedStock lastCreated;
  private int createCount;
  private int updateCount;

  public void setOwnedStocksBySymbol(List<OwnedStock> ownedStocks)
  {
    this.ownedStocksBySymbol = ownedStocks;
  }

  @Override public List<OwnedStock> getAllByStockSymbol(String stockSymbol)
  {
    return ownedStocksBySymbol;
  }

  @Override public void create(OwnedStock ownedStock)
  {
    createCount++;
    lastCreated = ownedStock;
  }

  @Override public void update(OwnedStock ownedStock)
  {
    updateCount++;
  }

  public OwnedStock getLastCreated() { return lastCreated; }
  public int getCreateCount() { return createCount; }
  public int getUpdateCount() { return updateCount; }

  // These are unused by BuyStockService — keep as stubs
  @Override public OwnedStock getById(UUID id) { return null; }
  @Override public List<OwnedStock> getAll() { return List.of(); }
  @Override public void delete(UUID id) { }
}
