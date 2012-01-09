package eva2.server.go.problems;

import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiModalProblemKnown;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 01.07.2005
 * Time: 17:45:10
 * To change this template use File | Settings | File Templates.
 */
public class FM8Problem extends AbstractMultiModalProblemKnown implements java.io.Serializable {
    protected int numMinima = 10;

    public FM8Problem() {
//    	setDefaultRange(10);
        this.m_Template = new ESIndividualDoubleData();
        this.m_ProblemDimension = 2;
//        this.m_Range            = new double [this.m_ProblemDimension][2];
//        this.m_Range[0][0]      = -65.536;
//        this.m_Range[0][1]      = +65.536;
//        this.m_Range[1][0]      = -65.536;
//        this.m_Range[1][1]      = +65.536;
//        this.m_Range[0][0]      = 0;
//        this.m_Range[0][1]      = 10;
//        this.m_Range[1][0]      = 0;
//        this.m_Range[1][1]      = 10;
//        this.m_Extrema          = new double[2];
//        this.m_Extrema[0]       = -0;
//        this.m_Extrema[1]       = 500;
    }

    public double getRangeLowerBound(int i) {
        return 0;
    }

    public double getRangeUpperBound(int i) {
        return 10;
    }

    public FM8Problem(FM8Problem b) {
        super(b);
        numMinima = b.numMinima;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM8Problem(this);
    }

    /**
     * This method returns the unnormalized function value for a maximization problem
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];
        double sum = 0, tmp;

//        result[0]   = 1/500;
//        for (int i = 0; i <= 24; i++) {
//            tmp = 1 + i;
//            for (int j = 0; j <= 1; j++) {
//                tmp += Math.pow((x[j] - this.getA(i, j)),6);
//            }
//            result[0] += 1/tmp;
//        }
//        return result;
//        
        for (int i = 0; i < numMinima; i++) {
            tmp = getC(i);
            for (int j = 0; j < x.length; j++) tmp += Math.pow(x[j] - getA(i, j), 2);
            sum += 1 / tmp;
        }
        result[0] = sum;
        return result;
    }

    private double getC(int i) {
        //c = [0.1 0.2 0.2 0.4 0.4 0.6 0.3];
//    	c = [ 0.1, 0.2, 0.2, 0.4, 0.4, 0.6, 0.3, 0.7, 0.5, 0.5];
        final double[] cArr = {0.1, 0.2, 0.2, 0.4, 0.4, 0.6, 0.3, 0.7, 0.5, 0.5};
        return cArr[i];
    }

    private double getA(int i, int j) {
//    	a = [4, 1, 8, 6, 3, 2, 5, 8, 6, 7;
//           4, 1, 8, 6, 7, 9, 5, 1, 2, 3.6;
//           4, 1, 8, 6, 3, 2, 3, 8, 6, 7;
//           4, 1, 8, 6, 7, 9, 3, 1, 2, 3.6];

        switch (i) {
            case 0:
                return 4;
            case 1:
                return 1;
            case 2:
                return 8;
            case 3:
                return 6;
            case 4:
                return ((j % 2) == 1) ? 3 : 7;
            case 5:
                return ((j % 2) == 1) ? 2 : 9;
            case 6:
                return (j < 2) ? 5 : 3;
            case 7:
                return ((j % 2) == 1) ? 8 : 1;
            case 8:
                return ((j % 2) == 1) ? 6 : 2;
            case 9:
                return ((j % 2) == 1) ? 7 : 3.6;
            default:
                System.err.println("invalid index i in FM8Problem!");
                return -1;
        }
//        int index;
//        if (j == 0) {
//            index = i%5;
//            switch (index) {
//                case 0 : return -32;
//                case 1 : return -16;
//                case 2 : return -0;
//                case 3 : return 16;
//                case 4 : return 32;
//            }
//        } else {
//            index = (int)Math.floor(i/5.0);
//            switch (index) {
//                case 0 : return -32;
//                case 1 : return -16;
//                case 2 : return -0;
//                case 3 : return 16;
//                case 4 : return 32;
//            }
//        }
//
//        return 0;
    }

//    public static void main(String[] args) {
//        FM8Problem t = new FM8Problem();
//        double[] tmpD = new double[2];
//        double      tmpd;
//        String      tmp;
//        tmpD[0] = 0;
//        tmpD[1] = 1;
//        t.doEvaluation(tmpD);
//        for (double x = t.m_Range[0][0]; x <= t.m_Range[0][1]; x += 2) {
//            tmp = "";
//            for (double y = t.m_Range[1][0]; y <= t.m_Range[1][1]; y += 2) {
//                if (tmp.length() >0) tmp += "; ";
//                tmpD[0] = x;
//                tmpD[1] = y;
//                tmpd = t.doEvaluation(tmpD)[0];
//                tmp += (1-tmpd);
//            }
//            System.out.println(tmp);
//        }
//    }

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
//    	a = [4, 1, 8, 6, 3, 2, 5, 8, 6, 7;
//           4, 1, 8, 6, 7, 9, 5, 1, 2, 3.6;
//      4, 1, 8, 6, 3, 2, 3, 8, 6, 7;
//      4, 1, 8, 6, 7, 9, 3, 1, 2, 3.6];
        // the exact positions depend on the number of optima!
        for (int i = 0; i < numMinima; i++) {
            this.add2DOptimum(getA(i, 0), getA(i, 1));
        }
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
        return "M8-Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Shekel's Foxholes. Exact optimum positions depend on the number of optima.";
    }

    /**
     * @return the numMinima
     */
    public int getNumMinima() {
        return numMinima;
    }

    /**
     * @param numMinima the numMinima to set
     */
    public void setNumMinima(int numMinima) {
        if ((numMinima < 1) || (numMinima > 10)) System.err.println("Error, numMinima must be in {1..10}, forcing...");
        if (numMinima < 1) numMinima = 1;
        if (numMinima > 10) numMinima = 10;
        this.numMinima = numMinima;
    }

    public String numMinimaTipText() {
        return "The number of minima for the Shekel function, 3 <= n <= 10";
    }
}