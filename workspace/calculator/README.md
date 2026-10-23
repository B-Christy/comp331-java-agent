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

![Calculator Test Results](Test-Results.png)
