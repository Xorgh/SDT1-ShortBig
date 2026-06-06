# Introduction to JavaFX Data Bindings

JavaFX **data bindings** let you _connect values_ so that when one changes, others update automatically, without writing listeners or manual synchronization code. This learning path explains the concept and the APIs.

![data bindings](Data Bindings/Resources/Quantum.png)

## What Are Data Bindings?

In ordinary code, a value is just a value: you put a number or string in a variable, and nothing else knows when it changes. **Data binding** means wrapping that value in an **observable container** (in JavaFX, a **property**). When the value inside the container changes, anything that **depends** on it can be updated automatically.

Yes, this is sort of the observer pattern, we are using here. Just smaller, on a value level.

So instead of "variable A changed, so I must remember to update B and C," you say "B is bound to A" and "C is bound to A." When A changes, B and C stay in sync without extra code.

## Why Does JavaFX Have Properties and Bindings?

A graphical UI must stay in sync with data: when the user edits a field, the model updates; when the model updates, the display should reflect it. JavaFX provides **properties** (observable values) and **bindings** (relationships between them) so that:

- Changing a property automatically updates anything bound to it.
- You can bind a UI control's property (e.g. a label's text) to a value in your model, and the screen updates when the model changes.

Using these APIs in a full JavaFX application (with controllers, view models, and FXML) is covered elsewhere. Here we focus only on **properties and bindings themselves**.

## Scope of This Learning Path

This path covers **only**:

- What properties are and how to create and use them.
- Unidirectional binding (one property follows another).
- Bidirectional binding (two properties stay in sync).
- Computed bindings (a value derived from one or more properties, including mapping between types).

All examples use a **simple `main` method**: we create properties, bind them, and print values. No Stage, Scene, FXML, or application lifecycle. That keeps the focus on the binding APIs. When you later connect model to UI, you will use these same properties and bindings.

## What You Will See

1. **Property types**: Integer, Double, String, Boolean, and Object properties; how to create them and get/set values.
2. **Unidirectional binding**: `bind()`: one property (the target) follows another (the source) and becomes read-only.
3. **Bidirectional binding**: `bindBidirectional()`: two properties stay in sync; both remain writable.
4. **Computed bindings**: Values derived from other properties (e.g. sum of two numbers, concatenation of strings, or a number formatted as text). This includes **mapping between types** (e.g. number → string).

Once you understand these, you can use the same ideas when wiring up a real JavaFX UI.


---

# Properties in JavaFX

Before you can bind values, you need **properties**: observable wrappers around a value. JavaFX provides several property types and simple implementations you can use in your code.

## Observable and Property

- **Observable**: Something that can notify listeners when it changes. JavaFX uses this so that bindings and UI controls can react to updates.
- **Property**: A read/write value that is also an `Observable`. You can get and set the value and observe changes.

You do not need to implement these interfaces yourself. Use the **simple property** classes (e.g. `SimpleIntegerProperty`, `SimpleStringProperty`) that JavaFX provides. They already implement the right behaviour.

## Property Types

All of these live in the package `javafx.beans.property`:

| Type | Interface | Simple implementation | Holds |
|------|-----------|------------------------|-------|
| Integer | `IntegerProperty` | `SimpleIntegerProperty` | `int` |
| Double | `DoubleProperty` | `SimpleDoubleProperty` | `double` |
| Long | `LongProperty` | `SimpleLongProperty` | `long` |
| Float | `FloatProperty` | `SimpleFloatProperty` | `float` |
| String | `StringProperty` | `SimpleStringProperty` | `String` |
| Boolean | `BooleanProperty` | `SimpleBooleanProperty` | `boolean` |
| Any object | `ObjectProperty<T>` | `SimpleObjectProperty<T>` | Reference type `T` |

For most uses, you work with the **Simple** implementation: create it, get and set values, and later bind it to other properties or to UI controls.

## Creating and Using Properties

Create a property with an initial value, then use `get()` and `set()` to read and write it:

```java
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PropertiesExample {
    public static void main(String[] args) {
        IntegerProperty count = new SimpleIntegerProperty(0);
        StringProperty name = new SimpleStringProperty("");

        System.out.println(count.get());   // 0
        System.out.println(name.get());    // ""

        count.set(10);
        name.set("Alice");

        System.out.println(count.get());   // 10
        System.out.println(name.get());    // Alice
    }
}
```

- **Constructor**: `new SimpleIntegerProperty(0)` gives an integer property with value 0. `new SimpleStringProperty("")` gives an empty string. Other types work the same way.
- **get()**: Returns the current value (unboxed for primitives, or the object for `ObjectProperty`/`StringProperty`).
- **set(value)**: Updates the value. When we introduce bindings, changing a property will automatically update anything bound to it.

