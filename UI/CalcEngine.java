package UI;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import java.util.ArrayList;
import javacalculus.core.CalculusEngine;

public class CalcEngine {

    private CalculusEngine calc = new CalculusEngine();
    private static double RECT_WIDTH = 1.E-005D;
    private static double INTERSECT_ACCURACY = 9.999999999999999E-012D;
    private String theta = "Θ";

    public double integrateMSum(double aLimit, double bLimit, String flop) {
        String function1 = flop.replace(theta, "x");
        double negate;
        double aLim;
        double bLim;
        if (aLimit < bLimit) {
            aLim = aLimit;
            bLim = bLimit;
            negate = 1.0D;
        } else {
            aLim = bLimit;
            bLim = aLimit;
            negate = -1.0D;
        }
        Calculable f1 = null;
        try {
            f1 = new ExpressionBuilder(function1).withVariableNames(new String[]{"x", "pi", "e"}).build();

            f1.setVariable("pi", Math.PI);
            f1.setVariable("e", Math.E);
        } catch (UnknownFunctionException | UnparsableExpressionException ex) {
        }
        double integralResult = 0.0D;
        double rectWidth = 1.0D;
        double div = 1.0D;
        while (rectWidth > RECT_WIDTH) {
            rectWidth = (bLim - aLim) / div;
            div += 1.0D;
        }

        for (double i = aLim + (rectWidth / 2); i < bLim; i += rectWidth) {
            f1.setVariable("x", i);
            integralResult += rectWidth * calculate(f1);
        }

        return negate * integralResult;
    }

    public double integrateMSum(double aLimit, double bLimit, String function1, String function2) {
        double negate;
        double aLim;
        double bLim;
        if (aLimit < bLimit) {
            aLim = aLimit;
            bLim = bLimit;
            negate = 1.0D;
        } else {
            aLim = bLimit;
            bLim = aLimit;
            negate = -1.0D;
        }
        Calculable f1 = null;
        Calculable f2 = null;
        try {
            f1 = new ExpressionBuilder(function1).withVariableNames(new String[]{"x", "pi", "e"}).build();

            f1.setVariable("pi", Math.PI);
            f1.setVariable("e", Math.E);
        } catch (UnknownFunctionException | UnparsableExpressionException ex) {
        }
        try {
            f2 = new ExpressionBuilder(function2).withVariableNames(new String[]{"x", "pi", "e"}).build();

            f2.setVariable("pi", Math.PI);
            f2.setVariable("e", Math.E);
        } catch (UnknownFunctionException | UnparsableExpressionException ex) {
        }
        double integralResult = 0.0D;
        double rectWidth = 1.0D;
        double div = 1.0D;
        while (rectWidth > RECT_WIDTH) {
            rectWidth = (bLim - aLim) / div;
            div += 1.0D;
        }

        for (double i = aLim + (rectWidth / 2); i < bLim; i += rectWidth) {
            f1.setVariable("x", i);
            f2.setVariable("x", i);
            integralResult += rectWidth * Math.abs(calculate(f1) - calculate(f2));
        }

        return negate * integralResult;
    }

