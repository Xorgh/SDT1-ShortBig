package presentation.views.mainmenu;

import business.services.GameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
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
  @FXML private HBox statusBar;
  @FXML private Label statusText;

  private final GameService gameService;
  private ContextMenu settingsMenu;
  private String currentView = "/MarketView";
  private Label currentTab;

  public MainViewController(GameService gameService)
  {
    this.gameService = gameService;
  }

  @FXML public void initialize()
  {
    ViewManager.setNotificationManager(new StatusBarNotificationManager(statusBar, statusText));
    ViewManager.setAlertNotificationManager(new AlertNotificationManager());

    buildSettingsMenu();
    currentTab = tabMarket;
    Platform.runLater(() -> showView("/MarketView", tabMarket));
  }

  private void buildSettingsMenu()
  {
    MenuItem startGame = new MenuItem("Start Game");
    MenuItem resetGame = new MenuItem("Reset Game");
    MenuItem loadTestData = new MenuItem("Load Test Data");
    MenuItem exitApp = new MenuItem("Exit");

    startGame.setOnAction(_ -> handleStartGame());
    resetGame.setOnAction(_ -> handleResetGame());
    loadTestData.setOnAction(_ -> handleLoadTestData());
    exitApp.setOnAction(_ -> handleExit());

    settingsMenu = new ContextMenu(startGame, resetGame, loadTestData, new SeparatorMenuItem(), exitApp);
  }

  @FXML public void handleShowSettings()
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
    gameService.loadGame();
    gameService.startTicker();
    showView(currentView, currentTab);
    ViewManager.getNotificationManager().notify("Game started", NotificationType.INFO);
  }


  private void handleResetGame()
  {
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
        "This will wipe all game data and start fresh. Are you sure?");
    confirm.setTitle("Reset Game");
    confirm.setHeaderText("Reset Game");
    confirm.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK)
      {
        gameService.stopTicker();
        gameService.resetGame();

        // Immediately reload the current view with fresh data
        showView(currentView, currentTab);

        ViewManager.getNotificationManager()
            .notify("Game data wiped. Press Start Game to begin.", NotificationType.INFO);
      }
    });
  }

  private void handleLoadTestData()
  {
    gameService.loadGame();
    gameService.startTicker();
    showView(currentView, currentTab);
    ViewManager.getNotificationManager().notify("Game loaded", NotificationType.INFO);
  }

  private void handleExit()
  {
    gameService.stopTicker();
    Platform.exit();
  }

  @FXML public void handleShowMarket()
  {
    showView("/MarketView", tabMarket);
  }

  @FXML public void handleShowPortfolio()
  {
    showView("/PortfolioView", tabPortfolio);
  }

  @FXML public void handleShowTransactions()
  {
    showView("/TransactionView", tabTransactions);
  }

  private void showView(String viewName, Label activeTab)
  {
    currentView = viewName;       // ← track it
    currentTab = activeTab;       // ← track it
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
