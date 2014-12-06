package Tester;

import java.util.Scanner;
import java.util.Set;
import javacalculus.core.CalculusEngine;

public class javaCalcTester {

    public static void main(String[] args) {
        CalculusEngine calc = new CalculusEngine();
        Scanner s = new Scanner(System.in);
        System.out.print(">>> ");
        String in = s.nextLine();
        String ans = "";
        while (!in.equals("exit")) {
            if (in.equals("threads")) {
                Set<Thread> threads = Thread.getAllStackTraces().keySet();
                for (Thread t : threads) {
                    System.out.println(t);
                }
            } else {
                try {
                    if (!in.isEmpty()) {
                        in = in.replace("ans", "("+ans+")");
                        ans = calc.execute(in);
                        //ans = calc.executeWithStats(in);
                        System.out.print("> ");
                        System.out.println(ans);
                    }
                } catch (Exception e) {
                    System.out.print("> ");
                    System.out.println("ERROR, "+e.toString()+"\n"+e.getMessage());
                    ans = "";
                }
            }
            System.out.print(">>> ");
            in = s.nextLine();
        }
        //FACTOR(2*x^3+4*x^2+6*x)
        //FACTOR(x^2+2.0*x+3.0)
        //INT((x^2+1)/(x^3+3*x),x)
        //FAIL
        //(1+x^2)*1/((VARIABLE)*(3+3*x^2))
        //0.9999999+1.3333332*x^2+0.3333333*x^4
        //0.9999999+1.3333332*x^2+0.3333333*x^4
        //-0.3333333*(1+x^2)*1/(1/(x^3+3*x)*(0.9999999+0.9999999*x^2))
        
        //NOT SIMPLIFYING?
        //(1+x^2)*1/(x^3+3*x)*1/(3+3*x^2)
        //(1+x^2)*1/(x*(3+x^2)+3*(0.9999999+0.9999999*x^2))
        //SIMPLIFY((1+x^2)*1/(x*(3+x^2)+3*(0.9999999+0.9999999*x^2)))
        //SIMPLIFY((1+x^2)*1/(x^3+3*x)*1/(3+3*x^2))
        //(1+x^2)*1/(0.9999999+0.9999999*x^2)
        //3*x^2/3
        //0.9999999*x^2
        //INT((x^3)*E^(3*x^2),x)
        //(x^3)*E^(3*x^2)
        //INT(E^(3*x^2)*(x^3),x)
        //E^(3*x^2)*(x^3)
        //0.5*(0.3333333*x^2*2.718282^(3*x^2)-0.1111111*2.718282^(3*x^2))
        
        //0.5*u*2.718282^(3*u)
        //u = x^2
        
        //0.5*(0.3333333*x^2*2.718282^(3*x^2)-0.1111111*2.718282^(3*x^2))
        //0.5*((1/3)*x^2*E^(3*x^2)-(1/9)*E^(3*x^2))
        
        //CHALLENGE INTEGRAL
        //INT((4*x^5-1)/((x^5+x+1)^2),x)
        
        //LESS CHALLENGING BUT STILL REALLY CHALLENGING INTEGRAL
        //INT(2*(x+x^2)^2*x*E^(1+x^2)+2*E^(1+x^2)*(x+x^2)*(1+2*x),x)
        //(x^2+x)^2*E^(x^2+1)
        
        //2*E^(1+x^2)*x*(x+x^2)^2+2*E^(1+x^2)*(x+x^2)*(1+2*x)
        
        //WWHAT
        //SIMPLIFY(0.1666667*2.718282^(3*x^2)*x^2)
        
        //FACTOR(0.1666667*2.718282^(3*x^2)*x^2)
        
        //intbyparts ans:x*2.718282^(1+x)-2.718282^(1+x)
        
        //true dat
        
        //TODO
        //solve for U during U sub (solve basic algebra)
        //trig sub
        //INT((1-SIN(x)^2)/(COS(x)^2),x)
        //INT(SIN(x)^3*COS(x)^-4,x)
        
        
        //INT((x+1)*(x+2)^0.5,x)
        //> 0.6666667*(2+x)^1.5+0.4000000*x^2.5 < wrong
        //> (2/3)*(2+x)^1.5+(2/5)*x^2.5
        //0.6666667*(2+x)^1.5+0.6666667*x*(2+x)^1.5-0.2666667*(2+x)^2.5
        //
        //so it solves this using parts, which is fine by me
        
        //INT(x^0.5*SIN(x^0.5),x)
        //2*x^0.5*(-x^0.5*COS(x^0.5)+SIN(x^0.5))-2*(-(x^0.5*SIN(x^0.5)+COS(x^0.5))-COS(x^0.5))
        //
        //0.6666667*((2+x)^1.5+x*(2+x)^1.5-0.4000000*(2+x)^2.5)
        
        
        //INT(SIN(x)^7*COS(x)^-4,x)
        //-(3*COS(x)-0.3333333*COS(x)^-3+3*1/COS(x)-0.3333333*COS(x)^3)
        
        // x^-4*(1-x^2)^3
        //INT(SIN(x)^2*COS(x)^2,x)
        //0.0625*(4*x-(2*x+0.5*SIN(4*x)))

        
        //INT(SIN(x)^7*COS(x)^4,x)
        //-(0.2000000*(1-COS(x)^2)^3*COS(x)^5+1.200000*(0.1428571*COS(x)^7-0.2222222*COS(x)^9+0.09090909*COS(x)^11))
        //INT(x^5*E^(x^3),x)
        //??
        //overflow
        //
    }
}