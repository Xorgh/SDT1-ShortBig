# Introduction to Assignment 4 - State Design Pattern

This time we will solve the following problem:

We must simulate a stock market, with stocks fluctuating in price. The Stock price calculation can differ based on the current state of the stock.

## Logging

Generally, there may be a lot of information, you want to print to the console, for debugging purposes.  
Therefore, you should use the Logger class from the previous assignment.

## AppConfig

You should use the AppConfig class from the previous assignment.

## Assignment 3 deliverables

- LiveStock state machine diagram
- LiveStock implementation using the State Design Pattern
- StockMarket singleton
- Market update thread
- New, updated class diagram

## Deadline

See itslearning.

## Handing in

On itslearning, you will just submit a link to the sub-folder containing LiveStock code.

---

# The LiveStock class

You currently have a Stock entity, in your domain model. This entity can change over time, and players can purchase and sell shares of the stock. This data is persisted to a file.

But we must simulate the change in price of the stock over time. This data lives live in the memory as long as the program is running.

Therefore, we sort of have two sides of the same coin:
- The Stock entity, which is persisted to a file.
- The LiveStock entity, which is stored in memory and updated in real-time.

You already have the former, now you must implement the latter.

## Project structure

The behaviour of the LiveStock is considered business logic. Therefore, it should be in the business logic layer.

I recommend a package here: src/business/stockmarket/simulation/

## LiveStock class

Create a class called `LiveStock`.

### Fields

The class must have three fields:
- The `symbol` of the stock it represents, e.g. "AAPL", or "GOOG", or "MSFT". This must match the symbol of the Stock entity.
- The `currentState`, i.e. a reference to a state object. See next page.
- The `currentPrice`, i.e. the price of the stock at the current time.

### Constructor

You will probably need more than one constructor:
1) One for creating a new LiveStock object the first time.
2) One for creating re-creating a LiveStock when the game is loaded.

**The former (1.)** should simply take the `symbol` as a parameter. `currentState` can be set to a default state of your choice. `currentPrice` should be found in the `AppConfig` singleton.

**The latter (2.)** may be done later, if you need it. It will take the relevant data from the Stock entity, and setup a LiveStock. This means mapping the _name_ of a state, to the actual state object.


### Update price method

Add this method to the LiveStock class, `updatePrice()`.  
This method should call the `currentState`'s `updatePrice()` method, which returns _a change in price_. This change is added to the `currentPrice`.

If the current price drops below 0, the stock is considered bankrupt, and the state should be set to "Bankrupt".  
You _may_ check for bankrupcy in each of the state classes. I found it easier to do it here.

```java
public void updatePrice() {
    double priceChange = currentState.calculatePriceChange(this);
    
    currentPrice += priceChange;
    
    // check for bankruptcy?
}
```

### set state method

You will need a method to set the state of the LiveStock. This is to be called from other states.

Consider access modifier...?

### getters

You will probably need getters for one or more fields.  
Including a method to get the current state _name_, so that this information can be persisted.

---

# The State Machine

For the LiveStock, you must draw a State Machine diagram.

You must include _at least_ the following states:
- Steady: This will make minor changes to the price, equally likely to go up or down.
- Growing: This will make significant changes to the price, likely to go up.
- Declining: This will make significant changes to the price, likely to go down.
- Bankrupt: This will set the price to 0. Stock is bankrupt, and cannot be purchased. After a while, it will reset.

You must decide upon valid transitions between states. From which state can you transition to which other state?

Where will the state go after being bankrupt? 

Personally, I have included a Reset state. When Bankrupt, the state will go to Reset, just to reset the price, and then to the next state to be part of the game again.

---

# The State interface

You will need an interface to represent the states of the LiveStock.

Put this in the same package. Consider the access modifier.

The interface will need at least two methods:

- `calculatePriceChange`: this returns a double, representing the change in price.
- `getName`: this returns a string, representing the name of the state.



---

# State classes

