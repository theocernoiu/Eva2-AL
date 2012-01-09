package eva2.server.go.problems;

import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiModalProblemKnown;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 27.04.2003
 * Time: 18:48:46
 * To change this template use Options | File Templates.
 */
public class FM1Problem extends AbstractMultiModalProblemKnown implements java.io.Serializable {

    public FM1Problem() {
        this.m_Template = new ESIndividualDoubleData();
        this.m_ProblemDimension = 2;
//        this.m_Range            = new double [this.m_ProblemDimension][2];
//        this.m_Range[0][0]      = -2.0;
//        this.m_Range[0][1]      =  2.0;
//        this.m_Range[1][0]      = -2.0;
//        this.m_Range[1][1]      =  2.0;
//        this.m_Extrema          = new double[2];
//        this.m_Extrema[0]       = 0;
//        this.m_Extrema[1]       = 1;
    }

    public FM1Problem(FM1Problem b) {
        super(b);
    }

    public double getRangeUpperBound(int dim) {
        return 2.0;
    }

    public double getRangeLowerBound(int dim) {
        return -2.0;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM1Problem(this);
    }

    /**
     * This method returns the unnormalized function value for an maximisation problem
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];
        result[0] = Math.sin(2.2 * Math.PI * x[0] + 0.5 * Math.PI) * (2 - Math.abs(x[1])) / 2.0 * (3 - Math.abs(x[0])) / 2.0 +
                Math.sin(0.5 * Math.PI * x[1] * x[1] + 0.5 * Math.PI) * (2 - Math.abs(x[1])) / 2.0 * (3 - Math.abs(x[0])) / 2.0;
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
        //this.add2DOptimum( -1.78396, 0 );
        //this.add2DOptimum( -0.88927, 0);
        //this.add2DOptimum(  0, 0 );
        //this.add2DOptimum(  0.88927, 0);
        //this.add2DOptimum(  1.78396, 0 );

        // These are Matlab fminserach results with tol = 0.00000000001
        // The central spine
        this.add2DOptimum(-1.78391420169792, 0.00000000000000);
        this.add2DOptimum(-0.88928582795946, 0.00000000000000);
        this.add2DOptimum(0.00000000000000, 0.00000000000000);
        this.add2DOptimum(0.88928582713113, 0.00000000000000);
        this.add2DOptimum(1.78391420170262, 0.00000000000000);
        // lower humps
        this.add2DOptimum(0.896875802594440884, -1.77405618325147096);
        //this.add2DOptimum( 0.90194088817456, -1.67859600538785);
        this.add2DOptimum(0.00000000000000, -1.7737664745411252);
        //this.add2DOptimum( 0.00000000000000, -1.72782233293100);
        this.add2DOptimum(-0.896875802594440774, -1.77405618325147096);
        //this.add2DOptimum(-0.90194088817456, -1.67859600538785);

        // upper humps
        this.add2DOptimum(.896875802594440884, 1.77405618325147096);
        //this.add2DOptimum( 0.90194088817456,  1.67859600538785);
        this.add2DOptimum(0.00000000000000, 1.7737534139890971);
        //this.add2DOptimum( 0.00000000000000,  1.72782233293100);
        this.add2DOptimum(-.896875802594440774, 1.77405618325147096);
        //this.add2DOptimum(-0.90194088817456,  1.67859600538785);

        // lower humps
        this.add2DOptimum(-1.79684624865462216, -1.77467528003759246);
        this.add2DOptimum(1.79684624865462216, -1.77467528003759246);
        // upper humps
        this.add2DOptimum(-1.79684624865462216, 1.77467528003759246);
        this.add2DOptimum(1.79684624865462216, 1.77467528003759246);
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
        return "M1-Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Five hills and four valleys.";
    }
}
