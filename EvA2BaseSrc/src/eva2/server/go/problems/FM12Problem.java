package eva2.server.go.problems;

import java.io.Serializable;

import eva2.gui.GenericObjectEditor;
import eva2.server.go.operators.postprocess.SolutionHistogram;

/**
 * The general L-function, L(x)=prod_1^n(sin^k(5 pi x_i)*exp(-l3*(x_i -l4)^2/l5^2),
 * 5^n equidistant peaks of increasing height, single-funnel, but very flat in high dimensions.
 * <p/>
 * See, e.g., Mahfouds thesis (1995), or Shir & Bäck: Niching in ES, GECCO 05.
 * Shir & Bäck went up to n=10 for this function, considering 11 optima, finding success rate 80% (n=10) and 100% (n<=5).
 */

public class FM12Problem extends AbstractProblemDouble
        implements Serializable, Interface2DBorderProblem, InterfaceMultimodalProblem, InterfaceInterestingHistogram {
    int dim = 2;
    private boolean tough = true;

    public FM12Problem() {
        super();
    }

    public FM12Problem(FM12Problem o) {
        super(o);
        dim = o.dim;
    }

    public FM12Problem(int dim, boolean doRotation, boolean beTough) {
        super();
        setProblemDimension(dim);
        setDoRotation(doRotation);
        tough = beTough;
    }

    @Override
    public void hideHideable() {
        super.hideHideable();
        GenericObjectEditor.setHideProperty(this.getClass(), "defaultRange", true);
    }

    @Override
    public double getRangeLowerBound(int dim) {
        return 0.;
    }

    @Override
    public double getRangeUpperBound(int dim) {
        return 1.;
    }

    @Override
    public double[] eval(double[] x) {
        x = rotateMaybe(x);

        int k = 2;
        double l1 = 5, l2 = 0;
        double l3 = 2 * Math.log(2.), l4 = 0.1, l5 = 0.8; // these are the default values, e.g., from Mahfouds thesis, 1995

        double s, e, prd = 1;
        for (int i = 0; i < x.length; i++) {
            s = Math.pow(Math.sin(l1 * Math.PI * x[i] + l2), k);
            e = Math.exp(-l3 * Math.pow((x[i] - l4) / l5, 2.));
            prd *= (s * e);
        }

        double[] y = new double[1];
        y[0] = 1. - prd; // convert to minimisation
        return y;
    }

    @Override
    public int getProblemDimension() {
        return dim;
    }

    public void setProblemDimension(int d) {
        dim = d;
    }

    @Override
    public Object clone() {
        return new FM12Problem(this);
    }

    public String getName() {
        return "FM12-Problem" + (isDoRotation() ? "R" : "") + (tough ? "T" : "");
    }

    public String globalInfo() {
        return "The general L-function, L(x)=prod_1^n(sin^k(5 pi x_i)*exp(-l3*(x_i -l4)^2/l5^2), 5^n equidistant peaks of increasing height, single-funnel, but very flat in high dimensions.";
    }

    public SolutionHistogram getHistogram() {
        if (tough) return new SolutionHistogram(-0.01, 0.319, 16);
        else return new SolutionHistogram(-0.01, 0.479, 16);
    }

}
