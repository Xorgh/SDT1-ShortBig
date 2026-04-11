package persistence.fileimplementation;

import entities.Transaction;
import persistence.interfaces.TransactionDAO;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class TransactionFileDAO implements TransactionDAO
{
  private final FileUnitOfWork uow;

  public TransactionFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Transaction transaction)
  {
    uow.getTransactions().add(transaction);
  }

  @Override public Transaction getById(UUID id)
  {
    return uow.getTransactions().stream().filter(transaction -> transaction.getId().equals(id)).findFirst()
        .orElse(null);
  }

  @Override public List<Transaction> getAll()
  {
    return uow.getTransactions();
  }

  @Override public List<Transaction> getByPortfolioId(UUID portfolioId)
  {
    return uow.getTransactions().stream()
        .filter(t -> t.getPortfolioId().equals(portfolioId))
        .sorted(Comparator.comparing(Transaction::getTimestamp))
        .toList();
  }

  @Override public List<Transaction> getByPortfolioId(UUID portfolioId, int page, int pageSize)
  {
    return uow.getTransactions().stream()
        .filter(t -> t.getPortfolioId().equals(portfolioId))
        .sorted(Comparator.comparing(Transaction::getTimestamp))
        .skip((long) page * pageSize)
        .limit(pageSize)
        .toList();
  }

  @Override public void delete(UUID id)
  {
    uow.getTransactions().remove(getById(id));
  }
}