## UI Controls and Properties

In a JavaFX UI, controls expose properties. For example, a `TextField` has a `textProperty()` that returns a `StringProperty`; a `Label` has `textProperty()` as well. You can bind the label's text to the text field's text so the label always shows what the user types, without writing a listener. We do not build full UI here; just be aware that the same property types you create in code are what those controls use under the hood.

## Summary

- **Observable**: can notify when it changes; **Property**: observable and read/write.
- Use **SimpleXxxProperty** (e.g. `SimpleIntegerProperty`, `SimpleStringProperty`) to create properties.
- Use **get()** and **set()** to read and write the value. In the next files we will bind properties together so that one update propagates to others.


---

# Unidirectional Binding

**Unidirectional binding** means one property (the **target**) is tied to another (the **source**). The target always reflects the source and cannot be set directly. When the source changes, the target updates automatically.

## The API

Call **`target.bind(source)`** on the property that should follow:

- The **target** is the one you call `bind` on; it becomes the "follower."
- The **source** is the argument; it is the "leader."
- After binding, the target's value is always equal to the source's value. Setting the target directly is not allowed and will throw an exception.

## Rules

- The target must be **writable** before you call `bind`. Once bound, it is **read-only**: you must not call `set()` on it.
- The source remains writable; change it and the target updates automatically.
- To change what the target follows, first call **`target.unbind()`**, then call **`target.bind(newSource)`** with a different source. After unbinding, the target is writable again (but its value is unchanged until you set or re-bind it).

## Example: Integer Properties

```java
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class UnidirectionalInteger {
    public static void main(String[] args) {
        IntegerProperty source = new SimpleIntegerProperty(100);
        IntegerProperty target = new SimpleIntegerProperty(0);

        System.out.println("Before bind: source=" + source.get() + ", target=" + target.get());

        target.bind(source);
        System.out.println("After bind:  source=" + source.get() + ", target=" + target.get());

        source.set(50);
        System.out.println("After set source to 50: source=" + source.get() + ", target=" + target.get());

        // target.set(200);  // Would throw UnsupportedOperationException
    }
}
```

Output:

```
Before bind: source=100, target=0
After bind:  source=100, target=100
After set source to 50: source=50, target=50
```

The target immediately takes the source's value when bound, and then follows every change to the source.

Notice that after the target is bound to the source, and the source's value is set to 50 (line 14), the target's value is also updated to 50. This is because the target is bound to the source, and the source's value is set to 50.

## Example: String Properties

```java
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UnidirectionalString {
    public static void main(String[] args) {
        StringProperty source = new SimpleStringProperty("Hello");
        StringProperty target = new SimpleStringProperty("");

        target.bind(source);
        System.out.println("target = " + target.get());   // Hello

        source.set("World");
        System.out.println("target = " + target.get());   // World
    }
}
```

Same idea: the target is bound to the source, so it always shows the source's value and cannot be set on its own.

## Summary

- **`target.bind(source)`**: Target follows source and becomes read-only.
- Only the **source** should be modified after binding; the target updates automatically.
- Use **`target.unbind()`** if you need to re-bind the target to a different source or set it manually again.


---

# Bidirectional Binding

**Bidirectional binding** keeps two properties in sync: changing either one updates the other. Both remain writable. This is useful when two properties represent the same logical value (e.g. the same field in different parts of the UI or in a model and a copy).

## The API

Call **`prop1.bindBidirectional(prop2)`**. After that:

- Setting `prop1` updates `prop2` to the same value.
- Setting `prop2` updates `prop1` to the same value.
- Neither property becomes read-only; you can use `set()` on either.

To remove the link, call **`prop1.unbindBidirectional(prop2)`** (same two properties as when you bound them).

## Example: Two Integer Properties

```java
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BidirectionalInteger {
    public static void main(String[] args) {
        IntegerProperty a = new SimpleIntegerProperty(10);
        IntegerProperty b = new SimpleIntegerProperty(20);

        System.out.println("Before bindBidirectional: a=" + a.get() + ", b=" + b.get());

        a.bindBidirectional(b);
        System.out.println("After bindBidirectional:  a=" + a.get() + ", b=" + b.get());

        a.set(100);
        System.out.println("After a.set(100):         a=" + a.get() + ", b=" + b.get());

        b.set(200);
        System.out.println("After b.set(200):        a=" + a.get() + ", b=" + b.get());
    }
}
```

Output:

```
Before bindBidirectional: a=10, b=20
After bindBidirectional:  a=20, b=20
After a.set(100):         a=100, b=100
After b.set(200):         a=200, b=200
```

