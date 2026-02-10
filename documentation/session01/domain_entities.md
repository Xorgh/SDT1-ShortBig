# Domain entities

- Portfolio
  - private final UUID id [PK]
  - private double currentBalance
  - private ArrayList<UUID> ownedStockIds [FK]
  - private ArrayList<UUID> transactionIds [FK]
  
- Transaction
  - private final UUID id [PK]
  - private final UUID portfolioId [FK]
  - private final String stockSymbol [FK]
  - private final TransactionType type
  - private final int quantity
  - private final double pricePerShare
  - private final double totalAmount
  - private final double fee
  - private final LocalDateTime timestamp

TransactionType <<enum>>
  - BUY
  - SELL
  
- OwnedStock
  - private final UUID id [PK]
  - private UUID portfolioId [FK]
  - private final String stockSymbol [FK]
  - private final int numberOfShares
  
- Stock
  - private final String symbol [PK]
  - private final String name
  - private double currentPrice
  - private StockState currentState

- StockState  <<enum>>
  
- StockPriceHistory
  - private final UUID id [PK]
  - private final String symbol [FK]
  - private final int price
  - private final LocalDateTime timestamp
