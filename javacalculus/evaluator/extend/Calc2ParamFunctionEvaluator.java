package javacalculus.evaluator.extend;

import java.io.Serializable;
import javacalculus.core.CALC;
import javacalculus.struct.CalcDouble;
import javacalculus.struct.CalcFraction;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcInteger;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

public abstract class Calc2ParamFunctionEvaluator implements CalcFunctionEvaluator, Serializable {

    @Override
    public CalcObject evaluate(CalcFunction input) {
        if (input.size() == 2) {
            CalcObject returnVal = evaluateBinary(input.get(0), input.get(1));
            return (returnVal == null) ? input : returnVal;
        } else {
            return null;
        }
    }

    protected CalcObject evaluateBinary(CalcObject parameter1, CalcObject parameter2) {

        CalcObject returnVal = evaluateObject(parameter1, parameter2);

        if (returnVal != null) {
            return returnVal;
        } else if (parameter1 instanceof CalcInteger) {
            if (parameter2 instanceof CalcInteger) {
                return evaluateInteger((CalcInteger) parameter1, (CalcInteger) parameter2);
            }
            if (parameter2 instanceof CalcFraction) {
                return evaluateFraction(new CalcFraction((CalcInteger) parameter1, CALC.ONE), (CalcFraction) parameter2);
            }
            if (parameter2 instanceof CalcDouble) {
                return evaluateDouble(new CalcDouble((CalcInteger) parameter1), (CalcDouble) parameter2);
            }
        } else if (parameter1 instanceof CalcFraction) {
            if (parameter2 instanceof CalcInteger) {
                return evaluateFraction((CalcFraction) parameter1, new CalcFraction((CalcInteger) parameter2, CALC.ONE));
            }
            if (parameter2 instanceof CalcFraction) {
                return evaluateFraction(new CalcFraction((CalcInteger) parameter1, CALC.ONE), (CalcFraction) parameter2);
            }
        } else if (parameter1 instanceof CalcDouble) {
            if (parameter2 instanceof CalcDouble) {
                return evaluateDouble((CalcDouble) parameter1, (CalcDouble) parameter2);
            }
            if (parameter2 instanceof CalcInteger) {
                return evaluateDouble((CalcDouble) parameter1, new CalcDouble((CalcInteger) parameter2));
            }
        } else if (parameter1 instanceof CalcFunction) {
            if (parameter2 instanceof CalcFunction) {
                return evaluateFunction((CalcFunction) parameter1, (CalcFunction) parameter2);
            }
            if (parameter2 instanceof CalcInteger) {
                return evaluateFunctionAndInteger((CalcFunction) parameter1, (CalcInteger) parameter2);
            }
        } else if (parameter1 instanceof CalcSymbol) {
            if (parameter2 instanceof CalcSymbol) {
                return evaluateSymbol((CalcSymbol) parameter1, (CalcSymbol) parameter2);
            }
        } else {
            return null;
        }

        return null;
    }

    protected abstract CalcObject evaluateObject(CalcObject input1, CalcObject input2);

    protected abstract CalcObject evaluateInteger(CalcInteger input1, CalcInteger input2);

    protected abstract CalcObject evaluateDouble(CalcDouble input1, CalcDouble input2);

    protected abstract CalcObject evaluateFraction(CalcFraction input1, CalcFraction input2);

    protected abstract CalcObject evaluateSymbol(CalcSymbol input1, CalcSymbol input2);

    protected abstract CalcObject evaluateFunction(CalcFunction input1, CalcFunction input2);

    protected abstract CalcObject evaluateFunctionAndInteger(CalcFunction input1, CalcInteger input2);
}
