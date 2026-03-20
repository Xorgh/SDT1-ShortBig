package mocks;

import entities.Transaction;
import persistence.interfaces.TransactionDAO;

import java.util.List;
import java.util.UUID;

public class MockTransactionDAO implements TransactionDAO
{
  private Transaction transactionToReturn;
  private Transaction lastCreated;

  public void setTransactionToReturn(Transaction transactionToReturn)
  {
    this.transactionToReturn = transactionToReturn;
  }

  @Override public void create(Transaction transaction)
  {
    lastCreated = transaction;
  }

  @Override public Transaction getById(UUID id)
  {
    return transactionToReturn;
  }

  @Override public List<Transaction> getAll()
  {
    return List.of();
  }

  @Override public void delete(UUID id)
  {

  }
}
