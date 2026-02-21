package persistence.interfaces;

import entities.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionDAO
{
    void create(Transaction transaction);
    Transaction getById(UUID id);
    List<Transaction> getAll();
    void delete(UUID id);
}
