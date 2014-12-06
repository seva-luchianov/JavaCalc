/**
 *
 */
package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.CalcDouble;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

public class CalcSUB implements CalcFunctionEvaluator {

    /**
     * Constructor
     */
    public CalcSUB() {
    }

    /* (non-Javadoc)
     * @see javacalculus.evaluator.CalcFunctionEvaluator#evaluate(javacalculus.struct.CalcFunction)
     */
    @Override
    public CalcObject evaluate(CalcFunction input) {
        CalcSymbol functionName = input.getHeader();

        if (CALC.hasDefinedVariable(functionName)) {
            CalcObject obj = CALC.getDefinedVariable(functionName);
            CalcSymbol key = CALC.getDefinedVariableKey(obj);
            if (obj instanceof CalcFunction) {
                CalcFunction definition = (CalcFunction) obj;
                int inputSize = input.size();
                int definitionSize = key.getNumberOfVariables();
                if (inputSize == definitionSize) {
                    CalcFunction returnVal = definition;

                    for (int ii = 0; ii < inputSize; ii++) {
                        returnVal = traverseAndSubstitute(returnVal, key.getVariable(ii), input.get(ii));
                    }

                    return returnVal;
                } else {
                    throw new CalcWrongParametersException("Number of substitutions does not match definition");
                }
            } else {
                return input;
            }
        } else if (input.size() == 3) {
            CalcObject obj1 = input.get(0);
            CalcObject obj2 = input.get(1);
            CalcObject obj3 = input.get(2);
            if (obj1.isNumber()) {	//if the input function is a number, no substitution required
                return obj1;
            } else if (obj1.equals(obj2)) {	//special case f(x) = x return substitution
                return obj3;
            } else {
                return traverseAndSubstitute((CalcFunction) obj1, (CalcSymbol) obj2, obj3);
            }
        } else {
            throw new CalcWrongParametersException("SUBS -> wrong number of parameters");
        }
//		CalcObject functionDefinition = CALC.getDefinedVariable(input.getHeader());
//		
//		if (functionDefinition == null) return null;
//		
//		if (functionDefinition.isNumber()) {
//			return functionDefinition; //function is a constant. No substitution needed.
//		}
//		else if (functionDefinition instanceof CalcFunction){
//			CalcFunction function = (CalcFunction) functionDefinition;
//			int numberOfVars = CALC.getDefinedVariableKey(function).getNumberOfVariables();
//			if (input.size() != numberOfVars) 
//				throw new CalcWrongParametersException("SUB -> wrong number of variables");
//			else return traverseAndSubstitute(function, input);
//		}
//		else return functionDefinition;	
    }

    public static CalcFunction traverseAndSubstitute(CalcFunction definition, CalcSymbol variable, CalcObject substitution) {
        CalcFunction returnVal = new CalcFunction(definition.getHeader());

        for (int ii = 0; ii < definition.size(); ii++) {
            CalcObject currentTerm = definition.get(ii);
            if (currentTerm.isNumber()) {
                returnVal.add(currentTerm);
            } else if (currentTerm.equals(variable)) {
                returnVal.add(substitution);
            } else if (currentTerm instanceof CalcFunction) {
                returnVal.add(traverseAndSubstitute((CalcFunction) currentTerm, variable, substitution));
            } else {
                returnVal.add(currentTerm);
            }
        }

        return returnVal;
    }

    public static CalcObject numericSubstitute(CalcObject input, CalcSymbol variable, CalcDouble number) {
        if (input.isNumber()) {
            return input;
        }
        if (input instanceof CalcSymbol) {
            return number;
        }
        CalcFunction inputFunction = (CalcFunction) input;
        CalcFunction result = new CalcFunction(input.getHeader());

        for (CalcObject obj : inputFunction) {
            if (obj.equals(variable)) {
                result.add(number);
            } else if (obj instanceof CalcFunction) {
                result.add(numericSubstitute((CalcFunction) obj, variable, number));
            } else {
                result.add(obj);
            }
        }
        return result;
    }
}
