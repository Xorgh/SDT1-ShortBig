package presentation.views.stockmarket;

import dtos.StockDTO;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import presentation.core.ViewManager;
import presentation.core.notification.NotificationType;
import presentation.views.stockmarket.StockListCell;

public class MarketViewController
{
  @FXML private ListView<StockDTO> stockListView;
  @FXML private AreaChart<Number, Number> priceChart;
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

    xAxis.setTickLabelFormatter(viewModel.getXAxisFormatter());
    xAxis.setTickLabelRotation(-45);

    viewModel.setYAxis(yAxis);

    stockListView.getSelectionModel().selectedItemProperty().addListener((obs, old, stock) -> {
      if (stock != null)
      {
        viewModel.selectStock(stock);
        chartTitle.setText(stock.symbol() + " – " + stock.name());

        // TODO remove after verifying
        ViewManager.getNotificationManager()
            .notify("Selected " + stock.symbol() + " — $" + String.format("%.2f", stock.currentPrice()),
                NotificationType.INFO);
      }
    });

    viewModel.loadInitialStock();
    if (viewModel.selectedStockProperty().get() != null)
    {
      stockListView.getSelectionModel().selectFirst();
    }

    // Auto-refresh all cards every 5 seconds
    viewModel.startAutoRefresh();

    // Stop auto-refresh when this view is removed from the scene
    stockListView.sceneProperty().addListener((_, _, newScene) -> {
      if (newScene == null)
      {
        viewModel.stopAutoRefresh();
      }
    });
  }
}
