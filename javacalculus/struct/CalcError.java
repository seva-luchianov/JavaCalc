package javacalculus.struct;

import java.io.Serializable;
import javacalculus.core.CALC;

public class CalcError implements CalcObject, Serializable {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CalcError;
    }

    @Override
    public boolean isNumber() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CalcSymbol getHeader() {
        return CALC.ERROR;
    }

    @Override
    public CalcObject evaluate() {
        return this;
    }

    @Override
    public String toString() {
        return "Error";
    }

    @Override
    public int compareTo(CalcObject obj) {
        return -1;
    }

    @Override
    public int getHierarchy() {
        return CalcObject.SYMBOL;
    }

    @Override
    public int getPrecedence() {
        return -1;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
