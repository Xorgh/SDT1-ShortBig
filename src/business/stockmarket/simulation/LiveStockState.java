package business.stockmarket.simulation;

interface LiveStockState
{
  double calculateNewPrice(double currentPrice);
  String getName();
}
