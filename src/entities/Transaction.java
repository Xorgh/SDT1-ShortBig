package entities;

import shared.configuration.AppConfig;

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

//  new object
  public Transaction(UUID portfolioId, String stockSymbol, TransactionType type, int quantity,
      double pricePerShare)
  {
    id = UUID.randomUUID();
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.type = type;
    this.quantity = quantity;
    this.pricePerShare = pricePerShare;
    fee = AppConfig.INSTANCE.getTransactionFee();
    totalAmount = (quantity * pricePerShare) + fee;
    timestamp = LocalDateTime.now();
  }

  //  loaded object
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


  @Override public String toString()
  {
    return "Transaction{" + "id=" + id + ", portfolioId=" + portfolioId + ", stockSymbol='" + stockSymbol + '\''
        + ", type=" + type + ", quantity=" + quantity + ", pricePerShare=" + pricePerShare + ", totalAmount="
        + totalAmount + ", fee=" + fee + ", timestamp=" + timestamp + '}';
  }

  public UUID getId()
  {
    return id;
  }

  public UUID getPortfolioId()
  {
    return portfolioId;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public TransactionType getType()
  {
    return type;
  }

  public int getQuantity()
  {
    return quantity;
  }

  public double getPricePerShare()
  {
    return pricePerShare;
  }

  public double getTotalAmount()
  {
    return totalAmount;
  }

  public double getFee()
  {
    return fee;
  }

  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }
}
