package functions;

import functions.meta.Composition;
import functions.meta.Mult;
import functions.meta.Power;
import functions.meta.Scale;
import functions.meta.Shift;
import functions.meta.Sum;

public final class Functions {
    private Functions() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }

    /**
     * Numerically integrates the given function on [leftX; rightX] using the trapezoidal rule.
     *
     * @param function function to integrate
     * @param leftX    left border of integration segment (inclusive)
     * @param rightX   right border of integration segment (inclusive)
     * @param step     discretization step (positive)
     * @return approximate integral value
     * @throws IllegalArgumentException if borders are outside the function domain or step is non-positive
     */
    public static double integrate(Function function, double leftX, double rightX, double step) {
        if (function == null) {
            throw new IllegalArgumentException("Function must not be null");
        }
        if (!(step > 0.0)) {
            throw new IllegalArgumentException("Step must be positive");
        }
        if (rightX < leftX) {
            throw new IllegalArgumentException("Right border must not be less than left border");
        }
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Integration borders are outside function domain");
        }

        double length = rightX - leftX;
        if (length == 0.0) {
            return 0.0;
        }

        int fullSteps = (int) Math.floor(length / step);
        double x = leftX;
        double result = 0.0;

        for (int i = 0; i < fullSteps; i++) {
            double xNext = x + step;
            double y1 = function.getFunctionValue(x);
            double y2 = function.getFunctionValue(xNext);
            result += (y1 + y2) * 0.5 * step;
            x = xNext;
        }

        double remainder = rightX - x;
        if (remainder > 0.0) {
            double y1 = function.getFunctionValue(x);
            double y2 = function.getFunctionValue(rightX);
            result += (y1 + y2) * 0.5 * remainder;
        }

        return result;
    }
}
