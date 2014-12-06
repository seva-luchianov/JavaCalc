package javacalculus.evaluator.extend;

import java.io.Serializable;
import javacalculus.struct.*;

public abstract class Calc1ParamFunctionEvaluator implements CalcFunctionEvaluator, Serializable {

    @Override
    public CalcObject evaluate(CalcFunction input) {
        if (input.size() == 1) {
            CalcObject parameter = input.get(0);
            //parameter = CALC.SYM_EVAL(parameter);
            CalcObject returnVal = evaluateObject(parameter);
            if (returnVal != null) {
                return returnVal;
            } else if (parameter instanceof CalcInteger) {
                return evaluateInteger((CalcInteger) parameter);
            } else if (parameter instanceof CalcDouble) {
                return evaluateDouble((CalcDouble) parameter);
            } else if (parameter instanceof CalcFraction) {
                return evaluateFraction((CalcFraction) parameter);
            } else if (parameter instanceof CalcSymbol) {
                return evaluateSymbol((CalcSymbol) parameter);
            } else if (parameter instanceof CalcFunction) {
                return evaluateFunction((CalcFunction) parameter);
            } else {
                return input;
            }
        } else {
            return null; //function has more than 1 parameter. This evaluator does not apply so return null
        }
    }

    protected abstract CalcObject evaluateObject(CalcObject input);

    protected abstract CalcObject evaluateInteger(CalcInteger input);

    protected abstract CalcObject evaluateDouble(CalcDouble input);

    protected abstract CalcObject evaluateFraction(CalcFraction input);

    protected abstract CalcObject evaluateSymbol(CalcSymbol input);

    protected abstract CalcObject evaluateFunction(CalcFunction input);
}
