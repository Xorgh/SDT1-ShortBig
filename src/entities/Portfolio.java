package entities;

import java.util.ArrayList;
import java.util.UUID;

public class Portfolio
{
  private final UUID id;
  private double currentBalance;
  private ArrayList<UUID> ownedStockIds = new ArrayList<>();
  private ArrayList<UUID> transactionIds = new ArrayList<>();

  public Portfolio()
  {
    id = UUID.randomUUID();
    currentBalance = 0;
  }

  public Portfolio(UUID id, double currentBalance, ArrayList<UUID> ownedStockIds, ArrayList<UUID> transactionIds)
  {
    this.id = id;
    this.currentBalance = currentBalance;
    this.ownedStockIds = ownedStockIds;
    this.transactionIds = transactionIds;
  }

  //  TODO Getters/Setters og andre metoder
}
