# Boundary Value Analysis: Introduction

Boundary value analysis (BVA) is a test case design technique. The idea is simple: when an input has limits, many defects appear at or near those limits.

What are limits? Examples:
* A number must be between 0 and 100
* A string must be at least 3 characters long
* A date must be in the future
* A file must be less than 100MB
* A password must be at least 8 characters long
* A username must be at least 3 characters long
* A username must be less than 20 characters long
* A username must not contain any special characters

## What Is BVA?

BVA selects test values at the **edges** of an input domain:

- the smallest allowed value,
- the largest allowed value,
- and values just inside or just outside those limits.

You can use this when designing unit tests, integration tests, or API validation tests.

## Why Boundaries Matter

Boundary defects are very common:

- off-by-one mistakes,
- using `<` when `<=` was intended,
- using `>` when `>=` was intended,
- fence-post errors in loops and indexing, meaning you forgot to test the first or last element of a collection.

Testing only "middle" values often misses these issues.

## Scope of BVA

BVA focuses on boundary-related faults. It is usually combined with other test design approaches (for example equivalence partitioning), not used as a full replacement for all testing.

## What This Learning Path Covers

- In page `2`, how to choose boundary values for a simple valid range.
- In page `3`, how open/half-open ranges and multiple partitions affect BVA values.
- In page `4`, how to apply BVA when writing practical test cases.


---

# The Valid Range and Test Values

This page focuses on one input variable with a closed valid range: **[min, max]**.

## Example Range

Assume valid values are from **1 to 100** (inclusive).

BVA test values are:

- `min - 1` -> `0`, just outside the lower boundary
- `min` -> `1`, the lower boundary
- `min + 1` -> `2`, just inside the lower boundary
- `max - 1` -> `99`, just inside the upper boundary
- `max` -> `100`, the upper boundary
- `max + 1` -> `101`, just outside the upper boundary


It can be illustrated as a number line:

```text
Valid range [1, 100]

invalid      boundary      inside ... inside      boundary      invalid
   |            |                                   |              |
---+------------+------------+----------- ... ------+--------------+---
   0            1            2                     100            101
```

## Why These Specific Values?

- `1` and `100` test the exact accepted boundaries.
- `2` and `99` test values just inside the boundaries.
- `0` and `101` test values just outside the boundaries.

For `0` and `101`, tests should verify **rejection** (for example false result, error, or exception), not acceptance.

## 4-Value vs 6-Value Variant

Above, we used 6 values, two of which were "just inside", but not "on" the boundary. This is the 6-value variant.

You might cut down to 4 values, by using only the "just outside", and "on" the boundary, at both ends. This is the 4-value variant.

The 6-value boundary set is usually better when you specifically want strong boundary coverage. And, really, creating these tests are fairly quick, so better just be safe.

## Summary

If a method accepts integers from `1` to `100`, BVA suggests at least: `0, 1, 2, 99, 100, 101`.


---

# Ranges and Partitions

Boundary value analysis changes slightly when the specification is not a simple closed range.

## Open, Closed, and Half-Open Ranges

A closed range includes both ends: `[min, max]`.

But specs may use open or half-open intervals.

Example: valid values are `(0, 100]`.

- `0` is invalid (left side is open)
- `1` is the first valid value
- `100` is valid
- `101` is invalid

Useful BVA-style values here: `0, 1, 2, 99, 100, 101`.

This is just a slightly rephrased version of the 6-value variant, on the previous page. The point is just to be vigilant about the boundaries.

## Multiple Partitions

Sometimes one input has several partitions, not just one valid interval.

Example:

- `x < 0` -> invalid
- `0..50` -> low
- `51..100` -> high
- `x > 100` -> invalid

Then apply boundary checks at **each partition border**:

- around 0: `-1, 0, 1`
- around 50/51: `49, 50, 51`
- around 100: `99, 100, 101`

This gives focused test values that target transition points between partitions.

## Quick Rule

Whenever behavior changes at a value, that value and its neighbors are boundary candidates.


---

# Designing Test Cases with BVA

This page shows how to apply boundary value analysis while writing test cases.

## Step-by-Step Use

1. Identify inputs with ranges or partitions.
2. Mark each boundary where behavior may change.
3. Pick boundary and near-boundary values.
4. Define expected outcomes (accept/reject, result, or exception).
5. Add these as explicit test cases.

## BVA and Equivalence Partitioning

A common combination is:

- first split input into equivalence classes (valid/invalid or sub-ranges),
- then apply BVA at each class boundary.

This gives better coverage with fewer tests than random value selection.

I have another article on Equivalence Partitioning.

## Worked Example (One Input)

Rule: score must be in range `[0, 100]`.

Suggested BVA test inputs and expected outcomes:

| Input | Expected |
|------:|----------|
| -1    | reject   |
| 0     | accept   |
| 1     | accept   |
| 99    | accept   |
| 100   | accept   |
| 101   | reject   |

### Optional Minimal Java Example

```java
public static boolean isValidScore(int score) {
    return score >= 0 && score <= 100;
}
```

```java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ScoreValidatorTest {
    @Test void bvaScoreRange() {
        assertFalse(isValidScore(-1));
        assertTrue(isValidScore(0));
        assertTrue(isValidScore(1));
        assertTrue(isValidScore(99));
        assertTrue(isValidScore(100));
        assertFalse(isValidScore(101));
    }
}
```

## Optional Two-Input Idea

If a method has two ranged inputs, hold one input at a **fixed** value (e.g. a typical value in the middle of its range) while you boundary-test the other, then swap.

Example: test boundaries of `A` while `B` is held fixed, then test boundaries of `B` while `A` is held fixed.

## Summary

BVA means testing at and around boundaries. This catches many boundary defects quickly.

Values just outside the valid range should usually assert **rejection**. BVA is strongest when combined with other focused test design techniques.
