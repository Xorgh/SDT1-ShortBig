package presentation.views.portfolio;

import entities.StockState;

public record PortfolioHoldingRow(String symbol, int shares, double totalValue, StockState state) {}
