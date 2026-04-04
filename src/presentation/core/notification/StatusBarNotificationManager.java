package presentation.core.notification;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class StatusBarNotificationManager implements NotificationManager
{
  private final HBox statusBar;
  private final Label statusText;

  private static final Duration AUTO_CLEAR_DELAY = Duration.seconds(5);

  public StatusBarNotificationManager(HBox statusBar, Label statusText)
  {
    this.statusBar = statusBar;
    this.statusText = statusText;
  }

  @Override
  public void notify(String message, NotificationType type)
  {
    Runnable update = () ->
    {
      // Remove previous variant classes
      statusBar.getStyleClass().removeAll("info", "success", "warning", "error", "idle");

      // Map NotificationType → CSS class
      String cssClass = switch (type)
      {
        case INFO    -> "info";
        case WARNING -> "warning";
        case ERROR   -> "error";
      };
      statusBar.getStyleClass().add(cssClass);
      statusText.setText(message);

      // Auto-clear back to idle after delay
      PauseTransition pause = new PauseTransition(AUTO_CLEAR_DELAY);
      pause.setOnFinished(_ ->
      {
        statusBar.getStyleClass().removeAll("info", "success", "warning", "error");
        statusBar.getStyleClass().add("idle");
        statusText.setText("Klar");
      });
      pause.play();
    };

    if (Platform.isFxApplicationThread())
      update.run();
    else
      Platform.runLater(update);
  }
}