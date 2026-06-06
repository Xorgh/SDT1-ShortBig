# Introduction to Refactoring

Welcome to **Refactoring**! This is a fundamental practice in software development that helps you maintain and improve code quality over time.

## What is Refactoring?

**Refactoring** is the process of improving the internal structure of code **without changing its external behavior**.

### The Core Idea

Refactoring means:
- **Improving code structure** - Making code cleaner, more readable, better organized
- **Without changing behavior** - The code does exactly the same thing before and after
- **Improving design** - Making code easier to understand, modify, and extend

## The Definition

Martin Fowler, in his book *Refactoring*, defines it as:

> **"Refactoring is a disciplined technique for restructuring an existing body of code, altering its internal structure without changing its external behavior."**

## What Refactoring Is NOT

It's important to understand what refactoring is not:

### Refactoring ≠ Adding Features

```java
// This is NOT refactoring - it adds new functionality
public class UserService {
    public void createUser(User user) {
        saveUser(user);
        sendWelcomeEmail(user);  // NEW FEATURE - not refactoring
    }
}
```

### Refactoring ≠ Fixing Bugs

```java
// This is NOT refactoring - it fixes a bug
public void calculateTotal(Order order) {
    // Before: Bug - missing tax
    double total = order.getSubtotal();
    
    // After: Bug fix - includes tax
    double total = order.getSubtotal() + order.getTax();  // BUG FIX
}
```

### Refactoring = Improving Structure

```java
// This IS refactoring - improves structure, same behavior
// Before:
public void processOrder(Order order) {
    if (order.getTotal() > 1000) {
        applyDiscount(order);
    }
}

// After:
private static final double DISCOUNT_THRESHOLD = 1000.0;

public void processOrder(Order order) {
    if (order.getTotal() > DISCOUNT_THRESHOLD) {
        applyDiscount(order);
    }
}
// Same behavior, better structure
```

## Why Refactor?

Refactoring helps you:

1. **Improve Readability** - Code becomes easier to understand
2. **Reduce Complexity** - Simplify complex code
3. **Improve Maintainability** - Make future changes easier
4. **Remove Duplication** - Eliminate copy-paste code
5. **Improve Design** - Apply design principles (SOLID, etc.)
6. **Reduce Technical Debt** - Pay down accumulated problems

## When to Refactor

### The "Rule of Three"

Refactor when you see the same pattern three times:
- First time: Write it
- Second time: Notice the duplication
- Third time: Refactor to remove it

### Common Triggers

- **Code smells** - Long methods, deep nesting, magic numbers
- **Duplication** - Copy-paste code appears
- **Complexity** - Code is hard to understand
- **Before adding features** - Clean up code before extending it
- **After fixing bugs** - Improve the area you just fixed
- **During code review** - Address issues found in review

## The Two Hats

Kent Beck describes the "Two Hats" metaphor:

1. **Adding Functionality Hat** - Write new features, fix bugs
2. **Refactoring Hat** - Improve code structure

**Important:** Don't wear both hats at once. Either add functionality OR refactor, not both in the same commit.

## Connection to Design Principles

Refactoring is how you apply design principles:

- **SOLID Principles** - Refactor to achieve Single Responsibility, Open/Closed, etc.
- **Coupling and Cohesion** - Refactor to reduce coupling, increase cohesion
- **Broken Window Principle** - Refactor to fix broken windows
- **Mountains and Islands** - Refactor to flatten nested code

## Summary

Refactoring is:
- **Improving code structure** without changing behavior
- **A disciplined practice** with specific techniques
- **Essential for maintainability** - Keeps code clean over time
- **Not adding features** - That's a different activity
- **Not fixing bugs** - That's also different

In the following sections, we'll explore common refactoring techniques and see practical examples.



---

# Common Refactoring Techniques

Here are some of the most common and useful refactoring techniques you'll use regularly.

## Extract Method

**Problem:** A method is too long or does too many things.

**Solution:** Extract part of the method into a separate method.

### Example

