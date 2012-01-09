package eva2.server.go.problems;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

import eva2.gui.GenericObjectEditor;

/**
 * This might have been a test problem for ScatterSearch...?
 *
 * @author mkron
 */
public class FM10Problem extends AbstractProblemDouble {

    public FM10Problem() {
        setDefaultRange(2.);
    }

    @Override
    public double[] eval(double[] u) {
        u = rotateMaybe(u);
        double x = u[0];
        double y = u[1];
        double[] res = new double[1];
        res[0] = sin(2.2 * PI * x + PI / 2) * ((2 - Math.abs(y)) / 2) * ((3 - Math.abs(x)) / 2)
                + sin(0.5 * PI * y * y + PI / 2) * ((2 - Math.abs(y)) / 2) * ((2 - Math.abs(x)) / 2);

//		res[0]=(y*y-4.5*y*y)*x*y-4.7*cos(3*x-y*y*(2+x))*sin(2.5*Math.PI*x)+(0.3*x)*(0.3*x); // this was FM4
//		res[0] = (cos(2*x+1)+2*cos(3*x+2)+3*cos(4*x+3)+ // this was shubert
//			4*cos(5*x+4)+5*cos(6*x+5))*
//			(cos(2*y+1)+2*cos(3*y+2)+3*cos(4*y+3)+
//			4*cos(5*y+4)+5*cos(6*y+5));
        return res;
    }

    @Override
    public int getProblemDimension() {
        return 2;
    }

    @Override
    public Object clone() {
        return new FM10Problem();
    }

    public String getName() {
        return "M10-Problem";
    }
}
