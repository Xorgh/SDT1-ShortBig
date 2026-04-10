package presentation.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import presentation.core.notification.NotificationManager;

public class ViewManager
{
  private static final String FXML_DIRECTORY_PATH = "/fxml";

  private static BorderPane mainLayout;
  private static ControllerFactory controllerFactory;
  private static NotificationManager statusBarNotificationManager;
  private static NotificationManager alertNotificationManager;

  public ViewManager()
  {
  }

  public static void setNotificationManager(NotificationManager manager)
  {
    statusBarNotificationManager = manager;
  }

  public static NotificationManager getNotificationManager()
  {
    return statusBarNotificationManager;
  }

  public static void setAlertNotificationManager(NotificationManager manager)
  {
    alertNotificationManager = manager;
  }

  public static NotificationManager getAlertNotificationManager()
  {
    return alertNotificationManager;
  }

  public static void init(Stage primaryStage, String initialView, AppContext context) throws Exception
  {
    controllerFactory = new ControllerFactory(context);
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(ViewManager.class.getResource(FXML_DIRECTORY_PATH + initialView + ".fxml"));
    loader.setControllerFactory(controllerFactory);
    BorderPane root = loader.load();
    mainLayout = root;
    Scene scene = new Scene(root, 1920, 1080);
    primaryStage.setTitle("Short Big - Stock Game");
    primaryStage.setScene(scene);

    // Lock window to fixed 1920x1080
    primaryStage.setResizable(false);   // ← prevents drag-resize
    primaryStage.setMinWidth(1920);
    primaryStage.setMaxWidth(1920);
    primaryStage.setMinHeight(1080);
    primaryStage.setMaxHeight(1080);

    primaryStage.show();
  }

  public static void showView(String viewName, Object argument)
  {
    try
    {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(ViewManager.class.getResource(FXML_DIRECTORY_PATH + viewName + ".fxml"));
      loader.setControllerFactory(controllerFactory);
      Parent root = loader.load();
      Object controller = loader.getController();
      passArgumentIfSupported(controller, argument);
      mainLayout.setCenter(root);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      Alert error = new Alert(Alert.AlertType.ERROR, "Cannot load view: " + viewName + "\n" + e.getMessage());
      error.show();
    }
  }

  @SuppressWarnings("unchecked") private static void passArgumentIfSupported(Object controller, Object argument)
  {
    if (argument == null)
    {
      return;
    }
    if (controller instanceof ArgumentReceiver<?> receiver)
    {
      ArgumentReceiver<Object> typedReceiver = (ArgumentReceiver<Object>) receiver;
      typedReceiver.setArgument(argument);
    }
  }

}
