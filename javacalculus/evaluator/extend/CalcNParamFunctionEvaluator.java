package javacalculus.evaluator.extend;

import java.io.Serializable;
import javacalculus.struct.*;

public abstract class CalcNParamFunctionEvaluator extends Calc2ParamFunctionEvaluator implements Serializable {

    @Override
    public CalcObject evaluate(CalcFunction input) {
        if (input.size() == 2) {
            return super.evaluate(input);
        } else if (input.size() > 2) {
            CalcSymbol symbol = input.getHeader();
            CalcFunction result = new CalcFunction(symbol);
            CalcObject part;
            CalcObject current = input.get(0);
            boolean evaluated = false;
            int index = 1;

            while (index < input.size()) {
                part = super.evaluateBinary(current, input.get(index));

                if (part == null) {
                    for (int ii = index + 1; ii < input.size(); ii++) {
                        part = super.evaluateBinary(current, input.get(ii));

                        if (part != null) {
                            evaluated = true;
                            current = part;
                            input.remove(ii);
                            break;
                        }
                    }

                    if (part == null) {
                        result.add(current);

                        if (index == input.size() - 1) {
                            result.add(input.get(index));
                        } else {
                            current = input.get(index);
                        }
                        index++;
                    }
                } else {
                    evaluated = true;
                    current = part;
                    if (index == (input.size() - 1)) {
                        result.add(current);
                    }

                    index++;
                }
            }

            if (evaluated) {
				//if (symbol.hasProperty(CalcSymbol.ASSOCIATIVE)) {
                //	result = result.associativeSimplify();
                //}
                if ((result.size() == 1) && symbol.hasProperty(CalcSymbol.UNIPARAM_IDENTITY)) {
                    return result.get(0);
                }
                return result;
            }
            return result;
        }
        return null;
    }

}
