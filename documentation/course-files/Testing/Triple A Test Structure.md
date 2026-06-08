# Tripple A Test Structure: Introduction

Arrange-Act-Assert (AAA) is a simple way to structure unit tests so they are easy to read and easy to maintain.

## What Is AAA?

- **Arrange**: prepare input data and test setup.
- **Act**: execute the behavior under test.
- **Assert**: verify the expected result.

This keeps a test focused: setup first, _one_ main action second, verification last.

## Why This Structure Helps

- Faster reading: you can scan what the test prepares, does, and checks.
- Easier maintenance: changes are localized to the right section.
- Better debugging: failures are easier to interpret when structure is clear.

## Setup-Execute-Verify (SEV)

You can also call the same structure **Setup-Execute-Verify**.

- Arrange = Setup
- Act = Execute
- Assert = Verify

SEV is an equivalent, dyslexic-friendly phrasing of AAA. Both names refer to the same three-phase test structure.

## What This Learning Path Covers

- In page `2`, each phase in detail.
- In page `3`, common mistakes and fixes.
- In page `4`, full practical examples you can copy.


---

# The Three Phases

This page explains each phase of Arrange-Act-Assert in detail.

## Arrange

Use this section to prepare everything needed before the action:

- create objects,
- prepare input values,
- configure simple test doubles if needed.

Do not run the behavior under test here. Do not assert here.

## Act

Run the one primary behavior being tested.

- usually one method call,
- one clear action.

Avoid multiple unrelated actions in the same test.

## Assert

Check that the result matches expectations.

You can verify:

- return value,
- object state,
- thrown exception (`assertThrows`).

Keep assertions focused on the intended behavior.

## ASCII Template

```text
// Arrange (Setup)
...

// Act (Execute)
...

// Assert (Verify)
...
```

## Tiny Java/JUnit Example

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AaaStructureExampleTest {

    @Test
    void shouldReturnSumForTwoNumbers() {
        // Arrange (Setup)
        int a = 2;
        int b = 3;

        // Act (Execute)
        int result = a + b;

        // Assert (Verify)
        assertEquals(5, result);
    }
}
```


---

# Common Mistakes

Even when teams know AAA, tests can drift away from the structure. This page shows common mistakes and quick fixes.

## 1) Arrange After Act

**Bad**, wrong order:

```java
int result = calculator.add(2, 3);
int expected = 5;
assertEquals(expected, result);
```

**Also bad**, magic number:

```java
int result = calculator.add(2, 3);
assertEquals(5, result);
```

Do extract the expected value to a variable, and use that variable in the assertion. It just clarifies your test.

**Better**:

```java
int expected = 5;                 // Arrange
int result = calculator.add(2, 3); // Act
assertEquals(expected, result);    // Assert
```

## 2) Multiple Acts in One Test

Bad:

```java
int sum = calculator.add(2, 3);
int product = calculator.multiply(2, 3);
assertEquals(5, sum);
assertEquals(6, product);
```

Better: split into two tests, one behavior per test.

## 3) Assertions Mixed Into Arrange

Bad:

```java
int a = 2;
assertTrue(a > 0);
int result = a + 3;
assertEquals(5, result);
```

Better: keep verification in Assert/Verify section.

## 4) No Clear Separation

Bad:

```java
int a = 2; int b = 3; int result = a + b; assertEquals(5, result);
```

Better: use explicit sections and spacing for readability.

## 5) Over-Asserting Unrelated Behavior

Bad:

```java
assertEquals(5, result);
assertNotNull(logger);
assertEquals("v1", buildVersion);
```

Better: assert the behavior this test is about; move unrelated checks to other tests.

## Quick Rule

One test should usually have one clear Act step and assertions that verify that specific behavior. More, smaller tests are better than fewer, larger tests.


---

# Practical Examples

This page gives complete AAA examples with simple logic.

## Example 1: Add Two Numbers

```java
@Test
void shouldReturnSumWhenAddingTwoNumbers() {
    // Arrange (Setup)
    int a = 4;
    int b = 6;

    // Act (Execute)
    int result = a + b;

    // Assert (Verify)
    assertEquals(10, result);
}
```

## Example 2: String Normalization

```java
@Test
void shouldTrimAndLowercaseText() {
    // Arrange (Setup)
    String raw = "  HeLLo  ";

    // Act (Execute)
    String normalized = raw.trim().toLowerCase();

    // Assert (Verify)
    assertEquals("hello", normalized);
}
```

## Example 3: Exception Case

```java
@Test
void shouldThrowWhenDividingByZero() {
    // Arrange (Setup)
    int value = 10;

    // Act + Assert (Execute + Verify)
    assertThrows(ArithmeticException.class, () -> {
        int ignored = value / 0;
    });
}
```

In exception tests, Act and Assert can be combined inside `assertThrows`.

## Final Checklist

- Can I identify Arrange/Act/Assert quickly?
- Is there one primary Act behavior?
- Do my assertions verify the intended behavior?
- Am I avoiding unrelated asserts in this test?
- Is the test readable by someone else in 10 seconds?
