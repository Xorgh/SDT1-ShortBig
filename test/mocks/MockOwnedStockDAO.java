package mocks;

import entities.OwnedStock;
import persistence.interfaces.OwnedStockDAO;

import java.util.List;
import java.util.UUID;

public class MockOwnedStockDAO implements OwnedStockDAO
{
  // INPUT — configure what getAllByStockSymbol returns
  private List<OwnedStock> ownedStocksBySymbol = List.of();

  private List<OwnedStock> allOwnedStocks = List.of();

  public void setAllOwnedStocks(List<OwnedStock> ownedStocks) {
    this.allOwnedStocks = ownedStocks;
  }

  @Override public List<OwnedStock> getAll() {
    return allOwnedStocks;
  }

  // OUTPUT — capture what create/update received
  private OwnedStock lastCreated;
  private OwnedStock shouldReturnOwnedStock;
  private int createCount;
  private int updateCount;
  private int deleteCount;

  public int getDeleteCount()
  {
    return deleteCount;
  }

  public void setShouldReturnOwnedStock(OwnedStock shouldReturnOwnedStock)
  {
    this.shouldReturnOwnedStock = shouldReturnOwnedStock;
  }

  public void setOwnedStocksBySymbol(List<OwnedStock> ownedStocks)
  {
    this.ownedStocksBySymbol = ownedStocks;
  }

  public OwnedStock getShouldReturnOwnedStock()
  {
    return shouldReturnOwnedStock;
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

  @Override public void delete(UUID id) {deleteCount++;}

  public OwnedStock getLastCreated() { return lastCreated; }
  public int getCreateCount() { return createCount; }
  public int getUpdateCount() { return updateCount; }

  @Override public OwnedStock getById(UUID id) { return null; }
}
