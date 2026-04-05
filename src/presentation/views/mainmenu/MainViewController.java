package presentation.views.mainmenu;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import presentation.core.ViewManager;
import presentation.core.notification.AlertNotificationManager;
import presentation.core.notification.NotificationType;
import presentation.core.notification.StatusBarNotificationManager;

public class MainViewController
{
  @FXML private Label tabMarket;
  @FXML private Label tabPortfolio;
  @FXML private Label tabTransactions;
  @FXML private Label settingsButton;
  @FXML private HBox  statusBar;
  @FXML private Label statusText;

  private ContextMenu settingsMenu;

  @FXML
  public void initialize()
  {
    ViewManager.setNotificationManager(
        new StatusBarNotificationManager(statusBar, statusText));
    ViewManager.setAlertNotificationManager(
        new AlertNotificationManager());

    buildSettingsMenu();

    Platform.runLater(() -> showView("/MarketView", tabMarket));
  }

  private void buildSettingsMenu()
  {
    MenuItem startGame    = new MenuItem("Start Game");
    MenuItem resetGame    = new MenuItem("Reset Game");
    MenuItem loadTestData = new MenuItem("Load Test Data");
    MenuItem exitApp      = new MenuItem("Exit");

    startGame.setOnAction(_    -> handleStartGame());
    resetGame.setOnAction(_    -> handleResetGame());
    loadTestData.setOnAction(_ -> handleLoadTestData());
    exitApp.setOnAction(_      -> handleExit());

    settingsMenu = new ContextMenu(
        startGame,
        resetGame,
        loadTestData,
        new SeparatorMenuItem(),
        exitApp
    );
  }

  @FXML
  public void handleShowSettings()
  {
    if (settingsMenu.isShowing())
    {
      settingsMenu.hide();
    }
    else
    {
      settingsMenu.show(settingsButton, Side.LEFT, 0, 0);
    }
  }

  private void handleStartGame()
  {
    // TODO: wire to GameService.startNewGame()
    ViewManager.getNotificationManager()
        .notify("Starting new game...", NotificationType.INFO);
  }

  private void handleResetGame()
  {
    // TODO: wire to GameService.resetGame()
    ViewManager.getNotificationManager()
        .notify("Game reset", NotificationType.INFO);
  }

  private void handleLoadTestData()
  {
    // TODO: wire to GameService.loadGame()
    ViewManager.getNotificationManager()
        .notify("Test data loaded", NotificationType.INFO);
  }

  private void handleExit()
  {
    Platform.exit();
  }

  @FXML
  public void handleShowMarket()       { showView("/MarketView",      tabMarket); }
  @FXML
  public void handleShowPortfolio()    { showView("/PortfolioView",   tabPortfolio); }
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