When you bind bidirectionally, one of the two "wins" for the initial value (here `b`'s value is used for both). After that, whichever property you set, the other follows.

## Example: Two String Properties

```java
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BidirectionalString {
    public static void main(String[] args) {
        StringProperty first = new SimpleStringProperty("Alice");
        StringProperty second = new SimpleStringProperty("Bob");

        first.bindBidirectional(second);
        System.out.println("first=" + first.get() + ", second=" + second.get());  // both "Bob" (second's value)

        first.set("Carol");
        System.out.println("first=" + first.get() + ", second=" + second.get());  // both "Carol"

        second.set("Dave");
        System.out.println("first=" + first.get() + ", second=" + second.get());  // both "Dave"
    }
}
```

Same idea: after `bindBidirectional`, both properties always hold the same value, and you can set either one.

## Summary

- **`prop1.bindBidirectional(prop2)`**: The two properties stay in sync; both stay writable.
- Changing either property updates the other.
- Use **`prop1.unbindBidirectional(prop2)`** to remove the link. After that, each property keeps its current value and can be set independently again.


---

# Computed Bindings and the Bindings Class

A **computed binding** (or **derived binding**) is a value that depends on one or more observables. When any dependency changes, the derived value can be recomputed. JavaFX does this **lazily**: the value is recalculated when something asks for it (e.g. when you call `get()` or when a UI control reads it), not on every dependency change.

The **`Bindings`** class in `javafx.beans.binding` provides factory methods for creating such bindings without implementing interfaces yourself.

## The Bindings Class

`javafx.beans.binding.Bindings` has static methods that return bindings. For example:

- **`Bindings.add(a, b)`**: numeric sum (for `IntegerProperty`, `DoubleProperty`, etc.).
- **`Bindings.createStringBinding(callable, dependencies...)`**: a string that is computed from the given observables; whenever a dependency changes, the string is recomputed when needed.
- **`Bindings.format(format, args...)`**: a formatted string (like `String.format`), with format arguments that can be observables so the string updates when they change.

The result of these methods is usually a **Binding** (read-only). You do not set it; you only read it. It stays in sync with its dependencies.

## Example: Sum of Two Numbers

Two numeric properties, and a binding that is their sum:

```java
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ComputedSum {
    public static void main(String[] args) {
        IntegerProperty a = new SimpleIntegerProperty(10);
        IntegerProperty b = new SimpleIntegerProperty(20);

        NumberBinding sum = Bindings.add(a, b);
        System.out.println("sum.get() = " + sum.getValue());   // 30

        a.set(100);
        System.out.println("sum.get() = " + sum.getValue());   // 120

        b.set(5);
        System.out.println("sum.get() = " + sum.getValue());   // 105
    }
}
```

`sum` is a **NumberBinding**: it has `getValue()` (and for number types, you can use `intValue()`, `doubleValue()`, etc.). It always reflects `a.get() + b.get()` and updates when either `a` or `b` changes.

## Example: Concatenating Two Strings

A string that combines two `StringProperty` values (e.g. first and last name):

```java
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ComputedConcat {
    public static void main(String[] args) {
        StringProperty first = new SimpleStringProperty("John");
        StringProperty last = new SimpleStringProperty("Doe");

        StringBinding fullName = Bindings.createStringBinding(
            () -> first.get() + " " + last.get(),
            first, last
        );

        System.out.println("fullName = " + fullName.get());   // John Doe

        first.set("Jane");
        System.out.println("fullName = " + fullName.get());   // Jane Doe

        last.set("Smith");
        System.out.println("fullName = " + fullName.get());   // Jane Smith
    }
}
```

**`Bindings.createStringBinding(callable, dependencies)`**: The first argument is a lambda (or `Callable<String>`) that computes the string; the rest are observables the binding depends on. Whenever any of those observables change, the next time you call `fullName.get()` the string is recomputed.

## Example: Mapping a Number to a String

A common case is **mapping between types**: e.g. a numeric property and a string that displays it (perhaps with a prefix or formatting). Use `createStringBinding` and read the number inside the lambda:

```java
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ComputedNumberToString {
    public static void main(String[] args) {
        DoubleProperty value = new SimpleDoubleProperty(3.14);

        StringBinding display = Bindings.createStringBinding(
            () -> "Value: " + value.get(),
            value
        );

        System.out.println(display.get());   // Value: 3.14

        value.set(2.71);
        System.out.println(display.get());   // Value: 2.71
    }
}
```

The binding depends only on `value`. Whenever `value` changes, the next call to `display.get()` returns the updated string. This is the same pattern you use when binding a label's text to a number: the binding does the type conversion (number → string) for you.

For formatted numbers (e.g. fixed decimal places), you can use **`Bindings.format`**:

```java
StringBinding formatted = Bindings.format("Value: %.2f", value);
```

Then `formatted.get()` returns something like `"Value: 3.14"` and updates when `value` changes.

## Summary

- **Computed bindings** are values derived from one or more observables; they recompute (lazily) when dependencies change.
- **`Bindings.add(a, b)`** gives a numeric binding (sum).
- **`Bindings.createStringBinding(() -> ..., dep1, dep2, ...)`** gives a string binding that depends on the listed observables; the lambda computes the string.
- **`Bindings.format(format, ...)`** gives a formatted string; arguments can be observables so the result updates when they change.
- Use these when you need a **derived** or **mapped** value (e.g. number to string, or combining several properties into one) that stays in sync with the source properties.


---

# More Complex Examples

This page ties the concepts together with a few slightly more involved examples, still using only a simple `main` method. Then we recap when to use which kind of binding.

You will probably not need this, but, just to let you know it exists.

## Formatted Total: Price and Quantity

Two numeric properties (e.g. price and quantity) and a string binding that shows a formatted total:

```java
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class FormattedTotal {
    public static void main(String[] args) {
        DoubleProperty price = new SimpleDoubleProperty(10.0);
        DoubleProperty quantity = new SimpleDoubleProperty(3.0);

        StringBinding totalDisplay = Bindings.createStringBinding(
            () -> String.format("Total: %.2f (price %.2f x quantity %.0f)",
                price.get() * quantity.get(), price.get(), quantity.get()),
            price, quantity
        );

        System.out.println(totalDisplay.get());
        // Total: 30.00 (price 10.00 x quantity 3)

        price.set(5.5);
        quantity.set(4);
        System.out.println(totalDisplay.get());
        // Total: 22.00 (price 5.50 x quantity 4)
    }
}
```

The binding depends on both `price` and `quantity`. Any change to either recomputes the displayed string when `get()` is called.

## Boolean Binding: Above a Threshold

A numeric property and a boolean that is true when the number is above a threshold. JavaFX has **BooleanBinding** and `Bindings` helpers for comparisons:

```java
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ThresholdBinding {
    public static void main(String[] args) {
        IntegerProperty value = new SimpleIntegerProperty(5);
        int threshold = 10;

        BooleanBinding isAboveThreshold = Bindings.greaterThan(value, threshold);

        System.out.println("value=" + value.get() + ", isAboveThreshold=" + isAboveThreshold.get());  // false

        value.set(15);
        System.out.println("value=" + value.get() + ", isAboveThreshold=" + isAboveThreshold.get());  // true

        value.set(10);
        System.out.println("value=" + value.get() + ", isAboveThreshold=" + isAboveThreshold.get());  // false (10 is not greater than 10)
    }
}
```

**`Bindings.greaterThan(a, b)`** returns a `BooleanBinding` that is true when `a.get() > b`. Similar methods exist for less-than, equal-to, etc. The binding updates whenever the observable (here `value`) changes.

## Chained Dependencies

A depends on B, and C depends on A. Changing B should propagate to A and then to C:

```java
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ChainedBinding {
    public static void main(String[] args) {
        IntegerProperty b = new SimpleIntegerProperty(1);
        IntegerProperty a = new SimpleIntegerProperty(0);
        IntegerProperty c = new SimpleIntegerProperty(0);

        a.bind(b);   // a follows b
        c.bind(a);   // c follows a

        System.out.println("b=" + b.get() + ", a=" + a.get() + ", c=" + c.get());  // 1, 1, 1

        b.set(10);
        System.out.println("b=" + b.get() + ", a=" + a.get() + ", c=" + c.get());  // 10, 10, 10
    }
}
```

When `b` is set to 10, `a` is updated (because it is bound to `b`), and then `c` is updated (because it is bound to `a`). Bindings propagate along the chain.

## When to Use Which Binding

- **Unidirectional (`bind`)**: Use when one value should always follow another and the target should not be changed directly. Example: a label's text that displays a value from the model; the label should only reflect the model, not be edited.
- **Bidirectional (`bindBidirectional`)**: Use when two properties represent the same logical value and either might be updated. Example: the same field in the model and in a text field; editing either should update the other.
- **Computed bindings (`Bindings.add`, `createStringBinding`, `format`, etc.)**: Use when the displayed or stored value is **derived** from one or more observables (sum, concatenation, formatting, comparison). The result is read-only and stays in sync with its dependencies.

In a real JavaFX app, you will often combine these: e.g. a computed binding for a total, bound unidirectionally to a label, and bidirectional binding between a text field and a property in the model.
