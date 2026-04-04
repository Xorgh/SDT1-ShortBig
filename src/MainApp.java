import javafx.application.Application;
import javafx.stage.Stage;
import presentation.core.AppContext;
import presentation.core.ViewManager;

public class MainApp extends Application
{
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    ViewManager.init(primaryStage, "/MainView", AppContext.getInstance());
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}