    public double integrate(double aLimit, double bLimit, String function1, String function2) {
        double negate;
        double aLim;
        double bLim;
        if (aLimit < bLimit) {
            aLim = aLimit;
            bLim = bLimit;
            negate = 1.0D;
        } else {
            aLim = bLimit;
            bLim = aLimit;
            negate = -1.0D;
        }
        Calculable f1 = null;
        Calculable f2 = null;
        try {
            f1 = new ExpressionBuilder(function1).withVariableNames(new String[]{"x", "pi", "e"}).build();

            f1.setVariable("pi", Math.PI);
            f1.setVariable("e", Math.E);
        } catch (UnknownFunctionException | UnparsableExpressionException ex) {
        }
        try {
            f2 = new ExpressionBuilder(function2).withVariableNames(new String[]{"x", "pi", "e"}).build();

            f2.setVariable("pi", Math.PI);
            f2.setVariable("e", Math.E);
        } catch (UnknownFunctionException | UnparsableExpressionException ex) {
        }
        ArrayList<Double> intersects = new ArrayList<>();
        for (double i = aLim; i < bLim; i += 0.01D) {
            f1.setVariable("x", i);
            f2.setVariable("x", i);
            if (Math.abs(calculate(f1) - calculate(f2)) <= INTERSECT_ACCURACY) {
                intersects.add(Double.valueOf(i));
            }
        }

        ArrayList limits = new ArrayList();
        limits.add(Double.valueOf(aLim));
        for (Double testInt : intersects) {
            double d = testInt.doubleValue();
            if ((d > aLim) && (d < bLim)) {
                limits.add(Double.valueOf(d));
            }
        }
        limits.add(Double.valueOf(bLim));
        double integralSum = 0.0D;
        for (int i = 0; i < limits.size() - 1; i++) {
            double testVal = (((Double) limits.get(i)).doubleValue() + ((Double) limits.get(i + 1)).doubleValue()) / 2.0D;
            f1.setVariable("x", testVal);
            f2.setVariable("x", testVal);
            if (calculate(f1) > calculate(f2)) {
                double currIntegral = integrateLimits(((Double) limits.get(i)).doubleValue(), ((Double) limits.get(i + 1)).doubleValue(), function1) - integrateLimits(((Double) limits.get(i)).doubleValue(), ((Double) limits.get(i + 1)).doubleValue(), function2);
                integralSum += currIntegral;
                //System.out.println(currIntegral);
                //System.out.println("f1 is on top");
            } else {
                double currIntegral = integrateLimits(((Double) limits.get(i)).doubleValue(), ((Double) limits.get(i + 1)).doubleValue(), function2) - integrateLimits(((Double) limits.get(i)).doubleValue(), ((Double) limits.get(i + 1)).doubleValue(), function1);
                integralSum += currIntegral;
                //System.out.println(currIntegral);
                //System.out.println("f2 is on top");
            }
        }
        return negate * integralSum;
    }

    public double limitDerivative(double xCoord, Calculable func) {
        func.setVariable("x", xCoord);
        double y1 = func.calculate();
        func.setVariable("x", xCoord - RECT_WIDTH);
        double y2 = func.calculate();
        double littleToTheLeft = (y2 - y1) / (-RECT_WIDTH);
        func.setVariable("x", xCoord + RECT_WIDTH);
        y2 = func.calculate();
        double littleToTheRight = (y2 - y1) / (RECT_WIDTH);
        return (littleToTheLeft + littleToTheRight) / 2;
    }

    public double integrateLimits(double aLimit, double bLimit, String function1) {
        Calculable integral = null;
        try {
            integral = new ExpressionBuilder(integrateFunction(function1)).withVariableNames(new String[]{"x", "pi", "e", "c"}).build();

            integral.setVariable("pi", Math.PI);
            integral.setVariable("e", Math.E);
            integral.setVariable("c", 0.0D);
        } catch (UnknownFunctionException | UnparsableExpressionException ex) {
        }
        integral.setVariable("x", bLimit);
        double integralResult = integral.calculate();
        integral.setVariable("x", aLimit);
        integralResult -= integral.calculate();
        return integralResult;
    }

    public double integrateLimits(double aLimit, double bLimit, Calculable function1) {
        Calculable integral = function1;
        integral.setVariable("x", bLimit);
        double integralResult = integral.calculate();
        integral.setVariable("x", aLimit);
        integralResult -= integral.calculate();
        return integralResult;
    }

    public String integrateFunction(String function) throws ArithmeticException {
        String funInCaps = function.replace(theta, "x").toUpperCase();
        funInCaps = funInCaps.replace("X", "x");
        funInCaps = funInCaps.replace("LOG", "LN");
        String output = this.calc.execute("INT(" + funInCaps + ",x)");
        if (output.equals("Error")) {
            throw new ArithmeticException();
        }
        output = output.toLowerCase().replace("ln", "log");
        return output + "+c";
    }

    public String deriveFunction(String function) {
        String funInCaps = function.replace(theta, "x").toUpperCase();
        funInCaps = funInCaps.replace("X", "x");
        funInCaps = funInCaps.replace("LOG", "LN");
        String output;
        output = this.calc.execute("DIFF(" + funInCaps + ",x)");
        if (output.equals("Error")) {
            output = this.calc.execute("DIFF(EXPAND(" + funInCaps + "),x)");
        }
        if (output.equals("Error")) {
            throw new ArithmeticException();
        }
        output = output.toLowerCase().replace("ln", "log");
        return output;
    }

    public double calculate(Calculable function) {
        try {
            return function.calculate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0.0D;
    }
}