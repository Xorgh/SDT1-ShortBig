package business.services.queries;

import business.events.StockPriceUpdateEvent;
import business.stockmarket.StockMarket;
import dtos.StockDTO;
import persistence.interfaces.StockDAO;

import java.util.List;
import java.util.function.Consumer;

public class StockQueryService
{
  private final StockDAO stockDAO;

  public StockQueryService(StockDAO stockDAO)
  {
    this.stockDAO = stockDAO;
  }

  public List<StockDTO> getAllStocks()
  {
    return stockDAO.getAll().stream()
        .map(s -> new StockDTO(s.getSymbol(), s.getName(), s.getCurrentPrice(), s.getCurrentState()))
        .toList();
  }

  public void addPriceChangeListener(Consumer<StockPriceUpdateEvent> listener)
  {
    StockMarket.INSTANCE.onStockPriceChange.add(listener);
  }
}