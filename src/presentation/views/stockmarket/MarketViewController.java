package presentation.views.stockmarket;

import dtos.StockDTO;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import presentation.core.ViewManager;
import presentation.core.notification.NotificationType;
import presentation.views.shared.StockListCell;

public class MarketViewController
{
  @FXML private ListView<StockDTO> stockListView;
  @FXML private LineChart<Number, Number> priceChart;
  @FXML private NumberAxis xAxis;
  @FXML private NumberAxis yAxis;
  @FXML private Label chartTitle;

  private final MarketViewModel viewModel;

  public MarketViewController(MarketViewModel viewModel)
  {
    this.viewModel = viewModel;
  }

  @FXML public void initialize()
  {
    stockListView.setCellFactory(_ -> new StockListCell());
    stockListView.setItems(viewModel.getStocks());

    priceChart.getData().add(viewModel.getPriceSeries());

    // Format X axis ticks as timestamps from the data points
    xAxis.setTickLabelFormatter(viewModel.getXAxisFormatter());
    xAxis.setTickLabelRotation(-45);

    // Let the ViewModel control Y axis bounds for centering
    viewModel.setYAxis(yAxis);

    stockListView.getSelectionModel().selectedItemProperty().addListener((obs, old, stock) -> {
      if (stock != null)
      {
        viewModel.selectStock(stock);
        chartTitle.setText(stock.symbol() + " – " + stock.name());

        // Test notification
        // TODO remove after verifying
        ViewManager.getNotificationManager()
            .notify("Selected " + stock.symbol() + " — $" + String.format("%.2f", stock.currentPrice()),
                NotificationType.INFO);

        // Critical popup test —
        // TODO remove after verifying
//        ViewManager.getAlertNotificationManager()
//            .notify("⚠ CRITICAL: " + stock.symbol() + " selected at $" + String.format("%.2f", stock.currentPrice()),
//                NotificationType.ERROR);
      }
    });

    viewModel.loadInitialStock();
    if (viewModel.selectedStockProperty().get() != null)
    {
      stockListView.getSelectionModel().selectFirst();
    }
  }
}