As mentioned, you need at least four state classes:
- Steady
- Growing
- Declining
- Bankrupt

You may consider more states:
- Reset: I use this to just reset the price of a stock, and then go to the default initial state.
- Rapid growth: Grows fast, maybe for a fixed number of ticks, or higher chance of transitioning.
- Rapid decline
- Rapid crash: Just cras
- Wild fluctuations
- ... whatever else you can come up with.

Create these classes in the same package as the LiveStock class.

I will provide some example code below, you can use it as a starting point.

## Steady state class

Create a class called `SteadyState`.

### Fields

I have a static final field to hold a `Random` object. This class is used to generate random numbers.

Like this:

```java
private static final Random random = new Random();
```

### calculatePriceChange method

You use the `Random` object to generate a random change in price. Then you return this change.

You may find two relevant methods:
- `nextInt(int bound)`: returns a random integer between 0 and `bound` (exclusive).
- `nextDouble()`: returns a random double between 0.0 and 1.0.

Using these, you can generate a random change in price.

**Examples:**

```java
int change = random.nextInt(10) - 5; // between -5 and 5
```

or 

```java
double change = random.nextDouble() - 0.5; // between -0.5 and 0.5
```

or if you want a percentage change:

```java
double changePercent = (random.nextDouble() * 2 - 1) / 100; // -0.01 to +0.01
```

### Transitioning

After the price change is calculated, but before it is returned to the LiveStock class, you should check for a transition. 

I suggest that transitions happen randomly, with a certain probability. You can also just do a fixed number of ticks before a transition happens, like 10 updates. But maybe this becomes too predictable. Or find a third way. Up to you.

For example:

```java
double rand = random.nextDouble();
        
// 5% chance to transition to Growing
if (rand < 0.05) {
    liveStock.setState(new GrowingState());
} 
// 5% chance to transition to Declining (total 10%)
else if (rand < 0.10) {
    liveStock.setState(new DecliningState());
}
// more transitions..?
// Otherwise stay Steady (90%)
```	

### Constructor

The constructor should take the `liveStock` as a parameter.

## Other state classes

Their implementation will be pretty similar. But the random change in price will be a bit skewed.

For example, if the stock is growing, it should be more likely to go up than down. And if the stock is declining, it should be more likely to go down than up. Just shift the probabilities a bit.

You can use the same methods as in the `SteadyState` class to generate the random change in price. But you may need to adjust the probabilities.

## Bankrupt state class

The `BankruptState` class is simple. It just sets the price to 0, and stays there. After a while, random or fixed number of ticks, it should transition to another state. Either pick a Reset, or a default state. Reset the price of the Stock to the value from the AppConfig singleton. Now the Stock is back in the game again.

## Reset state class

I have this state, which on first tick just sets the price to the value from the AppConfig singleton. Then it transitions to the default state, for me that is the Steady state.

This means this state is only used for one tick. Maybe it is overkill. I just though resetting/reviving a Stock is different from a Stock being brankrupt. But up to you.

---

# The Stock Market

This class represents the live stock market, and manages the LiveStock objects and their updates.

It is a singleton, and should be called `StockMarket`.

## Location

I would put this in the "stockmarket" package, in the business logic layer.

## Fields

You will need a `List<LiveStock>` field.

You may also need access to a `Logger`, to print out information about the stock market, so you can see what is happening.

## Add new stock method

You must create a method to add a new LiveStock object to the market. The parameter of the method is just a Stock symbol. Then, create a new LiveStock object, and add it to the list.

## Add existing stock method

You will probably, eventually, also need to add an existing Stock to the market, e.g. when the game is loaded. The parameter of the method is a Stock object. Then, create a new LiveStock object, and add it to the list.

## Update all stocks method

This method should iterate over all the LiveStock objects in the market, and call their `updatePrice()` method.

At the end of the loop, you should log the information about the LiveStock, using the `Logger`, just as a way to test what is happening.

