# Black and White Testing: Introduction

Black-box and white-box testing are two common perspectives for designing tests.

## What Is Black-box Testing?

Black-box testing treats the system as a "box" where we only focus on:

- input,
- output,
- expected behavior from the specification.

The internal code structure is not required to design these tests.

This let's us focus on the behavior of the system, and not the implementation details. Implementation details are often not relevant to the behavior of the system, and can change without the behavior changing.

## What Is White-box Testing?

White-box testing uses knowledge of internal code structure, for example:

- statements,
- branches,
- conditions,
- loops,
- paths.

Tests are designed with awareness of how the implementation is written.

While this can help you get better coverage of the internal logic, it can also be a hindrance. If the implementation changes, the tests may need to be updated.

## Complementary, Not Competing

These approaches are not enemies. They answer different questions:

- Black-box: "Does behavior match the specification?"
- White-box: "Did we exercise important internal logic paths?"

In practice, strong test suites usually combine both.

You may have a particular complicated method, which is _just_ internal detail, but still it is important enough to test explicitly. 

## Where They Are Used

- Unit tests
- API and validation tests
- Integration tests

## What This Learning Path Covers

- In page `2`, black-box approach with simple examples.
- In page `3`, white-box approach with simple branch-oriented examples.
- In page `4`, how to compare and combine both approaches.

```text
Black-box:  Input -> [System] -> Output
White-box:  Input -> [Internal paths/branches] -> Output
```


---

# Black-box Testing

Black-box testing designs tests from the outside view of the system.

Generally, your unit tests are more _robust_ and _stable_ if you use a black-box approach.

This approach is also good together with Test-Driven Development (TDD), as it allows you to write tests before you write the code.

TDD is a development process where you write the tests before you write the code. This is done in a cycle of:

1. Write a test
2. Run the test and see it fail
3. Write the code to make the test pass
4. Run the test and see it pass
5. Refactor the code to make it better
6. Repeat


## Viewpoint

The tester focuses on:

- requirements,
- input/output behavior,
- acceptance or rejection rules.

The internal implementation is not the primary basis for test design.

## Strengths

- Good for validating requirements.
- Less tied to implementation details.
- Usually more stable when code is refactored internally.

## Limitations

- Internal branch/path defects may be missed.
- Internal logic coverage is less visible from behavior alone.

## Simple Example: Score Validation

Rule: score must be between `0` and `100`.

A black-box test set can be described as input -> expected result:

```text
Input   Expected
-5      reject
0       accept
50      accept
100     accept
150     reject
```

This test design comes from the rule, not from inspecting source code.

## Connection to Other Techniques

Equivalence Partitioning and Boundary Value Analysis are common black-box test design techniques:

- EP helps select representative values from input classes.
- BVA emphasizes values at and near boundaries.


---

# White-box Testing

White-box testing designs tests using knowledge of the code internals.

## Viewpoint

The tester looks at implementation structure and asks:

- Which statements were executed?
- Which branches were executed?
- Which conditions were evaluated true/false?

## Simple Structural Targets

At intro level, common targets are:

- **Statement coverage**: execute each statement at least once.
- **Branch coverage**: execute each branch (e.g. true and false of an `if`).
- **Condition checks**: ensure key conditions are evaluated both ways when relevant.

## Strengths

- Finds path-specific and logic defects.
- Reveals untested branches and dead spots.

## Limitations

- More coupled to implementation details.
- Tests may need updates when internal structure changes.

## Tiny Example

```java
public static String classify(int n) {
    if (n < 0) {
        return "negative";
    } else {
        return "non-negative";
    }
}
```

To cover both branches:

- test with `n = -1` (true branch)
- test with `n = 0` (false branch)

ASCII control-flow view:

```text
          [n < 0 ?]
           /     \
        true     false
        /          \
  "negative"   "non-negative"
```


---

# Comparing and Combining

Black-box and white-box testing solve different but related problems.

## Side-by-side Comparison

| Aspect | Black-box | White-box |
|-------|-----------|-----------|
| Perspective | External behavior | Internal structure |
| Design basis | Requirements/specification | Source code paths/branches |
| Strength | Validates expected behavior | Validates internal path execution |
| Blind spot | Hidden internal path defects | Can over-focus on implementation |
| Maintenance | Usually stable across refactors | More sensitive to internal changes |

## Use Them Together

A practical combination is:

- black-box tests to ensure behavior and rules are correct,
- white-box checks to ensure key branches/conditions are exercised.

Ideally, you want to test observable behaviour, not the implementation details. This, however, requires that you have good documentation of the expected behavior. If this is lacking, you will have to look into the code.

If you do TDD, you can only rely on the specification, as you don't even have code to look at.

## Practical Checklist

- Do we have behavior-focused tests based on requirements?
- Do we exercise important branches/conditions in key logic?
- Are tests too tightly coupled to implementation details?
- If internals change, do our behavior tests still express the same expected outcomes?

Balanced use of black-box and white-box testing usually gives clearer, stronger test suites.
