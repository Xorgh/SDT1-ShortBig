package persistence.fileimplementation;

import entities.*;
import persistence.interfaces.UnitOfWork;
import shared.logging.LogLevel;
import shared.logging.Logger;

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
  private final Logger logger = Logger.getInstance();

  private List<OwnedStock> ownedStocks;
  private List<Portfolio> portfolios;
  private List<Stock> stocks;
  private List<StockPriceHistory> stockPriceHistoryList;
  private List<Transaction> transactions;

  public FileUnitOfWork(String directoryPath)
  {
    this.directoryPath = directoryPath;
    FileStorageInitializer.initializeFile(directoryPath);
  }

  private List<String[]> readAndParseLines(String filePath)
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
      logger.log(LogLevel.ERROR, e + " - Failed to read file. Filepath: " + filePath);
      return result; // Return empty list on error
    }
  }

  public List<OwnedStock> getOwnedStocks()
  {
    if (ownedStocks == null)
    {
      ownedStocks = loadOwnedStocks();
    }
    return ownedStocks;
  }

  private List<OwnedStock> loadOwnedStocks()
  {
    List<OwnedStock> result = new ArrayList<>();
    for (String[] parts : readAndParseLines(getOwnedStockFilePath()))
    {
      OwnedStock ownedStock = new OwnedStock(
          UUID.fromString(parts[0]),
          UUID.fromString(parts[1]),
          parts[2],
          Integer.parseInt(parts[3])
      );
      result.add(ownedStock);
    }
    return result;
  }

  private void saveOwnedStocks()
  {
    List<String> lines = new ArrayList<>();
    for (OwnedStock ownedStock : getOwnedStocks())
    {
      String line = String.join("|",
          ownedStock.getId().toString(),
          ownedStock.getPortfolioId().toString(),
          ownedStock.getStockSymbol(),
          String.valueOf(ownedStock.getNumberOfShares())
      );
      lines.add(line);
    }
    try
    {
      Files.write(Paths.get(getOwnedStockFilePath()), lines);
    }
    catch (IOException e)
    {
      logger.log(LogLevel.ERROR,e + " Failed to write owned stocks file");
    }
  }

  public List<Portfolio> getPortfolios()
  {
    {
      if (portfolios == null)
      {
        portfolios = loadPortfolios();
      }
      return portfolios;
    }
  }

  private List<Portfolio> loadPortfolios()
  {
    List<Portfolio> result = new ArrayList<>();
    for (String[] parts : readAndParseLines(getPortfolioFilePath()))
    {
      Portfolio portfolio = new Portfolio(
          UUID.fromString(parts[0]),
          Double.parseDouble(parts[1])
      );
      result.add(portfolio);
    }
    return result;
  }

  private void savePortfolios()
  {
    List<String> lines = new ArrayList<>();
    for (Portfolio portfolio : getPortfolios())
    {
      String line = String.join("|",
          portfolio.getId().toString(),
          String.valueOf(portfolio.getCurrentBalance())
      );
      lines.add(line);
    }
    try
    {
      Files.write(Paths.get(getPortfolioFilePath()), lines);
    }
    catch (IOException e)
    {
      logger.log(LogLevel.ERROR,e + " Failed to write portfolio file");
    }
  }

  public List<Stock> getStocks()
  {
    if (stocks == null)
    {
      stocks = loadStocks();
    }
    return stocks;
  }

  private List<Stock> loadStocks()
  {
    List<Stock> result = new ArrayList<>();
    for (String[] parts : readAndParseLines(getStockFilePath()))
    {
      Stock stock = new Stock(
          parts[0],
          parts[1],
          Double.parseDouble(parts[2]),
          StockState.valueOf(parts[3])
      );
      result.add(stock);
    }
    return result;
  }

  private void saveStocks()
  {
    List<String> lines = new ArrayList<>();
    for (Stock stock : getStocks())
    {
      String line = String.join("|",
          stock.getSymbol(),
          stock.getName(),
          String.valueOf(stock.getCurrentPrice()),
          stock.getCurrentState().name()
      );
      lines.add(line);
    }
    try
    {
      Files.write(Paths.get(getStockFilePath()), lines);
    }
    catch (IOException e)
    {
      logger.log(LogLevel.ERROR,e + " Failed to write stocks file");
    }
  }

  public List<StockPriceHistory> getStockPriceHistoryList()
  {
    {
      if (stockPriceHistoryList == null)
      {
        stockPriceHistoryList = loadStockPriceHistory();
      }
      return stockPriceHistoryList;
    }
  }

  private List<StockPriceHistory> loadStockPriceHistory()
  {
    List<StockPriceHistory> result = new ArrayList<>();
    for (String[] parts : readAndParseLines(getStockPriceHistoryFilePath()))
    {
      StockPriceHistory history = new StockPriceHistory(
          UUID.fromString(parts[0]),
          parts[1],
          Double.parseDouble(parts[2]),
          LocalDateTime.parse(parts[3])
      );
      result.add(history);
    }
    return result;
  }

  private void saveStockPriceHistory()
  {
    List<String> lines = new ArrayList<>();
    for (StockPriceHistory history : getStockPriceHistoryList())
    {
      String line = String.join("|",
          history.getId().toString(),
          history.getStockSymbol(),
          String.valueOf(history.getPrice()),
          history.getTimestamp().toString()
      );
      lines.add(line);
    }
    try
    {
      Files.write(Paths.get(getStockPriceHistoryFilePath()), lines);
    }
    catch (IOException e)
    {
      logger.log(LogLevel.ERROR,e + " Failed to write StockPriceHistory file");
    }
  }

  public List<Transaction> getTransactions()
  {
    {
      if (transactions == null)
      {
        transactions = loadTransactions();
      }
      return transactions;
    }
  }

  private List<Transaction> loadTransactions()
  {
    List<Transaction> result = new ArrayList<>();
    for (String[] parts : readAndParseLines(getTransactionFilePath()))
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

  private void saveTransaction()
  {
    List<String> lines = new ArrayList<>();
    for (Transaction transaction : getTransactions())
    {
      String line = String.join("|",
          transaction.getId().toString(),
          transaction.getPortfolioId().toString(),
          transaction.getStockSymbol(),
          transaction.getType().name(),
          String.valueOf(transaction.getQuantity()),
          String.valueOf(transaction.getPricePerShare()),
          String.valueOf(transaction.getTotalAmount()),
          String.valueOf(transaction.getFee()),
          transaction.getTimestamp().toString()
      );
      lines.add(line);
    }
    try
    {
      Files.write(Paths.get(getTransactionFilePath()), lines);
    }
    catch (IOException e)
    {
      logger.log(LogLevel.ERROR,e + " Failed to write transaction file");
    }
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
    if (ownedStocks != null) saveOwnedStocks();
    if (portfolios != null) savePortfolios();
    if (stocks != null) saveStocks();
    if (stockPriceHistoryList != null) saveStockPriceHistory();
    if (transactions != null) saveTransaction();
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
}
