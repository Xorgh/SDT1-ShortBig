package presentation.views.mainmenu;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import presentation.core.ViewManager;
import presentation.core.notification.AlertNotificationManager;
import presentation.core.notification.NotificationManager;
import presentation.core.notification.StatusBarNotificationManager;

public class MainViewController
{
  @FXML private Label tabMarket;
  @FXML private Label tabPortfolio;
  @FXML private Label tabTransactions;
  @FXML private HBox  statusBar;
  @FXML private Label statusText;

  @FXML
  public void initialize()
  {
    // Status bar for non-blocking info/warning
    ViewManager.setNotificationManager(
        new StatusBarNotificationManager(statusBar, statusText));

    // Alert popups for critical breaking notifications
    ViewManager.setAlertNotificationManager(
        new AlertNotificationManager());

    Platform.runLater(() -> showView("/MarketView", tabMarket));
  }

  // ...existing handlers & helpers...
  @FXML
  public void handleShowMarket()    { showView("/MarketView",      tabMarket); }
  @FXML
  public void handleShowPortfolio() { showView("/PortfolioView",   tabPortfolio); }
  @FXML
  public void handleShowTransactions() { showView("/TransactionView", tabTransactions); }

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