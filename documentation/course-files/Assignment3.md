# Introduction to Assignment 3 - Persistence

This time we will solve the following problem:

- Adding file persistence to the application
- Data Access Object pattern
- Unit of Work pattern


I recommend you read through the entire assignment before you start, just so you know what you are getting into.

## Assignment 3 deliverables

- Data Access Object pattern implementation
- Unit of Work pattern implementation
- Updated class diagram

## Deadline

See itslearning.

## Handing in

On itslearning, you will just submit a link to the sub-folder containing your code.

---

# Updating the project structure

I recommend creating a new package called "persistence". This implies that stuff in this package is related to persistence. You might further create sub-packages for "interfaces" and "fileimplementation".

---

# Unit of Work pattern

Given that the Data Access Objects will depend on the Unit of Work implementation, you should start with the Unit of Work implementation.

## Logging

Do keep in mind if there are relevant exceptions, you should log them. You have that functionality from the previous assignment.

## The interface

Start by defining the interface, with the three methods you will need. You could call it `UnitOfWork`.

## The implementation

Then create the class implementing the interface. You could call it `FileUnitOfWork`, as it is a file-based implementation of the Unit of Work pattern. If/when you swap to Postgres, you could call it `PostgresUnitOfWork`.

## Collections

It should contain a List for each entity type in your system.

## Constructor

The constructor

* Will receive a String argument, which is the directory path where the files are located.
* Should verify that the relevant files exist, and if not, create them. Remember, you will need a file per entity type.

```java
public FileUnitOfWork(String directoryPath)
{
    this.directoryPath = directoryPath;
    ensureFilesExist();
}
```

## Get methods

For each List, you should have a method that returns the list.\
If the list is null, you should create a new list, and load all the entities from the file into the list.

I recommend a dedicated function to convert from Pipe Separated Value to the entity type.

Read through the lines of the file, or read them all at once into a list of Strings. The convert to entities.

Return the list.

This method will read a file into a list of Strings, for each line of the file:

```java
private List<String> readAllLines(String filePath)
{
    try
    {
        return Files.readAllLines(Paths.get(filePath));
    }
    catch (IOException e)
    {
        throw new RuntimeException("Failed to read from file: " + filePath, e);
    }
}	
```

A similar approach can be used to write to a file.


## To text (PSV)

You may choose your text format, but adhere to the following requirements:
- It must be text-based, i.e. readable when opening the file. Not binary.
- It must be line-based, i.e. each entity is on a separate line.
- It must be entity-based, i.e. each entity is represented by a line of string.

I will explain the PSV format. You may pick CSV, JSON, XML, or invent your own format.

I recommend writing a dedicated method for each entity type to convert it to a Pipe Separated Value string. For example:

```java
private String toPSV(Stock stock)
{
    return stock.getSymbol() + "|" + stock.getName() + "|" + stock.getCurrentPrice() + "|" + stock.getCurrentState();
}
```

## From PSV

And similarly, a dedicated method for each entity type to convert it from a Pipe Separated Value string to the entity type. For example:

```java
private Stock fromPSV(String psv)
{
    String[] parts = psv.split("\\|"); // The split method takes a regular expression, which is why we need to escape the pipe character.
    return new Stock(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
}
```

## Begin transaction method

This method should simply set all lists to null, indicating no data has been loaded or modified yet.

## Rollback method

This method should simply set all lists to null. This will essentially undo all changes made to the lists of entities.

## Commit method

For each List, check if it is not null. If it is not null, convert each entity to a Pipe Separated Value string, and write it to the file.

There is a catch here: **Multithreading**. You will eventually have multiple threads in your system, each thread can use a Unit of Work. To avoid race conditions when writing to the files, you must synchronize the writing to the files.

You will need an Object field variable to use as lock.

```java
private static final Object FILE_WRITE_LOCK = new Object();
```

And you must synchronize on this lock when writing to the files.

```java
@Override
public void commit()
{
    synchronized (FILE_WRITE_LOCK)
    {
        if (stocks != null)
        {
            //... write all stocks to file
        }
        
        //... write all other entities to files
    }


    // Clear the data, to reset the Unit of Work
    clearData();
}
```

## Clear data method

I have multiple methods which needs to clear the data, so I have made a dedicated method for this.



---

# Data Access Object pattern

Next, we will implement the Data Access Object pattern.

## Required

Some DAOs are required. These are the DAOs for the following entities:
- Stock
- Portfolio
- OwnedStock

Then the domain model from assignment 1 also included entities for StockPriceHistory and Transaction.\
These are less important for the core game play, and you may choose to not implement them.

## Logging

Do keep in mind if there are relevant exceptions, you should log them. You have that functionality from the previous assignment.


## The interface

For each entity type, you should create an interface. You could call it `EntityDao`, e.g. `StockDao`.

The interface should contain the methods you will need to perform CRUD operations on the entity type.

You will need to get an entity by ID, get all entities, add an entity, update an entity, and delete an entity.

For the "get all entities" method, just keep it simple for now. As your system grows, you can consider adding filtering options. Or adding more complex queries.

## The implementation

For each entity type you should create a class implementing the interface. You could call it `FileEntityDao`, e.g. `FileStockDao`. If/when you swap to Postgres, you could call it `PostgresEntityDao`, e.g. `PostgresStockDao`.

## The constructor

The constructor should receive a FileUnitOfWork object, i.e. the implementation, not the interface.

I recommend also getting all the entities from the UoW, finding the highest ID, and storing that in a static field variable. This will make it easier to assign new IDs to new entities.

```java
private static int nextId = 1;

public StockPurchaseFileDAO(FileUnitOfWork uow)
{
    this.uow = uow;
    findNextId();
}
```

## Create method

This method accepts an entity as a parameter.

