package integration;

import business.services.queries.PortfolioQueryService;
import business.services.queries.StockQueryService;
import business.services.queries.TransactionQueryService;
import business.services.requests.BuyStockService;
import business.services.requests.SellStockService;
import entities.Portfolio;
import entities.Stock;
import entities.StockState;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import persistence.fileimplementation.*;
import presentation.views.portfolio.PortfolioViewModel;
import shared.configuration.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

import static integration.IntegrationTestUtilities.cleanupTestDir;
import static integration.IntegrationTestUtilities.createTestDir;
import static org.junit.jupiter.api.Assertions.*;

public class BuyStockIntegrationTests
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
  //      Starts up JavaFX engine
  {
    Platform.startup(() -> {
    });
  }

  @BeforeEach public void setup() throws IOException
  {
    testDirPath = createTestDir();

    uow = new FileUnitOfWork(testDirPath);

    //    arrange
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
    cleanupTestDir(testDirPath);
  }

  @Nested class GivenValidInput
  {

    @BeforeEach public void setupPortfolio()
    {
      uow.begin();

      // Seed a stock
      stockDAO.create(new Stock("AAPL", "Apple", 100.0, StockState.STEADY));

      // Seed a portfolio with $10,000
      Portfolio portfolio = new Portfolio(10000.0);
      portfolioDAO.create(portfolio);

      uow.commit();

      // Load the VM with the seeded data
      vm.load(portfolio.getId());
    }

    @Test void buyStock_decreasesBalance()
    {
      double balanceBefore = vm.cashBalanceProperty().get();

      // Act
      vm.selectedBuySymbolProperty().set("AAPL");
      vm.buyQuantityProperty().set(2);
      vm.buyStock();

      // Assert
      double expected = balanceBefore - ((2 * stockDAO.getBySymbol("AAPL").getCurrentPrice()
          + AppConfig.INSTANCE.getTransactionFee()));
      assertEquals(expected, vm.cashBalanceProperty().get());

    }

    @Test void buyStock_addsToHoldings()
    {
      // Act
      vm.selectedBuySymbolProperty().set("AAPL");
      vm.buyQuantityProperty().set(3);
      vm.buyStock();

      // Assert
      assertTrue(vm.getHoldings().stream().anyMatch(h -> h.symbol().equals("AAPL") && h.shares() == 3));
    }

    @Test void buyStock_ownedStockSymbolsUpdates()
    {
      // Act
      vm.selectedBuySymbolProperty().set("AAPL");
      vm.buyQuantityProperty().set(3);
      vm.buyStock();

      // Assert
      assertTrue(vm.getOwnedStockSymbols().stream().anyMatch(s -> s.equals("AAPL")));
    }

    @Test void buyStock_transactionIsRecorded()
    {
      // Act
      vm.selectedBuySymbolProperty().set("AAPL");
      vm.buyQuantityProperty().set(3);
      vm.buyStock();

      // Assert
      var transactions = transactionDAO.getAll();
      assertEquals(1, transactions.size());
      assertEquals("AAPL", transactions.get(0).getStockSymbol());
      assertEquals(3, transactions.get(0).getQuantity());
    }

    @Test void buyStock_addsToExistingHoldings()
    {
      // Arrange
      vm.selectedBuySymbolProperty().set("AAPL");
      vm.buyQuantityProperty().set(3);
      vm.buyStock();
      int numberOfSharesBefore = vm.getHoldings().get(0).shares();

      // Act
      vm.selectedBuySymbolProperty().set("AAPL");
      vm.buyQuantityProperty().set(2);
      vm.buyStock();
      int numberOfSharesAfter = vm.getHoldings().get(0).shares();

      // Assert
      assertEquals(5, numberOfSharesAfter);
    }
  }

  @Nested class GivenInValidInput
  {

    @BeforeEach public void setupPortfolio()
    {
      uow.begin();
      stockDAO.create(new Stock("AAPL", "Apple", 100.0, StockState.STEADY));
      stockDAO.create(new Stock("DEAD", "DeadCo", 0.0, StockState.BANKRUPT));
      Portfolio portfolio = new Portfolio(50.0); // Only $50 — not enough for 1 share
      portfolioDAO.create(portfolio);
      uow.commit();
      vm.load(portfolio.getId());
    }

    @Test void buyStock_insufficientBalance_throws()
    {
      vm.selectedBuySymbolProperty().set("AAPL");
      vm.buyQuantityProperty().set(1);
      assertThrows(IllegalArgumentException.class, () -> vm.buyStock());
    }

    @Test void buyStock_insufficientBalance_balanceUnchanged()
    {
      double before = vm.cashBalanceProperty().get();
      vm.selectedBuySymbolProperty().set("AAPL");
      vm.buyQuantityProperty().set(1);
      assertThrows(IllegalArgumentException.class, () -> vm.buyStock());
      assertEquals(before, vm.cashBalanceProperty().get());
    }

    @Test void buyStock_bankruptStock_throws()
    {
      vm.selectedBuySymbolProperty().set("DEAD");
      vm.buyQuantityProperty().set(1);
      assertThrows(IllegalArgumentException.class, () -> vm.buyStock());
    }

    @Test void buyStock_nonExistentStock_throws()
    {
      vm.selectedBuySymbolProperty().set("FAKE");
      vm.buyQuantityProperty().set(1);
      assertThrows(IllegalArgumentException.class, () -> vm.buyStock());
    }
  }
}
