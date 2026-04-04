package presentation.views.portfolio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PortfolioHoldingCell extends ListCell<PortfolioHoldingRow>
{
  private final HBox root     = new HBox(8);
  private final VBox info     = new VBox(2);
  private final Label symbol  = new Label();
  private final Label shares  = new Label();
  private final Label value   = new Label();

  public PortfolioHoldingCell()
  {
    symbol.getStyleClass().add("h3");
    shares.getStyleClass().add("text-muted");
    value.getStyleClass().add("h3");

    HBox.setHgrow(info, Priority.ALWAYS);

    info.getChildren().addAll(symbol, shares);
    root.getChildren().addAll(info, value);
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

    setGraphic(root);
  }
}