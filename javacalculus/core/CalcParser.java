package javacalculus.core;

import java.util.ArrayList;

import javacalculus.evaluator.CalcSUB;
import javacalculus.exception.CalcSyntaxException;
import javacalculus.exception.CalcUnsupportedException;
import javacalculus.struct.CalcDouble;
import javacalculus.struct.CalcFraction;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcInteger;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

public final class CalcParser {

    /**
     * These static constants define what values the token can take based on
     * what currentChar is. This keeps track of the TYPE of currentChar.
     */
    private final static int CALC_NULL = 0, //end of line/file or unsupported token
            CALC_POWER = 1, //exponentiation
            CALC_MULTIPLY = 2, //multiply
            CALC_DIVIDE = 3, //divide
            CALC_SUBTRACT = 4, //subtract
            CALC_ADD = 5, //add
            CALC_PARENTHESISOPEN = 6, //open parenthesis
            CALC_PARENTHESISCLOSE = 7, //close parenthesis
            CALC_MATRIXOPEN = 8, //open matrix declaration
            CALC_MATRIXCLOSE = 9, //close matrix declaration
            CALC_IDENTIFIER = 10, //variable names. function names, symbolic anything
            CALC_DIGIT = 11, //numbers
            CALC_COMMA = 12, //commas (mostly used in function argument list)
            CALC_DEFINE = 13, //variable assignment (i.e. x=10, f(x)=x+4, etc)
            CALC_FACTORIAL = 14, //factorial (x!)
            CALC_ABS = 15;				//absolute value (|x|)
    private String inputString;
    private char currentChar;
    private int currentCharIndex;
    private int token;

    /**
     * Constructor
     *
     * @param StringIn The string that needs to be converted into a CalcObject
     * hierarchy
     */
    public CalcParser(String StringIn) {
        inputString = StringIn;
    }

    /**
     * Empty constructor
     */
    public CalcParser() {
        inputString = null;
    }

    public CalcObject parse(String input) throws CalcSyntaxException {
        inputString = input;
        currentChar = ' ';
        currentCharIndex = 0;
        token = CALC_NULL;
        return parse();
    }

    public CalcObject parse() throws CalcSyntaxException {
        parseNextToken(); //initialize the token for the parser

        CalcObject returnVal = parseDefine(); //step through the precedence levels, starting with highest -> define

        return returnVal;
    }

    /**
     * Identifies the next sequence of characters by a unique int stored in
     * global variable <b>token</b>
     *
     * @throws CalcSyntaxException
     */
    private void parseNextToken() throws CalcSyntaxException {
        while (inputString.length() > currentCharIndex) {
            currentChar = inputString.charAt(currentCharIndex++);
            token = CALC_NULL;

            if (currentChar != '\n' && currentChar != '\t'
                    && currentChar != '\r' && currentChar != ' ') { //make sure the char is not terminating or whitespace
                if ((currentChar >= 'a' && currentChar <= 'z') //is the char a letter (identifier)?
                        || (currentChar >= 'A' && currentChar <= 'Z')) {

                    token = CALC_IDENTIFIER;
                    return;
                }

                if (currentChar >= '0' && currentChar <= '9') { //is the char a number?
                    token = CALC_DIGIT;
                    return;
                }

                switch (currentChar) { //brute force identify the char and store the identification in token
                    case '(':
                        token = CALC_PARENTHESISOPEN;
                        break;
                    case ')':
                        token = CALC_PARENTHESISCLOSE;
                        break;
                    case '[':
                        token = CALC_MATRIXOPEN;
                        break;
                    case ']':
                        token = CALC_MATRIXCLOSE;
                        break;
                    case ',':
                        token = CALC_COMMA;
                        break;
                    case '^':
                        token = CALC_POWER;
                        break;
                    case '+':
                        token = CALC_ADD;
                        break;
                    case '-':
                        token = CALC_SUBTRACT;
                        break;
                    case '*':
                        token = CALC_MULTIPLY;
                        break;
                    case '/':
                        token = CALC_DIVIDE;
                        break;
                    case '=':
                        token = CALC_DEFINE;
                        break;
                    case '!':
                        token = CALC_FACTORIAL;
                        break;
                    case '|':
                        token = CALC_ABS;
                        break;
                    default:
                        throw new CalcSyntaxException("Unidentified character: " + currentChar);
                }

                if (token == CALC_NULL) {
                    throw new CalcSyntaxException("No token identified");
                }

                return;
            }
        }

        //end of loop variable reset
        currentCharIndex = inputString.length() + 1;
        currentChar = ' ';
        token = CALC_NULL;

    }

    private CalcObject parseDefine() throws CalcSyntaxException {
        CalcObject returnVal = parseExpression(); //get next precendence level: expression
        int tempToken;

        while (token == CALC_DEFINE) {
            tempToken = token;
            parseNextToken();

            if (tempToken == CALC_DEFINE) {
                returnVal = CALC.DEFINE.createFunction(returnVal, parseExpression());
            }
        }
        return returnVal;
    }

