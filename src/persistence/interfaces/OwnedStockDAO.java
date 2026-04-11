package persistence.interfaces;

import entities.OwnedStock;

import java.util.List;
import java.util.UUID;

public interface OwnedStockDAO
{
  void create(OwnedStock ownedStock);
  OwnedStock getById(UUID id);
  List<OwnedStock> getAll();
  List<OwnedStock> getAllByPortfolioId(UUID portfolioId);
  List<OwnedStock> getAllByStockSymbol(String stockSymbol);
  void update(OwnedStock ownedStock);
  void delete(UUID id);
}


