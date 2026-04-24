package presentation.views.portfolio;

import business.events.StockPriceUpdateEvent;
import business.requests.BuyStockRequest;
import business.requests.SellStockRequest;
import business.services.queries.PortfolioQueryService;
import business.services.queries.StockQueryService;
import business.services.queries.TransactionQueryService;
import business.services.requests.BuyStockService;
import business.services.requests.SellStockService;
import dtos.BalanceHistoryDTO;
import dtos.PortfolioSummaryDTO;
import dtos.StockDTO;
import entities.OwnedStock;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import shared.configuration.AppConfig;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PortfolioViewModel
{
  private final PortfolioQueryService portfolioQueryService;
  private final TransactionQueryService transactionQueryService;
  private final StockQueryService stockQueryService;
  private final BuyStockService buyStockService;
  private final SellStockService sellStockService;
  private final Consumer<StockPriceUpdateEvent> priceListener;

  private final ObservableList<PortfolioHoldingRow> holdings = FXCollections.observableArrayList();
  private final XYChart.Series<Number, Number> balanceSeries = new XYChart.Series<>();

  // Summary properties
  private final DoubleProperty cashBalance = new SimpleDoubleProperty();
  private final DoubleProperty holdingsValue = new SimpleDoubleProperty();

  // Combo box data
  private final ObservableList<String> allStockSymbols = FXCollections.observableArrayList();
  private final ObservableList<String> ownedStockSymbols = FXCollections.observableArrayList();

  private UUID currentPortfolioId;
  private List<BalanceHistoryDTO> currentDataPoints = List.of();
  private NumberAxis yAxis;

  private static final DateTimeFormatter TICK_FORMAT = DateTimeFormatter.ofPattern("dd MMM HH:mm");
  private static final double Y_AXIS_PADDING_FACTOR = 0.20;

  private final Runnable cacheInvalidator;

  public PortfolioViewModel(PortfolioQueryService portfolioQueryService,
      TransactionQueryService transactionQueryService, StockQueryService stockQueryService,
      BuyStockService buyStockService, SellStockService sellStockService, Runnable cacheInvalidator)
  {
    this.portfolioQueryService = portfolioQueryService;
    this.transactionQueryService = transactionQueryService;
    this.stockQueryService = stockQueryService;
    this.buyStockService = buyStockService;
    this.sellStockService = sellStockService;
    this.cacheInvalidator = cacheInvalidator;
    priceListener = _ -> Platform.runLater(() -> {
      if (currentPortfolioId != null)
        load(currentPortfolioId);
    });
    stockQueryService.addPriceChangeListener(priceListener);
  }

  public void setYAxis(NumberAxis yAxis)
  {
    this.yAxis = yAxis;
  }

  /**
   * Loads holdings list and balance chart for the given portfolio.
   * Resolves the first portfolio automatically when portfolioId is null.
   */
  public void load(UUID portfolioId)
  {
    cacheInvalidator.run();  // clear stale cache before reading

    if (portfolioId == null)
    {
      portfolioId = resolveDefaultPortfolioId();
      if (portfolioId == null)
        return;
    }
    this.currentPortfolioId = portfolioId;

    PortfolioSummaryDTO summary = portfolioQueryService.getPortfolioSummary(portfolioId);
    loadHoldings(summary);
    loadSummary(summary);
    loadBalanceChart(portfolioId);
    loadComboData(summary);
  }

  private void loadSummary(PortfolioSummaryDTO summary)
  {
    cashBalance.set(summary.balance());

    double totalVal = holdings.stream().mapToDouble(PortfolioHoldingRow::totalValue).sum();
    holdingsValue.set(totalVal);
  }

  private void loadComboData(PortfolioSummaryDTO summary)
  {
    allStockSymbols.setAll(stockQueryService.getAllStocks().stream().map(StockDTO::symbol).toList());

    ownedStockSymbols.setAll(summary.ownedStocks().stream().map(OwnedStock::getStockSymbol).toList());
  }

  public void buyStock(String symbol, int shares)
  {
    if (currentPortfolioId == null || symbol == null)
      return;
    buyStockService.handleBuyStockRequest(new BuyStockRequest(symbol, shares, currentPortfolioId));
    load(currentPortfolioId);   // refresh everything
  }

  public void sellStock(String symbol, int shares)
  {
    if (currentPortfolioId == null || symbol == null)
      return;
    sellStockService.handleSellStockRequest(new SellStockRequest(symbol, shares, currentPortfolioId));
    load(currentPortfolioId);   // refresh everything
  }

  // Getters for new properties
  public DoubleProperty cashBalanceProperty()
  {
    return cashBalance;
  }

  public DoubleProperty holdingsValueProperty()
  {
    return holdingsValue;
  }

  public ObservableList<String> getAllStockSymbols()
  {
    return allStockSymbols;
  }

  public ObservableList<String> getOwnedStockSymbols()
  {
    return ownedStockSymbols;
  }

  private UUID resolveDefaultPortfolioId()
  {
    return portfolioQueryService.getDefaultPortfolioId();
  }

  private void loadHoldings(PortfolioSummaryDTO summary)
  {
    Map<String, StockDTO> stockBySymbol = stockQueryService.getAllStocks().stream()
        .collect(Collectors.toMap(StockDTO::symbol, s -> s));

    List<PortfolioHoldingRow> rows = summary.ownedStocks().stream().map(os -> {
      StockDTO stock = stockBySymbol.get(os.getStockSymbol());
      double price = stock != null ? stock.currentPrice() : 0.0;
      entities.StockState state = stock != null ? stock.currentState() : entities.StockState.STEADY;
      return new PortfolioHoldingRow(os.getStockSymbol(), os.getNumberOfShares(), os.getNumberOfShares() * price,
          state);
    }).toList();

    holdings.setAll(rows);
  }

  private void loadBalanceChart(UUID portfolioId)
  {
    currentDataPoints = transactionQueryService.getBalanceHistory(portfolioId, AppConfig.INSTANCE.getStartingBalance());

    balanceSeries.setName("Balance");
    balanceSeries.getData().setAll(IntStream.range(0, currentDataPoints.size())
        .mapToObj(i -> new XYChart.Data<Number, Number>(i, currentDataPoints.get(i).balanceAfter())).toList());

    if (currentDataPoints.isEmpty() && yAxis != null)
    {
      yAxis.setAutoRanging(true);
      return;
    }
    centerYAxis();
  }

  private void centerYAxis()
  {
    if (yAxis == null || currentDataPoints.isEmpty())
      return;

    double min = currentDataPoints.stream().mapToDouble(BalanceHistoryDTO::balanceAfter).min().orElse(0);
    double max = currentDataPoints.stream().mapToDouble(BalanceHistoryDTO::balanceAfter).max().orElse(0);
    double mean = (min + max) / 2.0;
    double halfRange = (max - min) / 2.0;

    halfRange = Math.max(halfRange, Math.abs(mean) * 0.05);

    double padding = halfRange * Y_AXIS_PADDING_FACTOR;
    double axisMin = mean - halfRange - padding;
    double axisMax = mean + halfRange + padding;

    axisMin = Math.floor(axisMin / 5.0) * 5.0;
    axisMax = Math.ceil(axisMax / 5.0) * 5.0;

    yAxis.setAutoRanging(false);
    yAxis.setLowerBound(axisMin);
    yAxis.setUpperBound(axisMax);

    double visibleRange = axisMax - axisMin;
    yAxis.setTickUnit(niceTickUnit(visibleRange));

    yAxis.setTickLabelFormatter(new StringConverter<>()
    {
      @Override public String toString(Number value)
      {
        return String.valueOf(value.intValue());
      }

      @Override public Number fromString(String s)
      {
        return 0;
      }
    });
  }

  private double niceTickUnit(double range)
  {
    double rough = range / 8.0;
    return Math.max(5, Math.ceil(rough / 5.0) * 5.0);
  }

  public StringConverter<Number> getXAxisFormatter()
  {
    return new StringConverter<>()
    {
      @Override public String toString(Number index)
      {
        int i = index.intValue();
        if (i < 0 || i >= currentDataPoints.size())
          return "";
        return currentDataPoints.get(i).timestamp().format(TICK_FORMAT);
      }

      @Override public Number fromString(String s)
      {
        return 0;
      }
    };
  }

  public ObservableList<PortfolioHoldingRow> getHoldings()
  {
    return holdings;
  }

  public XYChart.Series<Number, Number> getBalanceSeries()
  {
    return balanceSeries;
  }

  public void dispose()
  {
    stockQueryService.removePriceChangeListener(priceListener);
  }
}