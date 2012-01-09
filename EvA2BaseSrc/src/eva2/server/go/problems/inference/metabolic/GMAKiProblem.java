package eva2.server.go.problems.inference.metabolic;

import eva2.gui.BeanInspector;
import eva2.server.go.problems.inference.metabolic.odes.GMAKiSystem;
import eva2.server.go.strategies.InterfaceOptimizer;

// Created at 2007-02-02

/**
 * @author Andreas Dr&auml;ger
 */
public class GMAKiProblem extends AbstractValineCurveFittingProblem {

    /**
     * Generated serial version id.
     */
    private static final long serialVersionUID = -3635573692736142863L;

    /**
     * Default constructor for simulation of the valine data.
     */
    public GMAKiProblem() {
        super(new GMAKiSystem());
        m_ProblemDimension = system.getNumberOfParameters();
    }

    public GMAKiProblem(GMAKiProblem gmakiProblem) {
        super(gmakiProblem);
        this.m_ProblemDimension = system.getNumberOfParameters();
    }

    public String getName() {
        return "GMAKiProblem";
    }

    public static void main(String[] args) {
        double[] x = new double[]{0.009627438072633075, 2.7631396279031226, 1.0389410453085917, 0.0, 0.004356345301468399, 0.0010162038221844982, 0.0,
                0.0, 1.3738487913271277E-17, 45.019041290968715, 0.0, 0.0, 0.0, 0.0, 0.8443630238635461, 0.0, 0.09703511966194323, 1.6001561604842542};
        double[] x2 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 279.9254961069882, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.431560123128834, 0.0};
        GMAKiProblem prob = new GMAKiProblem();
        prob.initProblem();
//		AbstractEAIndividual indy = prob.getEAIndividual();
//		((InterfaceDataTypeDouble)indy).SetDoubleGenotype(x);
//		double[] fit = prob.eval(x);
        System.out.println("x is " + BeanInspector.toString(x));
        System.out.println("fit is " + BeanInspector.toString(prob.eval(x)));
    }

    /*
      * (non-Javadoc)
      * @see eva2.server.go.problems.inference.metabolic.AbstractValineCurveFittingProblem#getParameterRanges()
      */
    protected double[][] getParameterRanges() {
        int i;
        // set the range
        double[][] range = new double[this.m_ProblemDimension][2];
        for (i = 0; i < 12; i++) {
            range[i][0] = 0;
            range[i][1] = 2000;
        }
        for (i = 12; i < this.m_ProblemDimension; i++) {
            range[i][0] = 0;
            range[i][1] = 8;
        }
        return range;
    }

    /*
      * (non-Javadoc)
      *
      * @see eva2.server.go.problems.AbstractOptimizationProblem#clone()
      */
    public Object clone() {
        return new GMAKiProblem(this);
    }

    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        return "Parameter optimization problem for the valine and leucine biosynthesis"
                + " in C. glutamicum, where all reactions are modeled using generalized"
                + " mass-action kinetics. Only two reactions are considered irreversible.";
    }

}