```java
// Before: Long method
public void processOrder(Order order) {
    // Validation
    if (order == null) {
        throw new IllegalArgumentException("Order cannot be null");
    }
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Order must have items");
    }
    if (order.getTotal() <= 0) {
        throw new IllegalArgumentException("Order total must be positive");
    }
    
    // Processing
    calculateTax(order);
    applyDiscount(order);
    saveOrder(order);
}

// After: Extracted validation
public void processOrder(Order order) {
    validateOrder(order);
    calculateTax(order);
    applyDiscount(order);
    saveOrder(order);
}

private void validateOrder(Order order) {
    if (order == null) {
        throw new IllegalArgumentException("Order cannot be null");
    }
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Order must have items");
    }
    if (order.getTotal() <= 0) {
        throw new IllegalArgumentException("Order total must be positive");
    }
}
```

## Extract Variable (Introduce Explaining Variable)

**Problem:** A complex expression is hard to understand.

**Solution:** Extract the expression into a well-named variable.

### Example

```java
// Before: Magic number and complex expression
public void applyDiscount(Order order) {
    if (order.getTotal() > 1000 && order.getCustomer().getMembershipLevel() == MembershipLevel.PREMIUM) {
        order.setDiscount(0.1);
    }
}

// After: Extracted variables
private static final double DISCOUNT_THRESHOLD = 1000.0;
private static final double DISCOUNT_RATE = 0.1;

public void applyDiscount(Order order) {
    boolean exceedsThreshold = order.getTotal() > DISCOUNT_THRESHOLD;
    boolean isPremiumMember = order.getCustomer().getMembershipLevel() == MembershipLevel.PREMIUM;
    
    if (exceedsThreshold && isPremiumMember) {
        order.setDiscount(DISCOUNT_RATE);
    }
}
```

## Rename Variable/Method

**Problem:** A name doesn't clearly express what it does.

**Solution:** Rename it to something more descriptive.

### Example

```java
// Before: Unclear name
public void process(Order o) {
    double t = calculateTotal(o);
    save(o, t);
}

// After: Clear names
public void processOrder(Order order) {
    double total = calculateTotal(order);
    saveOrder(order, total);
}
```

## Extract Constant

**Problem:** Magic numbers or strings appear in code.

**Solution:** Extract them into named constants.

### Example

```java
// Before: Magic numbers
public void calculateShipping(Order order) {
    if (order.getWeight() > 10) {
        return order.getWeight() * 0.5;
    }
    return order.getWeight() * 0.3;
}

// After: Named constants
private static final double HEAVY_PACKAGE_WEIGHT_KG = 10.0;
private static final double HEAVY_SHIPPING_RATE = 0.5;
private static final double STANDARD_SHIPPING_RATE = 0.3;

public void calculateShipping(Order order) {
    if (order.getWeight() > HEAVY_PACKAGE_WEIGHT_KG) {
        return order.getWeight() * HEAVY_SHIPPING_RATE;
    }
    return order.getWeight() * STANDARD_SHIPPING_RATE;
}
```

## Remove Dead Code

**Problem:** Code that is no longer used.

**Solution:** Delete it.

### Example

```java
// Before: Dead code
public class UserService {
    public void createUser(User user) {
        saveUser(user);
    }
    
    // Dead code - never called
    public void oldCreateUser(User user) {
        // 50 lines of old implementation
    }
}

// After: Removed
public class UserService {
    public void createUser(User user) {
        saveUser(user);
    }
}
```

## Extract Class

**Problem:** A class has too many responsibilities.

**Solution:** Extract some responsibilities into a new class.

### Example

```java
// Before: Class does too much
public class Order {
    private String id;
    private List<Item> items;
    private Customer customer;
    
    // Order data
    public String getId() { return id; }
    
    // Order calculation (should be separate)
    public double calculateTotal() {
        double subtotal = items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        return subtotal * 1.1; // Tax
    }
    
    // Order validation (should be separate)
    public boolean isValid() {
        return items != null && !items.isEmpty() && customer != null;
    }
}

// After: Extracted responsibilities
public class Order {
    private String id;
    private List<Item> items;
    private Customer customer;
    
    public String getId() { return id; }
    // Just data
}

public class OrderCalculator {
    public double calculateTotal(Order order) {
        double subtotal = order.getItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        return subtotal * 1.1;
    }
}

public class OrderValidator {
    public boolean isValid(Order order) {
        return order.getItems() != null 
            && !order.getItems().isEmpty() 
            && order.getCustomer() != null;
    }
}
```

