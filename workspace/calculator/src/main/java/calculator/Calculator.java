package calculator;

public class Calculator {

    private enum Operation {
        ADD {
            @Override
            double apply(double first, double second) {
                return first + second;
            }
        },
        SUBTRACT {
            @Override
            double apply(double first, double second) {
                return first - second;
            }
        },
        MULTIPLY {
            @Override
            double apply(double first, double second) {
                return first * second;
            }
        },
        DIVIDE {
            @Override
            double apply(double first, double second) {
                if (Double.doubleToRawLongBits(second) == 0L) {
                    throw new IllegalArgumentException("Cannot divide by zero");
                }
                return first / second;
            }
        };

        abstract double apply(double first, double second);
    }

    public double add(double first, double second) {
        return Operation.ADD.apply(first, second);
    }

    public double subtract(double first, double second) {
        return Operation.SUBTRACT.apply(first, second);
    }

    public double multiply(double first, double second) {
        return Operation.MULTIPLY.apply(first, second);
    }

    public double divide(double first, double second) {
        return Operation.DIVIDE.apply(first, second);
    }
}
