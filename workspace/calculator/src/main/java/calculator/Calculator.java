package calculator;

public class Calculator {

    private enum Operation {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE
    }

    public double add(double first, double second) {
        return calculate(first, second, Operation.ADD);
    }

    public double subtract(double first, double second) {
        return calculate(first, second, Operation.SUBTRACT);
    }

    public double multiply(double first, double second) {
        return calculate(first, second, Operation.MULTIPLY);
    }

    public double divide(double first, double second) {
        return calculate(first, second, Operation.DIVIDE);
    }

    private double calculate(double first, double second, Operation operation) {
        switch (operation) {
            case ADD:
                return first + second;
            case SUBTRACT:
                return first - second;
            case MULTIPLY:
                return first * second;
            case DIVIDE:
                if (second == 0.0) {
                    throw new IllegalArgumentException("Cannot divide by zero");
                }
                return first / second;
            default:
                throw new AssertionError("Unexpected operation: " + operation);
        }
    }
}
