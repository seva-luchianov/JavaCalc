package javacalculus.evaluator.extend;

import java.io.Serializable;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcObject;

public class CalcNullEvaluator implements CalcFunctionEvaluator, Serializable {

    @Override
    public CalcObject evaluate(CalcFunction input) {
        return null;
    }

}
