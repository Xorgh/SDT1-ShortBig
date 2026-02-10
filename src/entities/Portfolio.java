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

  //  TODO Getters/Setters og andre metoder
  //  TODO How to load data
}
