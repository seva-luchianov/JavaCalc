/**
 *
 */
package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.*;

public class CalcTAYLOR implements CalcFunctionEvaluator {

    /**
     *
     */
    public CalcTAYLOR() {
    }

    @Override
    public CalcObject evaluate(CalcFunction function) { //TAYLOR(function, variable, center (default 0), terms (default 10))
        if (function.size() < 2) {
            throw new CalcWrongParametersException("TAYLOR -> Wrong number of parameters");
        } else if (function.get(1) instanceof CalcSymbol) {
            if (function.size() > 2 && function.get(2).isNumber()) {
                if (function.size() > 3 && function.get(3) instanceof CalcInteger) {	//TAYLOR(function, variable, center, terms)
                    return constructTaylor((CalcFunction) function.get(0), (CalcSymbol) function.get(1), new CalcDouble(function.get(2).toString()), (CalcInteger) function.get(3));
                } else if (function.size() < 4) { //TAYLOR(function, variable, center, default(10))
                    return constructTaylor((CalcFunction) function.get(0), (CalcSymbol) function.get(1), new CalcDouble(function.get(2).toString()), new CalcInteger(10));
                } else {
                    throw new CalcWrongParametersException("TAYLOR -> 4th parameter syntax");
                }
            } else if (function.size() < 3) { //TAYLOR(function, variable, default(0.0), default(10))
                return constructTaylor((CalcFunction) function.get(0), (CalcSymbol) function.get(1), new CalcDouble(0.0D), new CalcInteger(10));
            } else {
                throw new CalcWrongParametersException("TAYLOR -> 3rd parameter syntax");
            }
        } else {
            throw new CalcWrongParametersException("TAYLOR -> 2nd parameter syntax");
        }
    }

    private CalcFunction constructTaylor(CalcFunction function, CalcSymbol variable, CalcDouble center, CalcInteger terms) {
        CalcFunction taylorPolynomial = CALC.ADD.createFunction();

        for (CalcInteger ii = CALC.ZERO; ii.compareTo(terms) < 0; ii = ii.add(CALC.ONE)) {
            CalcObject denominator = CALC.POWER.createFunction(CALC.FACTORIAL.createFunction(ii), CALC.NEG_ONE);
            CalcObject differentialTerm = CalcSUB.numericSubstitute(CALC.SYM_EVAL(CALC.DIFF.createFunction(function, variable, ii)), variable, center);
            center.negate();
            CalcObject variableTerm = CALC.POWER.createFunction(CALC.ADD.createFunction(variable, center), ii);
            CalcObject taylorTerm = CALC.MULTIPLY.createFunction(differentialTerm, variableTerm, denominator);
            taylorPolynomial.add(taylorTerm);
        }
        return taylorPolynomial;
    }
}
