package UI;

import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import javax.swing.JOptionPane;

public class EvalThread extends Thread {

    private MathCalcGUI overlord;
    public boolean weGotAnError = false;

    public EvalThread(MathCalcGUI m) {
        overlord = m;
    }

    @Override
    public void run() {
        try {
            String functionToBeProcessed;
            boolean findResult = false;
            if (overlord.integralMode.isSelected()) {
                overlord.displayDerivativeLine = false;
                findResult = (overlord.aLimit != null && overlord.bLimit != null);
                if (!overlord.polarMode) {
                    if (overlord.isRotate.isSelected()) {
                        if (overlord.f2 == null && !overlord.xAxisRotation) {
                            overlord.function2.setText("0");
                            try {
                                overlord.f2 = new ExpressionBuilder("0").withVariableNames(new String[]{"x"}).build();
                            } catch (UnknownFunctionException | UnparsableExpressionException ex) {
                            }
                        }
                        if (overlord.aLimit == null) {
                            weGotAnError = true;
                            JOptionPane.showMessageDialog(overlord, "Please specify the A limit", "Error", 2);
                        }
                        if (overlord.bLimit == null) {
                            weGotAnError = true;
                            JOptionPane.showMessageDialog(overlord, "Please specify the B limit", "Error", 2);
                        }
                        findResult = true;
                        if (overlord.xAxisRotation) {
                            functionToBeProcessed = "pi*abs((" + overlord.function1.getText() + " - " + overlord.axisOfRotation.calculate() + ")^2 - (" + overlord.function2.getText() + " - " + overlord.axisOfRotation.calculate() + ")^2)";
                        } else {
                            functionToBeProcessed = "2.0*pi*(abs(x-" + overlord.axisOfRotation.calculate() + "))*(abs(" + overlord.function1.getText() + "-" + overlord.function2.getText() + "))";
                        }
                    } else {
                        if (overlord.f2 == null) {
                            functionToBeProcessed = overlord.function1.getText();
                        } else {
                            functionToBeProcessed = "abs(" + overlord.function1.getText() + "-" + overlord.function2.getText() + ")";
                        }
                    }
                } else {
                    functionToBeProcessed = "0.5*(" + overlord.function1.getText() + ")^2";
                }
                if (overlord.actuallyOperate) {
                    String result = overlord.calcEngine.integrateFunction(functionToBeProcessed);
                    try {
                        overlord.resultFunction = new ExpressionBuilder(result).withVariableNames(new String[]{"x", "pi", "e", "c"}).build();
                        overlord.resultFunction.setVariable("pi", Math.PI);
                        overlord.resultFunction.setVariable("e", Math.E);
                        overlord.resultFunction.setVariable("c", 0);
                    } catch (UnknownFunctionException | UnparsableExpressionException ex) {
                        JOptionPane.showMessageDialog(overlord, ex.getMessage(), "Error", 2);
                    }
                    if (!overlord.disableResultFunction) {
                        overlord.resultFunctionTextField.setText(result);
                    }
                    if (findResult) {
                        overlord.displayIntegralArea = true;
                        double resultVal = overlord.calcEngine.integrateLimits(overlord.aLimit.calculate(), overlord.bLimit.calculate(), overlord.resultFunction);
                        String tryVal = "" + overlord.roundFourDecimals((resultVal / Math.PI));
                        System.out.println(tryVal);
                        if (isInt(tryVal)) {
                            overlord.resultTextBox.setText("pi*" + tryVal);
                        } else {
                            overlord.resultTextBox.setText("" + resultVal);
                        }
                    }
                } else {
                    if (!overlord.disableResultFunction) {
                        overlord.resultFunctionTextField.setText("INTEGRAL(" + functionToBeProcessed + ")");
                    }
                    if (findResult) {
                        overlord.displayIntegralArea = true;
                        double resultVal = overlord.calcEngine.integrateMSum(overlord.aLimit.calculate(), overlord.bLimit.calculate(), functionToBeProcessed);
                        String tryVal = "" + overlord.roundFourDecimals((resultVal / Math.PI));
                        System.out.println(tryVal);
                        if (isInt(tryVal)) {
                            overlord.resultTextBox.setText("pi*" + tryVal);
                        } else {
                            overlord.resultTextBox.setText("" + resultVal);
                        }
                    }
                }
            } else if (overlord.derivativeMode.isSelected()) {
                overlord.displayIntegralArea = false;
                findResult = overlord.aLimit != null;
                functionToBeProcessed = overlord.function1.getText();
                if (overlord.actuallyOperate) {
                    String result = overlord.calcEngine.deriveFunction(functionToBeProcessed);
                    try {
                        overlord.resultFunction = new ExpressionBuilder(result).withVariableNames(new String[]{"x", "pi", "e"}).build();
                        overlord.resultFunction.setVariable("pi", Math.PI);
                        overlord.resultFunction.setVariable("e", Math.E);
                    } catch (UnknownFunctionException | UnparsableExpressionException ex) {
                        JOptionPane.showMessageDialog(overlord, ex.getMessage(), "Error", 2);
                    }
                    if (!overlord.disableResultFunction) {
                        overlord.resultFunctionTextField.setText(result);
                    }
                    if (findResult) {
                        overlord.displayDerivativeLine = true;
                        overlord.resultFunction.setVariable("x", overlord.aLimit.calculate());
                        double resultVal = overlord.resultFunction.calculate();
                        String tryVal = "" + overlord.roundFourDecimals((resultVal / Math.PI));
                        System.out.println(tryVal);
                        if (isInt(tryVal)) {
                            overlord.resultTextBox.setText("pi*" + tryVal);
                        } else {
                            overlord.resultTextBox.setText("" + resultVal);
                        }
                    }
                } else {
                    if (!overlord.disableResultFunction) {
                        overlord.resultFunctionTextField.setText("DERIVATIVE(" + functionToBeProcessed + ")");
                    }
                    if (findResult) {
                        overlord.displayDerivativeLine = true;
                        double resultVal = overlord.calcEngine.limitDerivative(overlord.aLimit.calculate(), overlord.f1);
                        String tryVal = "" + overlord.roundFourDecimals((resultVal / Math.PI));
                        System.out.println(tryVal);
                        if (isInt(tryVal)) {
                            overlord.resultTextBox.setText("pi*" + tryVal);
                        } else {
                            overlord.resultTextBox.setText("" + resultVal);
                        }
                    }
                }
            }
        } catch (Exception e) {
            weGotAnError = true;
        }
    }

    public boolean isInt(String thing) {
        try {
            return Double.parseDouble(thing) == (new Double(thing).intValue());
        } catch (Exception e) {
            return false;
        }
    }
}
