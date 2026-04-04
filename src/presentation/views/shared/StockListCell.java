package presentation.views.shared;

import dtos.StockDTO;
import entities.StockState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StockListCell extends ListCell<StockDTO>
{
  private final HBox   root     = new HBox(8);
  private final VBox   info     = new VBox(2);
  private final Label  symbol   = new Label();
  private final Label  name     = new Label();
  private final Label  price    = new Label();
  private final Label  state    = new Label();
  private final HBox   priceBox = new HBox(8);

  public StockListCell()
  {
    symbol.getStyleClass().add("h3");
    name.getStyleClass().add("text-muted");
    price.getStyleClass().add("h3");

    HBox.setHgrow(info, Priority.ALWAYS);

    priceBox.setMinWidth(160);
    priceBox.setMaxWidth(160);
    priceBox.setAlignment(Pos.CENTER_LEFT);
    priceBox.getChildren().addAll(price, state);

    info.getChildren().addAll(symbol, name);
    root.getChildren().addAll(info, priceBox);
    root.setAlignment(Pos.CENTER_LEFT);
    root.setPadding(new Insets(8, 16, 8, 0));
  }

  @Override
  protected void updateItem(StockDTO stock, boolean empty)
  {
    super.updateItem(stock, empty);
    if (empty || stock == null) { setGraphic(null); return; }

    symbol.setText(stock.symbol());
    name.setText(stock.name());
    price.setText(String.format("$%.2f", stock.currentPrice()));

    state.getStyleClass().setAll("pill", pillClass(stock.currentState()));
    state.setText(stock.currentState().name());

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