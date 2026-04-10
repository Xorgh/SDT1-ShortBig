package presentation.views.stockmarket;

import business.events.StockPriceUpdateEvent;
import business.services.queries.StockPriceHistoryQueryService;
import business.services.queries.StockQueryService;
import business.services.queries.PriceHistoryRange;
import business.stockmarket.StockMarket;
import dtos.StockDTO;
import dtos.StockPriceHistoryDTO;
import entities.StockPriceHistory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

public class MarketViewModel
{
  private final ObservableList<StockDTO> stocks = FXCollections.observableArrayList();
  private final XYChart.Series<Number, Number> priceSeries = new XYChart.Series<>();
  private final ObjectProperty<StockDTO> selectedStock = new SimpleObjectProperty<>();
  private final ObjectProperty<PriceHistoryRange> selectedRange = new SimpleObjectProperty<>(
      PriceHistoryRange.ALL_TIME);

  private final StockQueryService stockQueryService;
  private final StockPriceHistoryQueryService historyQueryService;

  private List<StockPriceHistory> currentDataPoints = List.of();
  private NumberAxis yAxis;
  private Timeline autoRefreshTimer;

  private static final DateTimeFormatter TICK_FORMAT = DateTimeFormatter.ofPattern("dd MMM HH:mm");
  private static final double Y_AXIS_PADDING_FACTOR = 0.20;
  private static final double AUTO_REFRESH_SECONDS = 5.0;
  private final Runnable cacheInvalidator;

  private final java.util.function.Consumer<StockPriceUpdateEvent> priceListener;

  public MarketViewModel(StockQueryService stockQueryService, StockPriceHistoryQueryService historyQueryService,
      Runnable cacheInvalidator)
  {
    this.stockQueryService = stockQueryService;
    this.historyQueryService = historyQueryService;
    this.cacheInvalidator = cacheInvalidator;
    stocks.setAll(stockQueryService.getAllStocks());

    priceListener = event -> Platform.runLater(() -> {
      cacheInvalidator.run();
      stocks.setAll(stockQueryService.getAllStocks());
    });
    stockQueryService.addPriceChangeListener(priceListener);
  }

  public void startAutoRefresh()
  {
    if (autoRefreshTimer != null)
      return;

    autoRefreshTimer = new Timeline(new KeyFrame(Duration.seconds(AUTO_REFRESH_SECONDS), _ -> refreshAll()));
    autoRefreshTimer.setCycleCount(Timeline.INDEFINITE);
    autoRefreshTimer.play();
  }

  public void stopAutoRefresh()
  {
    if (autoRefreshTimer != null)
    {
      autoRefreshTimer.stop();
      autoRefreshTimer = null;
    }
    stockQueryService.removePriceChangeListener(priceListener);
  }

  private void refreshAll()
  {
    cacheInvalidator.run();  // clear stale file cache

    stocks.setAll(stockQueryService.getAllStocks());

    if (selectedStock.get() != null)
    {
      String selectedSymbol = selectedStock.get().symbol();
      stocks.stream().filter(s -> s.symbol().equals(selectedSymbol)).findFirst()
          .ifPresent(fresh -> selectedStock.set(fresh));

      refreshChart();
    }
  }

  public void setYAxis(NumberAxis yAxis)
  {
    this.yAxis = yAxis;
  }

  public void loadInitialStock()
  {
    if (!stocks.isEmpty())
    {
      selectedStock.set(stocks.getFirst());
      refreshChart();
    }
  }

  public void selectStock(StockDTO stock)
  {
    selectedStock.set(stock);
    refreshChart();
  }

  private void refreshChart()
  {
    StockPriceHistoryDTO dto = historyQueryService.getHistory(selectedStock.get().symbol(), selectedRange.get());

    currentDataPoints = dto.dataPoints();
    priceSeries.setName(dto.symbol());
    priceSeries.getData().setAll(IntStream.range(0, currentDataPoints.size())
        .mapToObj(i -> new XYChart.Data<Number, Number>(i, currentDataPoints.get(i).getPrice())).toList());

    if (currentDataPoints.isEmpty() && yAxis != null)
    {
      yAxis.setAutoRanging(true);
      return;
    }
    centerYAxis();
  }

  /**
   * Centers the Y axis around the data with symmetric padding.
   * Bounds and tick units snap to multiples of 5 — no decimals.
   */
  private void centerYAxis()
  {
    if (yAxis == null || currentDataPoints.isEmpty())
      return;

    double min = currentDataPoints.stream().mapToDouble(StockPriceHistory::getPrice).min().orElse(0);
    double max = currentDataPoints.stream().mapToDouble(StockPriceHistory::getPrice).max().orElse(0);
    double mean = (min + max) / 2.0;
    double halfRange = (max - min) / 2.0;

    // Ensure a minimum visible range so a flat line doesn't collapse the axis
    halfRange = Math.max(halfRange, mean * 0.05);

    double padding = halfRange * Y_AXIS_PADDING_FACTOR;
    double axisMin = mean - halfRange - padding;
    double axisMax = mean + halfRange + padding;

    // Don't show negative prices
    axisMin = Math.max(0, axisMin);

    // Snap bounds to nearest multiple of 5
    axisMin = Math.floor(axisMin / 5.0) * 5.0;
    axisMax = Math.ceil(axisMax / 5.0) * 5.0;

    yAxis.setAutoRanging(false);
    yAxis.setLowerBound(axisMin);
    yAxis.setUpperBound(axisMax);

    // Pick a tick unit that is a multiple of 5
    double visibleRange = axisMax - axisMin;
    yAxis.setTickUnit(niceTickUnit(visibleRange));

    // No decimals on Y axis labels
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

  /**
   * Picks a "nice" tick unit (5, 10, 20, 25, 50, 100, …) that is always
   * a multiple of 5, targeting roughly 5–10 ticks on the axis.
   */
  private double niceTickUnit(double range)
  {
    double rough = range / 8.0;
    // Snap up to nearest multiple of 5 (minimum 5)
    double unit = Math.max(5, Math.ceil(rough / 5.0) * 5.0);
    return unit;
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
        return currentDataPoints.get(i).getTimestamp().format(TICK_FORMAT);
      }

      @Override public Number fromString(String s)
      {
        return 0;
      }
    };
  }

  public ObservableList<StockDTO> getStocks()
  {
    return stocks;
  }

  public XYChart.Series<Number, Number> getPriceSeries()
  {
    return priceSeries;
  }

  public ObjectProperty<StockDTO> selectedStockProperty()
  {
    return selectedStock;
  }

  public ObjectProperty<PriceHistoryRange> selectedRangeProperty()
  {
    return selectedRange;
  }
}