package eva2.server.go.problems;

import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiModalProblemKnown;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 23.04.2003
 * Time: 17:02:19
 * To change this template use Options | File Templates.
 */
public class FM2Problem extends AbstractMultiModalProblemKnown implements java.io.Serializable {

    public FM2Problem() {
        this.m_Template = new ESIndividualDoubleData();
        this.m_ProblemDimension = 2;
        setDefaultRange(2);
//        this.m_Range            = new double [this.m_ProblemDimension][2];
//        this.m_Range[0][0]      = -2.0;
//        this.m_Range[0][1]      =  2.0;
//        this.m_Range[1][0]      = -2.0;
//        this.m_Range[1][1]      =  2.0;
//        this.m_Extrema          = new double[2];
//        this.m_Extrema[0]       = 0;
//        this.m_Extrema[1]       = 6/4.0;
    }

    public FM2Problem(FM2Problem b) {
        super(b);
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM2Problem(this);
    }

    /**
     * This method returns the unnormalized function value for an maximisation problem
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];

        result[0] = 3 * Math.sin(0.5 * x[0] * Math.PI + 0.5 * Math.PI) * (2 - Math.sqrt(x[0] * x[0] + x[1] * x[1])) / 4.0;

        return result;
    }

/**********************************************************************************************************************
 * Implementation of InterfaceMultimodalProblem
 */

    /**
     * This method will prepare the problem to return a list of all optima
     * if possible and to return quality measures like NumberOfOptimaFound and
     * the MaximumPeakRatio. This method should be called by the user.
     */
    public void initListOfOptima() {
//        this.m_GlobalOpt = Double.NEGATIVE_INFINITY;
//        this.m_Optima = new Population();
        this.add2DOptimum(-2, -2);
        this.add2DOptimum(-2, 2);
        this.add2DOptimum(2, -2);
        this.add2DOptimum(2, 2);
        this.add2DOptimum(0, 0);
    }

    /**********************************************************************************************************************
     * These are for GUI
     */
    /**
     * This method allows the CommonJavaObjectEditorPanel to read the
     * name to the current object.
     *
     * @return The name.
     */
    public String getName() {
        return "M2 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "One center peak and four neighbours.";
    }
}