## Replace Magic Number with Constant

**Problem:** Numbers appear in code without explanation.

**Solution:** Replace with named constants.

### Example

```java
// Before: Magic number
public boolean isEligibleForDiscount(User user) {
    return user.getAge() >= 65;
}

// After: Named constant
private static final int SENIOR_CITIZEN_AGE = 65;

public boolean isEligibleForDiscount(User user) {
    return user.getAge() >= SENIOR_CITIZEN_AGE;
}
```

## Simplify Conditional

**Problem:** Complex if-else statements are hard to understand.

**Solution:** Simplify using early returns, guard clauses, or extract methods.

### Example

```java
// Before: Nested conditionals
public void processOrder(Order order) {
    if (order != null) {
        if (order.getItems() != null) {
            if (!order.getItems().isEmpty()) {
                // Process order
                saveOrder(order);
            }
        }
    }
}

// After: Guard clauses (early returns)
public void processOrder(Order order) {
    if (order == null) return;
    if (order.getItems() == null) return;
    if (order.getItems().isEmpty()) return;
    
    // Process order
    saveOrder(order);
}
```

## Move Method

**Problem:** A method is in the wrong class.

**Solution:** Move it to a more appropriate class.

### Example

```java
// Before: Method in wrong class
public class Order {
    private Customer customer;
    
    public String getCustomerFullName() {  // Should be in Customer
        return customer.getFirstName() + " " + customer.getLastName();
    }
}

// After: Moved to correct class
public class Customer {
    private String firstName;
    private String lastName;
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
```

## Summary

Common refactoring techniques:

1. **Extract Method** - Break down long methods
2. **Extract Variable** - Make expressions clear
3. **Rename** - Use descriptive names
4. **Extract Constant** - Remove magic numbers
5. **Remove Dead Code** - Delete unused code
6. **Extract Class** - Separate responsibilities
7. **Simplify Conditional** - Make logic clear
8. **Move Method** - Put code in the right place

These techniques help you improve code structure while maintaining behavior.



---

# When to Refactor

Knowing when to refactor is as important as knowing how. Here are the key times to refactor.

## The "Rule of Three"

A simple guideline for when to refactor:

1. **First time:** Write the code
2. **Second time:** Notice the duplication
3. **Third time:** Refactor to remove it

### Example

```java
// First time: Write it
public void processOrder(Order order) {
    if (order.getTotal() > 1000) {
        applyDiscount(order);
    }
}

// Second time: Notice duplication
public void processPayment(Payment payment) {
    if (payment.getAmount() > 1000) {  // Same threshold!
        applyFee(payment);
    }
}

// Third time: Refactor
private static final double THRESHOLD = 1000.0;

public void processOrder(Order order) {
    if (order.getTotal() > THRESHOLD) {
        applyDiscount(order);
    }
}

public void processPayment(Payment payment) {
    if (payment.getAmount() > THRESHOLD) {
        applyFee(payment);
    }
}
```

## Before Adding Features

**Refactor before extending code.** Clean up the area you're about to modify.

### Example

```java
// You need to add email notification to order processing
// Before adding the feature, refactor the existing code:

// Current code (needs refactoring):
public void processOrder(Order order) {
    if (order.getTotal() > 1000) {
        applyDiscount(order);
    }
    calculateTax(order);
    saveOrder(order);
}

// Refactor first:
public void processOrder(Order order) {
    validateOrder(order);
    applyDiscounts(order);
    calculateTax(order);
    saveOrder(order);
}

// Then add the feature:
public void processOrder(Order order) {
    validateOrder(order);
    applyDiscounts(order);
    calculateTax(order);
    saveOrder(order);
    sendConfirmationEmail(order);  // New feature
}
```

