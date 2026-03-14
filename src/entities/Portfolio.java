package entities;

import java.util.UUID;

public class Portfolio
{
  private final UUID id;
  private double currentBalance;

  public Portfolio(double currentBalance)
  {
    id = UUID.randomUUID();
    this.currentBalance = currentBalance;
  }

  public Portfolio(UUID id, double currentBalance)
  {
    this.id = id;
    this.currentBalance = currentBalance;
  }

  public UUID getId()
  {
    return id;
  }

  public double getCurrentBalance()
  {
    return currentBalance;
  }

  public void setCurrentBalance(double newBalance)
  {
    this.currentBalance = newBalance;
  }

  @Override public String toString()
  {
    return "Portfolio{" + "id=" + id + ", currentBalance=" + currentBalance + '}';
  }
}
