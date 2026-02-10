package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Transaction
{
  private final UUID id;
  private final UUID portfolioId;
  private final String stockSymbol;
  private final TransactionType type;
  private final int quantity;
  private final double pricePerShare;
  private final double totalAmount;
  private final double fee;
  private final LocalDateTime timestamp;

  public Transaction(UUID portfolioId, String stockSymbol, TransactionType type, int quantity,
      double pricePerShare, double totalAmount, double fee, LocalDateTime timestamp)
  {
    id = UUID.randomUUID();
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.type = type;
    this.quantity = quantity;
    this.pricePerShare = pricePerShare;
    this.totalAmount = totalAmount;
    this.fee = fee;
    this.timestamp = timestamp;
  }

  public Transaction(UUID id, UUID portfolioId, String stockSymbol, TransactionType type, int quantity,
      double pricePerShare, double totalAmount, double fee, LocalDateTime timestamp)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.type = type;
    this.quantity = quantity;
    this.pricePerShare = pricePerShare;
    this.totalAmount = totalAmount;
    this.fee = fee;
    this.timestamp = timestamp;
  }

  //  TODO Getters/Setters og andre metoder
}
