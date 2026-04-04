package presentation.views.mainmenu;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import presentation.core.ViewManager;

public class MainViewController
{
  @FXML private Label tabMarket;
  @FXML private Label tabPortfolio;
  @FXML private Label tabTransactions;

  @FXML
  public void initialize()
  {
    // Defer until ViewManager.init() has finished setting mainLayout
    Platform.runLater(() -> showView("/MarketView", tabMarket));
  }

  @FXML
  public void handleShowMarket()    { showView("/MarketView",      tabMarket); }

  @FXML
  public void handleShowPortfolio() { showView("/PortfolioView",   tabPortfolio); }

  @FXML
  public void handleShowTransactions() { showView("/TransactionView", tabTransactions); }

  // ── helpers ──────────────────────────────────────────────
  private void showView(String viewName, Label activeTab)
  {
    ViewManager.showView(viewName, null);
    setActiveTab(activeTab);
  }

  private void setActiveTab(Label active)
  {
    tabMarket.getStyleClass().remove("active");
    tabPortfolio.getStyleClass().remove("active");
    tabTransactions.getStyleClass().remove("active");
    active.getStyleClass().add("active");
  }
}