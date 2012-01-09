package eva2.server.go.problems;

import eva2.gui.GenericObjectEditor;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.problems.AbstractMultiModalProblemKnown;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 01.07.2005
 * Time: 17:39:16
 * To change this template use File | Settings | File Templates.
 */
public class FM7Problem extends AbstractMultiModalProblemKnown implements java.io.Serializable {

    public FM7Problem() {
        this.m_Template = new ESIndividualDoubleData();
        this.m_ProblemDimension = 2;
//        this.m_Range            = new double [this.m_ProblemDimension][2];
//        this.m_Range[0][0]      = -5;
//        this.m_Range[0][1]      = 10;
//        this.m_Range[1][0]      = -0;
//        this.m_Range[1][1]      =  15;
//        this.m_Extrema          = new double[2];
//        this.m_Extrema[0]       = -0;
//        this.m_Extrema[1]       = 300;
    }

    public void hideHideable() {
        super.hideHideable();
        GenericObjectEditor.setHideProperty(this.getClass(), "defaultRange", true);
    }

    public double getRangeUpperBound(int dim) {
        if (dim == 0) return 10;
        else return 15;
    }

    public double getRangeLowerBound(int dim) {
        if (dim == 0) return -5;
        else return 0;
    }

    public FM7Problem(FM7Problem b) {
        super(b);
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM7Problem(this);
    }

    /**
     * This method returns the unnormalized function value for an maximisation problem
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];

//        result[0]   = 300 - Math.pow((5+x[0])/Math.PI -(5.1*x[0]*x[0])/(4*Math.PI*Math.PI) + x[1] -6,2) + 10 * (1-(Math.cos(x[0])/((8*Math.PI))));
        result[0] = 300 - (Math.pow((x[1] - (5 / (4 * Math.PI * Math.PI)) * x[0] * x[0]) + (5 * x[0] / Math.PI) - 6, 2) + 10 * (1 - (1 / (8 * Math.PI))) * Math.cos(x[0]) + 10);

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
        // The central spine
        this.add2DOptimum(3.141592657950245, 2.249999968719379);
        this.add2DOptimum(-3.141592645545167, 12.249999965800779);
        this.add2DOptimum(9.42477796047877, 2.2499999841345817);
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
        return "M7 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The Branin function, three optima.";
    }
}