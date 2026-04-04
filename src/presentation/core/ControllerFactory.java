package presentation.core;

import javafx.util.Callback;
import presentation.views.mainmenu.MainViewController;
import presentation.views.portfolio.PortfolioViewController;
import presentation.views.stockmarket.MarketViewController;

public class ControllerFactory implements Callback<Class<?>, Object>
{
  private final AppContext context;

  public ControllerFactory(AppContext context)
  {
    this.context = context;
  }

  @Override public Object call(Class<?> controllerType)
  {
    if (controllerType == MainViewController.class)
    {
      return new MainViewController();
    }
    if (controllerType == MarketViewController.class)
    {
      return new MarketViewController();
    }
    if (controllerType == PortfolioViewController.class)
    {
      return new PortfolioViewController(context.getPortfolioViewModel());
    }
    throw new RuntimeException("Controller not support: " + controllerType.getSimpleName());
  }
}
