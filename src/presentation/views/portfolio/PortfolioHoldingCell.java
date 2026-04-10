package presentation.views.portfolio;

import entities.StockState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PortfolioHoldingCell extends ListCell<PortfolioHoldingRow>
{
  private final HBox  root     = new HBox(8);
  private final VBox  info     = new VBox(2);
  private final Label symbol   = new Label();
  private final Label shares   = new Label();
  private final Label value    = new Label();
  private final Label state    = new Label();
  private final HBox  valueBox = new HBox(8);

  public PortfolioHoldingCell()
  {
    symbol.getStyleClass().add("h3");
    shares.getStyleClass().add("text-muted");
    value.getStyleClass().add("h3");

    HBox.setHgrow(info, Priority.ALWAYS);

    valueBox.setMinWidth(160);
    valueBox.setMaxWidth(160);
    valueBox.setAlignment(Pos.CENTER_LEFT);
    valueBox.getChildren().addAll(value, state);

    info.getChildren().addAll(symbol, shares);
    root.getChildren().addAll(info, valueBox);
    root.setAlignment(Pos.CENTER_LEFT);
    root.setPadding(new Insets(8, 16, 8, 0));
  }

  @Override
  protected void updateItem(PortfolioHoldingRow row, boolean empty)
  {
    super.updateItem(row, empty);
    if (empty || row == null)
    {
      setGraphic(null);
      return;
    }

    symbol.setText(row.symbol());
    shares.setText(row.shares() + " shares");
    value.setText(String.format("$%.2f", row.totalValue()));

    state.getStyleClass().setAll("pill", pillClass(row.state()));
    state.setText(row.state().name());

    setGraphic(root);
  }

  private String pillClass(StockState s)
  {
    return switch (s) {
      case GROWING             -> "positive";
      case DECLINING, BANKRUPT -> "negative";
      default                  -> "primary";
    };
  }
}