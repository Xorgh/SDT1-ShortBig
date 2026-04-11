package persistence.interfaces;

import entities.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionDAO
{
    void create(Transaction transaction);
    Transaction getById(UUID id);
    List<Transaction> getAll();
    List<Transaction> getByPortfolioId(UUID portfolioId);
    List<Transaction> getByPortfolioId(UUID portfolioId, int page, int pageSize);
    void delete(UUID id);
}
