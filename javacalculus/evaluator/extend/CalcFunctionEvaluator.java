package javacalculus.evaluator.extend;

import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcObject;

public interface CalcFunctionEvaluator {

    public CalcObject evaluate(CalcFunction input);
}
