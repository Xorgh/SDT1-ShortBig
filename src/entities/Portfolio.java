package entities;

import java.util.UUID;

public class Portfolio
{
  private final UUID id;
  private double currentBalance;

  public Portfolio()
  {
    id = UUID.randomUUID();
    currentBalance = 0;
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

  public void setCurrentBalance(double currentBalance)
  {
    this.currentBalance = currentBalance;
  }

  @Override public String toString()
  {
    return "Portfolio{" + "id=" + id + ", currentBalance=" + currentBalance + '}';
  }
}
