package persistence.interfaces;

import entities.Portfolio;
import java.util.List;
import java.util.UUID;

public interface PortfolioDAO
{
  void create(Portfolio portfolio);
  Portfolio getById(UUID id);
  List<Portfolio> getAll();
  void update(Portfolio portfolio);
  void delete(UUID id);
}