package mocks;

import persistence.interfaces.UnitOfWork;

public class MockUnitOfWork implements UnitOfWork
{
  private int beginCount;
  private int commitCount;
  private int rollbackCount;


  @Override public void begin()
  {
    beginCount ++;
  }

  @Override public void commit()
  {
    commitCount ++;
  }

  @Override public void rollback()
  {
    rollbackCount ++;
  }

  public int getBeginCount()
  {
    return beginCount;
  }

  public int getCommitCount()
  {
    return commitCount;
  }

  public int getRollbackCount()
  {
    return rollbackCount;
  }
}
