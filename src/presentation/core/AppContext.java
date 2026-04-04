package presentation.core;

import business.services.PortfolioQueryService;
import persistence.fileimplementation.FileUnitOfWork;
import persistence.fileimplementation.OwnedStockFileDAO;
import persistence.fileimplementation.PortfolioFileDAO;
import persistence.fileimplementation.TransactionFileDAO;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.TransactionDAO;
import presentation.views.portfolio.PortfolioViewModel;
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

  public PortfolioViewModel getPortfolioViewModel()
  {
    return createPortfolioViewModel();
  }

  private PortfolioViewModel createPortfolioViewModel()
  {
    FileUnitOfWork unitOfWork = createFileUnitOfWork();
    return new PortfolioViewModel(createPortfolioQueryService(unitOfWork));
  }

  private PortfolioQueryService createPortfolioQueryService(FileUnitOfWork unitOfWork)
  {
    return new PortfolioQueryService(createPortfolioDAO(unitOfWork), createOwnedStockDAO(unitOfWork), createTransactionDAO(unitOfWork));
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