    private CalcObject parseExpression() throws CalcSyntaxException {

        int tempToken;
        CalcObject returnVal;

        if (token == CALC_ADD || token == CALC_SUBTRACT) {
            tempToken = token;
            parseNextToken();

            if (tempToken == CALC_SUBTRACT) {
                returnVal = parseMultiplication(true);
            } else {
                returnVal = parseMultiplication(false);
            }
        } else {
            returnVal = parseMultiplication(false);
        }

        if (token == CALC_ADD || token == CALC_SUBTRACT) {
            CalcFunction returnFunction = CALC.ADD.createFunction(returnVal);

            while (token == CALC_ADD || token == CALC_SUBTRACT) {
                if (token == CALC_ADD) {
                    parseNextToken();
                    returnFunction.add(parseMultiplication(false));
                } else {
                    parseNextToken();
                    returnFunction.add(parseMultiplication(true));
                }
            }

            return returnFunction;
        } else {
            return returnVal;
        }
    }

    private CalcObject parseMultiplication(boolean isNegative) throws CalcSyntaxException {
        CalcObject returnVal = parseDivision();

        if (isNegative) { //handle negated case
            if (returnVal instanceof CalcInteger) {
                returnVal = ((CalcInteger) returnVal).multiply(CALC.NEG_ONE);
            } else if (returnVal instanceof CalcFraction) {
                returnVal = ((CalcFraction) returnVal).multiply(CALC.NEG_ONE);
            } else {
                returnVal = CALC.MULTIPLY.createFunction(CALC.NEG_ONE, returnVal);
            }
        }

        if (token != CALC_MULTIPLY) {
            return returnVal;
        }

        CalcFunction returnFunction = new CalcFunction(CALC.MULTIPLY, returnVal);

        while (token == CALC_MULTIPLY) {
            parseNextToken();
            returnFunction.add(parseDivision());
        }

        return returnFunction;

    }

    /**
     * Parses division precedence expression. The reason why I did not create or
     * use a separate "Divide" evaluation class, instead using multiply to
     * negative one power, is because of the properties of the multiply function
     * over the divide function (same with add over subtract) -> commutativity
     * and associativity. Makes life a lot easier in evaluation but will
     * probably come back to haunt me. Delete this comment when this issue is
     * resolved.
     *
     * @return the parsed hierarchy tree at division level
     * @throws CalcSyntaxException
     */
    private CalcObject parseDivision() throws CalcSyntaxException {
        CalcObject numerator = parsePower();
        CalcObject denominator;

        if (token != CALC_DIVIDE) {
            return numerator;
        }

        parseNextToken();

        denominator = parsePower();

        if (token != CALC_DIVIDE) {
            if (numerator instanceof CalcInteger && denominator instanceof CalcInteger) {
                if (denominator == CALC.ZERO) {
                    throw new CalcSyntaxException("Division by zero.");
                }
                return new CalcFraction((CalcInteger) numerator, (CalcInteger) denominator);
            } else {
                CalcFunction reciprocal = CALC.POWER.createFunction(denominator, CALC.NEG_ONE);
                return new CalcFunction(CALC.MULTIPLY, numerator, reciprocal);
            }
        }

        CalcFunction reciprocal = CALC.POWER.createFunction(denominator, CALC.NEG_ONE);
        CalcFunction function = new CalcFunction(CALC.MULTIPLY, numerator, reciprocal);

        while (token == CALC_DIVIDE) { //handle continued fraction expressions
            parseNextToken();
            function.add(CALC.POWER.createFunction(parsePower(), CALC.NEG_ONE));
        }

        return function;
    }

    private CalcObject parsePower() throws CalcSyntaxException {
        CalcObject returnVal = parseFactorial();

        if (token != CALC_POWER) {
            return returnVal;
        }

        while (token == CALC_POWER) {
            parseNextToken();
            CalcFunction function = new CalcFunction(CALC.POWER, returnVal);
            function.add(parseFactorial());
            returnVal = function;
        }

        return returnVal;
    }

    private CalcObject parseFactorial() throws CalcSyntaxException {
        CalcObject returnVal = parseAbs();

        while (token == CALC_FACTORIAL) {
            parseNextToken();
            returnVal = CALC.FACTORIAL.createFunction(returnVal);
        }

        return returnVal;
    }

    private CalcObject parseAbs() throws CalcSyntaxException {
        CalcObject returnVal;

        if (token == CALC_ABS) {
            parseNextToken();

            returnVal = CALC.ABS.createFunction(parseExpression());

            if (token != CALC_ABS) {
                throw new CalcSyntaxException("Missing close vertical bar");
            }

            parseNextToken();

            return returnVal;
        } else {
            return parseTerm();
        }
    }

