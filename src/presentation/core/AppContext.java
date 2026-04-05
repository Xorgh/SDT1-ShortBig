package presentation.core;

import business.services.queries.PortfolioQueryService;
import business.services.queries.StockPriceHistoryQueryService;
import business.services.queries.StockQueryService;
import business.services.queries.TransactionQueryService;
import business.services.requests.BuyStockService;
import business.services.requests.SellStockService;
import entities.StockPriceHistory;
import persistence.fileimplementation.*;
import persistence.interfaces.*;
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
    StockQueryService stockQueryService = createStockQueryService(unitOfWork);
    StockPriceHistoryQueryService historyQueryService = createStockPriceHistoryQueryService(unitOfWork);
    return new MarketViewModel(stockQueryService, historyQueryService);
  }

  public PortfolioViewModel getPortfolioViewModel()
  {
    FileUnitOfWork unitOfWork = createFileUnitOfWork();
    StockDAO stockDAO = createStockDAO(unitOfWork);
    PortfolioDAO portfolioDAO = createPortfolioDAO(unitOfWork);
    OwnedStockDAO ownedStockDAO = createOwnedStockDAO(unitOfWork);
    TransactionDAO transactionDAO = createTransactionDAO(unitOfWork);

    return new PortfolioViewModel(
        createPortfolioQueryService(portfolioDAO, ownedStockDAO),
        createTransactionQueryService(transactionDAO),
        createStockQueryService(stockDAO),
        createBuyStockService(unitOfWork, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO),
        createSellStockService(unitOfWork, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO)
    );
  }


  public TransactionViewModel getTransactionViewModel() { return createTransactionViewModel(); }


  private StockQueryService createStockQueryService(FileUnitOfWork unitOfWork)
  {
    return new StockQueryService(createStockDAO(unitOfWork));
  }

  private StockQueryService createStockQueryService(StockDAO stockDAO)
  {
    return new StockQueryService(stockDAO);
  }

  private PortfolioQueryService createPortfolioQueryService(PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO)
  {
    return new PortfolioQueryService(portfolioDAO, ownedStockDAO);
  }

  private BuyStockService createBuyStockService(UnitOfWork uow, StockDAO stockDAO,
      PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO, TransactionDAO transactionDAO)
  {
    return new BuyStockService(uow, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO);
  }

  private SellStockService createSellStockService(UnitOfWork uow, StockDAO stockDAO,
      PortfolioDAO portfolioDAO, OwnedStockDAO ownedStockDAO, TransactionDAO transactionDAO)
  {
    return new SellStockService(uow, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO);
  }

  private TransactionViewModel createTransactionViewModel()
  {
    FileUnitOfWork unitOfWork = createFileUnitOfWork();
    return new TransactionViewModel(createTransactionQueryService(unitOfWork));
  }

  // Used by getTransactionViewModel() — creates its own DAO
  private TransactionQueryService createTransactionQueryService(FileUnitOfWork unitOfWork)
  {
    return new TransactionQueryService(createTransactionDAO(unitOfWork));
  }

  // Used by getPortfolioViewModel() — reuses existing DAO
  private TransactionQueryService createTransactionQueryService(TransactionDAO transactionDAO)
  {
    return new TransactionQueryService(transactionDAO);
  }


  private StockPriceHistoryQueryService createStockPriceHistoryQueryService(FileUnitOfWork unitOfWork)
  {
    return new StockPriceHistoryQueryService((createStockPriceHistoryDAO(unitOfWork)));
  }

  private StockDAO createStockDAO(FileUnitOfWork unitOfWork)
  {
    return new StockFileDAO(unitOfWork);
  }

  private StockPriceHistoryDAO createStockPriceHistoryDAO(FileUnitOfWork unitOfWork)
  {
    return new StockPriceHistoryFileDAO(unitOfWork);
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
