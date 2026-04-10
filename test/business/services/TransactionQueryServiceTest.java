package business.services;

import business.services.queries.TransactionQueryService;
import dtos.BalanceHistoryDTO;
import entities.Transaction;
import entities.TransactionType;
import mocks.MockTransactionDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionQueryServiceTest
{
  private static final UUID PORTFOLIO_ID = UUID.fromString("a75948fc-82ab-4590-8509-b4eab7189b35");

  private MockTransactionDAO mockTransactionDAO;
  private TransactionQueryService service;

  @BeforeEach void setup()
  {
    mockTransactionDAO = new MockTransactionDAO();
    service = new TransactionQueryService(mockTransactionDAO);
  }

  //  # getBalanceHistory:

  //  ## Zero & One
  @Test void getBalanceHistory_NoTransactions_ShouldReturnEmptyList()
  {
    // Arrange — defaults from setup are sufficient

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertTrue(results.isEmpty());
  }

  @Test void getBalanceHistory_SingleBuyTransaction_ShouldReturnOneEntry()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertEquals(1, results.size());
  }

  @Test void getBalanceHistory_SingleBuyTransaction_ShouldReturnNegativeBalance()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertEquals(-100.05, results.getFirst().balanceAfter(), 0.001);
  }

  @Test void getBalanceHistory_SingleSellTransaction_ShouldReturnPositiveBalance()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.SELL, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertEquals(99.95, results.getFirst().balanceAfter(), 0.001);
  }

  //  ## Many & Boundaries
  @Test void getBalanceHistory_MultipleBuyTransactions_ShouldAccumulateNegativeBalance()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100),
        new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100),
        new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertEquals(-300.15, results.getLast().balanceAfter(), 0.001);
  }

  @Test void getBalanceHistory_MultipleBuyTransactions_ShouldReturnCorrectCount()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100),
        new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100),
        new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertEquals(3, results.size());
  }

  @Test void getBalanceHistory_MixedBuyAndSell_ShouldCalculateCorrectRunningBalance()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100),
        new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.SELL, 1, 100),
        new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert — (-100.05) + 99.95 + (-100.05) = -100.15
    assertEquals(-100.15, results.getLast().balanceAfter(), 0.001);
  }

  @Test void getBalanceHistory_MultipleTransactions_ShouldBeSortedByTimestamp()
  {
    // Arrange — intentionally out of order
    mockTransactionDAO.setAllTransactions(List.of(
        new Transaction(UUID.randomUUID(), PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100, 100.05, 0.05,
            LocalDateTime.of(2026, 1, 1, 12, 0)),
        new Transaction(UUID.randomUUID(), PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100, 100.05, 0.05,
            LocalDateTime.of(2026, 1, 1, 10, 0)),
        new Transaction(UUID.randomUUID(), PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100, 100.05, 0.05,
            LocalDateTime.of(2026, 1, 1, 11, 0))));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert — first result should have the earliest timestamp
    assertTrue(results.get(0).timestamp().isBefore(results.get(1).timestamp()));
  }

  //  ## Interface & Exceptions
  @Test void getBalanceHistory_TransactionsFromOtherPortfolio_ShouldBeExcluded()
  {
    // Arrange
    UUID otherPortfolioId = UUID.randomUUID();
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100),
        new Transaction(otherPortfolioId, "AAPL", TransactionType.BUY, 1, 100),
        new Transaction(otherPortfolioId, "MSFT", TransactionType.SELL, 1, 50)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertEquals(1, results.size());
  }

  @Test void getBalanceHistory_NullPortfolioId_ShouldThrow()
  {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> service.getBalanceHistory(null, 0));
  }

  //  ## State & Behavior
  @Test void getBalanceHistory_SingleBuy_ShouldHaveCorrectTransactionType()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertEquals(TransactionType.BUY, results.getFirst().type());
  }

  @Test void getBalanceHistory_SingleBuy_ShouldHaveCorrectAmount()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 0);

    // Assert
    assertEquals(100.05, results.getFirst().amount(), 0.001);
  }

  @Test void getBalanceHistory_WithStartingBalance_ShouldOffsetAllResults()
  {
    // Arrange
    mockTransactionDAO.setAllTransactions(List.of(new Transaction(PORTFOLIO_ID, "AAPL", TransactionType.BUY, 1, 100)));

    // Act
    List<BalanceHistoryDTO> results = service.getBalanceHistory(PORTFOLIO_ID, 10000);

    // Assert — 10000 - 100.05 = 9899.95
    assertEquals(9899.95, results.getFirst().balanceAfter(), 0.001);
  }
}

