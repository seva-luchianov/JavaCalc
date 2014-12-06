package javacalculus.core;

import java.util.logging.Logger;

import javacalculus.exception.CalcSyntaxException;
import javacalculus.struct.CalcObject;

public final class CalculusEngine {
    private final static Logger LOGGER = Logger.getLogger(CalculusEngine.class.getCanonicalName());
    private String result = "No commands executed.";
    private long currentTime, deltaTime;
    private CalcParser parser;

    /**
     * Constructor
     */
    public CalculusEngine() {
        CALC.operator_notation = true;
        CALC.max_recursion_depth = 6;
        CALC.full_integrate_mode = true;

        parser = new CalcParser();
    }

    /**
     * This is the most important function in CalculusEngine. The user specifies
     * an input that is sent through the algorithm, producing a mathematical
     * output that satisfies the grammar used in the command.
     * 
     * @param command
     * @return The result obtained by parsing and evaluating <b>command</b>.
     */
    public String execute(String command) {
        CalcObject parsed = null;
        try {
            //currentTime = System.nanoTime();
            parsed = parser.parse(command);
        } catch (CalcSyntaxException e) {
            e.printStackTrace();
        }
        //String s = CALC.SYM_EVAL(parsed).toString();
        //System.out.println(s);
        //CALC.toggleOperatorNotation();
        return CALC.SYM_EVAL(parsed).toString();
    }
    
    public String calc(String command) throws CalcSyntaxException{
        return CALC.SYM_EVAL(parser.parse(command)).toString();
    }

    /**
     * Executes the command and include some debugging stats.
     * 
     * @param command
     * @return
     */
    public String executeWithStats(String command) {
        currentTime = System.nanoTime();
        String processed = execute(command);
        deltaTime = System.nanoTime() - currentTime;
        result = "Input: " + command + "\n";
        result = "Output: " + processed + "\n";
        result += "Time used: " + deltaTime + " nanoseconds\n";
        return result;
    }

    /**
     * 
     * @return the previous result obtained by <b>execute</b>
     */
    public String getResult() {
        return result;
    }

    /**
     * Set the floating point precision to <b>precision</b> digits
     * 
     * @param precision
     */
    public void setPrecision(int precision) {
        CALC.setMathContext(precision);
    }
}