If relevant, increment and set the ID of the new entity. Use the static field variable to get the next ID.

From the UoW, get the list of entities. 

Add the entity to the list.

## Update method

This method accepts an entity as a parameter.

You have three options here:

1. Delete the entity from the list, and add the new entity.
2. Find the entity in the list and update the fields.
3. Set the entity. The List interface has a `set(index, element)` method, which overwrites the element at the given index with the new element. 

Either way, you first need to get the list of entities from the UoW.

Consider what to do if the entity is not found in the list. Should you throw an exception? Should you return false? 

## Delete method

This method accepts an ID as a parameter.

Get the list of entities from the UoW.

Delete the entity from the list.

Consider what to do if the entity is not found in the list. Should you throw an exception? Should you return false? 

## Get by ID method

This method accepts an ID as a parameter.

Get the list of entities from the UoW.

Find the entity in the list with the given ID.

Return the entity.

Consider what to do if the entity is not found in the list. Should you throw an exception? Should you return null? Do you use the Option pattern?

## Get by something else method

For some of your entity types, you may want to find an entity by something other than ID. You can add methods for this as needed.

## Get all method

This method returns a list of all entities.

**Optional challenge:** Returning a list of entities gives the caller the option to do something with the entities, like modifying an entity or deleting an entity. This is not ideal, as there are specific methods for such modifications. 

You may consider returning an immutable list. You may consider copying each entity in the list to a new immutable list. This will break the connection to the data held in the UoW, ensuring other classes cannot misuse the List.

For this project I am fine with just accepting this potential problem.

When you attach an actual database, this is no longer a problem.

---

# Update class diagram

Update your class diagram with the new packages, interfaces, and classes you have created for this assignment.

Export as svg. Include it in your assignment, and push to GitHub along with the code.

---

# Append method

> This is an optional challenge. You can choose to skip this.

## The problem

Some of your daos will update the data quite often, but not modify any existing data. We could call these read-only. This includes the StockPriceHistoryDao and the TransactionDao.

- You do not modify the history of stock prices. Or, at least, I don't see why you would want to.
- You do not modify the transactions. Again, this is historical data.

In both cases, you will mainly just _add_ new entity instances to the data storage (file).

The price history is updated about once per second. When you have played the game for a minute, you have 60 entries. The amount of data will grow quite quickly.

In the current version, every time you need to add a new entry, the Unit of Work will load the entire file content. This quickly becomes inefficient.

## The solution

For the type of entities which are _append-heavy_, we can implement a shortcut, where the UoW will not load the entire file content, but rather append the new entity to the end of the file.

## Unit of Work

First we must update the UoW for this optimization.


### Buffer Lists

In this class, add a new List for each entity which is append-heavy. You may call this List "&lt;entity&gt;Buffer", e.g. "StockPriceHistoryBuffer". It is initially a new, empty list. Unlike the other Lists, which are null.


### Get buffer method
You add a new method, to return this list: `getStockPriceHistoryAppendOnly()`.	

### Update commit method

In the commit method, where you are writing other data to the files, add a check for the buffer list. If it is not empty, you append the entities in the buffer to the file.

Java has a convenient method to append to a file:

```java
private void appendLinesToFile(String filePath, List<String> list)
{
    try
    {
        Files.write(Path.of(filePath), list, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    catch (IOException e)
    {
        // ... handle the exception
    }
}
```

### rollback method

To this method just add a line to clear the buffer list. Or assign it to a new, empty list.

## Data Access Object

In the DAO classes, add a new method, to append the entity to the buffer.

### Interface

You should add a new method to the interface: `append(entity)`.

### Implementation

This should be implemented in the relevant implementing DAO class.

The method should retrieve the buffer list from the UoW, and add the entity to the list.

When the UoW is committed, the data is appended to the end of the file.

## Create entity

Do you still need this method? Maybe not, maybe it can just be removed. Or, you could leave it, just in case. And just remember to use the append method when relevant.

---

# Testing your code

I recommend you test your code, by updating the main method.

You should instantiate a UnitOfWork, and a DataAccessObject for each entity type.

Then, you should test that data can be written to, and read from, the files.

---

# Status

After completing this assignment, your project structure should look like:

```console
📁 yourname.stockgame
├── 📁 business
├── 📁 dtos
├── 📁 entities
│   ├── 📄 Stock.java
│   ├── 📄 Portfolio.java
│   ├── 📄 StockPurchase.java
│   ├── 📄 Transaction.java
│   ├── 📄 StockPriceHistory.java
│   └── 📄 PortfolioValueHistory.java
├── 📁 persistence
│   ├── 📁 interfaces
│   │   ├── 📄 PortfolioDAO.java
│   │   ├── 📄 StockDAO.java
│   │   ├── 📄 StockPriceHistoryDAO.java
│   │   ├── 📄 StockPurchaseDAO.java
│   │   ├── 📄 TransactionDAO.java
│   │   └── 📄 UnitOfWork.java
│   └── 📁 fileimplementation
│       ├── 📄 FileUnitOfWork.java
│       ├── 📄 PortfolioFileDAO.java
│       ├── 📄 StockFileDAO.java
│       ├── 📄 StockPriceHistoryFileDAO.java
│       ├── 📄 StockPurchaseFileDAO.java
│       └── 📄 TransactionFileDAO.java
├── 📁 presentation
└── 📁 shared
    ├── 📁 configuration
    │   └── 📄 AppConfig.java
    └── 📁 logging
        ├── 📄 Logger.java
        ├── 📄 LogOutput.java
        └── 📄 ConsoleLogOutput.java
```

---

# Handing in

You will find an assignment on itslearning. Just hand in a link to the persistence sub-folder containing your code on GitHub.

Do make sure to upload relevant other documentation, such as the class diagram.