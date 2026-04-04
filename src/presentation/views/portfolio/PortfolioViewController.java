package presentation.views.portfolio;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import presentation.core.ArgumentReceiver;

import java.util.UUID;

public class PortfolioViewController implements ArgumentReceiver<UUID>
{
  @FXML private ListView<PortfolioHoldingRow> holdingsListView;
  @FXML private LineChart<Number, Number> balanceChart;
  @FXML private NumberAxis xAxis;
  @FXML private NumberAxis yAxis;
  @FXML private Label chartTitle;

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

    // Auto-load with default portfolio (no argument needed from navigation)
    viewModel.load(null);
  }

  @Override
  public void setArgument(UUID portfolioId)
  {
    viewModel.load(portfolioId);
  }
}