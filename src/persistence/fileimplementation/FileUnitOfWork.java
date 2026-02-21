package persistence.fileimplementation;

import entities.*;
import persistence.interfaces.UnitOfWork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUnitOfWork implements UnitOfWork
{
  private final String directoryPath;

  private List<OwnedStock> ownedStocks;
  private List<Portfolio> portfolios;
  private List<Stock> stocks;
  private List<StockPriceHistory> stockPriceHistoryList;
  private List<Transaction> transactions;

  public FileUnitOfWork(String directoryPath)
  {
    this.directoryPath = directoryPath;
    FileStorageInitializer.ensureFilesExist(directoryPath);
  }

  private List<String[]> readAndParseLinesFromFile(String filePath)
  {
    List<String[]> result = new ArrayList<>();
    try
    {
      List<String> lines = Files.readAllLines(Paths.get(filePath));
      for (String line : lines)
      {
        if (line.isBlank())
          continue;
        result.add(line.split("\\|"));
      }
      return result;
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed to read file: " + filePath, e);
    }
  }

  public List<OwnedStock> getOwnedStocks()
  {
    if (ownedStocks == null)
    {
      ownedStocks = loadOwnedStocksFromFile();
    }
    return ownedStocks;
  }

  private List<OwnedStock> loadOwnedStocksFromFile()
  {
    List<OwnedStock> result = new ArrayList<>();
    for (String[] parts : readAndParseLinesFromFile(getOwnedStockFilePath()))
    {
      OwnedStock ownedStock = new OwnedStock(UUID.fromString(parts[0]), UUID.fromString(parts[1]), parts[2],
          Integer.parseInt(parts[3]));
      result.add(ownedStock);
    }
    return result;
  }

  public List<Portfolio> getPortfolios()
  {
    {
      if (portfolios == null)
      {
        portfolios = loadPortfoliosFromFile();
      }
      return portfolios;
    }
  }

  private List<Portfolio> loadPortfoliosFromFile()
  {
    List<Portfolio> result = new ArrayList<>();
    for (String[] parts : readAndParseLinesFromFile(getPortfolioFilePath()))
    {
      Portfolio portfolio = new Portfolio(UUID.fromString(parts[0]), Double.parseDouble(parts[1]));
      result.add(portfolio);
    }
    return result;
  }

  public List<Stock> getStocks()
  {
    if (stocks == null)
    {
      stocks = loadStocksFromFile();
    }
    return stocks;
  }

  private List<Stock> loadStocksFromFile()
  {
    List<Stock> result = new ArrayList<>();
    for (String[] parts : readAndParseLinesFromFile(getStockFilePath()))
    {
      Stock stock = new Stock(parts[0], parts[1], Double.parseDouble(parts[2]), StockState.valueOf(parts[3]));
      result.add(stock);
    }
    return result;
  }

  public List<StockPriceHistory> getStockPriceHistoryList()
  {
    {
      if (stockPriceHistoryList == null)
      {
        stockPriceHistoryList = loadStockPriceHistoryFromFile();
      }
      return stockPriceHistoryList;
    }
  }

  private List<StockPriceHistory> loadStockPriceHistoryFromFile()
  {
    List<StockPriceHistory> result = new ArrayList<>();
    for (String[] parts : readAndParseLinesFromFile(getStockPriceHistoryFilePath()))
    {
      StockPriceHistory history = new StockPriceHistory(UUID.fromString(parts[0]), parts[1], Double.parseDouble(parts[2]),
          LocalDateTime.parse(parts[3]));
      result.add(history);
    }
    return result;
  }

  public List<Transaction> getTransactions()
  {
    {
      if (transactions == null)
      {
        transactions = loadTransactionsFromFile();
      }
      return transactions;
    }
  }

  private List<Transaction> loadTransactionsFromFile()
  {
    List<Transaction> result = new ArrayList<>();
    for (String[] parts : readAndParseLinesFromFile(getTransactionFilePath()))
    {
      Transaction transaction = new Transaction(
          UUID.fromString(parts[0]),
          UUID.fromString(parts[1]),
          parts[2],
          TransactionType.valueOf(parts[3]),
          Integer.parseInt(parts[4]),
          Double.parseDouble(parts[5]),
          Double.parseDouble(parts[6]),
          Double.parseDouble(parts[7]),
          LocalDateTime.parse(parts[8])
      );
      result.add(transaction);
    }
    return result;
  }

  private String getOwnedStockFilePath()
  {
    return directoryPath + "ownedstocks.txt";
  }

  private String getPortfolioFilePath()
  {
    return directoryPath + "portfolio.txt";
  }

  private String getStockFilePath()
  {
    return directoryPath + "stocks.txt";
  }

  private String getStockPriceHistoryFilePath()
  {
    return directoryPath + "stockpricehistories.txt";
  }

  private String getTransactionFilePath()
  {
    return directoryPath + "transactions.txt";
  }

  @Override public void begin()
  {
    resetLists();
  }

  @Override public void commit()
  {

    resetLists();
  }

  @Override public void rollback()
  {
    resetLists();
  }

  private void resetLists()
  {
    ownedStocks = null;
    portfolios = null;
    stockPriceHistoryList = null;
    stocks = null;
    transactions = null;
  }


//  TODO implement save to files.
}
