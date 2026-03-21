package business.services;

import business.events.SellStockRequest;
import entities.*;
import mocks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SellStockServiceTest
{
  private static final UUID PORTFOLIO_ID = UUID.fromString("a75948fc-82ab-4590-8509-b4eab7189b35");

  // Fields — visible to all test methods
  private MockUnitOfWork mockUow;
  private MockStockDAO mockStockDAO;
  private MockPortfolioDAO mockPortfolioDAO;
  private MockOwnedStockDAO mockOwnedStockDAO;
  private MockTransactionDAO mockTransactionDAO;
  private SellStockService service;

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

    // Sell setup: User owns 10 shares by default
    OwnedStock ownedStock = new OwnedStock(PORTFOLIO_ID, "AAPL", 10);
    mockOwnedStockDAO.setShouldReturnOwnedStock(ownedStock);
    mockOwnedStockDAO.setOwnedStocksBySymbol(List.of(ownedStock));

    service = new SellStockService(mockUow, mockStockDAO, mockPortfolioDAO, mockOwnedStockDAO, mockTransactionDAO);
  }

  @Test void handleSellStockRequest_SingleValidOwnedStock_ShouldUpdateBalance()
  {
    // Arrange
    Portfolio portfolio = mockPortfolioDAO.getPortfolioToReturn();

    SellStockRequest request = new SellStockRequest("AAPL", 1, PORTFOLIO_ID);

    // Act
    service.handleSellStockRequest(request);

    // Assert
    assertEquals(5099.95, portfolio.getCurrentBalance(), 0.001);
  }

  @Test void handleSellStockRequest_ZeroQuantityValidStock_ShouldThrow()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", 0, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_SellAllShares_ShouldDeleteOwnedStock()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act
    service.handleSellStockRequest(request);

    // Assert
    assertEquals(1, mockOwnedStockDAO.getDeleteCount());
  }

  @Test void handleSellStockRequest_SellPartialShares_ShouldDecrementShares()
  {
    // Arrange
    OwnedStock ownedStock = mockOwnedStockDAO.getShouldReturnOwnedStock();
    SellStockRequest request = new SellStockRequest("AAPL", 5, PORTFOLIO_ID);

    // Act
    service.handleSellStockRequest(request);

    // Assert
    assertEquals(5, ownedStock.getNumberOfShares());
  }

  @Test void handleSellStockRequest_SellPartialShares_ShouldUpdateObject()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", 5, PORTFOLIO_ID);

    // Act
    service.handleSellStockRequest(request);

    // Assert
    assertEquals(1, mockOwnedStockDAO.getUpdateCount());
  }

  @Test void handleSellStockRequest_SellOneMoreThanOwned_ShouldThrow()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", 11, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_LargeQuantityValidOwnedStock_ShouldUpdateBalance()
  {
    // Arrange
    Portfolio portfolio = mockPortfolioDAO.getPortfolioToReturn();
    OwnedStock ownedStock = new OwnedStock(PORTFOLIO_ID, "AAPL", 1_000_000);
    mockOwnedStockDAO.setShouldReturnOwnedStock(ownedStock);
    mockOwnedStockDAO.setOwnedStocksBySymbol(List.of(ownedStock));

    SellStockRequest request = new SellStockRequest("AAPL", 1_000_000, PORTFOLIO_ID);

    // Act
    service.handleSellStockRequest(request);

    // Assert
    assertEquals(100_004_999.95, portfolio.getCurrentBalance(), 0.001);
  }

  @Test void handleSellStockRequest_NegativeQuantityValidStock_ShouldThrow()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", -5, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_BankruptStock_ShouldThrow()
  {
    // Arrange
    mockStockDAO.setStockToReturn(new Stock("AAPL", "Apple", 100.0, StockState.BANKRUPT));
    SellStockRequest request = new SellStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_StockSymbolIsNull_ShouldThrow()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest(null, 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_StockSymbolIsEmpty_ShouldThrow()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("", 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_StockSymbolIsUnknown_ShouldThrow()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("NVDA", 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_StockNotInPortfolio_ShouldThrow()
  {
    // Arrange
    mockStockDAO.setStockToReturn(new Stock("MSFT", "Microsoft", 100.0, StockState.STEADY));
    mockOwnedStockDAO.setOwnedStocksBySymbol(List.of()); // User doesn't own MSFT
    SellStockRequest request = new SellStockRequest("MSFT", 10, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_ValidSale_ShouldHaveCorrectTransactionType()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act
    service.handleSellStockRequest(request);

    // Assert
    assertEquals(TransactionType.SELL, mockTransactionDAO.getLastCreated().getType());
  }

  // Omitting Timestamp format testing, my service uses LocalDateTime, formatting happens in persistence layer.
  @Test void handleSellStockRequest_ValidSale_ShouldHaveRecentTransactionTimestamp()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act
    LocalDateTime before = LocalDateTime.now();
    service.handleSellStockRequest(request);
    LocalDateTime after = LocalDateTime.now();

    // Assert
    LocalDateTime timestamp = mockTransactionDAO.getLastCreated().getTimestamp();
    assertNotNull(timestamp);
    assertFalse(timestamp.isBefore(before));
    assertFalse(timestamp.isAfter(after));
  }

  @Test void handleSellStockRequest_ValidSale_ShouldCommit()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", 10, PORTFOLIO_ID);

    // Act
    service.handleSellStockRequest(request);

    // Assert
    assertEquals(1, mockUow.getCommitCount());
  }

  @Test void handleSellStockRequest_PortfolioNotFound_ShouldThrow()
  {
    // Arrange
    mockPortfolioDAO.setPortfolioToReturn(null);
    SellStockRequest request = new SellStockRequest("AAPL", 1, PORTFOLIO_ID);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.handleSellStockRequest(request));
  }

  @Test void handleSellStockRequest_InValidSale_ShouldRollback()
  {
    // Arrange
    SellStockRequest request = new SellStockRequest("AAPL", 11, PORTFOLIO_ID);

    // Act & Assert
    try { service.handleSellStockRequest(request); } catch (IllegalArgumentException ignored) {}
    assertEquals(1, mockUow.getRollbackCount());
  }
}