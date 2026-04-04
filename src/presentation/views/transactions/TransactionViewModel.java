package presentation.views.transactions;

import business.services.queries.TransactionQueryService;

public class TransactionViewModel
{
  private final TransactionQueryService transactionQueryService;

  public TransactionViewModel(TransactionQueryService transactionQueryService)
  {
    this.transactionQueryService = transactionQueryService;
  }

  // transactionQueryService.getTransactionsByPortfolio() → transaction list
}