**Why:** It's easier to add features to clean, well-structured code.

## After Fixing Bugs

**Refactor the area you just fixed.** Improve the code that had the bug.

### Example

```java
// You just fixed a bug in this method
public void calculateTotal(Order order) {
    double subtotal = 0;
    for (Item item : order.getItems()) {
        subtotal += item.getPrice();  // Bug: missing quantity
    }
    // Fixed:
    subtotal += item.getPrice() * item.getQuantity();
    return subtotal;
}

// Refactor to prevent future bugs:
public void calculateTotal(Order order) {
    double subtotal = calculateSubtotal(order);
    return subtotal;
}

private double calculateSubtotal(Order order) {
    return order.getItems().stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
}
```

**Why:** The area just had a bug - it likely needs improvement.

## When You See Code Smells

Refactor when you encounter common code smells:

### Long Methods

```java
// Smell: Method is 200 lines long
public void processOrder(Order order) {
    // 200 lines of code
}

// Refactor: Break it down
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotals(order);
    applyDiscounts(order);
    saveOrder(order);
    sendConfirmation(order);
}
```

### Deep Nesting

```java
// Smell: Too many nested levels
public void process(Order order) {
    if (order != null) {
        if (order.getItems() != null) {
            if (!order.getItems().isEmpty()) {
                // Process
            }
        }
    }
}

// Refactor: Use guard clauses
public void process(Order order) {
    if (order == null) return;
    if (order.getItems() == null) return;
    if (order.getItems().isEmpty()) return;
    
    // Process
}
```

### Magic Numbers

```java
// Smell: Magic numbers
if (age >= 65) { }

// Refactor: Extract constant
private static final int SENIOR_CITIZEN_AGE = 65;
if (age >= SENIOR_CITIZEN_AGE) { }
```

### Duplication

```java
// Smell: Duplicated code
public void processOrder(Order order) {
    if (order.getTotal() > 1000) { }
}

public void processPayment(Payment payment) {
    if (payment.getAmount() > 1000) { }
}

// Refactor: Extract common logic
private static final double THRESHOLD = 1000.0;
```

## During Code Review

**Refactor issues found in code review** before merging.

### Common Review Comments

- "This method is too long" → Extract method
- "What does this number mean?" → Extract constant
- "This is duplicated" → Remove duplication
- "This class does too much" → Extract class

## When Understanding Code

**Refactor code you're trying to understand.** As you read and understand code, refactor it to make it clearer.

### Example

```java
// You're reading this to understand how it works:
public void process(Order o) {
    double t = 0;
    for (Item i : o.getItems()) {
        t += i.getPrice() * i.getQuantity();
    }
    if (t > 1000) {
        t = t * 0.9;
    }
    save(o, t);
}

// Refactor as you understand it:
public void processOrder(Order order) {
    double total = calculateSubtotal(order);
    if (exceedsDiscountThreshold(total)) {
        total = applyDiscount(total);
    }
    saveOrder(order, total);
}
```

**Why:** Refactoring helps you understand, and leaves the code better.

## The Boy Scout Rule

> **"Leave the code better than you found it."**

Every time you work on code:
- Fix at least one problem
- Improve at least one thing
- Don't make it worse

### Example

```java
// You're adding a feature to this class:
public class UserService {
    public void createUser(User user) {
        // TODO: Add validation  // Broken window!
        saveUser(user);
    }
}

// Apply Boy Scout Rule - fix the broken window:
public class UserService {
    public void createUser(User user) {
        validateUser(user);  // Fixed!
        saveUser(user);
    }
    
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
    }
}
```

## When NOT to Refactor

Sometimes you should **not** refactor:

### Don't Refactor "Just Because"

- Don't refactor code that works and won't be touched
- Don't refactor everything you see
- Don't refactor without tests

### Don't Refactor During Feature Development

- Don't mix refactoring with adding features
- Either add features OR refactor, not both
- Use the "Two Hats" approach

### Don't Refactor Without Tests

