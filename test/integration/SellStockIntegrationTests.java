
package integration;

import business.services.queries.PortfolioQueryService;
import business.services.queries.StockQueryService;
import business.services.queries.TransactionQueryService;
import business.services.requests.BuyStockService;
import business.services.requests.SellStockService;
import entities.*;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import persistence.fileimplementation.*;
import presentation.views.portfolio.PortfolioViewModel;
import shared.configuration.AppConfig;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SellStockIntegrationTests
{
  private String testDirPath;

  FileUnitOfWork uow;
  StockFileDAO stockDAO;
  PortfolioFileDAO portfolioDAO;
  OwnedStockFileDAO ownedStockDAO;
  TransactionFileDAO transactionDAO;

  PortfolioQueryService portfolioQueryService;
  TransactionQueryService transactionQueryService;
  StockQueryService stockQueryService;
  BuyStockService buyStockService;
  SellStockService sellStockService;
  Runnable cacheInvalidator;

  PortfolioViewModel vm;

  @BeforeAll static void initToolkit()
  {
    try { Platform.startup(() -> {}); }
    catch (IllegalStateException ignored) {} // already started by BuyStockIntegrationTests
  }

  @BeforeEach public void setup() throws IOException
  {
    testDirPath = IntegrationTestUtilities.createTestDir();

    uow = new FileUnitOfWork(testDirPath);

    stockDAO = new StockFileDAO(uow);
    portfolioDAO = new PortfolioFileDAO(uow);
    ownedStockDAO = new OwnedStockFileDAO(uow);
    transactionDAO = new TransactionFileDAO(uow);

    portfolioQueryService = new PortfolioQueryService(portfolioDAO, ownedStockDAO);
    transactionQueryService = new TransactionQueryService(transactionDAO);
    stockQueryService = new StockQueryService(stockDAO);
    buyStockService = new BuyStockService(uow, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO);
    sellStockService = new SellStockService(uow, stockDAO, portfolioDAO, ownedStockDAO, transactionDAO);
    cacheInvalidator = uow::begin;

    vm = new PortfolioViewModel(portfolioQueryService, transactionQueryService, stockQueryService, buyStockService,
        sellStockService, cacheInvalidator);
  }

  @AfterEach public void cleanup() throws IOException
  {
    IntegrationTestUtilities.cleanupTestDir(testDirPath);
  }

  @Nested class GivenValidInput
  {
    Portfolio portfolio;

    @BeforeEach public void setupPortfolio()
    {
      uow.begin();
      stockDAO.create(new Stock("AAPL", "Apple", 100.0, StockState.STEADY));
      portfolio = new Portfolio(5000.0);
      portfolioDAO.create(portfolio);
      ownedStockDAO.create(new OwnedStock(portfolio.getId(), "AAPL", 10));
      uow.commit();
      vm.load(portfolio.getId());
    }

    @Test void sellStock_increasesBalance()
    {
      double before = vm.cashBalanceProperty().get();
      vm.sellStock("AAPL", 3);
      double expected = before + (3 * 100.0) - AppConfig.INSTANCE.getTransactionFee();
      assertEquals(expected, vm.cashBalanceProperty().get());
    }

    @Test void sellStock_decreasesHoldings()
    {
      vm.sellStock("AAPL", 3);
      assertTrue(vm.getHoldings().stream().anyMatch(h -> h.symbol().equals("AAPL") && h.shares() == 7));
    }

    @Test void sellStock_allShares_removesFromHoldings()
    {
      vm.sellStock("AAPL", 10);
      assertTrue(vm.getHoldings().stream().noneMatch(h -> h.symbol().equals("AAPL")));
    }

    @Test void sellStock_allShares_removesFromOwnedSymbols()
    {
      vm.sellStock("AAPL", 10);
      assertFalse(vm.getOwnedStockSymbols().contains("AAPL"));
    }

    @Test void sellStock_transactionIsRecorded()
    {
      vm.sellStock("AAPL", 4);
      var transactions = transactionDAO.getAll();
      assertEquals(1, transactions.size());
      assertEquals("AAPL", transactions.get(0).getStockSymbol());
      assertEquals(4, transactions.get(0).getQuantity());
    }
  }

  @Nested class GivenInvalidInput
  {
    Portfolio portfolio;

    @BeforeEach public void setupPortfolio()
    {
      uow.begin();
      stockDAO.create(new Stock("AAPL", "Apple", 100.0, StockState.STEADY));
      stockDAO.create(new Stock("DEAD", "DeadCo", 0.0, StockState.BANKRUPT));
      portfolio = new Portfolio(5000.0);
      portfolioDAO.create(portfolio);
      ownedStockDAO.create(new OwnedStock(portfolio.getId(), "AAPL", 5));
      uow.commit();
      vm.load(portfolio.getId());
    }

    @Test void sellStock_moreThanOwned_throws()
    {
      assertThrows(IllegalArgumentException.class, () -> vm.sellStock("AAPL", 10));
    }

    @Test void sellStock_stockNotOwned_throws()
    {
      assertThrows(IllegalArgumentException.class, () -> vm.sellStock("GOOG", 1));
    }

    @Test void sellStock_bankruptStock_throws()
    {
      assertThrows(IllegalArgumentException.class, () -> vm.sellStock("DEAD", 1));
    }

    @Test void sellStock_nonExistentStock_throws()
    {
      assertThrows(IllegalArgumentException.class, () -> vm.sellStock("FAKE", 1));
    }
  }
}