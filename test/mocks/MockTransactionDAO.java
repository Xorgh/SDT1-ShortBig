package mocks;

import entities.Transaction;
import persistence.interfaces.TransactionDAO;

import java.util.List;
import java.util.UUID;

public class MockTransactionDAO implements TransactionDAO
{
  private Transaction transactionToReturn;
  private Transaction lastCreated;
  private List<Transaction> allTransactions = List.of();

  public void setAllTransactions(List<Transaction> transactions) {
    this.allTransactions = transactions;
  }

  @Override public List<Transaction> getAll() {
    return allTransactions;
  }
  public void setTransactionToReturn(Transaction transactionToReturn)
  {
    this.transactionToReturn = transactionToReturn;
  }

  public Transaction getTransactionToReturn()
  {
    return transactionToReturn;
  }

  public Transaction getLastCreated()
  {
    return lastCreated;
  }

  @Override public void create(Transaction transaction)
  {
    lastCreated = transaction;
  }

  @Override public Transaction getById(UUID id)
  {
    return transactionToReturn;
  }

  @Override public void delete(UUID id)
  {

  }
}
