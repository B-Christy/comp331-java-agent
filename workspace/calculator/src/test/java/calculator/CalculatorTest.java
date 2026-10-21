package calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addsTwoNumbers() {
        assertEquals(5.0, calculator.add(2.0, 3.0), 0.000001);
    }

    @Test
    void subtractsTwoNumbers() {
        assertEquals(6.0, calculator.subtract(9.0, 3.0), 0.000001);
    }

    @Test
    void multipliesTwoNumbers() {
        assertEquals(12.0, calculator.multiply(3.0, 4.0), 0.000001);
    }

    @Test
    void dividesTwoNumbers() {
        assertEquals(2.5, calculator.divide(5.0, 2.0), 0.000001);
    }

    @Test
    void supportsNegativeNumbers() {
        assertEquals(-5.0, calculator.add(-2.0, -3.0), 0.000001);
    }

    @Test
    void supportsZeroAsAnOperand() {
        assertEquals(0.0, calculator.multiply(0.0, 7.0), 0.000001);
    }

    @Test
    void rejectsDivisionByZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.divide(10.0, 0.0)
        );
    }
}
