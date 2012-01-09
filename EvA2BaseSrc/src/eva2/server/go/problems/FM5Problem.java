package eva2.server.go.problems;

import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiModalProblemKnown;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 01.07.2005
 * Time: 17:30:05
 * To change this template use File | Settings | File Templates.
 */
public class FM5Problem extends AbstractMultiModalProblemKnown implements java.io.Serializable {

    public FM5Problem() {
        setDefaultRange(6);

        this.m_Template = new ESIndividualDoubleData();
        this.m_ProblemDimension = 2;
//        this.m_Range            = new double [this.m_ProblemDimension][2];
//        this.m_Range[0][0]      = -6;
//        this.m_Range[0][1]      = +6;
//        this.m_Range[1][0]      = -6;
//        this.m_Range[1][1]      = +6;
//        this.m_Extrema          = new double[2];
//        this.m_Extrema[0]       = -2000;
//        this.m_Extrema[1]       = 200;
    }

    public FM5Problem(FM5Problem b) {
        super(b);
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM5Problem(this);
    }

    /**
     * This method returns the unnormalized function value for an maximisation problem
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];

        result[0] = 100 - Math.pow(x[0] * x[0] + x[1] - 11, 2) - Math.pow(x[0] + x[1] * x[1] - 7, 2);

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
        // These are Matlab fminserach results with tol = 0.00000000001

        this.add2DOptimum(3, 2);
        this.add2DOptimum(3.584428340181519, -1.848126527160978);
        this.add2DOptimum(-3.779310253789459, -3.283185991486118);
        this.add2DOptimum(-2.805118086683826, 3.131312518079310);
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
        return "M5 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The Himmelblau function, four optima, global one at (3,2).";
    }
}