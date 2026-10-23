## Acceptance criteria

Run all automated checks from this directory with `mvn test`.

| Criterion                                                                                          | How it is checked                                                                                            |
| -------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| Addition, subtraction, multiplication, and division return the correct result for ordinary inputs. | `addsTwoNumbers`, `subtractsTwoNumbers`, `multipliesTwoNumbers`, and `dividesTwoNumbers` in `CalculatorTest` |
| Addition works with negative operands.                                                             | `supportsNegativeNumbers`                                                                                    |
| Multiplying an operand by zero returns zero.                                                       | `supportsZeroAsAnOperand`                                                                                    |
| Dividing by zero is rejected with `IllegalArgumentException`.                                      | `rejectsDivisionByZero`                                                                                      |
| All calculator tests pass when run through Maven.                                                  | Run `mvn test`; confirm the build succeeds and the test summary reports no failures or errors.               |

The tests use a small tolerance when comparing decimal results. Human review should also confirm that the test names and assertions match the intended calculator behavior.

### 1st Implementation Test Results:

Commit hash: 9a4a698ecf80ba5333f1c528ab1e92062c92eda2

![Calculator Test Results](../Screenshots/First-Test-Results.png)

### 1st AI Iteration Test Results:

Commit hash: e8a035491794838dd1d00ec70c8e3dee9c96519e

![AI Calculator Test Results](../Screenshots/1st-AI-iteration.png)

### 2nd AI Iteration Test Results:

Commit hash: f6c4f99794a2c4c0b84cf94408e30d58c9c0c9c1

![AI Calculator Test Results 2](../Screenshots/2nd-AI-Iteration.png)
