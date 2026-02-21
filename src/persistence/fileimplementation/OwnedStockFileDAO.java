package persistence.fileimplementation;

import entities.OwnedStock;
import persistence.interfaces.OwnedStockDAO;
import java.util.List;
import java.util.UUID;

public class OwnedStockFileDAO implements OwnedStockDAO
{
  private final FileUnitOfWork uow;

  public OwnedStockFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(OwnedStock ownedStock)
  {
    uow.getOwnedStocks().add(ownedStock);
  }

  @Override public OwnedStock getById(UUID id)
  {
    return uow.getOwnedStocks().stream().filter(ownedStock -> ownedStock.getId().equals(id)).findFirst().orElse(null);
  }

  @Override public List<OwnedStock> getAll()
  {
    return uow.getOwnedStocks();
  }

  @Override public void update(OwnedStock ownedStock)
  {
    OwnedStock existing = getById(ownedStock.getId());
    if (existing != null)
    {
      existing.setNumberOfShares(ownedStock.getNumberOfShares());
    }
  }

  @Override public void delete(UUID id)
  {
    uow.getOwnedStocks().remove(getById(id));
  }
}