    private CalcObject parseTerm() throws CalcSyntaxException {
        CalcObject returnVal;

        if (token == CALC_SUBTRACT) {
            parseNextToken();
            return CALC.MULTIPLY.createFunction(CALC.NEG_ONE, parseTerm());
        }
        if (token == CALC_IDENTIFIER) {
            CalcSymbol id = parseIdentifier();//also sets values for defined variables

            if (token == CALC_PARENTHESISOPEN) {
                return parseFunction(id);
            } else if (CALC.hasDefinedVariable(id)) {
                return CALC.getDefinedVariable(id);
            } else {
                return id;
            }
        }
        if (token == CALC_DIGIT) {
            return parseNumber();
        }
        if (token == CALC_PARENTHESISOPEN) {

            parseNextToken();

            returnVal = parseExpression(); //return all the way to root recursion

            if (token != CALC_PARENTHESISCLOSE) {
                throw new CalcSyntaxException("Missing close parenthesis");
            }

            parseNextToken();

            return returnVal;
        }

        switch (token) {
            case CALC_PARENTHESISCLOSE:
                throw new CalcSyntaxException("Extra closing parenthesis");
        }

        throw new CalcSyntaxException("Unable to parse term: " + currentChar);
    }

    private CalcObject parseFunction(CalcSymbol symbol) throws CalcSyntaxException {
        CalcFunction function = new CalcFunction(symbol);
        parseNextToken();

        if (token == CALC_PARENTHESISCLOSE) {
            parseNextToken();
            return function;
        }

        parseParameters(function);

        if (token == CALC_PARENTHESISCLOSE) {
            parseNextToken();
            return function;
        }

        throw new CalcSyntaxException("Expecting '('");
    }

    private void parseParameters(CalcFunction inputFunction) throws CalcSyntaxException {
        while (true) {	//this scares the SHIT out of me but I have to
            inputFunction.add(parseExpression());

            if (token != CALC_COMMA) {
                break; //no more parameters
            }
            parseNextToken();
        }
    }

    private CalcSymbol parseIdentifier() throws CalcSyntaxException {
        StringBuffer identifier = new StringBuffer();

        identifier.append(currentChar);

        parseNextChar();

        while ((currentChar >= 'a' && currentChar <= 'z')
                || (currentChar >= 'A' && currentChar <= 'Z')
                || (currentChar >= '0' && currentChar <= '9')) {
            identifier.append(currentChar);
            parseNextChar();
        }

        currentCharIndex--;

        parseNextToken();

        CalcSymbol symbol;

        if (CALC.isUpperCase(identifier.toString())) { //if the symbol is all upper case, it must be a built in function
            CalcSymbol temp;
            if ((temp = CALC.getSymbol(identifier.toString())) != null) {
                symbol = temp;
            } else {
                throw new CalcUnsupportedException(identifier.toString());
            }
        } else {
            symbol = new CalcSymbol(identifier);
            if (CALC.hasDefinedVariable(symbol)) { //if the symbol is a defined variable, use a substitution evaluator
                symbol.setEvaluator(new CalcSUB());
                //return CALC.getDefinedVariable(symbol);
            }
        }

        return symbol;
    }

    private CalcObject parseNumber() throws CalcSyntaxException {
        StringBuffer numberString = new StringBuffer();
        boolean IsFloating = false;

        numberString.append(currentChar); //append first digit

        parseNextChar();
        //append any digits beyond the first, including decimal place
        while ((currentChar >= '0' && currentChar <= '9') || currentChar == '.') {
            if (currentChar == '.') {
                if (IsFloating) {
                    break;
                }
                IsFloating = true;
                numberString.append(currentChar);
                parseNextChar();
            } else {
                numberString.append(currentChar);
                parseNextChar();
            }
        }

        currentCharIndex--;
        parseNextToken();

        /*if (!(currentChar >= '0' && currentChar <= '9') && currentChar != ',' && currentChar != ']') {		
         if (IsFloating) {
         return CALC.MULTIPLY.createFunction(new CalcDouble(numberString.toString()), parseTerm());
         }
         else {
         return CALC.MULTIPLY.createFunction(new CalcInteger(numberString.toString()), parseTerm());
         }
         }*/

        if (IsFloating) {
            return new CalcDouble(numberString.toString());
        } else {
            //LETS TRY THIS THING
            return new CalcInteger(numberString.toString());
            //return new CalcDouble(numberString.toString() + ".0");
        }
    }

    private void parseNextChar() {
        if (inputString.length() > currentCharIndex) {
            currentChar = inputString.charAt(currentCharIndex++);
            return;
        }

        currentCharIndex = inputString.length() + 1;
        currentChar = ' ';
        token = CALC_NULL;
    }

    @Override
    public String toString() {
        return inputString;
    }
}