package UI;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MathCalcGUI extends JFrame
        implements Runnable {

    public Thread animation = null;
    public Graphics offScreen;
    public Image image;
    public double xScale = 36.0D;
    public double yScale = 25.0D;
    public Calculable aLimit = null;
    public Calculable bLimit = null;
    public Calculable axisOfRotation;
    public boolean xAxisRotation = true;
    public boolean displayIntegralArea = false;
    public boolean displayDerivativeLine = false;
    public Calculable f1 = null;
    public Calculable f2 = null;
    public Calculable resultFunction = null;
    public CalcEngine calcEngine;
    public boolean disableResultFunction = false;
    public JLabel ResultFunctionLabel;
    public JTextField aIntegralLimit;
    public JLabel aLabel;
    public JTextField bIntegralLimit;
    public JLabel bLabel;
    public JPanel backgroundPanel;
    public JRadioButton derivativeMode;
    public JButton findResultButton;
    public JTextField function1;
    public JTextField function2;
    public JRadioButton integralMode;
    public JCheckBox isRotate;
    public JCheckBox lockScaleCheckBox;
    public JTextField resultFunctionTextField;
    public JLabel resultLabel;
    public JTextField resultTextBox;
    public JLabel rotateAboutLabel;
    public JTextField rotateAboutTextField;
    public JPanel topPanel;
    public JSlider xScaleSlider;
    public JLabel y1Label;
    public JLabel y2Label;
    public JSlider yScaleSlider;
    public String theta = "Θ";
    public boolean polarMode = false;
    public boolean actuallyOperate = true;

    public MathCalcGUI() {
        initComponents();
        this.image = createImage(800, 700);
        //this.image = createImage(720, 500);
        this.offScreen = this.image.getGraphics();
        this.backgroundPanel.grabFocus();
        //this.setComponentZOrder(backgroundPanel, 0);
        //super.setComponentZOrder(this, 1);
        System.setProperty("exp4j.unary.precedence.high", "false");
        try {
            this.f1 = new ExpressionBuilder("x").withVariableNames(new String[]{"x", "pi", "e"}).build();
            //this.aLimit = new ExpressionBuilder("0").build();
            //this.bLimit = new ExpressionBuilder("0").build();
            this.axisOfRotation = new ExpressionBuilder("0").build();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        this.f1.setVariable("pi", Math.PI);
        this.f1.setVariable("e", Math.E);
        this.calcEngine = new CalcEngine();
        start();
    }

    public final void start() {
        this.function1.setText("x");
        this.function2.setText("");
        if (this.animation == null) {
            this.animation = new Thread(this, "Thread");
            this.animation.start();
        }
    }

    @Override
    public void run() {
        Thread myThread = Thread.currentThread();
        while (this.animation == myThread) {
            try {
                Thread.sleep(15L);
            } catch (InterruptedException e) {
            }
        }
    }

    public void update() {
        repaint(15L, 40, 50, 720, 500);
        //repaint(15L);
        //backgroundPanel.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(offScreen);
        if (this.displayIntegralArea && !polarMode) {
            this.offScreen.setColor(Color.decode("#AF7817"));
            double bLim;
            double aLim;
            if (this.calculate(aLimit) < this.calculate(bLimit)) {
                aLim = this.calculate(aLimit);
                bLim = this.calculate(bLimit);
            } else {
                aLim = this.calculate(bLimit);
                bLim = this.calculate(aLimit);
            }
            for (double i = aLim; i < bLim; i += 1.0D / (this.xScale * 2.0D)) {
                if (this.f2 != null) {
                    double y1 = calculate(this.f1, i);
                    double y2 = calculate(this.f2, i);
                    this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(y1 * this.yScale), (int) xdc(i * this.xScale), (int) ydc(y2 * this.yScale));
                    if (this.isRotate.isSelected()) {
                        if (!this.xAxisRotation) {
                            double x1 = this.calculate(axisOfRotation) - (i - this.calculate(axisOfRotation));
                            this.offScreen.drawLine((int) xdc(x1 * this.xScale), (int) ydc(y1 * this.yScale), (int) xdc(x1 * this.xScale), (int) ydc(y2 * this.yScale));
                        } else {
                            y1 = this.calculate(axisOfRotation) - (calculate(this.f1, i) - this.calculate(axisOfRotation));
                            y2 = this.calculate(axisOfRotation) - (calculate(this.f2, i) - this.calculate(axisOfRotation));
                            this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(y1 * this.yScale), (int) xdc(i * this.xScale), (int) ydc(y2 * this.yScale));
                        }
                    }
                } else {
                    double y1 = calculate(this.f1, i);
                    if (this.isRotate.isSelected()) {
                        double newY1 = this.calculate(axisOfRotation) - (y1 - this.calculate(axisOfRotation));
                        this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(y1 * this.yScale), (int) xdc(i * this.xScale), (int) ydc(newY1 * this.yScale));
                    } else {
                        this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(0.0D), (int) xdc(i * this.xScale), (int) ydc(y1 * this.yScale));
                    }
                }
            }
        }
        if (!polarMode) {
            this.offScreen.setColor(Color.decode("#348017"));
            if (this.xAxisRotation) {
                this.offScreen.drawLine(40, (int) ydc(this.calculate(axisOfRotation) * this.yScale), 760, (int) ydc(this.calculate(axisOfRotation) * this.yScale));
            } else {
                this.offScreen.drawLine((int) xdc(this.calculate(axisOfRotation) * this.xScale), 50, (int) xdc(this.calculate(axisOfRotation) * this.xScale), 550);
            }
        }
        this.offScreen.setColor(Color.black);
        this.offScreen.drawLine(40, 300, 760, 300);
        this.offScreen.drawLine(400, 50, 400, 550);
        for (int i = 0; i < 250; i = (int) (i + this.yScale)) {
            this.offScreen.drawLine(400, (int) ydc(i), 403, (int) ydc(i));
        }
        for (int i = 0; i > -250; i = (int) (i - this.yScale)) {
            this.offScreen.drawLine(400, (int) ydc(i), 403, (int) ydc(i));
        }
        for (int i = 400; i < 760; i = (int) (i + this.xScale)) {
            this.offScreen.drawLine(i, 297, i, 300);
        }
        for (int i = 400; i > 40; i = (int) (i - this.xScale)) {
            this.offScreen.drawLine(i, 297, i, 300);
        }
        this.offScreen.setColor(Color.decode("#AF7817"));
        if (polarMode) {
            double firstThing;
            double xCoord1;
            double yCoord1;
            if (this.aLimit != null) {
                firstThing = calculate(this.f1, this.calculate(aLimit));
                xCoord1 = Math.cos(this.calculate(aLimit)) * firstThing;
                yCoord1 = Math.sin(this.calculate(aLimit)) * firstThing;
                this.offScreen.drawLine((int) xdc(xCoord1 * this.xScale), (int) ydc(yCoord1 * this.yScale), (int) xdc(0), (int) ydc(0));
            }
            if (this.bLimit != null) {
                firstThing = calculate(this.f1, this.calculate(bLimit));
                xCoord1 = Math.cos(this.calculate(bLimit)) * firstThing;
                yCoord1 = Math.sin(this.calculate(bLimit)) * firstThing;
                this.offScreen.drawLine((int) xdc(xCoord1 * this.xScale), (int) ydc(yCoord1 * this.yScale), (int) xdc(0), (int) ydc(0));
            }
        } else {
            if (this.aLimit != null) {
                this.offScreen.drawLine((int) xdc(this.calculate(aLimit) * this.xScale), 50, (int) xdc(this.calculate(aLimit) * this.xScale), 550);
            }
            if (this.bLimit != null) {
                this.offScreen.drawLine((int) xdc(this.calculate(bLimit) * this.xScale), 50, (int) xdc(this.calculate(bLimit) * this.xScale), 550);
            }
        }
        if (polarMode) {
            if (displayIntegralArea) {
                this.offScreen.setColor(Color.decode("#AF7817"));
                double bLim;
                double aLim;
                if (this.calculate(aLimit) < this.calculate(bLimit)) {
                    aLim = this.calculate(aLimit);
                    bLim = this.calculate(bLimit);
                } else {
                    aLim = this.calculate(bLimit);
                    bLim = this.calculate(aLimit);
                }
                for (double p = aLim; p < bLim; p += 1.0D / xScaleSlider.getMaximum()) {
                    double firstThing = calculate(this.f1, p);
                    double xCoord1 = Math.cos(p) * firstThing;
                    double yCoord1 = Math.sin(p) * firstThing;
                    this.offScreen.drawLine((int) xdc(xCoord1 * this.xScale), (int) ydc(yCoord1 * this.yScale), (int) xdc(0), (int) ydc(0));
                }
            }
            this.offScreen.setColor(Color.red);
            for (double p = 0; p < calculate(axisOfRotation); p += 1.0D / xScaleSlider.getMaximum()) {
                double firstThing = calculate(this.f1, p);
                double xCoord1 = Math.cos(p) * firstThing;
                double yCoord1 = Math.sin(p) * firstThing;
                double secondThing = calculate(this.f1, p + 1.0D / xScaleSlider.getMaximum());
                double xCoord2 = Math.cos(p + 1.0D / xScaleSlider.getMaximum()) * secondThing;
                double yCoord2 = Math.sin(p + 1.0D / xScaleSlider.getMaximum()) * secondThing;
                this.offScreen.drawLine((int) xdc(xCoord1 * this.xScale), (int) ydc(yCoord1 * this.yScale), (int) xdc((xCoord2 + 1.0D / xScaleSlider.getMaximum()) * this.xScale), (int) ydc(yCoord2 * this.yScale));
            }
        } else {
            this.offScreen.setColor(Color.red);
            for (double i = -360.0D / this.xScale; i < 360.0D / this.xScale; i += 1.0D / this.xScale) {
                if ((this.displayIntegralArea) && (this.isRotate.isSelected())) {
                    double bLim;
                    double aLim;
                    if (this.calculate(aLimit) < this.calculate(bLimit)) {
                        aLim = this.calculate(aLimit);
                        bLim = this.calculate(bLimit);
                    } else {
                        aLim = this.calculate(bLimit);
                        bLim = this.calculate(aLimit);
                    }
                    if ((i > aLim) && (i < bLim)) {
                        double firstThing = calculate(this.f1, i);
                        double secondThing = calculate(this.f1, i + 1.0D / this.xScale);
                        this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(firstThing * this.yScale), (int) xdc((i + 1.0D / this.xScale) * this.xScale), (int) ydc(secondThing * this.yScale));
                        if (this.xAxisRotation) {
                            double newY1 = this.calculate(axisOfRotation) - (firstThing - this.calculate(axisOfRotation));
                            double newY2 = this.calculate(axisOfRotation) - (secondThing - this.calculate(axisOfRotation));
                            this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(newY1 * this.yScale), (int) xdc((i + 1.0D / this.xScale) * this.xScale), (int) ydc(newY2 * this.yScale));
                        } else {
                            double x1 = this.calculate(axisOfRotation) - (i - this.calculate(axisOfRotation));
                            this.offScreen.drawLine((int) xdc(x1 * this.xScale), (int) ydc(firstThing * this.yScale), (int) xdc((x1 - 1.0D / this.xScale) * this.xScale), (int) ydc(secondThing * this.yScale));
                        }
                    }
                } else {
                    double firstThing = calculate(this.f1, i);
                    double secondThing = calculate(this.f1, i + 1.0D / this.xScale);
                    this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(firstThing * this.yScale), (int) xdc((i + 1.0D / this.xScale) * this.xScale), (int) ydc(secondThing * this.yScale));
                }
            }
        }
        if ((this.displayIntegralArea) && (this.isRotate.isSelected())) {
            if (this.xAxisRotation) {
                double aLim = this.calculate(aLimit);
                double bLim = this.calculate(bLimit);
                double aLimY = Math.abs(calculate(this.f1, aLim) - this.calculate(axisOfRotation));
                double bLimY = Math.abs(calculate(this.f1, bLim) - this.calculate(axisOfRotation));
                double yCoord = ydc((aLimY + this.calculate(axisOfRotation)) * this.yScale);
                double width = aLimY * this.yScale / 5.0D;
                double height = aLimY * 2.0D * this.yScale;
                double xCoord = xdc(aLim * this.xScale) - width / 2.0D;
                this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
                yCoord = ydc((bLimY + this.calculate(axisOfRotation)) * this.yScale);
                width = bLimY * this.yScale / 5.0D;
                height = bLimY * 2.0D * this.yScale;
                xCoord = xdc(bLim * this.xScale) - width / 2.0D;
                this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
            } else {
                double tempbLim;
                double tempaLim;
                if (this.calculate(aLimit) < this.calculate(bLimit)) {
                    tempaLim = this.calculate(aLimit);
                    tempbLim = this.calculate(bLimit);
                } else {
                    tempaLim = this.calculate(bLimit);
                    tempbLim = this.calculate(aLimit);
                }
                double min = 1.7976931348623157E+308D;
                double max = 4.9E-324D;
                double aLim = tempaLim - 2.0D / (this.xScale * 2.0D);
                double bLim = tempbLim + 2.0D / (this.xScale * 2.0D);
                for (double i = tempaLim; i <= tempbLim; i += 1.0D / (this.xScale * 2.0D)) {
                    double tempy = calculate(this.f1, i);
                    if (tempy < min) {
                        min = tempy;
                        aLim = i;
                    }
                    if (tempy > max) {
                        max = tempy;
                        bLim = i;
                    }
                }
                double aLimY = calculate(this.f1, aLim);
                double bLimY = calculate(this.f1, bLim);
                aLim = Math.abs(aLim - this.calculate(axisOfRotation));
                bLim = Math.abs(bLim - this.calculate(axisOfRotation));
                double width = aLim * this.xScale * 2.0D;
                double height = aLim * this.xScale / 5.0D;
                double xCoord = xdc((-aLim + this.calculate(axisOfRotation)) * this.xScale);
                double yCoord = ydc(aLimY * this.yScale) - height / 2.0D;
                this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
                width = bLim * this.xScale * 2.0D;
                height = bLim * this.xScale / 5.0D;
                xCoord = xdc((-bLim + this.calculate(axisOfRotation)) * this.xScale);
                yCoord = ydc(bLimY * this.yScale) - height / 2.0D;
                this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
                aLim = this.calculate(aLimit);
                bLim = this.calculate(bLimit);
                aLimY = calculate(this.f1, aLim);
                bLimY = calculate(this.f1, bLim);
                this.offScreen.drawLine((int) xdc(aLim * this.xScale), (int) ydc(aLimY * this.yScale), (int) xdc(aLim * this.xScale), (int) ydc(calculate(this.f2, aLim) * this.yScale));
                this.offScreen.drawLine((int) xdc(-(aLim - 2.0D * this.calculate(axisOfRotation)) * this.xScale), (int) ydc(aLimY * this.yScale), (int) xdc(-(aLim - 2.0D * this.calculate(axisOfRotation)) * this.xScale), (int) ydc(calculate(this.f2, aLim) * this.yScale));
                this.offScreen.drawLine((int) xdc(bLim * this.xScale), (int) ydc(bLimY * this.yScale), (int) xdc(bLim * this.xScale), (int) ydc(calculate(this.f2, bLim) * this.yScale));
                this.offScreen.drawLine((int) xdc(-(bLim - 2.0D * this.calculate(axisOfRotation)) * this.xScale), (int) ydc(bLimY * this.yScale), (int) xdc(-(bLim - 2.0D * this.calculate(axisOfRotation)) * this.xScale), (int) ydc(calculate(this.f2, bLim) * this.yScale));
                aLim = Math.abs(aLim - this.calculate(axisOfRotation));
                bLim = Math.abs(bLim - this.calculate(axisOfRotation));
                width = aLim * this.xScale * 2.0D;
                height = aLim * this.xScale / 5.0D;
                xCoord = xdc((-aLim + this.calculate(axisOfRotation)) * this.xScale);
                yCoord = ydc(aLimY * this.yScale) - height / 2.0D;
                this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);

                width = bLim * this.xScale * 2.0D;
                height = bLim * this.xScale / 5.0D;
                xCoord = xdc((-bLim + this.calculate(axisOfRotation)) * this.xScale);
                yCoord = ydc(bLimY * this.yScale) - height / 2.0D;
                this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
            }
        }
        if ((this.resultFunction != null) && (!this.disableResultFunction)) {
            this.offScreen.setColor(Color.decode("#348017"));
            for (double i = -360.0D / this.xScale; i < 360.0D / this.xScale; i += 1.0D / this.xScale) {
                double firstThing = calculate(resultFunction, i);
                double secondThing = calculate(resultFunction, i + 1.0D / this.xScale);
                this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(firstThing * this.yScale), (int) xdc((i + 1.0D / this.xScale) * this.xScale), (int) ydc(secondThing * this.yScale));
            }
        }

        if (this.f2 != null) {
            this.offScreen.setColor(Color.blue);
            for (double i = -360.0D / this.xScale; i < 360.0D / this.xScale; i += 1.0D / this.xScale) {
                if ((this.displayIntegralArea) && (this.isRotate.isSelected())) {
                    double bLim;
                    double aLim;
                    if (this.calculate(aLimit) < this.calculate(bLimit)) {
                        aLim = this.calculate(aLimit);
                        bLim = this.calculate(bLimit);
                    } else {
                        aLim = this.calculate(bLimit);
                        bLim = this.calculate(aLimit);
                    }
                    if ((i > aLim) && (i < bLim)) {
                        double firstThing = calculate(this.f2, i);
                        double secondThing = calculate(this.f2, i + 1.0D / this.xScale);
                        this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(firstThing * this.yScale), (int) xdc((i + 1.0D / this.xScale) * this.xScale), (int) ydc(secondThing * this.yScale));
                        if (this.xAxisRotation) {
                            double newY1 = this.calculate(axisOfRotation) - (firstThing - this.calculate(axisOfRotation));
                            double newY2 = this.calculate(axisOfRotation) - (secondThing - this.calculate(axisOfRotation));
                            this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(newY1 * this.yScale), (int) xdc((i + 1.0D / this.xScale) * this.xScale), (int) ydc(newY2 * this.yScale));
                        } else {
                            double x1 = this.calculate(axisOfRotation) - (i - this.calculate(axisOfRotation));
                            this.offScreen.drawLine((int) xdc(x1 * this.xScale), (int) ydc(firstThing * this.yScale), (int) xdc((x1 - 1.0D / this.xScale) * this.xScale), (int) ydc(secondThing * this.yScale));
                        }
                    }
                } else {
                    double firstThing = calculate(this.f2, i);
                    double secondThing = calculate(this.f2, i + 1.0D / this.xScale);
                    this.offScreen.drawLine((int) xdc(i * this.xScale), (int) ydc(firstThing * this.yScale), (int) xdc((i + 1.0D / this.xScale) * this.xScale), (int) ydc(secondThing * this.yScale));
                }
            }
            if ((this.displayIntegralArea) && (this.isRotate.isSelected())) {
                if (this.xAxisRotation) {
                    double aLim = this.calculate(aLimit);
                    double bLim = this.calculate(bLimit);
                    double aLimY = Math.abs(calculate(this.f2, aLim) - this.calculate(axisOfRotation));
                    double bLimY = Math.abs(calculate(this.f2, bLim) - this.calculate(axisOfRotation));
                    double yCoord = ydc((aLimY + this.calculate(axisOfRotation)) * this.yScale);
                    double width = aLimY * this.yScale / 5.0D;
                    double height = aLimY * 2.0D * this.yScale;
                    double xCoord = xdc(aLim * this.xScale) - width / 2.0D;
                    this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
                    yCoord = ydc((bLimY + this.calculate(axisOfRotation)) * this.yScale);
                    width = bLimY * this.yScale / 5.0D;
                    height = bLimY * 2.0D * this.yScale;
                    xCoord = xdc(bLim * this.xScale) - width / 2.0D;
                    this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
                } else {
                    double tempbLim;
                    double tempaLim;
                    if (this.calculate(aLimit) < this.calculate(bLimit)) {
                        tempaLim = this.calculate(aLimit);
                        tempbLim = this.calculate(bLimit);
                    } else {
                        tempaLim = this.calculate(bLimit);
                        tempbLim = this.calculate(aLimit);
                    }
                    double min = 1.7976931348623157E+308D;
                    double max = 4.9E-324D;
                    double aLim = tempaLim - 2.0D / (this.xScale * 2.0D);
                    double bLim = tempbLim + 2.0D / (this.xScale * 2.0D);
                    for (double i = tempaLim; i <= tempbLim; i += 1.0D / (this.xScale * 2.0D)) {
                        double tempy = calculate(this.f2, i);
                        if (tempy < min) {
                            min = tempy;
                            aLim = i;
                        }
                        if (tempy > max) {
                            max = tempy;
                            bLim = i;
                        }
                    }
                    double aLimY = calculate(this.f2, aLim);
                    double bLimY = calculate(this.f2, bLim);
                    aLim = Math.abs(aLim - this.calculate(axisOfRotation));
                    bLim = Math.abs(bLim - this.calculate(axisOfRotation));
                    double width = aLim * this.xScale * 2.0D;
                    double height = aLim * this.xScale / 5.0D;
                    double xCoord = xdc((-aLim + this.calculate(axisOfRotation)) * this.xScale);
                    double yCoord = ydc(aLimY * this.yScale) - height / 2.0D;
                    this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
                    width = bLim * this.xScale * 2.0D;
                    height = bLim * this.xScale / 5.0D;
                    xCoord = xdc((-bLim + this.calculate(axisOfRotation)) * this.xScale);
                    yCoord = ydc(bLimY * this.yScale) - height / 2.0D;
                    this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
                    aLim = this.calculate(aLimit);
                    bLim = this.calculate(bLimit);
                    aLimY = calculate(this.f2, aLim);
                    bLimY = calculate(this.f2, bLim);
                    aLim = Math.abs(aLim - this.calculate(axisOfRotation));
                    bLim = Math.abs(bLim - this.calculate(axisOfRotation));
                    width = aLim * this.xScale * 2.0D;
                    height = aLim * this.xScale / 5.0D;
                    xCoord = xdc((-aLim + this.calculate(axisOfRotation)) * this.xScale);
                    yCoord = ydc(aLimY * this.yScale) - height / 2.0D;
                    this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);

                    width = bLim * this.xScale * 2.0D;
                    height = bLim * this.xScale / 5.0D;
                    xCoord = xdc((-bLim + this.calculate(axisOfRotation)) * this.xScale);
                    yCoord = ydc(bLimY * this.yScale) - height / 2.0D;
                    this.offScreen.drawOval((int) xCoord, (int) yCoord, (int) width, (int) height);
                }
            }
        }
        if (this.displayDerivativeLine) {
            double slope = Double.parseDouble(this.resultTextBox.getText());
            double y = calculate(this.f1, this.calculate(aLimit));
            double intersect = y - slope * this.calculate(aLimit);
            Calculable slopeLine = null;
            try {
                slopeLine = new ExpressionBuilder(slope + "*x+" + intersect).withVariableNames(new String[]{"x", "pi", "e", "c"}).build();

                slopeLine.setVariable("pi", Math.PI);
                slopeLine.setVariable("e", Math.E);
                slopeLine.setVariable("c", 0.0D);
            } catch (Exception ex) {
                try {
                    slopeLine = new ExpressionBuilder("0").withVariableNames(new String[]{"x"}).build();
                } catch (UnknownFunctionException | UnparsableExpressionException ex2) {
                }
            }

            this.offScreen.setColor(Color.blue);
            double dist = 1.7976931348623157E+308D;

            double y1 = 0.0D;
            double y2 = 0.0D;
            double x1 = this.calculate(aLimit) - 20.0D;
            double x2 = this.calculate(aLimit) + 20.0D;
            while (dist > 40.0D) {
                x1 += 1.0D / this.xScale;
                x2 -= 1.0D / this.xScale;
                y1 = calculate(slopeLine, x1);
                y2 = calculate(slopeLine, x2);
                dist = distance(xdc(x1 * this.xScale), ydc(y1 * this.yScale), xdc(x2 * this.xScale), ydc(y2 * this.yScale));
            }

            this.offScreen.drawLine((int) xdc(x1 * this.xScale), (int) ydc(y1 * this.yScale), (int) xdc(x2 * this.xScale), (int) ydc(y2 * this.yScale));
            this.offScreen.drawLine((int) xdc(x1 * this.xScale), (int) ydc(y1 * this.yScale) + 1, (int) xdc(x2 * this.xScale), (int) ydc(y2 * this.yScale) + 1);
            this.offScreen.drawLine((int) xdc(x1 * this.xScale), (int) ydc(y1 * this.yScale) - 1, (int) xdc(x2 * this.xScale), (int) ydc(y2 * this.yScale) - 1);
        }

        this.offScreen.setColor(Color.black);
        this.offScreen.drawRect(40, 50, 720, 500);
        //super.paintComponents(g);
        //super.paint(g);
        g.drawImage(this.image, 0, 0, this);
        //backgroundPanel.paint(g);
        //g.fillRect(40, 550, 720, 100);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void stop() {
        this.animation = null;
    }

    public double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2.0D) + Math.pow(y1 - y2, 2.0D));
    }

    public double xmc(double dc) {
        return dc - 400.0D;
    }

    public double ymc(double dc) {
        return -dc + 300.0D;
    }

    public double xdc(double mc) {
        return 400.0D + mc;
    }

    public double ydc(double mc) {
        return 300.0D - mc;
    }

    public double roundFourDecimals(double d) {
        if (Double.isInfinite(d)) {
            return d;
        }
        if (Double.isNaN(d)) {
            return Double.POSITIVE_INFINITY;
        }
        try {
            DecimalFormat twoDForm = new DecimalFormat("#.####");
            return Double.valueOf(twoDForm.format(d)).doubleValue();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return d;
    }

    public double calculate(Calculable function, double x) {
        try {
            function.setVariable("x", x);
            return function.calculate();
        } catch (Exception e) {
            try {
                //System.err.println(e.getMessage());
                function.setVariable("x", x + 0.00001);
                if (function.calculate() >= 0) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return Double.NEGATIVE_INFINITY;
                }
            } catch (Exception ex) {
                //System.err.println(e.getMessage());
                return 0;
            }
        }
    }

    public double calculate(Calculable function) {
        try {
            return function.calculate();
        } catch (Exception e) {
            //System.err.println(e.getMessage());
            return 0;
        }
    }

    private void initComponents() {
        this.backgroundPanel = new JPanel();
        this.xScaleSlider = new JSlider();
        this.yScaleSlider = new JSlider();
        this.function1 = new JTextField();
        this.function2 = new JTextField();
        this.isRotate = new JCheckBox();
        this.resultTextBox = new JTextField();
        this.aLabel = new JLabel();
        this.aIntegralLimit = new JTextField();
        this.bLabel = new JLabel();
        this.bIntegralLimit = new JTextField();
        this.findResultButton = new JButton();
        this.y1Label = new JLabel();
        this.y2Label = new JLabel();
        this.rotateAboutLabel = new JLabel();
        this.rotateAboutTextField = new JTextField();
        this.integralMode = new JRadioButton();
        this.derivativeMode = new JRadioButton();
        this.ResultFunctionLabel = new JLabel();
        this.resultFunctionTextField = new JTextField();
        this.resultLabel = new JLabel();
        this.lockScaleCheckBox = new JCheckBox();
        this.topPanel = new JPanel();

        setDefaultCloseOperation(3);
        setTitle("MathCalcGUI");
        setMaximumSize(new Dimension(1200, 700));
        setMinimumSize(new Dimension(800, 700));
        setPreferredSize(new Dimension(800, 700));
        setResizable(false);
        this.backgroundPanel.setPreferredSize(new Dimension(800, 700));
        this.xScaleSlider.setMaximum(360);
        this.xScaleSlider.setMinimum(1);
        this.xScaleSlider.setValue(36);
        this.xScaleSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                MathCalcGUI.this.xScaleSliderMousePressed(evt);
            }
        });
        this.xScaleSlider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                MathCalcGUI.this.xScaleSliderMouseDragged(evt);
            }
        });
        this.xScaleSlider.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                MathCalcGUI.this.xScaleSliderKeyPressed(evt);
            }
        });
        this.yScaleSlider.setMaximum(250);
        this.yScaleSlider.setMinimum(1);
        this.yScaleSlider.setOrientation(1);
        this.yScaleSlider.setValue(25);
        this.yScaleSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                MathCalcGUI.this.yScaleSliderMousePressed(evt);
            }
        });
        this.yScaleSlider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                MathCalcGUI.this.yScaleSliderMouseDragged(evt);
            }
        });
        this.yScaleSlider.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                MathCalcGUI.this.yScaleSliderKeyPressed(evt);
            }
        });
        this.function1.setText("0");
        this.function1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                MathCalcGUI.this.function1KeyPressed(evt);
            }
        });
        this.function2.setText("0");
        this.function2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                MathCalcGUI.this.function2KeyPressed(evt);
            }
        });
        this.isRotate.setText("Rotate?");
        this.isRotate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MathCalcGUI.this.isRotateActionPerformed(evt);
            }
        });
        this.resultTextBox.setText("");
        this.aLabel.setText("A:");
        this.aIntegralLimit.setText("");
        this.aIntegralLimit.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                MathCalcGUI.this.aIntegralLimitKeyPressed(evt);
            }
        });
        this.bLabel.setText("B:");
        this.bIntegralLimit.setText("");
        this.bIntegralLimit.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                MathCalcGUI.this.bIntegralLimitKeyPressed(evt);
            }
        });
        this.findResultButton.setText("Calculate");
        this.findResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MathCalcGUI.this.findResultButtonActionPerformed(evt);
            }
        });
        this.y1Label.setText("F1=");
        this.y2Label.setText("F2=");
        this.rotateAboutLabel.setText("y=");
        this.rotateAboutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                MathCalcGUI.this.rotateAboutLabelMousePressed(evt);
            }
        });
        this.rotateAboutTextField.setText("0");
        this.rotateAboutTextField.setEnabled(false);
        this.rotateAboutTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                MathCalcGUI.this.rotateAboutTextFeildKeyPressed(evt);
            }
        });
        this.integralMode.setSelected(true);
        this.integralMode.setText("Integrals");
        this.integralMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MathCalcGUI.this.integralModeActionPerformed(evt);
            }
        });
        this.derivativeMode.setText("Derivates");
        this.derivativeMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MathCalcGUI.this.derivativeModeActionPerformed(evt);
            }
        });
        this.ResultFunctionLabel.setText("F'(x)=");
        this.ResultFunctionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                MathCalcGUI.this.ResultFunctionLabelMousePressed(evt);
            }
        });
        this.resultFunctionTextField.setText("");
        this.resultLabel.setText("Result=");
        this.resultLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                MathCalcGUI.this.ResultLabelMousePressed(evt);
            }
        });
        this.lockScaleCheckBox.setText("Lock Aspect");
        this.lockScaleCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MathCalcGUI.this.lockScaleCheckBoxActionPerformed(evt);
            }
        });
        GroupLayout backgroundPanelLayout = new GroupLayout(this.backgroundPanel);
        this.backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(backgroundPanelLayout.createSequentialGroup().addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(this.yScaleSlider, -2, -1, -2).addGroup(backgroundPanelLayout.createSequentialGroup()
                .addComponent(this.ResultFunctionLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(this.resultFunctionTextField)).addGroup(GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                .addGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.y1Label)
                .addComponent(this.y2Label)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.function2)
                .addComponent(this.function1, -2, 369, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addGroup(backgroundPanelLayout.createSequentialGroup().addComponent(this.isRotate)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.rotateAboutLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.rotateAboutTextField, -2, 50, -2)//changed 30
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.aLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.aIntegralLimit, -2, 50, -2)//changed 60
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.bLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.bIntegralLimit, -2, 50, -2)//changed 60
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.findResultButton))
                .addGroup(GroupLayout.Alignment.LEADING, backgroundPanelLayout.createSequentialGroup().addComponent(this.integralMode)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.derivativeMode)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.resultLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.resultTextBox))))
                .addGroup(backgroundPanelLayout.createSequentialGroup().addComponent(this.lockScaleCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.xScaleSlider, -1, -1, 32767)))
                .addContainerGap(-1, 32767)));
        backgroundPanelLayout.setVerticalGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(backgroundPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.yScaleSlider, -2, 505, -2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(backgroundPanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.xScaleSlider, -2, -1, -2)
                .addComponent(this.lockScaleCheckBox)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.function1, -2, -1, -2).addComponent(this.isRotate).addComponent(this.aLabel)
                .addComponent(this.aIntegralLimit, -2, -1, -2).addComponent(this.bLabel).addComponent(this.y1Label)
                .addComponent(this.bIntegralLimit, -2, -1, -2).addComponent(this.rotateAboutLabel)
                .addComponent(this.rotateAboutTextField, -2, -1, -2).addComponent(this.findResultButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(backgroundPanelLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.function2, -2, -1, -2)
                .addComponent(this.resultTextBox, -2, -1, -2).addComponent(this.y2Label).addComponent(this.integralMode)
                .addComponent(this.derivativeMode).addComponent(this.resultLabel)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.ResultFunctionLabel).addComponent(this.resultFunctionTextField, -2, -1, -2))
                .addContainerGap(67, 32767)));
        this.topPanel.setPreferredSize(new Dimension(0, 2));
        GroupLayout topPanelLayout = new GroupLayout(this.topPanel);
        this.topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(topPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 0, 32767));
        topPanelLayout.setVerticalGroup(topPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 2, 32767));
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.backgroundPanel, -1, 770, 32767).addComponent(this.topPanel, -1, 770, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.topPanel, -2, -1, -2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.backgroundPanel, -2, -1, -2)));
        pack();
    }

    private void yScaleSliderMouseDragged(MouseEvent evt) {
        if (!this.lockScaleCheckBox.isSelected()) {
            this.yScale = this.yScaleSlider.getValue();
            update();
        }
    }

    private void xScaleSliderMouseDragged(MouseEvent evt) {
        this.xScale = this.xScaleSlider.getValue();
        if (this.lockScaleCheckBox.isSelected()) {
            this.yScale = this.xScale;
        }
        update();
    }

    private void xScaleSliderKeyPressed(KeyEvent evt) {
        this.xScale = this.xScaleSlider.getValue();
        if (this.lockScaleCheckBox.isSelected()) {
            this.yScale = this.xScale;
        }
        update();
    }

    private void yScaleSliderKeyPressed(KeyEvent evt) {
        if (!this.lockScaleCheckBox.isSelected()) {
            this.yScale = this.yScaleSlider.getValue();
            update();
        }
    }

    private void aIntegralLimitKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == 10) {
            if (isRotate.isSelected()) {
                this.displayIntegralArea = false;
            }
            if (this.aIntegralLimit.getText().equals("")) {
                this.aLimit = null;
                this.displayIntegralArea = false;
                this.displayDerivativeLine = false;
                this.resultTextBox.setText("");
            } else {
                try {
                    this.aLimit = new ExpressionBuilder(this.aIntegralLimit.getText()).withVariableNames(new String[]{"pi", "e"}).build();
                    this.aLimit.setVariable("pi", Math.PI);
                    this.aLimit.setVariable("e", Math.E);
                    //this.displayIntegralArea = false;
                    this.displayDerivativeLine = false;
                    this.resultTextBox.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
            }
            update();
        } else if (evt.getKeyCode() == 38) {
            if (isRotate.isSelected()) {
                this.displayIntegralArea = false;
            }
            if (this.aIntegralLimit.getText().equals("")) {
                this.aLimit = null;
                this.displayIntegralArea = false;
                this.displayDerivativeLine = false;
                this.resultTextBox.setText("");
            } else {
                try {
                    this.aLimit = new ExpressionBuilder((calculate(aLimit) + 0.01) + "").withVariableNames(new String[]{"pi", "e"}).build();
                    this.aLimit.setVariable("pi", Math.PI);
                    this.aLimit.setVariable("e", Math.E);
                    //this.displayIntegralArea = false;
                    this.displayDerivativeLine = false;
                    this.resultTextBox.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
                this.aIntegralLimit.setText(roundFourDecimals(calculate(aLimit)) + "");
            }
            update();
        } else if (evt.getKeyCode() == 40) {
            if (isRotate.isSelected()) {
                this.displayIntegralArea = false;
            }
            if (this.aIntegralLimit.getText().equals("")) {
                this.aLimit = null;
                this.displayIntegralArea = false;
                this.displayDerivativeLine = false;
                this.resultTextBox.setText("");
            } else {
                try {
                    this.aLimit = new ExpressionBuilder((calculate(aLimit) - 0.01) + "").withVariableNames(new String[]{"pi", "e"}).build();
                    this.aLimit.setVariable("pi", Math.PI);
                    this.aLimit.setVariable("e", Math.E);
                    //this.displayIntegralArea = false;
                    this.displayDerivativeLine = false;
                    this.resultTextBox.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
                this.aIntegralLimit.setText(roundFourDecimals(calculate(aLimit)) + "");
            }
            update();
        }
    }

    private void bIntegralLimitKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == 10) {
            if (isRotate.isSelected()) {
                this.displayIntegralArea = false;
            }
            if (this.bIntegralLimit.getText().equals("")) {
                this.bLimit = null;
                this.displayIntegralArea = false;
                //this.displayDerivativeLine = false;
                this.resultTextBox.setText("");
            } else {
                try {
                    this.bLimit = new ExpressionBuilder(this.bIntegralLimit.getText()).withVariableNames(new String[]{"pi", "e"}).build();

                    this.bLimit.setVariable("pi", Math.PI);
                    this.bLimit.setVariable("e", Math.E);
                    //this.displayIntegralArea = false;
                    this.displayDerivativeLine = false;
                    this.resultTextBox.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
            }
            update();
        } else if (evt.getKeyCode() == 38) {
            if (isRotate.isSelected()) {
                this.displayIntegralArea = false;
            }
            if (this.bIntegralLimit.getText().equals("")) {
                this.bLimit = null;
                this.displayIntegralArea = false;
                this.resultTextBox.setText("");
            } else {
                try {
                    this.bLimit = new ExpressionBuilder((calculate(bLimit) + 0.01) + "").withVariableNames(new String[]{"pi", "e"}).build();
                    this.bLimit.setVariable("pi", Math.PI);
                    this.bLimit.setVariable("e", Math.E);
                    //this.displayIntegralArea = false;
                    this.displayDerivativeLine = false;
                    this.resultTextBox.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
                this.bIntegralLimit.setText(roundFourDecimals(calculate(bLimit)) + "");
            }
            update();
        } else if (evt.getKeyCode() == 40) {
            if (isRotate.isSelected()) {
                this.displayIntegralArea = false;
            }
            if (this.bIntegralLimit.getText().equals("")) {
                this.bLimit = null;
                this.displayIntegralArea = false;
                this.resultTextBox.setText("");
            } else {
                try {
                    this.bLimit = new ExpressionBuilder((calculate(bLimit) - 0.01) + "").withVariableNames(new String[]{"pi", "e"}).build();
                    this.bLimit.setVariable("pi", Math.PI);
                    this.bLimit.setVariable("e", Math.E);
                    //this.displayIntegralArea = false;
                    this.displayDerivativeLine = false;
                    this.resultTextBox.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
                this.bIntegralLimit.setText(roundFourDecimals(calculate(bLimit)) + "");
            }
            update();
        }
    }

    private void findResultButtonActionPerformed(ActionEvent evt) {
        EvalThread t = new EvalThread(this);
        boolean success = false;
        t.start();
        while (!success && !t.weGotAnError) {
            success = !t.isAlive();
        }
        if (t.weGotAnError) {
            this.displayIntegralArea = false;
            this.displayDerivativeLine = false;
            this.resultFunctionTextField.setText("Operation failed");
        }
        update();
    }

    private void function2KeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == 10) {
            if (function2.getText().equals("polar")) {
                if (!polarMode) {
                    function2.setText("Polar mode on");
                    polarMode = true;
                    f2 = null;
                    if (isRotate.isSelected()) {
                        isRotate.doClick();
                    }
                    this.rotateAboutTextField.setEnabled(true);
                    this.derivativeMode.setEnabled(false);
                    this.isRotate.setSelected(false);
                    this.isRotate.setEnabled(false);
                    this.displayIntegralArea = false;
                    this.displayDerivativeLine = false;
                    this.y1Label.setText("r=");
                    this.rotateAboutTextField.setText("pi*2");
                    this.aLabel.setText(theta + "\u2081");
                    this.bLabel.setText(theta + "\u2082");
                    this.rotateAboutLabel.setText(theta);
                    try {
                        this.axisOfRotation = new ExpressionBuilder(Math.PI * 2 + "").build();
                    } catch (Exception ex) {
                    }
                }
            } else {
                if (polarMode) {
                    polarMode = false;
                    function2.setText("");
                    this.derivativeMode.setEnabled(true);
                    this.rotateAboutTextField.setEnabled(false);
                    this.rotateAboutLabel.setText("y=");
                    this.rotateAboutTextField.setText("0");
                    this.y1Label.setText("F1=");
                    isRotate.setEnabled(true);
                    function1.setText(function1.getText().replaceAll(theta, "x"));
                    this.bLabel.setText("B:");
                    if (this.integralMode.isSelected()) {
                        this.aLabel.setText("A:");
                    } else {
                        this.aLabel.setText("X:");
                    }
                    try {
                        this.axisOfRotation = new ExpressionBuilder("0").build();
                    } catch (Exception ex) {
                    }
                }
                if (!this.function2.getText().equals("")) {
                    try {
                        this.f2 = new ExpressionBuilder(this.function2.getText()).withVariableNames(new String[]{"x", "pi", "e"}).build();
                        this.f2.setVariable("pi", Math.PI);
                        this.f2.setVariable("e", Math.E);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                    }
                } else {
                    this.f2 = null;
                }

                this.displayIntegralArea = false;
                this.displayDerivativeLine = false;
                this.resultFunction = null;
                if (!this.disableResultFunction) {
                    this.resultFunctionTextField.setText("");
                }
            }
            update();
        }
    }

    private void isRotateActionPerformed(ActionEvent evt) {
        this.rotateAboutTextField.setEnabled(this.isRotate.isSelected());
        this.resultFunction = null;
        this.resultFunctionTextField.setText("");
        if (!this.xAxisRotation) {
            this.rotateAboutLabel.setText("y=");
            this.xAxisRotation = true;
        }
        if (!this.isRotate.isSelected()) {
            this.rotateAboutTextField.setText("0");
            try {
                this.axisOfRotation = new ExpressionBuilder("0").build();
                this.displayIntegralArea = false;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
            }
        }
    }

    private void function1KeyPressed(KeyEvent evt) {
        if (polarMode) {
            if (function1.getText().contains("theta")) {
                function1.setText(function1.getText().replaceAll("theta", theta));
            }
        }
        if (evt.getKeyCode() == 10) {
            if (!this.function1.getText().equals("")) {
                try {
                    this.f1 = new ExpressionBuilder(this.function1.getText().replaceAll(theta, "x")).withVariableNames(new String[]{"x", "pi", "e"}).build();
                    this.f1.setVariable("pi", Math.PI);
                    this.f1.setVariable("e", Math.E);
                    this.resultFunctionTextField.setEnabled(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
            } else if (!this.function2.getText().equals("") && !polarMode) {
                try {
                    this.f1 = new ExpressionBuilder(this.function2.getText()).withVariableNames(new String[]{"x", "pi", "e"}).build();
                    this.f1.setVariable("pi", Math.PI);
                    this.f1.setVariable("e", Math.E);
                    this.function1.setText(this.function2.getText());
                    this.function2.setText("");
                    this.f2 = null;
                    this.resultFunctionTextField.setEnabled(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
            } else {
                try {
                    this.f1 = new ExpressionBuilder("0").withVariableNames(new String[]{"x", "pi", "e"}).build();
                    this.f1.setVariable("pi", Math.PI);
                    this.f1.setVariable("e", Math.E);
                    this.function1.setText("0");
                    this.resultFunctionTextField.setEnabled(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
                }
            }
            this.displayIntegralArea = false;
            this.displayDerivativeLine = false;
            this.resultFunction = null;
            this.resultFunctionTextField.setEnabled(!this.disableResultFunction);
            update();
        }
    }

    private void integralModeActionPerformed(ActionEvent evt) {
        this.derivativeMode.setSelected(false);
        this.integralMode.setSelected(true);
        this.displayDerivativeLine = false;
        this.isRotate.setEnabled(true);
        this.function2.setEnabled(true);
        this.resultFunction = null;
        this.ResultFunctionLabel.setText("F'(x)=");
        this.aLabel.setText("A:");
        this.bIntegralLimit.setEnabled(true);
        bLimit = null;
        this.bIntegralLimit.setText("");
        if (actuallyOperate) {
            this.resultFunctionTextField.setText("");
        } else {
            resultFunction = null;
            if (this.integralMode.isSelected()) {
                this.resultFunctionTextField.setText("INTEGRAL(f(x))");
            } else if (this.derivativeMode.isSelected()) {
                this.resultFunctionTextField.setText("DERIVATIVE(f(x))");
            }
        }
    }

    private void derivativeModeActionPerformed(ActionEvent evt) {
        this.derivativeMode.setSelected(true);
        this.integralMode.setSelected(false);
        this.displayIntegralArea = false;
        this.bIntegralLimit.setText("");
        this.f2 = null;
        this.resultFunction = null;
        this.function2.setText("");
        bLimit = null;
        if (this.isRotate.isSelected()) {
            this.isRotate.doClick();
        }
        this.isRotate.setEnabled(false);
        this.bIntegralLimit.setEnabled(false);
        this.function2.setEnabled(false);
        this.aLabel.setText("X:");
        this.ResultFunctionLabel.setText("dy/dx=");
        this.resultFunctionTextField.setText("");
        if (actuallyOperate) {
            this.resultFunctionTextField.setText("");
        } else {
            resultFunction = null;
            if (this.integralMode.isSelected()) {
                this.resultFunctionTextField.setText("INTEGRAL(f(x))");
            } else if (this.derivativeMode.isSelected()) {
                this.resultFunctionTextField.setText("DERIVATIVE(f(x))");
            }
        }
    }

    private void rotateAboutLabelMousePressed(MouseEvent evt) {
        if (this.isRotate.isSelected()) {
            if (this.xAxisRotation) {
                this.rotateAboutLabel.setText("x=");
                this.xAxisRotation = false;
                if (this.f2 == null) {
                    this.function2.setText("0");
                    try {
                        this.f2 = new ExpressionBuilder("0").withVariableNames(new String[]{"x"}).build();
                        //this.resultFunctionTextFeild.setEnabled(false);
                    } catch (Exception ex) {
                    }
                }
            } else {
                this.rotateAboutLabel.setText("y=");
                this.xAxisRotation = true;
            }
        }
    }

    private void rotateAboutTextFeildKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == 10) {
            try {
                this.axisOfRotation = new ExpressionBuilder(this.rotateAboutTextField.getText()).withVariableNames(new String[]{"pi", "e"}).build();
                this.axisOfRotation.setVariable("pi", Math.PI);
                this.axisOfRotation.setVariable("e", Math.E);
                this.displayIntegralArea = false;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
            }
            this.rotateAboutTextField.setText("" + roundFourDecimals(calculate(axisOfRotation)));
            update();
        } else if (evt.getKeyCode() == 38) {
            try {
                this.axisOfRotation = new ExpressionBuilder(this.rotateAboutTextField.getText() + "+0.01").withVariableNames(new String[]{"pi", "e"}).build();
                this.axisOfRotation.setVariable("pi", Math.PI);
                this.axisOfRotation.setVariable("e", Math.E);
                this.displayIntegralArea = false;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
            }
            this.rotateAboutTextField.setText("" + roundFourDecimals(calculate(axisOfRotation)));
            update();
        } else if (evt.getKeyCode() == 40) {
            try {
                this.axisOfRotation = new ExpressionBuilder(this.rotateAboutTextField.getText() + "-0.01").withVariableNames(new String[]{"pi", "e"}).build();
                this.axisOfRotation.setVariable("pi", Math.PI);
                this.axisOfRotation.setVariable("e", Math.E);
                this.displayIntegralArea = false;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 2);
            }
            this.rotateAboutTextField.setText("" + roundFourDecimals(calculate(axisOfRotation)));
            update();
        }
    }

    private void ResultFunctionLabelMousePressed(MouseEvent evt) {
        this.disableResultFunction = (!this.disableResultFunction);
        if (actuallyOperate) {
            this.resultFunctionTextField.setText("");
        } else {
            resultFunction = null;
            if (this.integralMode.isSelected()) {
                this.resultFunctionTextField.setText("INTEGRAL(f(x))");
            } else if (this.derivativeMode.isSelected()) {
                this.resultFunctionTextField.setText("DERIVATIVE(f(x))");
            }
        }
        if (this.disableResultFunction) {
            this.resultFunction = null;
            this.resultFunctionTextField.setText("");
        }
        this.resultFunctionTextField.setEnabled(!this.disableResultFunction);
        if ((this.isRotate.isSelected()) || (this.f2 != null)) {
            this.resultFunctionTextField.setEnabled(false);
        }
        update();
    }

    private void ResultLabelMousePressed(MouseEvent evt) {
        actuallyOperate = !actuallyOperate;
        if (actuallyOperate) {
            this.resultFunctionTextField.setText("");
        } else {
            resultFunction = null;
            if (this.integralMode.isSelected()) {
                this.resultFunctionTextField.setText("INTEGRAL(f(x))");
            } else if (this.derivativeMode.isSelected()) {
                this.resultFunctionTextField.setText("DERIVATIVE(f(x))");
            }
        }
    }

    private void lockScaleCheckBoxActionPerformed(ActionEvent evt) {
        this.yScaleSlider.setEnabled(!this.lockScaleCheckBox.isSelected());
        this.yScaleSlider.setValue((int) this.xScale);
        this.yScale = this.xScale;
        update();
    }

    private void xScaleSliderMousePressed(MouseEvent evt) {
        this.xScale = this.xScaleSlider.getValue();
        if (this.lockScaleCheckBox.isSelected()) {
            this.yScale = this.xScale;
        }
        update();
    }

    private void yScaleSliderMousePressed(MouseEvent evt) {
        if (!this.lockScaleCheckBox.isSelected()) {
            this.yScale = this.yScaleSlider.getValue();
            update();
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MathCalcGUI().setVisible(true);
            }
        });
    }
}