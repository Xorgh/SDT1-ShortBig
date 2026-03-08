package persistence.interfaces;

import entities.Stock;

import java.util.List;

public interface UnitOfWork
{
  void begin();
  void commit();
  void rollback();
}
