package business.services;

import dtos.PortfolioSummaryDTO;
import entities.OwnedStock;
import entities.Portfolio;
import entities.Stock;
import entities.StockState;
import mocks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  //  getPortfolioSummary_ValidPortfolioMultipleOwnedStocks_ShouldReturnAll
  //  getPortfolioSummary_ValidPortfolioMultipleTransactions_ShouldReturnAll

  //  ## Interface & Exceptions
  //  getPortfolioSummary_PortfolioNotFound_ShouldThrow
  //  getPortfolioSummary_NullPortfolioId_ShouldThrow

  //  ## State & Behavior
  //  getPortfolioSummary_ValidPortfolio_ShouldReturnCorrectBalance
  //  getPortfolioSummary_ValidPortfolio_ShouldReturnCorrectPortfolioId
  //  getPortfolioSummary_MixedPortfolios_ShouldOnlyReturnOwnedStocksForRequestedPortfolio
  //  getPortfolioSummary_MixedPortfolios_ShouldOnlyReturnTransactionsForRequestedPortfolio

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
