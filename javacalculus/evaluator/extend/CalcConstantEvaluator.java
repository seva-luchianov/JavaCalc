/**
 *
 */
package javacalculus.evaluator.extend;

import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcObject;

public class CalcConstantEvaluator implements CalcFunctionEvaluator {

    CalcObject constant;

    /**
     *
     * @param obj
     */
    public CalcConstantEvaluator(CalcObject obj) {
        constant = obj;
    }

    /* (non-Javadoc)
     * @see javacalculus.evaluator.CalcFunctionEvaluator#evaluate(javacalculus.struct.CalcFunction)
     */
    @Override
    public CalcObject evaluate(CalcFunction input) {
        return null;
    }

    public CalcObject getValue() {
        return constant;
    }

}
