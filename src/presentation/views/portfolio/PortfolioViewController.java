package presentation.views.portfolio;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import presentation.core.ArgumentReceiver;
import presentation.core.ViewManager;
import presentation.core.notification.NotificationType;

import java.util.UUID;

public class PortfolioViewController implements ArgumentReceiver<UUID>
{
  @FXML private ListView<PortfolioHoldingRow> holdingsListView;
  @FXML private AreaChart<Number, Number> balanceChart;
  @FXML private NumberAxis xAxis;
  @FXML private NumberAxis yAxis;
  @FXML private Label chartTitle;

  // Summary card
  @FXML private Label cashLabel;
  @FXML private Label holdingsValueLabel;
  @FXML private ComboBox<String> buyStockCombo;
  @FXML private ComboBox<String> sellStockCombo;
  @FXML private Spinner<Integer> buySpinner;
  @FXML private Spinner<Integer> sellSpinner;

  private final PortfolioViewModel viewModel;

  public PortfolioViewController(PortfolioViewModel viewModel)
  {
    this.viewModel = viewModel;
  }

  @FXML
  public void initialize()
  {
    holdingsListView.setCellFactory(_ -> new PortfolioHoldingCell());
    holdingsListView.setItems(viewModel.getHoldings());

    balanceChart.getData().add(viewModel.getBalanceSeries());
    xAxis.setTickLabelFormatter(viewModel.getXAxisFormatter());
    xAxis.setTickLabelRotation(-45);
    viewModel.setYAxis(yAxis);

    // Spinners: 1–999, default 1
    buySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
    sellSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));

    // Combo boxes
    buyStockCombo.setItems(viewModel.getAllStockSymbols());
    sellStockCombo.setItems(viewModel.getOwnedStockSymbols());

    // Bind summary labels
    viewModel.cashBalanceProperty().addListener((_, _, v) ->
        cashLabel.setText(String.format("$%.2f", v.doubleValue())));
    viewModel.holdingsValueProperty().addListener((_, _, v) ->
        holdingsValueLabel.setText(String.format("$%.2f", v.doubleValue())));

    viewModel.load(null);

    // Dispose of the listener when this view is removed from the scene
    holdingsListView.sceneProperty().addListener((_, _, newScene) -> {
      if (newScene == null)
      {
        viewModel.dispose();
      }
    });
  }

  @FXML
  private void onBuy()
  {
    String symbol = buyStockCombo.getValue();
    if (symbol == null) return;
    try
    {
      viewModel.buyStock(symbol, buySpinner.getValue());
      ViewManager.getNotificationManager()
          .notify("Bought " + buySpinner.getValue() + " " + symbol, NotificationType.SUCCESS);
    }
    catch (Exception e)
    {
      ViewManager.getNotificationManager()
          .notify(e.getMessage(), NotificationType.ERROR);
    }
  }

  @FXML
  private void onSell()
  {
    String symbol = sellStockCombo.getValue();
    if (symbol == null) return;
    try
    {
      viewModel.sellStock(symbol, sellSpinner.getValue());
      ViewManager.getNotificationManager()
          .notify("Sold " + sellSpinner.getValue() + " " + symbol, NotificationType.SUCCESS);
    }
    catch (Exception e)
    {
      ViewManager.getNotificationManager()
          .notify(e.getMessage(), NotificationType.ERROR);
    }
  }

  @Override
  public void setArgument(UUID portfolioId)
  {
    viewModel.load(portfolioId);
  }
}