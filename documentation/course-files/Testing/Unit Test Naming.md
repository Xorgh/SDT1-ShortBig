# Unit Test Naming: Introduction

Unit test names are part of your documentation. When a test fails, the name should quickly tell you what behavior is broken.

## Why Test Naming Matters

Good test names make it easier to:

- understand failures quickly,
- maintain tests over time,
- communicate intent to teammates (which includes future you).

A name like `test1` gives almost no information. A name like `shouldReturnSum_WhenAddingTwoPositiveNumbers` immediately tells the expected behavior.

## Behavior Over Implementation

Prefer names that describe **observable behavior**, not private implementation details.

For example, "returns rejected status for negative input" is stronger than "calls validateInternalFlag".

## Scope

There is no single perfect naming convention for all teams. The key is to choose a convention that is readable for your context and use it consistently across the project.

## What This Learning Path Covers

- In page `2`, common naming conventions with examples.
- In page `3`, pros and cons of each style.
- In page `4`, practical rules for choosing one style and applying it consistently.


---

# Common Naming Conventions

This page introduces several naming styles for unit tests. Each style can work if applied consistently.

Do note that names are often long, and you may mix various naming styles, like camelCase and using underscores, to more clearly indicate what is being tested and how.

## 1) MethodName_WhenSomething_ExpectedResult

Example:

```java
@Test
void add_whenTwoPositiveNumbers_returnsSum() {
    assertEquals(5, 2 + 3);
}
```

The structures is "unit under test", then the particular test case, and finally the expected result.


## 2) Given_When_Then

Example:

```java
@Test
void givenTwoPositiveNumbers_whenAdd_thenReturnsSum() {
    assertEquals(5, 2 + 3);
}
```

This is a somewhat common approach, but the "unit under test", i.e. the specific method `add()` is somewhat burried in the name. Moving the unit under test up front can be a benefit, unless the entire class focuses on this single same unit.

## 3) Should Style

Example:

```java
@Test
void shouldReturnSumWhenAddingTwoPositiveNumbers() {
    assertEquals(5, 2 + 3);
}
```

## 4) UnitUnderTest_State_Expected (classic)

Example:

```java
@Test
void add_TwoPositiveNumbers_ReturnsSum() {
    assertEquals(5, 2 + 3);
}
```

## 5) Sentence-like Business Behavior Style

Example:

```java
@Test
void returns_total_price_with_tax_for_standard_rate() {
    assertEquals(125, 100 + 25);
}
```

## Mini Comparison

Same behavior, different naming styles:

| Convention | Example |
|------------|---------|
| MethodName_ExpectedResult | `add_whenTwoPositiveNumbers_returnsSum` |
| Given_When_Then | `givenTwoPositiveNumbers_whenAdd_thenReturnsSum` |
| Should style | `shouldReturnSumWhenAddingTwoPositiveNumbers` |
| UnitUnderTest_State_Expected | `add_TwoPositiveNumbers_ReturnsSum` |
| Sentence-like business style | `returns_total_price_with_tax_for_standard_rate` |

The naming pattern changes, but the tested behavior is the same.


---

# Pros and Cons of Naming Styles

No naming convention is perfect in every situation. This page compares the common trade-offs.

## Quick Comparison

| Convention | Pros | Cons |
|-----------|------|------|
| MethodName_ExpectedResult | Clear structure, readable, easy to scan | Can become long for complex scenarios |
| Given_When_Then | Very explicit context/action/outcome | Verbose; can feel repetitive |
| Should style | Natural language, good readability | Can become long if over-detailed |
| UnitUnderTest_State_Expected | Familiar classic format, clear separators | Mixed casing/underscores can look inconsistent |
| Sentence-like business style | Business intent is very visible | Can be too domain-heavy or very long |

## Practical Trade-offs

- **Readability**: explicit behavior-oriented names are easiest to understand during failures.
- **Verbosity**: very long names can become noisy in code reviews and reports.
- **Consistency pressure**: teams must follow one pattern to avoid mixed style clutter.
- **Report scanning**: compact but descriptive names are easiest to scan in test runners.
- **Beginner suitability**: Given/When/Then and Should styles are often easiest for beginners to learn.

## Optional Note on JUnit Display Names

If you use JUnit display names, method names can be shorter while display names carry readable sentences. Without display names, method naming quality matters even more.

Example:

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SumDisplayNameTest {

    @Test
    @DisplayName("returns sum when adding two positive numbers")
    void returnsSum() {
        assertEquals(5, 2 + 3);
    }
}
```

The result is that the test name is shorter, but the named displayed in the test runner overview is more readable. It is, however, more to "text" to maintain.

## Guidance

Short names are quick to type but may hide meaning. Very long names may over-explain. Aim for behavior-first names that are specific enough to diagnose failures.


---

# Practical Guidelines and Examples

This page gives a practical way to adopt a naming convention and keep it consistent.

## Recommended Process

1. Pick one naming convention for your course/project.
2. Apply it to all new tests.
3. Emphasize observable behavior in names.
4. Avoid naming tests after private/internal implementation details.

## Before/After Rename Examples

```text
test1                -> shouldReturnSumWhenAddingTwoPositiveNumbers
sumTest              -> givenTwoPositiveNumbers_whenAdd_thenReturnsSum
validationTest       -> rejectScoreWhenValueIsAboveMaximum
nullCase             -> shouldThrowWhenInputIsNull
```


## Pre-commit Checklist for Test Names

- Does the name describe behavior, not internals?
- Is expected outcome clear?
- Is naming style consistent with the rest of the test class/project?
- Is the name specific enough to understand a failure quickly?
- Is it concise enough to remain readable?

Consistent naming will make your tests easier to use long after they were written.


---

# Nested classes as organization

Nested classes can be used to organize tests into logical groups. This is useful when you have a lot of tests, and you want to be able to easily find the tests you are looking for.

It also organizes the test runner view according to the nested structure.

Here is a basic example:

```java
public class TestCalculator
{
    @Nested 
    class Add
    {
        @Test
        public void shouldFailWhenAIsTooSmall(){
        }

        @Test
        public void shouldPassWhenAIsLargerThanZero(){
        }

        //.. more tests for Add
    }

    @Nested 
    class Divide
    {
        @Test
        public void shouldFailWhenBIsSmallerThanZero(){
        }

        //.. more tests for Divide
    }
}
```

Here, I have a class, `TestCalculator`, which will contain unit tests for the `Calculator` class.

I then create a nested class, `Add`, which will contain unit tests for the `add` method of the `Calculator` class.

And another nested class, `Divide`, which will contain unit tests for the `divide` method of the `Calculator` class.

I could further nest classes inside `Add` for success and failure scenarios, or something else.

The output then organizes the tree accordingly:

![nested classes](Unit Test Naming/Resources/NestedClassesAsTestOrganization.java.png)

I have personally played a bit with this approach, but did not really find the right structure.