- Always have tests before refactoring
- Tests ensure behavior doesn't change
- Without tests, refactoring is risky

## Summary

Refactor when:

1. **Rule of Three** - Same pattern appears three times
2. **Before adding features** - Clean up first
3. **After fixing bugs** - Improve the area
4. **Code smells appear** - Long methods, duplication, etc.
5. **During code review** - Fix issues found
6. **When understanding code** - Make it clearer as you learn
7. **Boy Scout Rule** - Leave it better than you found it

Don't refactor:
- Code that works and won't be touched
- Without tests
- While adding features (use separate commits)

**The key:** Refactor continuously, in small steps, with tests.



---

# Example: Refactoring in Practice

A complete example showing the refactoring process from messy code to clean code.

## The Starting Point: Messy Code

```java
// Initial code - works but has problems
public class OrderProcessor {
    public void process(Order o) {
        if (o != null) {
            if (o.getItems() != null) {
                if (!o.getItems().isEmpty()) {
                    double t = 0;
                    for (Item i : o.getItems()) {
                        t += i.getPrice() * i.getQuantity();
                    }
                    if (t > 1000) {
                        t = t * 0.9;
                    }
                    double tax = t * 0.1;
                    double final = t + tax;
                    Database db = new PostgreSQLDatabase();
                    db.save("orders", o.getId(), final);
                    EmailService email = new GmailEmailService();
                    email.send(o.getCustomer().getEmail(), "Order processed: " + final);
                }
            }
        }
    }
}
```

**Problems:**
- Deep nesting (hard to read)
- Magic numbers (1000, 0.9, 0.1)
- Poor variable names (o, t, i, final)
- Mixed responsibilities (calculation, persistence, email)
- Direct dependencies (creates database and email service)
- Long method (does too much)

## Step 1: Extract Variables and Constants

```java
public class OrderProcessor {
    private static final double DISCOUNT_THRESHOLD = 1000.0;
    private static final double DISCOUNT_RATE = 0.9;
    private static final double TAX_RATE = 0.1;
    
    public void process(Order order) {
        if (order != null) {
            if (order.getItems() != null) {
                if (!order.getItems().isEmpty()) {
                    double subtotal = 0;
                    for (Item item : order.getItems()) {
                        subtotal += item.getPrice() * item.getQuantity();
                    }
                    double total = subtotal;
                    if (total > DISCOUNT_THRESHOLD) {
                        total = total * DISCOUNT_RATE;
                    }
                    double tax = total * TAX_RATE;
                    double finalTotal = total + tax;
                    Database db = new PostgreSQLDatabase();
                    db.save("orders", order.getId(), finalTotal);
                    EmailService email = new GmailEmailService();
                    email.send(order.getCustomer().getEmail(), "Order processed: " + finalTotal);
                }
            }
        }
    }
}
```

**Improvements:**
- Extracted constants (magic numbers removed)
- Better variable names (order, subtotal, total, finalTotal)

## Step 2: Simplify Conditionals (Guard Clauses)

```java
public class OrderProcessor {
    private static final double DISCOUNT_THRESHOLD = 1000.0;
    private static final double DISCOUNT_RATE = 0.9;
    private static final double TAX_RATE = 0.1;
    
    public void process(Order order) {
        if (order == null) return;
        if (order.getItems() == null) return;
        if (order.getItems().isEmpty()) return;
        
        double subtotal = 0;
        for (Item item : order.getItems()) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        double total = subtotal;
        if (total > DISCOUNT_THRESHOLD) {
            total = total * DISCOUNT_RATE;
        }
        double tax = total * TAX_RATE;
        double finalTotal = total + tax;
        Database db = new PostgreSQLDatabase();
        db.save("orders", order.getId(), finalTotal);
        EmailService email = new GmailEmailService();
        email.send(order.getCustomer().getEmail(), "Order processed: " + finalTotal);
    }
}
```

**Improvements:**
- Flattened nesting (guard clauses)
- Easier to read

## Step 3: Extract Methods

