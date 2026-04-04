package presentation.core;

import business.services.queries.PortfolioQueryService;
import business.services.queries.StockPriceHistoryQueryService;
import business.services.queries.StockQueryService;
import business.services.queries.TransactionQueryService;
import persistence.fileimplementation.*;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.TransactionDAO;
import presentation.views.portfolio.PortfolioViewModel;
import presentation.views.stockmarket.MarketViewModel;
import presentation.views.transactions.TransactionViewModel;
import shared.configuration.AppConfig;

public class AppContext
{
  private static AppContext instance;

  private AppContext()
  {
  }

  public static AppContext getInstance()
  {
    if (instance == null)
    {
      instance = new AppContext();
    }
    return instance;
  }

  public MarketViewModel getMarketViewModel()
  {
    FileUnitOfWork unitOfWork = createFileUnitOfWork();
    StockQueryService stockQueryService =
        new StockQueryService(new StockFileDAO(unitOfWork));
    StockPriceHistoryQueryService historyQueryService =
        new StockPriceHistoryQueryService(new StockPriceHistoryFileDAO(unitOfWork));
    return new MarketViewModel(stockQueryService, historyQueryService);
  }

  public PortfolioViewModel getPortfolioViewModel()
  {
    FileUnitOfWork unitOfWork = createFileUnitOfWork();
    return new PortfolioViewModel(
        new PortfolioQueryService(createPortfolioDAO(unitOfWork), createOwnedStockDAO(unitOfWork)),
        new TransactionQueryService(createTransactionDAO(unitOfWork)),
        new StockQueryService(new StockFileDAO(unitOfWork))
    );
  }

  public TransactionViewModel getTransactionViewModel() { return createTransactionViewModel(); }

  private TransactionViewModel createTransactionViewModel()
  {
    FileUnitOfWork unitOfWork = createFileUnitOfWork();
    return new TransactionViewModel(createTransactionQueryService(unitOfWork));
  }

  private TransactionQueryService createTransactionQueryService(FileUnitOfWork unitOfWork)
  {
    return new TransactionQueryService(createTransactionDAO(unitOfWork));
  }

  private OwnedStockDAO createOwnedStockDAO(FileUnitOfWork unitOfWork)
  {
    return new OwnedStockFileDAO(unitOfWork);
  }

  private PortfolioDAO createPortfolioDAO(FileUnitOfWork unitOfWork)
  {
    return new PortfolioFileDAO(unitOfWork);
  }

  private TransactionDAO createTransactionDAO(FileUnitOfWork unitOfWork)
  {
    return new TransactionFileDAO(unitOfWork);
  }

  private FileUnitOfWork createFileUnitOfWork()
  {
    return new FileUnitOfWork(AppConfig.INSTANCE.getTestDataDirectory());
  }


}
