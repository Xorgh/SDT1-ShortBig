package presentation.views.portfolio;

import business.services.queries.PortfolioQueryService;
import business.services.queries.StockQueryService;
import business.services.queries.TransactionQueryService;
import dtos.BalanceHistoryDTO;
import dtos.PortfolioSummaryDTO;
import dtos.StockDTO;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PortfolioViewModel
{
  private final PortfolioQueryService portfolioQueryService;
  private final TransactionQueryService transactionQueryService;
  private final StockQueryService stockQueryService;

  private final ObservableList<PortfolioHoldingRow> holdings = FXCollections.observableArrayList();
  private final XYChart.Series<Number, Number> balanceSeries = new XYChart.Series<>();

  private List<BalanceHistoryDTO> currentDataPoints = List.of();
  private NumberAxis yAxis;

  private static final DateTimeFormatter TICK_FORMAT = DateTimeFormatter.ofPattern("dd MMM HH:mm");
  private static final double Y_AXIS_PADDING_FACTOR = 0.20;

  public PortfolioViewModel(PortfolioQueryService portfolioQueryService,
      TransactionQueryService transactionQueryService,
      StockQueryService stockQueryService)
  {
    this.portfolioQueryService = portfolioQueryService;
    this.transactionQueryService = transactionQueryService;
    this.stockQueryService = stockQueryService;
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
    if (portfolioId == null)
    {
      portfolioId = resolveDefaultPortfolioId();
      if (portfolioId == null) return;
    }

    PortfolioSummaryDTO summary = portfolioQueryService.getPortfolioSummary(portfolioId);
    loadHoldings(summary);
    loadBalanceChart(portfolioId);
  }


  private UUID resolveDefaultPortfolioId()
  {
    return portfolioQueryService.getDefaultPortfolioId();
  }

  private void loadHoldings(PortfolioSummaryDTO summary)
  {
    Map<String, Double> priceBySymbol = stockQueryService.getAllStocks().stream()
        .collect(Collectors.toMap(StockDTO::symbol, StockDTO::currentPrice));

    List<PortfolioHoldingRow> rows = summary.ownedStocks().stream()
        .map(os -> {
          double price = priceBySymbol.getOrDefault(os.getStockSymbol(), 0.0);
          return new PortfolioHoldingRow(
              os.getStockSymbol(),
              os.getNumberOfShares(),
              os.getNumberOfShares() * price);
        })
        .toList();

    holdings.setAll(rows);
  }


  private void loadBalanceChart(UUID portfolioId)
  {
    currentDataPoints = transactionQueryService.getBalanceHistory(
        portfolioId, AppConfig.INSTANCE.getStartingBalance());

    balanceSeries.setName("Balance");
    balanceSeries.getData().setAll(
        IntStream.range(0, currentDataPoints.size())
            .mapToObj(i -> new XYChart.Data<Number, Number>(
                i, currentDataPoints.get(i).balanceAfter()))
            .toList());

    centerYAxis();
  }


  private void centerYAxis()
  {
    if (yAxis == null || currentDataPoints.isEmpty()) return;

    double min = currentDataPoints.stream()
        .mapToDouble(BalanceHistoryDTO::balanceAfter).min().orElse(0);
    double max = currentDataPoints.stream()
        .mapToDouble(BalanceHistoryDTO::balanceAfter).max().orElse(0);
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
      @Override public String toString(Number value) { return String.valueOf(value.intValue()); }
      @Override public Number fromString(String s)   { return 0; }
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
        if (i < 0 || i >= currentDataPoints.size()) return "";
        return currentDataPoints.get(i).timestamp().format(TICK_FORMAT);
      }

      @Override public Number fromString(String s) { return 0; }
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
}