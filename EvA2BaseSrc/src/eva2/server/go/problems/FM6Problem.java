package eva2.server.go.problems;

import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.operators.postprocess.SolutionHistogram;
import eva2.server.go.problems.AbstractMultiModalProblemKnown;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 23.04.2004
 * Time: 13:54:10
 * To change this template use File | Settings | File Templates.
 */
public class FM6Problem extends AbstractMultiModalProblemKnown implements java.io.Serializable, InterfaceInterestingHistogram {

    public FM6Problem() {
        this.m_Template = new ESIndividualDoubleData();
        this.m_ProblemDimension = 2;
//        this.m_Range            = new double [this.m_ProblemDimension][2];
//        this.m_Range[0][0]      = -0.0;
//        this.m_Range[0][1]      = +1.0;
//        this.m_Range[1][0]      = -0.0;
//        this.m_Range[1][1]      =  1.0;
//        this.m_Extrema          = new double[2];
//        this.m_Extrema[0]       = -1;
//        this.m_Extrema[1]       =  1;
    }

    public double getRangeUpperBound(int dim) {
        return 1.;
    }

    public double getRangeLowerBound(int dim) {
        return 0;
    }

    public FM6Problem(FM6Problem b) {
        super(b);
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM6Problem(this);
    }

    /**
     * This method returns the unnormalized function value for an maximisation problem
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];

        result[0] = 1 - Math.sin(30 * Math.pow(x[0], 3)) * Math.sin(25 * Math.pow(x[1], 2) * x[0]);

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

        // These are CBN-results refined with matlab, not necessarily all
        this.add2DOptimum(0.374110192247255446407195, 0.709824183985142687092207);
        this.add2DOptimum(0.421109488492242389767739, 1.000000000000000000000000);
        this.add2DOptimum(0.539560264269057232588978, 0.341247872953366504500394);
        this.add2DOptimum(0.539560264965340374843095, 0.763053443132230113654657);
        this.add2DOptimum(0.551532235200878351299991, 0.999999999999998223643161);
        this.add2DOptimum(0.639719430795008814349956, 0.542819938096035903285497);
        this.add2DOptimum(0.639719431011097849903990, 0.829171150852957450361203);
        this.add2DOptimum(0.656721443641557156567501, 1.000000000000000000000000);
        this.add2DOptimum(0.715647053134668142959640, 0.888918091459261372477840);
        this.add2DOptimum(0.715647053243688602286454, 0.662560426578526540097869);

        this.add2DOptimum(0.715647053574654079532991, 0.296306030328011893448803);
        this.add2DOptimum(0.778180559507292368692788, 0.492164599923967438499517);
        this.add2DOptimum(0.778180559605784916143989, 0.751793845038820407467028);
        this.add2DOptimum(0.778180559687442929828194, 0.942423458785602630172207);
        this.add2DOptimum(0.832013619954793837152351, 0.990823714170862457351063);
        this.add2DOptimum(0.832013620142055598805086, 0.274805054711154128632700);
        this.add2DOptimum(0.832013620159547939714173, 0.824415163485056612380220);
        this.add2DOptimum(0.832013620228365224917866, 0.614482781728452609470992);
        this.add2DOptimum(0.879658272832675280916703, 0.707101825834922537161731);
        this.add2DOptimum(0.879658272839060728642835, 0.886399047968868147862054);

        this.add2DOptimum(0.879658273088547271179038, 0.462906804878656263912973);
        this.add2DOptimum(0.922635074196862525752749, 0.583525657294287403153987);
        this.add2DOptimum(0.922635074236721308693632, 0.260960607397133448337456);
        this.add2DOptimum(0.922635074247729725094302, 0.940906849390896526941219);
        this.add2DOptimum(0.922635074251276110501863, 0.782881821523295329257053);
        this.add2DOptimum(0.961942651368731693750647, 0.442665800247205032391662);
        this.add2DOptimum(0.961942651368731693750647, 0.6761831802949057);
        this.add2DOptimum(0.961942651465875764316138, 0.847640474745526728561629);
        this.add2DOptimum(0.961942651368731693750647, 0.9898308222645393);
        this.add2DOptimum(0.998276254752867187036713, 0.250879146221261839500016);

        this.add2DOptimum(0.998276254959647779685383, 0.560982825428452680505131);
        this.add2DOptimum(0.998276254963161635558322, 0.752637439287642484586627);
        this.add2DOptimum(0.998276255187995231743514, 0.904557627223424143281250);

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
        return "M6 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "1 - sin(30*x^3) * sin(25*y^2*x), multimodal with 32 known non-equidistant optima.";
    }

    public SolutionHistogram getHistogram() {
        return new SolutionHistogram(-0.1, 1.1, 16);
    }
}