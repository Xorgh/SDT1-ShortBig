# Equivalence Partitioning: Introduction

Equivalence partitioning (EP) is a test case design technique where you divide the input domain into groups that should behave the same.

The point is to reduce the number of tests you need to write, by grouping inputs that should behave the same.

## What Is Equivalence Partitioning?

An **equivalence partition** (range of input) is a set of inputs expected to produce the same kind of outcome.

Instead of testing every value, you choose one (or a few) representative values from each partition.

## Core Idea

If all values in a partition should behave the same, testing one well-chosen value can represent that partition.

This gives fewer tests while still covering the key behavior.

## Why It Helps

- Reduces test count.
- Keeps tests focused and systematic.
- Makes expected behavior explicit per partition.

## Where It Is Used

- Unit testing
- Input validation tests
- API parameter validation
- Business rule checks

## Scope

EP does not replace all testing. It is commonly combined with other techniques, especially boundary value analysis (BVA).

## What This Learning Path Covers

- In page `2`, how to identify valid and invalid partitions.
- In page `3`, how to choose representative values.
- In page `4`, how to turn partitions into concrete test cases.


---

# Identifying Partitions

This page focuses on splitting input values into equivalence partitions.

## Valid and Invalid Partitions

When defining partitions, include both:

- **valid partitions** (inputs that should be accepted)
- **invalid partitions** (inputs that should be rejected)

## Example 1: Numeric Rule

Rule: score must be between `0` and `100`.

Possible partitions:

- Partition A: `score < 0` (invalid)
- Partition B: `0 <= score <= 100` (valid)
- Partition C: `score > 100` (invalid)

## Example 2: Category Rule

Rule: category must be `A`, `B`, or `C`.

Possible partitions:

- Partition A: `{A, B, C}` (valid)
- Partition B: everything else (invalid)

## ASCII Visual (Partition Example)

```text
score input:

< 0                  0...............100                  > 100
|--------------------|=================|--------------------|
  invalid partition    valid partition    invalid partition
```

## Quick Checklist

1. Find the input constraint.
2. Split the input into partitions with the same expected behavior.
3. Ensure partitions are non-overlapping for practical purposes.
4. Include invalid partitions, not only valid ones.


---

# Choosing Representative Values

After identifying partitions, choose one representative value from each partition.

## Representative Value Rule

Pick one (or a few) value(s) that is clearly inside each partition.

For EP specifically, avoid focusing on boundaries here. Boundary-focused values belong primarily to Boundary Value Analysis (BVA).

## Example Table (Score 0..100)

| Partition | Representative | Expected |
|----------|----------------|----------|
| `score < 0` | `-5`  | reject |
| `0..100`    | `50`  | accept |
| `score > 100` | `150` | reject |

## Why This Works

Each representative stands for one partition. If the representative behaves as expected, it increases confidence that the partition behavior is implemented correctly.

## ASCII Mini Visual

```text
Partitions:     [ invalid ]   [ valid ]   [ invalid ]
Values:             -5           50          150
```

## EP and BVA

- **EP** chooses partitions and one value per partition.
- **BVA** adds extra focus on class edges (boundary and near-boundary values).

They complement each other well.


---

# Designing Test Cases with EP

This page shows how to use equivalence partitioning to design concrete test cases.

## Step-by-Step

1. List the input condition(s).
2. Define valid and invalid partitions.
3. Pick one representative per partition.
4. Write expected outcome for each representative.
5. Add one test case per representative (minimum EP set).

## Worked Example

Rule: score must be in `[0, 100]`.

EP representatives:

- `-5` -> reject
- `50` -> accept
- `150` -> reject

### Minimal Test Case Table

| Input | Expected |
|------:|----------|
| -5    | reject   |
| 50    | accept   |
| 150   | reject   |

### Optional Minimal Java/JUnit Snippet

```java
public static boolean isValidScore(int score) {
    return score >= 0 && score <= 100;
}
```

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreValidatorEpTest {
    @Test
    void epScoreValidation() {
        assertFalse(isValidScore(-5));
        assertTrue(isValidScore(50));
        assertFalse(isValidScore(150));
    }
}
```

## Optional Two-Input Note

If there are two inputs, begin with one representative per partition for each input.

To avoid too many combinations, start with a simple set and extend only when needed.

## Summary

EP gives compact, structured tests by covering behavior classes instead of individual values.

It is very effective for input validation and pairs naturally with BVA for boundary-focused coverage.