```java
public class OrderProcessor {
    private static final double DISCOUNT_THRESHOLD = 1000.0;
    private static final double DISCOUNT_RATE = 0.9;
    private static final double TAX_RATE = 0.1;
    
    public void process(Order order) {
        if (order == null) return;
        if (order.getItems() == null) return;
        if (order.getItems().isEmpty()) return;
        
        double subtotal = calculateSubtotal(order);
        double total = applyDiscount(subtotal);
        double finalTotal = addTax(total);
        saveOrder(order, finalTotal);
        sendConfirmation(order, finalTotal);
    }
    
    private double calculateSubtotal(Order order) {
        double subtotal = 0;
        for (Item item : order.getItems()) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        return subtotal;
    }
    
    private double applyDiscount(double subtotal) {
        if (subtotal > DISCOUNT_THRESHOLD) {
            return subtotal * DISCOUNT_RATE;
        }
        return subtotal;
    }
    
    private double addTax(double total) {
        return total + (total * TAX_RATE);
    }
    
    private void saveOrder(Order order, double finalTotal) {
        Database db = new PostgreSQLDatabase();
        db.save("orders", order.getId(), finalTotal);
    }
    
    private void sendConfirmation(Order order, double finalTotal) {
        EmailService email = new GmailEmailService();
        email.send(order.getCustomer().getEmail(), "Order processed: " + finalTotal);
    }
}
```

**Improvements:**
- Extracted methods (each does one thing)
- Main method reads like a story
- Methods are testable independently

## Step 4: Extract Classes and Use Dependency Injection

```java
// Extracted calculation logic
public class OrderCalculator {
    private static final double DISCOUNT_THRESHOLD = 1000.0;
    private static final double DISCOUNT_RATE = 0.9;
    private static final double TAX_RATE = 0.1;
    
    public double calculateTotal(Order order) {
        double subtotal = calculateSubtotal(order);
        double total = applyDiscount(subtotal);
        return addTax(total);
    }
    
    private double calculateSubtotal(Order order) {
        return order.getItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
    }
    
    private double applyDiscount(double subtotal) {
        if (subtotal > DISCOUNT_THRESHOLD) {
            return subtotal * DISCOUNT_RATE;
        }
        return subtotal;
    }
    
    private double addTax(double total) {
        return total + (total * TAX_RATE);
    }
}

// Main processor with dependency injection
public class OrderProcessor {
    private final OrderCalculator calculator;
    private final OrderRepository repository;
    private final EmailService emailService;
    
    public OrderProcessor(OrderCalculator calculator, OrderRepository repository, EmailService emailService) {
        this.calculator = calculator;
        this.repository = repository;
        this.emailService = emailService;
    }
    
    public void process(Order order) {
        if (order == null) return;
        if (order.getItems() == null || order.getItems().isEmpty()) return;
        
        double finalTotal = calculator.calculateTotal(order);
        repository.save(order, finalTotal);
        emailService.sendConfirmation(order, finalTotal);
    }
}
```

**Improvements:**
- Extracted `OrderCalculator` class (single responsibility)
- Dependency injection (loose coupling, testable)
- Clean, focused `OrderProcessor`
- Follows SOLID principles

## The Result: Clean, Maintainable Code

### Before vs After

**Before:**
- 1 class, 1 long method
- Deep nesting
- Magic numbers
- Poor names
- Mixed responsibilities
- Direct dependencies
- Hard to test

**After:**
- 2 focused classes
- Flat structure
- Named constants
- Clear names
- Single responsibilities
- Injected dependencies
- Easy to test

### Benefits

1. **Readable** - Code tells a clear story
2. **Testable** - Can test each part independently
3. **Maintainable** - Easy to modify
4. **Flexible** - Can swap implementations
5. **Follows SOLID** - Single responsibility, dependency inversion

## Key Takeaways

This example shows:

1. **Refactoring is incremental** - Small steps, not big rewrites
2. **Each step improves something** - Constants, names, structure
3. **Tests ensure safety** - Behavior doesn't change
4. **End result is much better** - Clean, maintainable code

**Remember:** Refactoring is about improving structure while maintaining behavior. The code does the same thing, but it's much better organized.

