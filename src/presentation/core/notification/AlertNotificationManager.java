package presentation.core.notification;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertNotificationManager implements NotificationManager
{
  @Override
  public void notify(String message, NotificationType type)
  {
    Alert.AlertType alertType = switch (type)
    {
      case SUCCESS -> Alert.AlertType.CONFIRMATION;
      case INFO    -> Alert.AlertType.INFORMATION;
      case WARNING -> Alert.AlertType.WARNING;
      case ERROR   -> Alert.AlertType.ERROR;
    };

    // Ensure we're on the JavaFX Application Thread
    if (Platform.isFxApplicationThread())
    {
      showAlert(alertType, message);
    }
    else
    {
      Platform.runLater(() -> showAlert(alertType, message));
    }
  }

  private void showAlert(Alert.AlertType alertType, String message)
  {
    Alert alert = new Alert(alertType);
    alert.setTitle("Short Big - Stock Game");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
