package eva2.server.go.problems;

import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiModalProblemKnown;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 27.04.2003
 * Time: 18:58:49
 * To change this template use Options | File Templates.
 */
public class FM3Problem extends AbstractMultiModalProblemKnown implements java.io.Serializable {

    public FM3Problem() {
        this.m_Template = new ESIndividualDoubleData();
        this.m_ProblemDimension = 2;
//        this.m_Range                = new double [this.m_ProblemDimension][2];
//        this.m_Range[0][0]          = -1.9;
//        this.m_Range[0][1]          = +1.9;
//        this.m_Range[1][0]          = -1.1;
//        this.m_Range[1][1]          =  1.1;
//        this.m_Extrema              = new double[2];
//        this.m_Extrema[0]           = -6;
//        this.m_Extrema[1]           = 1;
    }

    public double getRangeUpperBound(int dim) {
        if (dim == 0) return 1.9;
        else return 1.1;
    }

    public double getRangeLowerBound(int dim) {
        return -1 * getRangeUpperBound(dim);
    }

    public FM3Problem(FM3Problem b) {
        super(b);
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM3Problem(this);
    }

    /**
     * This method returns the unnormalized function value for an maximisation problem
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];

        result[0] = 100. - ((4 - 2.1 * x[0] * x[0] + (x[0] * x[0] * x[0] * x[0]) / 3) * x[0] * x[0] + x[0] * x[1] + (-4 + 4 * x[1] * x[1]) * x[1] * x[1]);

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
//        this.add2DOptimum( -1.7035, 0.7959);
//        this.add2DOptimum(  1.7035,-0.7959);
//        this.add2DOptimum( -1.607,-0.569);
//        this.add2DOptimum(  1.607, 0.569);
//        this.add2DOptimum( -0.089915, 0.71265);
//        this.add2DOptimum(  0.089815,-0.71265);
        // These are Matlab fminserach results with tol = 0.00000000001
        // The central spine
        this.add2DOptimum(0.08984201436464, -0.71265640267836);
        this.add2DOptimum(-0.08984201237216, 0.71265640468014);
        this.add2DOptimum(1.70360671462676, -0.79608356723395);
        this.add2DOptimum(-1.70360671462676, 0.79608356723395);
        this.add2DOptimum(1.60710474533347, 0.56865144361152);
        this.add2DOptimum(-1.60710474533347, -0.56865144361152);
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
        return "M3 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Six hump camel back.";
    }
}
