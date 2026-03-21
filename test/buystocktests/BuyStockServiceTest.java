package buystocktests;

import business.events.BuyStockRequest;
import business.services.BuyStockService;
import entities.*;
import mocks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BuyStockServiceTest
{
  private static final UUID PORTFOLIO_ID = UUID.fromString("a75948fc-82ab-4590-8509-b4eab7189b35");

  // Fields — visible to all test methods
  private MockUnitOfWork mockUow;
  private MockStockDAO mockStockDAO;
  private MockPortfolioDAO mockPortfolioDAO;
  private MockOwnedStockDAO mockOwnedStockDAO;
  private MockTransactionDAO mockTransactionDAO;
  private BuyStockService service;

  @BeforeEach void setup()
  {
    mockUow = new MockUnitOfWork();
    mockStockDAO = new MockStockDAO();
    mockPortfolioDAO = new MockPortfolioDAO();
    mockOwnedStockDAO = new MockOwnedStockDAO();
    mockTransactionDAO = new MockTransactionDAO();

    // Default valid stock and portfolio for happy path tests
    mockStockDAO.setStockToReturn(new Stock("AAPL", "Apple", 100.0, StockState.STEADY));
    mockPortfolioDAO.setPortfolioToReturn(new Portfolio(PORTFOLIO_ID, 5000));

    service = new BuyStockService(mockUow, mockStockDAO, mockPortfolioDAO, mockOwnedStockDAO, mockTransactionDAO);
  }


  @Test void handleBuyStockRequest_SingleValidAffordableStock_ShouldUpdateBalance()
  {
    // Arrange
    // - Note @before each setup uses BuyStockService default contructor which read transactionfee from shared AppConfig
    Portfolio portfolio = new Portfolio(PORTFOLIO_ID, 5000);
    mockPortfolioDAO.setPortfolioToReturn(portfolio);

    BuyStockRequest request = new BuyStockRequest("AAPL", 1, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(4899.95, portfolio.getCurrentBalance());
  }

  @Test void handleBuyStockRequest_SingleValidAffordableStock_ShouldCommit()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("AAPL", 1, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(1, mockUow.getCommitCount());
  }

  @Test void handleBuyStockRequest_ZeroQuantityValidStock_ShouldThrow()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("AAPL", 0, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleBuyStockRequest(request));
  }

  @Test void handleBuyStockRequest_NegativeQuantityValidStock_ShouldThrow()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("AAPL", -1, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleBuyStockRequest(request));
  }

  @Test void handleBuyStockRequest_ValidStockNotInPortfolio_ShouldCreate()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("AAPL", 1, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(1, mockOwnedStockDAO.getCreateCount());
  }

  @Test void handleBuyStockRequest_ValidStockNotInPortfolio_ShouldCreateWithCorrectShares()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("AAPL", 3, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(3, mockOwnedStockDAO.getLastCreated().getNumberOfShares());
  }

  @Test void handleBuyStockRequest_ValidStockExistsInPortfolio_ShouldUpdateObject()
  {
    // Arrange
    OwnedStock existing = new OwnedStock(PORTFOLIO_ID, "AAPL", 5);
    mockOwnedStockDAO.setOwnedStocksBySymbol(List.of(existing));

    BuyStockRequest request = new BuyStockRequest("AAPL", 1, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(1, mockOwnedStockDAO.getUpdateCount());
  }

  @Test void handleBuyStockRequest_ValidStockExistsInPortfolio_ShouldIncrementShares()
  {
    // Arrange
    OwnedStock existing = new OwnedStock(PORTFOLIO_ID, "AAPL", 5);
    mockOwnedStockDAO.setOwnedStocksBySymbol(List.of(existing));

    BuyStockRequest request = new BuyStockRequest("AAPL", 3, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(8, existing.getNumberOfShares());
  }

  @Test void handleBuyStockRequest_LargeQuantityValidAffordableStock_ShouldUpdateBalance()
  {
    // Arrange
    Portfolio portfolio = new Portfolio(PORTFOLIO_ID, 500_000_000);
    mockPortfolioDAO.setPortfolioToReturn(portfolio);

    BuyStockRequest request = new BuyStockRequest("AAPL", 1_000_000, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(399_999_999.95, portfolio.getCurrentBalance());
  }

  @Test void handleBuyStockRequest_LargeQuantityValidAffordableStock_ShouldIncrementShares()
  {
    // Arrange
    Portfolio portfolio = new Portfolio(PORTFOLIO_ID, 500_000_000);
    mockPortfolioDAO.setPortfolioToReturn(portfolio);

    BuyStockRequest request = new BuyStockRequest("AAPL", 1_000_000, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(1_000_000, mockOwnedStockDAO.getLastCreated().getNumberOfShares());
  }

  @Test void handleBuyStockRequest_PortfolioBalanceEqualsCost_ShouldSucceed()
  {
    // Arrange
    Portfolio portfolio = new Portfolio(PORTFOLIO_ID, 1000.05);
    mockPortfolioDAO.setPortfolioToReturn(portfolio);

    BuyStockRequest request = new BuyStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(0, portfolio.getCurrentBalance());
  }

  @Test void handleBuyStockRequest_PortfolioBalanceOffByOneCent_ShouldThrow()
  {
    // Arrange
    Portfolio portfolio = new Portfolio(PORTFOLIO_ID, 1000.04);
    mockPortfolioDAO.setPortfolioToReturn(portfolio);

    BuyStockRequest request = new BuyStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleBuyStockRequest(request));
  }

  @Test void handleBuyStockRequest_NoTransactionFee_ShouldUpdateBalance()
  {
    // Arrange
    service = new BuyStockService(
        mockUow,
        mockStockDAO,
        mockPortfolioDAO,
        mockOwnedStockDAO,
        mockTransactionDAO,
        0);

    Portfolio portfolio = new Portfolio(PORTFOLIO_ID, 5000);
    mockPortfolioDAO.setPortfolioToReturn(portfolio);

    BuyStockRequest request = new BuyStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(4000, portfolio.getCurrentBalance());
  }

  @Test void handleBuyStockRequest_BankruptStock_ShouldThrow()
  {
    // Arrange
    mockStockDAO.setStockToReturn(new Stock("AAPL", "Apple", 100.0, StockState.BANKRUPT));
    BuyStockRequest request = new BuyStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleBuyStockRequest(request));
  }

  @Test void handleBuyStockRequest_StockSymbolIsNull_ShouldThrow()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest(null, 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleBuyStockRequest(request));
  }

  @Test void handleBuyStockRequest_StockSymbolIsEmpty_ShouldThrow()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("", 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleBuyStockRequest(request));
  }

  @Test void handleBuyStockRequest_StockSymbolIsUnknown_ShouldThrow()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("NVDA", 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleBuyStockRequest(request));
  }

  @Test void handleBuyStockRequest_ValidPurchase_ShouldHaveCorrectTransactionType()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act
    service.handleBuyStockRequest(request);

    // Assert
    assertEquals(TransactionType.BUY, mockTransactionDAO.getLastCreated().getType());
  }

  // Omitting Timestamp format testing, my service uses LocalDateTime, formatting happens in persistence layer.
  @Test void handleBuyStockRequest_ValidPurchase_ShouldHaveRecentTransactionTimestamp()
  {
    // Arrange
    BuyStockRequest request = new BuyStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act
    LocalDateTime before = LocalDateTime.now();
    service.handleBuyStockRequest(request);
    LocalDateTime after = LocalDateTime.now();

    // Assert
    LocalDateTime timestamp = mockTransactionDAO.getLastCreated().getTimestamp();
    assertNotNull(timestamp);
    assertFalse(timestamp.isBefore(before));
    assertFalse(timestamp.isAfter(after));
  }
}
