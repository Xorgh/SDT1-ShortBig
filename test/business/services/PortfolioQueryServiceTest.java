package business.services;

import dtos.PortfolioSummaryDTO;
import entities.OwnedStock;
import entities.Portfolio;
import entities.Transaction;
import entities.TransactionType;
import mocks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PortfolioQueryServiceTest
{
  private static final UUID PORTFOLIO_ID = UUID.fromString("a75948fc-82ab-4590-8509-b4eab7189b35");

  // Fields — visible to all test methods
  private MockPortfolioDAO mockPortfolioDAO;
  private MockOwnedStockDAO mockOwnedStockDAO;
  private MockTransactionDAO mockTransactionDAO;
  private PortfolioQueryService service;

  @BeforeEach void setup()
  {
    mockPortfolioDAO = new MockPortfolioDAO();
    mockOwnedStockDAO = new MockOwnedStockDAO();
    mockTransactionDAO = new MockTransactionDAO();

    // Default valid stock and portfolio for happy path tests
    mockPortfolioDAO.setPortfolioToReturn(new Portfolio(PORTFOLIO_ID, 5000));

    service = new PortfolioQueryService(mockPortfolioDAO, mockOwnedStockDAO, mockTransactionDAO);
  }

  // # getPortfolioSummary:
  // ## Zero & One

  @Test void getPortfolioSummary_ValidPortfolioNoStocksNoTransactions_ShouldReturnEmptyOwnedStockList()
  {
    // Arrange — defaults from setup are sufficient

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertTrue(result.ownedStocks().isEmpty());
  }

  @Test void getPortfolioSummary_ValidPortfolioNoStocksNoTransactions_ShouldReturnEmptyTransactionList()
  {
    // Arrange — defaults from setup are sufficient

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertTrue(result.transactionHistory().isEmpty());
  }

  @Test void getPortfolioSummary_ValidPortfolioOneOwnedStock_ShouldReturnOneOwnedStock()
  {
    // Arrange
    mockOwnedStockDAO.setAllOwnedStocks(List.of(new OwnedStock(PORTFOLIO_ID, "AAPL", 10)));

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertEquals(1, result.ownedStocks().size());
  }

  //  ## Many
  @Test void getPortfolioSummary_ValidPortfolioMultipleOwnedStocks_ShouldReturnAll()
  {
    // Arrange
    mockOwnedStockDAO.setAllOwnedStocks(
        List.of(new OwnedStock(PORTFOLIO_ID, "AAPL", 10), new OwnedStock(PORTFOLIO_ID, "MSFT", 10),
            new OwnedStock(PORTFOLIO_ID, "NVDA", 10)));

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertEquals(3, result.ownedStocks().size());
  }

  @Test void getPortfolioSummary_ValidPortfolioMultipleTransactions_ShouldReturnAll()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(
        List.of(
            new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 10, 100),
            new Transaction(PORTFOLIO_ID, "MSFT", TransactionType.SELL, 10, 100),
            new Transaction(PORTFOLIO_ID, "NVDA", TransactionType.BUY, 10, 100)
            ));

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertEquals(3, result.transactionHistory().size());
  }


  //  ## Interface & Exceptions

  @Test void getPortfolioSummary_PortfolioNotFound_ShouldThrow()
  {
    // Arrange
    mockPortfolioDAO.setPortfolioToReturn(null);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.getPortfolioSummary(PORTFOLIO_ID));
  }

  @Test void getPortfolioSummary_NullPortfolioId_ShouldThrow()
  {
    // Arrange

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.getPortfolioSummary(null));
  }


  //  ## State & Behavior
    @Test void getPortfolioSummary_ValidPortfolio_ShouldReturnCorrectBalance()
    {
      // Arrange

      // Act
      PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

      // Assert
      assertEquals(5000, result.balance());
    }

  //
  @Test void getPortfolioSummary_ValidPortfolio_ShouldReturnCorrectPortfolioId()
  {
    // Arrange

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertEquals(PORTFOLIO_ID.toString(), result.portfolioId().toString());
  }

  @Test void getPortfolioSummary_MixedPortfolios_ShouldOnlyReturnOwnedStocksForRequestedPortfolio()
  {
    // Arrange
    mockOwnedStockDAO.setAllOwnedStocks(
        List.of(
            new OwnedStock(PORTFOLIO_ID, "AAPL", 10),
            new OwnedStock(PORTFOLIO_ID, "MSFT", 10),
            new OwnedStock(PORTFOLIO_ID, "NVDA", 10),
            new OwnedStock(UUID.randomUUID(), "AAPL", 99),
            new OwnedStock(UUID.randomUUID(), "AAPL", 99)
        ));

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertEquals(3, result.ownedStocks().size());
      }

  @Test void getPortfolioSummary_MixedPortfolios_ShouldOnlyReturnTransactionsForRequestedPortfolio()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(
        List.of(
            new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.SELL, 10, 100),
            new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.SELL, 10, 100),
            new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.SELL, 10, 100),
            new Transaction(UUID.randomUUID(), "AAPL", TransactionType.SELL, 10, 100),
            new Transaction(UUID.randomUUID(), "AAPL", TransactionType.SELL, 10, 100)
        ));

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertEquals(3, result.transactionHistory().size());
  }

  //  # getBalanceHistory:

  //  ## Zero & One
  //  getBalanceHistory_NoTransactions_ShouldReturnEmptyList
  //  getBalanceHistory_SingleBuyTransaction_ShouldReturnNegativeBalance
  //  getBalanceHistory_SingleSellTransaction_ShouldReturnPositiveBalance

  //  ## Many & Boundaries
  //  getBalanceHistory_MultipleBuyTransactions_ShouldAccumulateNegativeBalance
  //  getBalanceHistory_MixedBuyAndSell_ShouldCalculateCorrectRunningBalance
  //  getBalanceHistory_MultipleTransactions_ShouldBeSortedByTimestamp

  //  ## Interface & Exceptions
  //  getBalanceHistory_TransactionsFromOtherPortfolio_ShouldBeExcluded

  //  ## State & Behavior
  //  getBalanceHistory_SingleBuy_ShouldHaveCorrectTransactionType
  //  getBalanceHistory_SingleBuy_ShouldHaveCorrectAmount

}
