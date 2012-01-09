package eva2.server.go.problems;

import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiModalProblemKnown;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 27.04.2003
 * Time: 19:06:40
 * To change this template use Options | File Templates.
 */
public class FM4Problem extends AbstractMultiModalProblemKnown implements java.io.Serializable {
    double yOffs = 0.;

    public FM4Problem() {
        this.m_Template = new ESIndividualDoubleData();
        this.m_ProblemDimension = 2;
//        this.m_Range            = new double [this.m_ProblemDimension][2];
//        this.m_Range[0][0]      = -0.9;
//        this.m_Range[0][1]      = +1.2;
//        this.m_Range[1][0]      = -1.2;
//        this.m_Range[1][1]      =  1.2;
//        this.m_Extrema          = new double[2];
//        this.m_Extrema[0]       = -10;
//        this.m_Extrema[1]       = 7;
    }

    public double getRangeUpperBound(int dim) {
        return 1.2;
    }

    public double getRangeLowerBound(int dim) {
        if (dim == 0) return -0.9;
        else return -1.2;
    }

    public FM4Problem(FM4Problem b) {
        super(b);
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM4Problem(this);
    }

    /**
     * This method returns the unnormalized function value for an maximisation problem
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];

//        result[0]   = Math.pow(0.3*x[0],3) - (x[1]*x[1] - 4.5*x[1]*x[1])*x[0]*x[1] - 4.7*Math.cos(3*x[0] - x[1]*x[1]*(x[0] + 2))*Math.sin(2.5*Math.PI*x[0]);
        result[0] = yOffs + Math.pow(0.3 * x[0], 3) + (3.5 * x[1] * x[1] * x[0] * x[1]) - 4.7 * Math.cos(3 * x[0] - x[1] * x[1] * (x[0] + 2)) * Math.sin(2.5 * Math.PI * x[0]);
//        if (result[0] <= Double.MIN_VALUE) result[0] = Double.MIN_VALUE; // TODO
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
//        this.add2DOptimum( -0.6056905022614584,-1.1775667787827147);
//        this.add2DOptimum( -0.6093666609619675,0.8072056564740993);
//        this.add2DOptimum( -0.1726961633323544,1.6960101146286605E-5);
//        this.add2DOptimum( 0.161837844630154,-1.2);
//        this.add2DOptimum( 0.20829636154225653,1.2);
//        this.add2DOptimum( 0.5865013507220145,-0.776702725435043);
//        this.add2DOptimum( 0.6176949017895114,0.8942309264151004);
//        this.add2DOptimum( 0.8840398575338573,1.2);
//        this.add2DOptimum( 1.0062912570708773,3.278889047891279E-5);
//        this.add2DOptimum( 1.2,1.2);
        // These are Matlab fminserach results with tol = 0.00000000001
        // The central spine
        this.add2DOptimum(1.20000000000000, 1.20000000000000);
        this.add2DOptimum(1.00628038537047, 0.00000000689096);
        this.add2DOptimum(0.58650408989149, -0.77670353932191);
        this.add2DOptimum(0.16183781451253, -1.20000000000000);
        this.add2DOptimum(-0.60568949512716, -1.17756193443749);
        this.add2DOptimum(-0.60936215388615, 0.80722389087779);
        this.add2DOptimum(-0.17269425778171, 0.00000000355572);
        this.add2DOptimum(0.20829705701501, 1.20000000000000);
        this.add2DOptimum(0.61771303085914, 0.89427682808624);
        this.add2DOptimum(0.87892611841173, 1.20000000000000);
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
        return "M4 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Waves with 10 peaks.";
    }
}