## Singleton

Make the `StockMarket` class a singleton.

---

# Market ticker thread

This class is responsible for making the stock market update in real-time.

## Initialization

It should get a reference to the StockMarket singleton.  
The `AppConfig` singleton should contain the update frequency in milliseconds.  
You may also need a reference to a `Logger`, to print out information about the market ticker, so you can see what is happening.

## Running the updates

The class should have a while(true) loop, and inside it, it should call the `StockMarket.updateAllStocks()` method.

Then the loop should sleep for the update frequency, i.e. pause this thread for the update frequency:

```java
Thread.sleep(updateFrequency);
```

This must be run in a separate thread, one way or another, started from the main method.

## The flow

1. Background thread triggers market tick every second
2. StockMarket calls updatePrice() on all LiveStock objects
3. Each LiveStock delegates to its current state to calculate price change
4. LiveStock applies the change to its current price
5. State may transition to a new state
6. LiveStock checks for bankruptcy (price <= 0)
7. StockMarket logs the price change and new price to console
8. (later session: StockMarket tells the application that the stock has been updated)

---

# OPTIONAL CHALLENGE: Irregular updates

This part is optional. You may skip it.

## Parallel updates

Currently, all LiveStocks are updated at the same time, in one loop through the List.

You may want to update the LiveStocks in parallel, i.e. each LiveStock is updated in its own thread.

## Irregular updates

Currently, the update frequency is fixed. You may want to make it irregular, i.e. the update frequency is random, between a minimum and maximum value. Or, the update frequency plus/minus a random value. This may make for a slightly more interesting stock market.

---

# OPTIONAL CHALLENGE: The Transition Manager


This part is optional. You may skip it.

With the common approach to the State Pattern, most state classes need to know about the other state classes. This can be a problem, as it makes the state classes tightly coupled. Each new state added _must_ require at least one other state class to be updated. Otherwise, how would the context transition to the new state?

This can be solved by using a Transition Manager. This class will know about all the states, and will be responsible for managing the transitions between them. Sure, this will also require updates whenever a new state is added, but it is centralized, one place, always the same to update. That's a decent trade-off.

## Introducing the Transition Manager

The concept is, each State class will have a reference to the Transition Manager. 

Then the Transition Manager...

* must somehow keep track of each possible state, and which _other_ states can be transitioned to.
* must define when a transition is valid.
* must provide a method to transition to a new state. This method will regularly be called by the State classes, and is then responsible for checking if the transition is valid, if it should happen, and if so, to transition to the new state.

Either:
- The Transition Manager will set the state on the context. Or
- The Transition Manager will return the new state, and who-ever called the Transition Manager will set the state on the context. It was probably a State class that called the Transition Manager.

Depending on the particular state machine, the strategy for implementing the transition manager will be different.

In our particular case, we can illustrate the transitions as a table:

| From \ To | Steady | Growing | Declining |
|-----------|--------|---------|-----------|
| **Steady**   | 80% | 10% | 10% |
| **Growing**  | 20% | 75% | 5%  |
| **Declining**| 25% | 10% | 65% |

Adding a new state will require updating the transition manager with a new column and row in the table, to define the posibility of transitioning to the new state from each of the other states. And _from_ the new state, to each of the other states.

If a transition is not possible, you can use a probability of 0. Then, either return null to indicate no new state, or just return a new instance of the current state (which only works, if your State classes have no field variables).

So, after each tick, the Transition Manager will check if a transition should happen. If so, it will transition to the new state.

---

# Test

You should rework your main method to test this latest code.

1) Add some Stocks to the StockMarket singleton.
2) Start the Market ticker thread.
3) Observe that stocks are updated regularly.

---

# Handing in

If you wish to hand in, you can do so on itslearning.

You will find an assignment on itslearning. Just hand in a link to the stockmarket sub-folder containing your code on GitHub.

Do make sure to upload relevant other documentation, such as the class diagram, and the state machine diagram.
