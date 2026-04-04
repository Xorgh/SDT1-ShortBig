package business.services;

import business.services.queries.PortfolioQueryService;
import dtos.PortfolioSummaryDTO;
import entities.OwnedStock;
import entities.Portfolio;
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
  private PortfolioQueryService service;

  @BeforeEach void setup()
  {
    mockPortfolioDAO = new MockPortfolioDAO();
    mockOwnedStockDAO = new MockOwnedStockDAO();

    // Default valid stock and portfolio for happy path tests
    mockPortfolioDAO.setPortfolioToReturn(new Portfolio(PORTFOLIO_ID, 5000));

    service = new PortfolioQueryService(mockPortfolioDAO, mockOwnedStockDAO);
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
        List.of(new OwnedStock(PORTFOLIO_ID, "AAPL", 10), new OwnedStock(PORTFOLIO_ID, "MSFT", 10),
            new OwnedStock(PORTFOLIO_ID, "NVDA", 10), new OwnedStock(UUID.randomUUID(), "AAPL", 99),
            new OwnedStock(UUID.randomUUID(), "AAPL", 99)));

    // Act
    PortfolioSummaryDTO result = service.getPortfolioSummary(PORTFOLIO_ID);

    // Assert
    assertEquals(3, result.ownedStocks().size());
  }

}
