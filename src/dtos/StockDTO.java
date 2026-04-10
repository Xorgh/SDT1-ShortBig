package dtos;

import entities.StockState;

public record StockDTO(String symbol, String name, double currentPrice, StockState currentState)
{
}