package presentation.core.notification;

import provided.CustomAlertBox;

public class CustomAlertBoxAdapter implements NotificationManager
{
  CustomAlertBox adaptee;

  public CustomAlertBoxAdapter(CustomAlertBox adaptee)
  {
    this.adaptee = adaptee;
  }

  @Override public void notify(String message, NotificationType type)
  {
    CustomAlertBox.AlertType alertType = switch (type)
    {
      case SUCCESS, INFO -> CustomAlertBox.AlertType.INFO;
      case WARNING -> CustomAlertBox.AlertType.WARNING;
      case ERROR -> CustomAlertBox.AlertType.ERROR;
    };

    String title = switch (type)
    {
      case SUCCESS -> "Success";
      case INFO -> "Info";
      case WARNING -> "Warning";
      case ERROR -> "Error";
    };

    adaptee.showAlert(message, title, alertType);
  }
}

