package presentation.core;

import business.services.GameService;
import business.services.handlers.StockAlertService;
import business.services.handlers.StockBankruptService;
import business.services.handlers.StockListenerService;
import business.services.handlers.StockResetService;
import business.services.queries.PortfolioQueryService;
import business.services.queries.StockPriceHistoryQueryService;
import business.services.queries.StockQueryService;
import business.services.queries.TransactionQueryService;
import business.services.requests.BuyStockService;
import business.services.requests.SellStockService;
import business.stockmarket.StockMarket;
import persistence.fileimplementation.*;
import persistence.interfaces.*;
import presentation.views.portfolio.PortfolioViewModel;
import presentation.views.stockmarket.MarketViewModel;
import presentation.views.transactions.TransactionViewModel;
import shared.configuration.AppConfig;

public class AppContext
{
  private static AppContext instance;

  private GameService gameService;

  private AppContext()
  {
  }

  public static AppContext getInstance()
  {
    if (instance == null)
    {
      instance = new AppContext();
      instance.initialize();
    }
    return instance;
  }

  private void initialize()
  {
    FileUnitOfWork gameUow = createFileUnitOfWork();
    StockDAO stockDAO = createStockDAO(gameUow);
    PortfolioDAO portfolioDAO = createPortfolioDAO(gameUow);
    OwnedStockDAO ownedStockDAO = createOwnedStockDAO(gameUow);
    TransactionDAO transactionDAO = createTransactionDAO(gameUow);
    StockPriceHistoryDAO historyDAO = createStockPriceHistoryDAO(gameUow);

    // Create GameService
    gameService = new GameService(gameUow, stockDAO, portfolioDAO,
        ownedStockDAO, transactionDAO, historyDAO);

    // Register observers — composition root wires everything
    registerObservers(gameUow, stockDAO, ownedStockDAO, historyDAO);
  }

  private void registerObservers(UnitOfWork uow, StockDAO stockDAO,
      OwnedStockDAO ownedStockDAO, StockPriceHistoryDAO historyDAO)
  {
    StockListenerService listenerService = new StockListenerService(uow, stockDAO, historyDAO);
    StockBankruptService bankruptService = new StockBankruptService(uow, ownedStockDAO);
    StockAlertService alertService = new StockAlertService();
    StockResetService resetService = new StockResetService();

    StockMarket market = StockMarket.INSTANCE;
    market.onStockPriceChange.add(listenerService::handlePriceChange);
    market.onStockStateChange.add(listenerService::handleStateChange);
    market.onStockBankruptcy.add(bankruptService::handleBankruptcy);
    market.onStockBankruptcy.add(alertService::handleBankruptcyAlert);
    market.onStockReset.add(resetService::handleStockReset);
    market.onStockReset.add(alertService::handleStockResetAlert);
  }

  public GameService getGameService()
  {
    return gameService;
  }


  public MarketViewModel getMarketViewModel()
  {
    FileUnitOfWork unitOfWork = createFileUnitOfWork();
    return new MarketViewModel(
        createStockQueryService(unitOfWork),
        createStockPriceHistoryQueryService(unitOfWork),
        unitOfWork::begin);
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
        createSellStockService(unitOfWork, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO),
        unitOfWork::begin
    );
  }

  public TransactionViewModel getTransactionViewModel()
  {
    FileUnitOfWork unitOfWork = createFileUnitOfWork();
    return new TransactionViewModel(createTransactionQueryService(unitOfWork));
  }

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
