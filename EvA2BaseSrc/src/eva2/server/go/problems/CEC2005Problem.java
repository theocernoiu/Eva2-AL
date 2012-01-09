package eva2.server.go.problems;

import java.util.List;

import eva2.gui.GenericObjectEditor;
import eva2.gui.TopoPlot;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.operators.postprocess.SolutionHistogram;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.BasicResourceLoader;
import eva2.tools.EVAERROR;
import eva2.tools.math.RNG;

/**
 * This implements the CEC 2005 Benchmarks (Special Session on Real-Parameter Optimization), cf.
 * P. N. Suganthan, N. Hansen, J. J. Liang, K. Deb, Y.-P. Chen, A. Auger and S. Tiwari,
 * "Problem Definitions and Evaluation Criteria for the CEC 2005 Special Session on Real-Parameter Optimization",
 * Technical Report, Nanyang Technological University, Singapore, May 2005 AND KanGAL Report #2005005, IIT Kanpur, India.
 * The original code was part of Maurice Clerc's TRIBES package recently imported into EvA, many thanks go to him.
 * During porting, I fixed some bugs in that version and hopefully introduced less new ones. I also replaced some of the
 * data by more complete versions (matrices for more dimensions).
 * The bugs were
 * - within the schwefel+noise noise calculation (additive instead of multiplicative)
 * - in the rotation method (matrix was incorrectly transponed)
 * - sphere vector o lacked one ','
 *
 * @author Maurice Clerc, Marcel Kronfeld
 */
public class CEC2005Problem extends AbstractProblemDouble
        implements InterfaceMultimodalProblem, Interface2DBorderProblem, InterfaceInterestingHistogram {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //	protected SelectedTag benchmark = new SelectedTag("ShiftSphere", "ShiftSchwefel", "ShiftRotEllipse", "ShiftSchwefelNoise", "ShiftSchwefelBounds", "ShiftRosenbrock", "ShiftRotGriewankBounds", "ShiftRotAckleyBounds", "ShiftRastrigin", "ShiftRotRastrigin", "ShiftRotWeierstrass", "ShiftSchwefel213");
    protected int problemDimension = 10;
    protected CECBenchSelection benchSel = CECBenchSelection.ShiftSphere;

    transient protected double[] A;
    transient protected double[] B;
    transient protected double[] alpha;
    transient protected double[] tmpAVals = null;

    private static double[] sphere_o = {
            -3.9311900e+001, 5.8899900e+001, -4.6322400e+001,
            -7.4651500e+001, -1.6799700e+001, -8.0544100e+001,
            -1.0593500e+001, 2.4969400e+001, 8.9838400e+001,
            9.1119000e+000, -1.0744300e+001, -2.7855800e+001,
            -1.2580600e+001, 7.5930000e+000, 7.4812700e+001,
            6.8495900e+001, -5.3429300e+001, 7.8854400e+001,
            -6.8595700e+001, 6.3743200e+001, 3.1347000e+001,
            -3.7501600e+001, 3.3892900e+001, -8.8804500e+001,
            -7.8771900e+001, -6.6494400e+001, 4.4197200e+001,
            1.8383600e+001, 2.6521200e+001, 8.4472300e+001,
            3.9176900e+001, -6.1486300e+001,
            -2.5603800e+001, -8.1182900e+001, 5.8695800e+001,
            -3.0838600e+001, -7.2672500e+001, 8.9925700e+001,
            -1.5193400e+001, -4.3337000e+000, 5.3430000e+000,
            1.0560300e+001, -7.7726800e+001, 5.2085900e+001,
            4.0394400e+001, 8.8332800e+001, -5.5830600e+001,
            1.3181000e+000, 3.6025000e+001, -6.9927100e+001,
            -8.6279000e+000, -5.6894400e+001, 8.5129600e+001,
            1.7673600e+001, 6.1529000e+000, -1.7695700e+001,
            -5.8953700e+001, 3.0356400e+001, 1.5920700e+001,
            -1.8008200e+001, 8.0641100e+001, -4.2391200e+001,
            7.6277600e+001, -5.0165200e+001, -7.3573600e+001,
            2.8336900e+001, -5.7990500e+001, -2.2732700e+001,
            5.2026900e+001, 3.9259900e+001, 1.0867900e+001,
            7.7820700e+001, 6.6039500e+001, -5.0066700e+001,
            5.5706300e+001, 7.3714100e+001, 3.8529600e+001,
            -5.6786500e+001, -8.9647700e+001, 3.7957600e+001,
            2.9472000e+001, -3.5464100e+001, -3.1786800e+001,
            7.7323500e+001, 5.4790600e+001, -4.8279400e+001,
            7.4271400e+001, 7.2610300e+001, 6.2964000e+001,
            -1.4144600e+001, 2.0492300e+001, 4.6589700e+001,
            -8.3602100e+001, -4.6480900e+001, 8.3737300e+001,
            -7.9661100e+001, 2.4347900e+001, -1.7230300e+001,
            7.2340400e+001, -3.6402200e+001
    };

    private static double[] schwefel_1_2_o = {
            3.5626700000000000e+001, -8.2912300000000002e+001,
            -1.0642300000000001e+001, -8.3581500000000005e+001,
            8.3155199999999994e+001, 4.7048000000000002e+001,
            -8.9435900000000004e+001, -2.7421900000000001e+001,
            7.6144800000000004e+001, -3.9059500000000000e+001,
            4.8885700000000000e+001, -3.9828000000000001e+000,
            -7.1924300000000002e+001, 6.4194699999999997e+001,
            -4.7733800000000002e+001, -5.9896000000000003e+000,
            -2.6282800000000002e+001, -5.9181100000000001e+001,
            1.4602800000000000e+001, -8.5477999999999994e+001,
            -5.0490099999999998e+001, 9.2400000000000004e-001,
            3.2397799999999997e+001, 3.0238800000000001e+001,
            -8.5094899999999996e+001, 6.0119700000000002e+001,
            -3.6218299999999999e+001, -8.5883000000000003e+000,
            -5.1970999999999998e+000, 8.1553100000000001e+001,
            -2.3431600000000000e+001, -2.5350500000000000e+001,
            -4.1248500000000000e+001, 8.8018000000000001e+000,
            -2.4222200000000001e+001, -8.7980699999999999e+001,
            7.8047300000000007e+001, -4.8052799999999998e+001,
            1.4017700000000000e+001
            , -3.6640500000000003e+001, 1.2216799999999999e+001,
            1.8144900000000000e+001, -6.4564700000000002e+001,
            -8.4849299999999999e+001, -7.6608800000000002e+001,
            -1.7041999999999999e+000, -3.6076099999999997e+001,
            3.7033600000000000e+001, 1.8443100000000001e+001,
            -6.4358999999999995e+001, -1.6750000000000000e+001,
            -7.6900000000000004e+000, -2.1280000000000001e+001,
            9.2019999999999996e+001, -1.3560000000000000e+001,
            -1.8109999999999999e+001, -8.8730000000000004e+001,
            -4.5509999999999998e+001, 1.3600000000000001e+000,
            -9.8900000000000006e+001, 5.9579999999999998e+001,
            -3.6479999999999997e+001, 7.5890000000000001e+001,
            3.2549999999999997e+001, -6.1210000000000001e+001,
            -1.9600000000000001e+001, 2.0239999999999998e+001,
            -1.3490000000000000e+001, 5.0149999999999999e+001,
            4.4729999999999997e+001, 4.1380000000000003e+001,
            -1.0000000000000001e-001, -4.4500000000000000e+001,
            3.0489999999999998e+001, 2.2180000000000000e+001,
            5.6299999999999999e+000, -6.2430000000000000e+001,
            -9.0859999999999999e+001, 6.2289999999999999e+001,
            5.0999999999999996e+000, -8.8969999999999999e+001,
            -5.9960000000000001e+001, -6.3469999999999999e+001,
            7.9079999999999998e+001, -8.7780000000000001e+001,
            5.9439999999999998e+001, -3.6579999999999998e+001,
            5.9780000000000001e+001, 7.7739999999999995e+001,
            2.2460000000000001e+001
            , 9.6790000000000006e+001, -1.1100000000000001e+000,
            4.1109999999999999e+001, 7.0609999999999999e+001,
            7.1739999999999995e+001, -9.9890000000000001e+001,
            9.2439999999999998e+001, 4.4289999999999999e+001,
            -7.8469999999999999e+001, -6.8659999999999997e+001
    };

    private static double[] elliptic_o = {
            -3.2201300e+001, 6.4977600e+001, -3.8300000e+001,
            -2.3258200e+001, -5.4008800e+001, 8.6628600e+001,
            -6.3009000e+000, -4.9364400e+001, 5.3499000e+000,
            5.2241800e+001, -1.3364300e+001, 7.3126300e+001,
            -8.5691000e+000, -2.0491500e+001, -6.0148700e+001,
            1.6088400e+001, -7.8331900e+001, 7.0038700e+001,
            -6.8521000e+000, -6.4797000e+001, 6.5400500e+001,
            -2.6023300e+001, -3.3875700e+001, 5.1589300e+001,
            2.7642700e+001, -6.9448500e+001, 2.5512300e+001,
            -5.9078200e+001, -6.6548100e+001, -5.1273300e+001,
            -8.1776000e+001, -7.1657200e+001, 3.7081000e+001,
            -6.3424800e+001, -6.4778500e+001, 3.1529900e+001,
            1.8538700e+001, 9.8342000e+000, -6.0370000e-001,
            1.7346000e+000, 7.0160500e+001, -8.2039100e+001,
            -4.2736800e+001, -8.3593000e+001, -8.5025500e+001,
            4.1177300e+001, 4.1649000e+000, -1.3450500e+001,
            -3.1000000e-001, -3.8794400e+001, 7.1270200e+001,
            6.5532000e+001, 8.7753000e+000, -5.5469100e+001,
            -2.0625200e+001, 2.2290100e+001, 1.3679800e+001,
            6.5674500e+001, 7.5841800e+001, 2.7892600e+001,
            -1.5061600e+001, -1.7303600e+001
            , 5.7934600e+001, -8.6632600e+001, 6.5059600e+001,
            4.7388400e+001, 2.9166000e+001, 6.5543500e+001,
            3.4643000e+000, -3.9814000e+001, 1.8226100e+001,
            7.7044600e+001, 6.2188200e+001, -1.1400000e+001,
            -1.0621800e+001, 7.0127600e+001, -4.0867300e+001,
            -2.4445100e+001, 5.2139800e+001, -1.0513600e+001,
            2.9239900e+001, 2.1705000e+000, 4.4086300e+001,
            8.1794300e+001, 8.0046600e+001, 8.8326600e+001,
            1.6609800e+001, -5.0257300e+001, -7.1699300e+001,
            7.1536800e+001, 6.1427300e+001, -3.6739000e+000,
            7.7942800e+001, -2.2329400e+001, 6.4763400e+001,
            -7.4282300e+001, 1.4189900e+001, 3.7847300e+001,
            -7.7712900e+001, 2.8995900e+001
    };

    private static double[] schwefel_bounds_o = {
            -5.5559000e+000, 7.9470000e+000, -1.5380000e+000,
            8.3897000e+000, 7.7182000e+000, -8.3147000e+000,
            -2.1423000e+000, -2.4392000e+000, -3.3787000e+000,
            -7.3047000e+000, 3.0580000e+000, 6.7613000e+000,
            2.3444000e+000, 5.6514000e+000, 1.0491000e+000,
            -8.3240000e-001, 1.3039000e+000, -6.5100000e-002,
            4.2400000e-002, -6.5176000e+000, -8.6977000e+000,
            2.7053000e+000, -1.4842000e+000, -8.8158000e+000,
            5.6475000e+000, -4.5999000e+000, 3.6337000e+000,
            -6.4068000e+000, 4.8867000e+000, 8.2225000e+000,
            6.6873000e+000, -5.8862000e+000, 2.5925000e+000,
            8.0270000e-001, 7.5525000e+000, 4.2621000e+000,
            3.5091000e+000, -2.6055000e+000, -8.4063000e+000,
            6.1947000e+000, -6.5024000e+000, -8.1440000e+000,
            -4.8444000e+000, 3.1572000e+000, 3.9624000e+000,
            -8.4969000e+000, 6.2642000e+000, 1.1448000e+000,
            3.9132000e+000, 3.6140000e+000, -8.4785000e+000,
            2.9550000e-001, 3.5597000e+000, -5.5854000e+000,
            -8.9173000e+000, 4.0627000e+000, 8.3870000e+000,
            -3.7680000e+000, 5.9001000e+000, 3.8212000e+000,
            -6.4771000e+000, 6.8886000e+000
            , -2.4951000e+000, 2.5007000e+000, 1.3866000e+000,
            -7.3488000e+000, -1.5349000e+000, 2.9223000e+000,
            7.1813000e+000, -4.7990000e+000, 8.3061000e+000,
            -8.7911000e+000, -6.3035000e+000, -6.1222000e+000,
            5.7116000e+000, 7.4369000e+000, 6.1738000e+000,
            -7.1118000e+000, 3.7062000e+000, 6.1274000e+000,
            1.3696000e+000, -4.5894000e+000, 4.5844000e+000,
            -1.2133000e+000, 4.1800000e+000, 3.3370000e-001,
            7.3355000e+000, 8.1600000e+000, 1.4422000e+000,
            7.9909000e+000, -8.1183000e+000, -6.8285000e+000,
            -2.9079000e+000, 2.0280000e-001, -6.2375000e+000,
            1.2940000e-001, 1.4740000e-001, 2.6981000e+000,
            4.2330000e-001, 6.1942000e+000};

    private static double[] rosenbrock_o = {
            8.1023200e+001, -4.8395000e+001, 1.9231600e+001,
            -2.5231000e+000, 7.0433800e+001,
            4.7177400e+001, -7.8358000e+000, -8.6669300e+001,
            5.7853200e+001, -9.9533000e+000,
            2.0777800e+001, 5.2548600e+001, 7.5926300e+001,
            4.2877300e+001, -5.8272000e+001,
            -1.6972800e+001, 7.8384500e+001, 7.5042700e+001,
            -1.6151300e+001, 7.0856900e+001,
            -7.9579500e+001, -2.6483700e+001, 5.6369900e+001,
            -8.8224900e+001, -6.4999600e+001,
            -5.3502200e+001, -5.4230000e+001, 1.8682600e+001,
            -4.1006100e+001, -5.4213400e+001,
            -8.7250600e+001, 4.4421400e+001, -9.8826000e+000,
            7.7726600e+001, -6.1210000e+000,
            -1.4643000e+001, 6.2319800e+001, 4.5274000e+000,
            -5.3523400e+001, 3.0984700e+001,
            6.0861300e+001, -8.6464800e+001, 3.2629800e+001,
            -2.1693400e+001, 5.9723200e+001,
            5.0630000e-001, 3.7704800e+001, -1.2799300e+001,
            -3.5168800e+001, -5.5862300e+001,
            -5.5182300e+001, 3.2800100e+001, -3.5502400e+001,
            7.5012000e+000, -6.2842800e+001,
            3.5621700e+001, -2.1892800e+001, 6.4802000e+001,
            6.3657900e+001, 1.6841300e+001,
            -6.2050000e-001, 7.1958400e+001, 5.7893200e+001,
            2.6083800e+001, 5.7235300e+001,
            2.8840900e+001, -2.8445200e+001, -3.7849300e+001,
            -2.8585100e+001, 6.1342000e+000,
            4.0880300e+001, -3.4327700e+001, 6.0929200e+001,
            1.2253000e+001, -2.3325500e+001,
            3.6493100e+001, 8.3828000e+000, -9.9215000e+000,
            3.5022100e+001, 2.1835800e+001,
            5.3067700e+001, 8.2231800e+001, 4.0662000e+000,
            6.8425500e+001, -5.8867800e+001,
            8.6354400e+001, -4.1139400e+001, -4.4580700e+001,
            6.7633500e+001, 4.2715000e+001,
            -6.5426600e+001, -8.7883700e+001, 7.0901600e+001,
            -5.4155100e+001, -3.6229800e+001,
            2.9059600e+001, -3.8806400e+001, -5.5396000e+000,
            -7.8339300e+001, 8.7900200e+001
    };

    private static double[] griewank_o = {
            -2.7626840e+002, -1.1911000e+001, -5.7878840e+002,
            -2.8764860e+002, -8.4385800e+001, -2.2867530e+002,
            -4.5815160e+002, -2.0221450e+002, -1.0586420e+002,
            -9.6489800e+001, -3.9574680e+002, -5.7294980e+002,
            -2.7036410e+002, -5.6685430e+002, -1.5242040e+002,
            -5.8838190e+002, -2.8288920e+002, -4.8888650e+002,
            -3.4698170e+002, -4.5304470e+002, -5.0658570e+002,
            -4.7599870e+002, -3.6204920e+002, -2.3323670e+002,
            -4.9198640e+002, -5.4408980e+002, -7.3445600e+001,
            -5.2690110e+002, -5.0225610e+002, -5.3723530e+002,
            -2.6314870e+002, -2.0592600e+002, -4.6582320e+002,
            -9.7565000e+000, -1.6914000e+001, -4.5293970e+002,
            -4.3061070e+002, -1.6231370e+002, -6.9020400e+001,
            -3.0240820e+002, -5.2789320e+002, -4.8942740e+002,
            -7.4256200e+001, -6.8285000e+000, -5.4636310e+002,
            -4.1518800e+002, -3.2214990e+002, -1.4538220e+002,
            -5.2700190e+002, -4.2217790e+002, -2.5425420e+002,
            -4.9572890e+002, -2.8610230e+002, -1.9616440e+002,
            -1.0493830e+002, -3.8844900e+002, -5.7932200e+001,
            -5.3995800e+001, -1.1438820e+002, -5.0943400e+001
            , -5.0809610e+002, -7.7027800e+001, -2.4296420e+002,
            -1.1586500e+001, -5.0694900e+002, -2.2216620e+002,
            -3.0383250e+002, -3.5940410e+002, -1.5607150e+002,
            -2.5297110e+002, -3.4570620e+002, -1.3306540e+002,
            -3.0187400e+001, -3.0661620e+002, -1.6066730e+002,
            -2.1051130e+002, -1.8625180e+002, -5.5031270e+002,
            -5.1903050e+002, -3.2077600e+002, -4.7110670e+002,
            -1.5917240e+002, -4.4075100e+001, -8.4125100e+001,
            -5.3803890e+002, -4.4419930e+002, -5.9093380e+002,
            -2.1680070e+002, -5.2327310e+002, -3.1720000e+002,
            -3.9366710e+002, -5.4843390e+002, -4.1604030e+002,
            -3.8038260e+002, -1.0954200e+002, -1.6776100e+002,
            -3.4551270e+002, -5.4486310e+002, -3.5782180e+002,
            -5.9821170e+002
    };

    private static double[] ackley_bounds_o = {
            -1.6823000e+001, 1.4976900e+001, 6.1690000e+000,
            9.5566000e+000, 1.9541700e+001, -1.7190000e+001,
            -1.8824800e+001, 8.5110000e-001, -1.5116200e+001,
            1.0793400e+001, 7.4091000e+000, 8.6171000e+000,
            -1.6564100e+001, -6.6800000e+000, 1.4543300e+001,
            7.0454000e+000, -1.8621500e+001, 1.4556100e+001,
            -1.1594200e+001, -1.9153100e+001, -4.7372000e+000,
            9.2590000e-001, 1.3241200e+001, -5.2947000e+000,
            1.8416000e+000, 4.5618000e+000, -1.8890500e+001,
            9.8008000e+000, -1.5426500e+001, 1.2722000e+000,
            -4.5920000e-001, -8.2939000e+000, -5.9257000e+000,
            1.5606500e+001, 2.0942000e+000, -1.3782900e+001,
            -1.3005100e+001, 1.5142400e+001, -1.4621400e+001,
            8.6143000e+000, 1.8052200e+001, 1.7577800e+001,
            -7.6810000e+000, -4.5826000e+000, 2.0896000e+000,
            4.7818000e+000, 9.4115000e+000, 1.0168000e+001,
            -2.8787000e+000, -4.0770000e-001, 3.0359000e+000,
            1.4492800e+001, 8.2224000e+000, 1.8095000e+000,
            3.6144000e+000, -3.4366000e+000, -1.0708600e+001,
            -3.1311000e+000, -9.4393000e+000, -5.0748000e+000,
            1.7545800e+001, 1.7436800e+001
            , 5.4145000e+000, 7.5412000e+000, -1.5661100e+001,
            1.7669800e+001, -1.6519000e+001, 1.1152500e+001,
            -1.2178100e+001, 1.7768500e+001, 1.0664900e+001,
            8.7514000e+000, -1.9787500e+001, 1.3069000e+000,
            9.7793000e+000, -1.1666900e+001, 2.6540000e-001,
            7.9918000e+000, -4.9550000e+000, 3.5881000e+000,
            -7.7524000e+000, -1.6297900e+001, 9.0324000e+000,
            -6.2570000e-001, 8.8854000e+000, -2.7198000e+000,
            9.4307000e+000, 9.0800000e+000, -6.5800000e-001,
            -1.1024400e+001, 1.9848400e+001, -1.3460000e-001,
            1.7819500e+001, 1.1721400e+001, -1.0295300e+001,
            -7.7800000e-001, 1.2843500e+001, -8.9002000e+000,
            1.7685600e+001, 6.1183000e+000};

    private static double[] rastrigin_o = {
            1.9005000e+000, -1.5644000e+000, -9.7880000e-001,
            -2.2536000e+000, 2.4990000e+000, -3.2853000e+000,
            9.7590000e-001, -3.6661000e+000, 9.8500000e-002,
            -3.2465000e+000, 3.8060000e+000, -2.6834000e+000,
            -1.3701000e+000, 4.1821000e+000, 2.4856000e+000,
            -4.2237000e+000, 3.3653000e+000, 2.1532000e+000,
            -3.0929000e+000, 4.3105000e+000, -2.9861000e+000,
            3.4936000e+000, -2.7289000e+000, -4.1266000e+000,
            -2.5900000e+000, 1.3124000e+000, -1.7990000e+000,
            -1.1890000e+000, -1.0530000e-001, -3.1074000e+000,
            -3.9641000e+000, -4.3387000e+000, 3.0705000e+000,
            3.3205000e+000, -3.8178000e+000, -1.4980000e+000,
            -4.3807000e+000, 2.7110000e+000, -3.7956000e+000,
            -2.3627000e+000, 4.0086000e+000, -1.3728000e+000,
            -4.4362000e+000, -2.9183000e+000, -2.2457000e+000,
            3.0650000e-001, -8.9240000e-001, -3.2364000e+000,
            -1.2521000e+000, 2.7198000e+000, -3.9787000e+000,
            3.0678000e+000, -4.2400000e+000, -3.9580000e+000,
            3.9479000e+000, 2.2030000e-001, -2.6124000e+000,
            2.6498000e+000, -2.3256000e+000, -1.5383000e+000,
            3.4760000e+000, 2.4462000e+000, 2.4575000e+000,
            3.7409000e+000, -2.4887000e+000, 3.8555000e+000
            , -1.1426000e+000, 1.3389000e+000, 2.2323000e+000,
            2.3137000e+000, -4.3370000e+000, 3.9260000e+000,
            3.5905000e+000, -1.2858000e+000, -2.0113000e+000,
            2.9087000e+000, 3.9278000e+000, 1.0812000e+000,
            -7.4610000e-001, 3.4740000e+000, 2.3036000e+000,
            -3.3781000e+000, -4.4910000e-001, 9.4020000e-001,
            -3.0583000e+000, -4.2165000e+000, -2.3604000e+000,
            8.3640000e-001, 1.4773000e+000, -2.7292000e+000,
            -1.5904000e+000, 7.6960000e-001, 5.1640000e-001,
            2.6576000e+000, -5.4270000e-001, 1.0358000e+000,
            6.9260000e-001, -4.2775000e+000, -1.5911000e+000,
            -3.5775000e+000
    };

    private static double[] weierstrass_o = {
            -1.3670000e-001, 1.1860000e-001, -9.6800000e-002,
            2.3700000e-002, -2.9330000e-001, -4.7800000e-002,
            3.5180000e-001, 3.5790000e-001, -5.8600000e-002,
            -3.7500000e-002, 2.5300000e-001, 3.1650000e-001,
            3.9740000e-001, 3.8130000e-001, 1.7180000e-001,
            -2.8540000e-001, 8.4900000e-002, -1.8000000e-001,
            -9.4200000e-002, -1.6840000e-001, -1.9390000e-001,
            -1.5400000e-002, 1.9800000e-001, -1.2520000e-001,
            2.2110000e-001, -5.3600000e-002, -2.0850000e-001,
            3.8830000e-001, -2.3050000e-001, 1.9720000e-001,
            1.8020000e-001, -2.7420000e-001, 3.2240000e-001,
            2.1330000e-001, 1.1600000e-001, -3.0530000e-001,
            3.3700000e-001, 2.2320000e-001, -6.1900000e-002,
            7.4300000e-002, -1.3370000e-001, -2.0710000e-001,
            2.3780000e-001, -1.3490000e-001, -9.0000000e-004,
            -2.8580000e-001, 1.8920000e-001, 7.7500000e-002,
            -3.2990000e-001, -3.8640000e-001, 2.9000000e-002,
            1.7970000e-001, -3.5910000e-001, 2.2990000e-001,
            2.2270000e-001, -7.4700000e-002, 2.2580000e-001,
            -2.9100000e-002, -5.5000000e-002, 3.4400000e-001,
            2.1900000e-002, -3.5780000e-001, 3.6950000e-001,
            -3.6000000e-003, 1.4460000e-001, 1.3300000e-002
            , -3.8030000e-001, 4.8300000e-002, -4.0600000e-002,
            1.3140000e-001, -3.7290000e-001, -1.1270000e-001,
            -1.5020000e-001, -1.9110000e-001, -3.0040000e-001,
            -1.8450000e-001, -1.7380000e-001, 7.7100000e-002,
            -3.1860000e-001, 2.7630000e-001, -1.1970000e-001,
            -3.6630000e-001, 1.6490000e-001, 2.0500000e-001,
            4.3100000e-002, 1.3100000e-002, -1.7090000e-001,
            3.4910000e-001, -1.3570000e-001, 1.0720000e-001,
            3.9580000e-001, -2.9180000e-001, 1.0650000e-001,
            -3.6170000e-001, 6.2100000e-002, -1.8300000e-002,
            4.4900000e-002, 3.7480000e-001, -3.5780000e-001,
            -1.2620000e-001
    };

    public CEC2005Problem() {
        setDoRotation(false);
        initProblem();
    }

    public CEC2005Problem(CECBenchSelection bench, int dim) {
        setDoRotation(false);
        setBenchmark(bench);
        setProblemDimension(dim);
        initProblem();
    }

    public CEC2005Problem(CEC2005Problem o) {
        super(o);
        this.benchSel = o.benchSel;
        this.problemDimension = o.getProblemDimension();
        this.m_Template = (AbstractEAIndividual) o.m_Template.clone();
        if (o.A != null) this.A = o.A.clone();
        if (o.B != null) this.B = o.B.clone();
        else this.B = null;
    }

    @Override
    public Object clone() {
        return new CEC2005Problem(this);
    }

    public void hideHideable() {
        super.hideHideable();
        GenericObjectEditor.setHideProperty(this.getClass(), "defaultRange", true);
        GenericObjectEditor.setHideProperty(this.getClass(), "doRotation", true);
    }

    public void setBenchmark(CECBenchSelection sel) {
        benchSel = sel;
        readData();
    }

    public CECBenchSelection getBenchmark() {
        return benchSel;
    }

    public String benchmarkTipText() {
        return "Choose a CEC 2005 benchmark (Suganthan 2005)";
    }

    public double[] eval(double[] x) {
        double[] fitness = new double[1];
//		System.out.println(individual.toString() + " is evaluated...");
        switch (benchSel) {
            case ShiftSphere: // Benchmark. Sphere (Parabola)
                fitness[0] = sphere(problemDimension, x);
                break;
            case ShiftSchwefel: // Benchmark. Schwefel
                fitness[0] = schwefel_1_2(problemDimension, x, false);
                break;
            case ShiftRotEllipse: // Benchmark. Elliptic
                fitness[0] = elliptic(problemDimension, x);
                break;
            case ShiftSchwefelNoise: // Benchmark. Schwefel + noise
                fitness[0] = schwefel_1_2(problemDimension, x, true);
                break;
            case ShiftSchwefelBounds: // Benchmark. Schwefel with optimum on bounds
                fitness[0] = schwefel_bounds(problemDimension, x);
                break;
            case ShiftRosenbrock: // Benchmark. Rosenbrock
                fitness[0] = rosenbrock(problemDimension, x);
                break;
            case ShiftRotGriewankBounds: // Benchmark. Griewank
                fitness[0] = griewank(problemDimension, x);
                break;
            case ShiftRotAckleyBounds: // Benchmark. Ackley with optimum on bounds
                fitness[0] = ackley_bounds(problemDimension, x);
                break;
            case ShiftRastrigin: // Benchmark. Rastrigin
                fitness[0] = rastrigin(problemDimension, x, false);
                break;
            case ShiftRotRastrigin: // Benchmark. Rastrigin, rotated
                fitness[0] = rastrigin(problemDimension, x, true);
                break;
            case ShiftRotWeierstrass: // Benchmark. Weierstrass
                fitness[0] = weierstrass(problemDimension, x);
                break;
            case ShiftSchwefel213: // Schwefel's Problem 2.13
                fitness[0] = schwefel213(problemDimension, x);
        }
//		System.out.println("value is "+fitness[0]);
        return fitness;
    }

    public double getDefaultRange() {
        switch (benchSel) {
            case ShiftSphere: // Benchmark. Sphere (Parabola)
            case ShiftSchwefel: // Benchmark. Schwefel
            case ShiftRotEllipse: // Benchmark. Elliptic
            case ShiftSchwefelNoise: // Benchmark. Schwefel + noise
            case ShiftSchwefelBounds: // Benchmark. Schwefel with optimum on bounds
            case ShiftRosenbrock: // Benchmark. Rosenbrock
                return 100.;
            case ShiftRotGriewankBounds: // Benchmark. Griewank
                return 600.;
            case ShiftRotAckleyBounds: // Benchmark. Ackley with optimum on bounds
                return 32.;
            case ShiftRastrigin: // Benchmark. Rastrigin
            case ShiftRotRastrigin: // Benchmark. Rastrigin, rotated
                return 5.;
            case ShiftRotWeierstrass: // Benchmark. Weierstrass
                return 0.5;
            case ShiftSchwefel213: // Schwefel 213
                return Math.PI;
            default:
                System.err.println("invalid benchmark ID in CEC2005Problem");
                return 10.;
        }
    }

    @Override
    public void initProblem() {
        super.initProblem();
        if (A == null) readData();
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        StringBuffer sb = new StringBuffer(200);
        sb.append("CEC 2005 Special Session on Numerical Optimization Benchmark Set\n");
        sb.append("Code mainly taken from M.Clerc's TRIBES implementation\n");
        sb.append("Current selected benchmark:");
        sb.append(benchSel.toString());
        sb.append("\nDimensions:");
        sb.append(getProblemDimension());
        return sb.toString();
    }

    protected void readData() {
        // For some problems, read extra data
        List<String> lines;

        switch (benchSel) {    // read the raw data
            case ShiftRotEllipse:
                lines = BasicResourceLoader.readLines("resources/CEC2005Benchmarks/elliptic_M.txt", false);
                break;
            case ShiftRotGriewankBounds:
                lines = BasicResourceLoader.readLines("resources/CEC2005Benchmarks/griewank_M.txt", false);
                break;
            case ShiftRotAckleyBounds:
                lines = BasicResourceLoader.readLines("resources/CEC2005Benchmarks/ackley_M.txt", false);
                break;
            case ShiftRotRastrigin:
                lines = BasicResourceLoader.readLines("resources/CEC2005Benchmarks/rastrigin_M.txt", false);
                break;
            case ShiftRotWeierstrass:
                lines = BasicResourceLoader.readLines("resources/CEC2005Benchmarks/weierstrass_M.txt", false);
                break;
            case ShiftSchwefelBounds:
                lines = BasicResourceLoader.readLines("resources/CEC2005Benchmarks/schwefel_bounds_A.txt", false);
                break;
            case ShiftSchwefel213:
                lines = BasicResourceLoader.readLines("resources/CEC2005Benchmarks/schwefel_213_data.txt", false);
                break;
            default:
                lines = null;
                break;
        }

        switch (benchSel) {     // read associated matrix from raw data
            case ShiftRotEllipse:
            case ShiftRotGriewankBounds:
            case ShiftRotAckleyBounds:
            case ShiftRotRastrigin:
            case ShiftRotWeierstrass:    // these cases are similar, splitting up at Dimensions 2, 10, 30 and 50
                if (problemDimension == 2) {
                    A = readMatrix(lines, 0, 2);
                } else if (problemDimension <= 10) {
                    // skip 2 lines
                    A = readMatrix(lines, 2, 10);
                } else if (problemDimension <= 30) {
                    // skip 12 lines
                    A = readMatrix(lines, 12, 30);
                } else if (problemDimension <= 50) {
                    // skip 42 lines
                    A = readMatrix(lines, 42, 50);
                } else {
                    System.err.println("Warning, this benchmark is defined for 50 dimensions at maximum");
                }
                break;
            case ShiftSchwefelBounds: // always 100 dim.
                A = readMatrix(lines, 0, 100);
                break;
            case ShiftSchwefel213:
                A = readMatrix(lines, 0, 100);
                B = readMatrix(lines, 100, 100);
                alpha = readMatrix(lines, 200, 10); // this is actually an array of length 100, but when reading the same as a matrix 10*10
                break;
        }
    }

    /**
     * This reads a matrix (as a vector) from a list of String lines, skipping whitespaces and,
     * if skipLines > 0, skipping as many lines at the top of the list. The matrix is returned if
     * enough values are read or the lines are read completely.
     */
    protected double[] readMatrix(List<String> lines, int skipLines, int dim) {
        if (lines == null) {
            System.err.println("Error in CEC2005Problem: lines are null! Did you copy the resources folder to the right place?");
            return null;
        }
        int cnt = 0;
        String[] vals;
        int offset = 0;
        double[] A = new double[dim * dim];
        for (String str : lines) {
            String l = str.trim();
            if (l.length() > 0) {
                if (cnt >= skipLines) {
                    vals = l.split(" +");
                    for (int j = 0; j < vals.length; j++) {
                        if ((offset + j) < A.length) A[offset + j] = Double.valueOf(vals[j]);
                    }
                    offset += vals.length;
                }
                if (offset >= A.length) return A;
                cnt++;
            }
        }
        return A;
    }

    /*
     Benchmark defined first for CEC 2005 (cf. Ponnuthurai Nagaratnam Suganthan EPNSugan@ntu.edu.sg)
      */

    public static double sphere(int D, double[] x) {
        int d;
        double f = 0;
        for (d = 0; d < D; d++) {
            f = f + Math.pow(x[d] - sphere_o[d], 2);
        }
        return f;
    }

    public static double schwefel_1_2(int D, double[] x, boolean withNoise) {
        int i;
        int j;
        double f = 0;
        double z;

        for (i = 0; i < D; i++) {
            z = 0;
            for (j = 0; j < i; j++) {
                z = z + x[j] - schwefel_1_2_o[j];

            }
            f = f + z * z;
        }
        if (withNoise) {
            double r = RNG.gaussianDouble(1);
            return f * (1 + 0.4 * Math.abs(r));
        } else return f;
    }

    public double elliptic(int D, double[] x) {
        int d;
        double[] z = new double[100];
        double[] zRot = new double[100];
        double ten6 = 1000000;

//		double[] m10 = {
//		1.7830682721057345e-001, 5.5786330587166588e-002,
//		4.7591905576669730e-001, 2.4551129863391566e-001,
//		3.1998625926387086e-001, 3.2102001448363848e-001,
//		2.7787561319902176e-002, 2.6664001046775621e-001,
//		4.1568009651337917e-001, -4.7771934552669726e-001
//		, 6.3516362859468667e-001, 5.0091423836646241e-002,
//		2.0110601384121973e-001, -6.8076882416633511e-001,
//		-4.9934546553907944e-002, -4.6399423424582961e-002,
//		-1.9460194646748039e-001, 1.8961539926194687e-001,
//		-1.9416259626804547e-002, 1.0639981029473855e-001
//		, 3.2762147366023187e-001, 3.6016598714114556e-001,
//		-2.3635655094044949e-001, -1.8566854017444848e-002,
//		-2.4479096747593634e-001, 4.4818973341886903e-001,
//		5.3518635733619568e-001, -3.1206925190530521e-001,
//		-1.3863719921728737e-001, -2.0713981146209595e-001
//		, -6.4783210587984280e-002, -4.9424101683695937e-001,
//		1.3101175297435969e-001, 3.1615171931194543e-002,
//		-1.7506107914871860e-001, 6.8908039344918381e-001,
//		1.0544234469094992e-002, 2.1948984793273507e-001,
//		-1.6468539805844565e-001, 3.9048550518513409e-001
//		, -2.7648044785371367e-001, 1.1383114506120220e-001,
//		-3.0818401502810994e-001, -3.5959407104438740e-001,
//		2.6446258034702191e-001, 2.8616788379157501e-002,
//		4.7528027904995646e-001, 4.0993994049770172e-001,
//		4.1131043368915432e-001, 2.2899345188886880e-001
//		, 1.5454249061641606e-001, 5.4899186274157996e-001,
//		-1.8382029941792261e-001, 3.3944461903909162e-001,
//		2.8596188774255699e-001, 1.2833167642713417e-001,
//		-2.5495080172376317e-001, 3.9460752302037100e-001,
//		-3.4524640270007412e-001, 2.9590318323368509e-001
//		, -5.1907977690014512e-002, -1.4450757809700329e-001,
//		-4.6086919626114314e-001, -5.3687964818368079e-002,
//		-3.6317793499109247e-001, 2.7439997038558633e-002,
//		-2.1422629652542946e-001, 5.0545148893084779e-001,
//		-9.8064717019089837e-002, -5.6346991018564507e-001
//		, 5.0142989354460654e-001, -5.3133659048457516e-001,
//		-3.7294385871521135e-001, 2.3370866431381510e-001,
//		4.4327537662488531e-001, -1.6972740381143742e-001,
//		2.0364148963331691e-001, -2.3717523924336927e-002,
//		-7.1805455862954920e-002, -7.3332178450339763e-003
//		, 1.0441248047680891e-001, 4.3064226149369542e-002,
//		-4.1675972625940993e-001, 1.6522876074361707e-002,
//		1.7437281849141879e-003, 2.9594944879030760e-001,
//		-5.1197487739368741e-001, -3.2679819762357892e-001,
//		5.8253106590933512e-001, 1.3204141339826148e-001
//		, -2.9645907657631693e-001, -3.1303011496605505e-002,
//		-7.8009154082116602e-002, -4.1548534874482024e-001,
//		5.6959403572443468e-001, 2.9095198400348149e-001,
//		-1.8560717510075503e-001, -2.4653488847859115e-001,
//		-3.7149025085479792e-001, -3.0015617693118707e-001
//		};
        double f = 0;

        // Translation
        for (d = 0; d < D; d++) {
            z[d] = x[d] - elliptic_o[d];
        }

        // Rotation
        zRot = rotation(z, A, D);

        for (d = 0; d < D; d++) {
            f = f +
                    Math.pow(ten6, (double) d / (double) D) * (zRot[d] * zRot[d]);
        }
        return f;
    }

    public double schwefel213(int D, double[] x) {
        double sum = 0;
        double tmp;
        for (int i = 0; i < D; i++) {
            tmp = cachedS213A(D, i) - s213B(D, i, x);
            sum += (tmp * tmp);
        }
        return sum;
    }

    protected double cachedS213A(int D, int i) {
        if (tmpAVals == null || (D != tmpAVals.length)) {
            tmpAVals = new double[D];
            for (int k = 0; k < D; k++) {
                tmpAVals[k] = s213A(D, k);
            }
        }
        return tmpAVals[i];
    }

    protected double s213A(int D, int i) {
        double sum = 0;
        for (int j = 0; j < D; j++) {
            sum += (getInMatrix(A, 100, i, j) * Math.sin(alpha[j])) + (getInMatrix(B, 100, i, j) * Math.cos(alpha[j]));
        }
//		System.out.println("D i sum: " + D + " " + i + " " + sum);
        return sum;
    }

    protected double s213B(int D, int i, double[] x) {
        double sum = 0;
        for (int j = 0; j < D; j++) {
            sum += (A[i * 100 + j] * Math.sin(x[j])) + (B[i * 100 + j] * Math.cos(x[j]));
//			sum += (getInMatrix(A, 100, i, j) * Math.sin(x[j])) + (getInMatrix(B, 100, i, j)*Math.cos(x[j]));
        }
        return sum;
    }

    /**
     * Return a matrix value from linear array m at coordinates i,j and
     * with the line length maxD.
     *
     * @param m
     * @param maxD
     * @param i
     * @param j
     * @return
     */
    protected double getInMatrix(double[] m, int maxD, int i, int j) {
        return m[i * maxD + j];
    }

    public double schwefel_bounds(int D, double[] x) {
        int d, d1;
        int k;
//		double f = -310;
        double max;
        double zz;
        double[] z = new double[100];

//		double[] A = new double[10000]; // Too big to be put here. Has been read in readPb
//		A = Tribes.A;
        // On bounds
        for (d = 0; d < D / 4; d++) {
            schwefel_bounds_o[d] = -100;
        }
        for (d = 3 * D / 4 - 1; d < D; d++) {
            schwefel_bounds_o[d] = 100;
        }

        // Translation
        for (d = 0; d < D; d++) {
            z[d] = x[d] - schwefel_bounds_o[d];
        }

        //Evaluation
        max = 0;
        for (d = 0; d < D; d++) {
            zz = 0;
            for (d1 = 0; d1 < D; d1++) {
                k = 100 * d + d1;
                zz = zz + A[k] * z[d1];
            }
            if (zz > max) {
                max = zz;
            }
        }
        return max;
//		f = f + max;
//		return f;
    }

    public static double rosenbrock(int D, double[] x) {
        int d;
        double f = 0;
        double f1, f2;
        double zz, zz1;

        f1 = 0;
        f2 = 0;
        for (d = 0; d < D - 1; d++) {
            zz = x[d] - rosenbrock_o[d] + 1;
            zz1 = x[d + 1] - rosenbrock_o[d + 1] + 1;
            f1 = f1 + Math.pow(zz * zz - zz1, 2);
            f2 = f2 + Math.pow(zz - 1, 2);
        }
        f = f + 100 * f1 + f2;
        return f;
    }

    public double griewank(int D, double[] x) {
        int d;
        double[] z = new double[100];
        double[] zRot = new double[100];
        double f = 0;
        double f1 = 0;
        double f2 = 1;

//		double[] m10 = {
//		-7.3696625825313500e-002, 1.5747490444892893e+000,
//		-6.4377942207169941e-002, 6.3201848730939580e-001,
//		-1.2455211411481415e+000, -3.5341187428098381e-001,
//		3.5031691018519090e-001, 6.2886479758992697e-001,
//		6.8593632355012335e-001, 1.3975663076173925e+000
//		, 6.3700016123079051e-001, -1.3833836770823484e+000,
//		-2.4437874951092337e-001, 1.6992995943357547e+000,
//		7.1757447137502850e-001, -7.7753800570270454e-002,
//		4.9291080765053624e-001, 1.1392847178100191e-001,
//		4.8163647386641817e-001, 2.8150613437207017e-001
//		, -1.4466181982194921e+000, -1.1273816086105013e+000,
//		-1.0665724848959319e+000, 2.1900088934332190e-001,
//		-5.8130776006865136e-002, -9.9187841926086026e-002,
//		-1.2465831572524580e-001, -5.0547372808368829e-001,
//		-2.1020191419640880e-001, 1.1509984987284301e+000
//		, 1.0410802679063424e+000, 4.7577677793232626e-001,
//		9.6430154567967874e-001, 1.5636976117984064e-002,
//		2.0539698111678034e-001, 2.5839780039821658e-001,
//		-5.1710361801897031e-001, -1.5449014589834349e+000,
//		-1.4560361158442292e+000, 9.9877904060730438e-001
//		, 2.6260272944635960e-001, 9.2947540741436874e-001,
//		-1.2953100028930926e+000, 6.6512029642561388e-001,
//		-2.7957781701655993e-001, 8.4060537698112758e-001,
//		-5.2922829607729160e-001, -8.6040220072910467e-001,
//		4.9503162769183251e-001, -6.3765376892958103e-001
//		, 8.1307889477954698e-002, 8.0062327426592494e-001,
//		7.2294618679188488e-002, 4.4874698427975906e-001,
//		1.7959858022699743e-001, -1.3634800693969209e+000,
//		7.5257943996576704e-002, -1.2486791053473751e+000,
//		6.8143526407032673e-001, 1.3558980136836016e-001
//		, 8.7913516653862697e-002, 2.1022739728349416e-001,
//		-1.5708234535904123e-001, -3.5182196550454031e-001,
//		-6.4190160213761838e-002, 1.5082748057903228e+000,
//		1.1168462803089814e+000, -3.6773042225135699e-001,
//		2.6828021681357744e-001, 4.9698836189165707e-001
//		, -9.5406235378612747e-001, 3.9879009060640763e-001,
//		5.8022630243503770e-001, 2.4831174649263604e-001,
//		1.1781385394000925e+000, 5.3134809745284084e-001,
//		7.8257240450327026e-001, -3.8166809840963106e-001,
//		-4.8082474351369503e-001, -6.2076533636514075e-001
//		, 2.7628599479864874e-001, 3.6188284466692094e-001,
//		-1.0302756351623272e+000, 7.2348644867809120e-001,
//		-3.7379075361566066e-001, -7.9223639600376997e-002,
//		1.6221551897070494e+000, -8.2880436781697358e-003,
//		-1.0881497169330046e+000, -1.9204701133595675e-001
//		, -3.0035568486304853e-001, -5.0758053487595001e-001,
//		3.1143454840627821e-001, -2.5444900307396151e-001,
//		-7.7988528102301924e-001, -6.8262839999436264e-001,
//		5.5932665521935510e-001, -7.9579050121422423e-001,
//		4.7071685181799255e-001, -8.0019268494895490e-001

//		};
//		Translation
        for (d = 0; d < D; d++) {
            z[d] = x[d] - griewank_o[d];
        }

//		Rotation
        zRot = rotation(z, A, D);

//		Evaluation
        for (d = 0; d < D; d++) {
            f1 = f1 + zRot[d] * zRot[d];
            f2 = f2 * Math.cos((zRot[d]) / Math.sqrt(d + 1));
        }

        f = f + f1 / 4000 - f2 + 1;

        return f;
    }

    public double ackley_bounds(int D, double[] x) {
        int d;
        double[] z = new double[100];
        double[] zRot = new double[100];
        double f = 0;
        double f1 = 0;
        double f2 = 0;

        // On bounds
        for (d = 0; d < D / 2; d++) {
            ackley_bounds_o[2 * d] = -32;
        }

        // Translation
        for (d = 0; d < D; d++) {
            z[d] = x[d] - ackley_bounds_o[d];
        }

        // Rotation
        zRot = rotation(z, A, D);

        // Evaluation
        for (d = 0; d < D; d++) {
            f1 = f1 + zRot[d] * zRot[d];
            f2 = f2 + Math.cos(2 * Math.PI * zRot[d]);
        }

        f = f - 20 * Math.exp(-0.2 * Math.sqrt(f1 / D)) - Math.exp(f2 / D) +
                20 + Math.E;
//		System.out.print("\n"+f1+" "+f2);
        return f;

    }

    public double rastrigin(int D, double[] x, boolean rotate) {
        int d;
        double[] z = new double[100];
        double[] zRot = new double[100];
        double f = 0;

        // Translation
        for (d = 0; d < D; d++) {
            z[d] = x[d] - rastrigin_o[d];
//			z[d] = 0;
        }

//		Rotation
        if (rotate) {
            zRot = rotation(z, A, D);
        } else {
            zRot = z;
        }

        for (d = 0; d < D; d++) {
            f = f + zRot[d] * zRot[d] -
                    10 * Math.cos(2 * Math.PI * zRot[d]);
        }
        f = f + 10 * D;

        return f;
    }

    public double weierstrass(int D, double[] x) {
        double a = 0.5;
        double ak;
        double b = 3;
        double bk;
        double f1, f2, zz;
        int d, k;
        int kmax = 20;

        double f = 0;
        double[] z = new double[100];
        double[] zRot = new double[100];

//		double[] m10 = {
//		-1.1110697888144878e+000, 3.6905789544385453e-001,
//		7.2920552921448034e-001, -4.2847443920418715e-001,
//		-3.2558066659661755e-001, -1.4465654526284950e+000,
//		-2.0224085659255202e-002, 1.5503062159856127e+000,
//		9.2230876812127960e-001, 1.1404581440230421e+000
//		, 1.5092777909404804e+000, -4.5992984681349941e-001,
//		-2.4554260512203940e-001, -5.5512646137750776e-001,
//		-1.8651997436174714e+000, -1.2195709189112197e-001,
//		-1.9679217757126555e+000, -1.4248750959518428e+000,
//		-8.1556345638779426e-001, 2.2223404831489502e-001
//		, -5.6402793610088053e-001, 5.6047918457013290e-001,
//		4.0273076601746371e-001, 1.0593056356204295e-001,
//		-1.8718870129434997e+000, -9.9366686709457752e-001,
//		1.7677849646336035e+000, 1.6106625259748026e+000,
//		-1.0123369759917022e+000, 6.0776659137740263e-001
//		, -1.0981806306146530e-001, 3.0970829860712834e-001,
//		-1.3245588099086854e+000, 9.8593994643373228e-001,
//		1.3559792736454929e-001, 2.8150671230047226e-002,
//		1.0015964191391451e+000, 8.5799006528080646e-002,
//		1.4468896133512495e-002, -1.2914400024570518e+000
//		, 8.7702802373865441e-001, -5.9955107852294909e-001,
//		3.4166677827547016e-002, 4.5444313333172359e-001,
//		6.1411380724843645e-001, 8.7135068847338182e-002,
//		-3.7193688015275456e-001, 3.0270728793727675e-001,
//		-3.9512527976833356e-002, -4.4801215556282553e-001
//		, 6.8882213745587073e-001, 1.1470043442495683e+000,
//		8.6320796730831229e-001, -4.7180326822629304e-001,
//		1.7206295945742422e+000, -9.1927310704643750e-001,
//		-1.2344873056018493e+000, 5.8678748622409993e-001,
//		7.1127821329041185e-001, 1.0565811136727492e+000
//		, 3.3995500494567416e-001, 4.4195266426309288e-001,
//		6.8246720879448897e-001, 1.6711889920958131e-001,
//		-2.3623524924091583e+000, 8.3913775481543093e-002,
//		9.5564658343937625e-002, 1.2213386723430246e-002,
//		-2.3738972474670303e-001, -2.7534697940495018e-001
//		, 1.1900798218550579e+000, -6.6705455203122299e-001,
//		8.7111754531159979e-001, -7.9856189102027175e-001,
//		6.9442401709101831e-001, -7.0357318374317968e-001,
//		-8.4608409349087366e-001, -1.0734520052438552e+000,
//		-6.8887108617896109e-001, -7.2354227462011367e-001
//		, -1.5363247693593787e-001, -4.6190789475651813e-002,
//		1.3335277878980332e+000, -1.0031163853521424e-001,
//		1.2209115415033431e+000, -1.2275314484176902e+000,
//		5.7371210992301205e-002, -1.1377154788313812e-001,
//		7.2692376058593899e-001, 1.9524121519232884e+000
//		, 1.3575578115989519e+000, -4.3041284719371020e-002,
//		7.1514778147551927e-001, -1.4644509947508315e+000,
//		-2.0237620747384981e+000, -8.3821541669785216e-001,
//		-1.4272030990811013e-001, -1.5849819639360752e-001,
//		1.5425476649857556e-001, 1.2515451419349455e+000

//		};

        // Translation
        for (d = 0; d < D; d++) {
            z[d] = x[d] - weierstrass_o[d];
        }

//		Rotation
        zRot = rotation(z, A, D);

//		Evaluation

        for (d = 0; d < D; d++) {
            zz = zRot[d] + 0.5;
            f1 = 0;
            for (k = 0; k <= kmax; k++) {
                ak = Math.pow(a, k);
                bk = 2 * Math.PI * Math.pow(b, k);
                f1 = f1 + ak * Math.cos(bk * zz);
            }
            f = f + f1;
        }

        f2 = 0;
        for (k = 0; k <= kmax; k++) {
            ak = Math.pow(a, k);
            bk = 2 * Math.PI * Math.pow(b, k);
            f2 = f2 + ak * Math.cos(bk * 0.5);
        }

        f = f - D * f2;

        return f;
    }

    private static double[] rotation(double[] z, double[] m, int dim) {
        double[] zRot = new double[dim];
        int i, j, k;

        for (i = 0; i < dim; i++) {
            zRot[i] = 0;
            for (j = 0; j < dim; j++) {
                // MK: this original version did a matrix transposition which is incorrect
                // (checked with the matlab code from Mr Suganthan)
//				k = i * dim + j;
                k = i + dim * j;
                zRot[i] = zRot[i] + m[k] * z[j];
            }
        }

        return zRot;
    }

    /**
     * This method allows the CommonJavaObjectEditorPanel to read the
     * name to the current object.
     *
     * @return The name.
     */
    public String getName() {
        return "CEC05-" + benchSel;
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Benchmarks of the CEC 2005, cf. Suganthan et al. 2005";
    }

    public int getProblemDimension() {
        return problemDimension;
    }

    public void setProblemDimension(int problemDimension) {
        if (this.problemDimension != problemDimension) {
            this.problemDimension = problemDimension;
            readData();
        }
    }

    public String problemDimensionTipText() {
        return "Dimension of the selected benchmark function, a maximum of 50 or 100, depending on the benchmark";
    }

    public String toString() {
        return "CECBenchmark-" + benchSel + "/" + getProblemDimension() + "D";
    }

    public SolutionHistogram getHistogram() {
        switch (getBenchmark()) {
            case ShiftRastrigin:
            case ShiftRotRastrigin:
                if (getProblemDimension() < 15) return new SolutionHistogram(-0.5, 15.5, 16);
                else if (getProblemDimension() < 25) return new SolutionHistogram(-0.5, 39.5, 16);
                else return new SolutionHistogram(0, 80, 16);
            case ShiftSchwefel213:
                if (getProblemDimension() < 15) return new SolutionHistogram(0, 80, 16);
                else return new SolutionHistogram(0, 320, 16);
            case ShiftRotGriewankBounds:
                if (getProblemDimension() < 15) return new SolutionHistogram(0, 1, 16);
                else return new SolutionHistogram(0, 4, 16);
            default:
                EVAERROR.errorMsgOnce("Error, getHistogramm not implemented for CEC2005Problem benchmark " + getBenchmark());
        }
        return new SolutionHistogram(0, 100, 10);
    }

    public static void main(String[] args) {
        CEC2005Problem p = new CEC2005Problem(CECBenchSelection.ShiftSchwefel213, 2);
        TopoPlot tp = new TopoPlot("Weier", "", "");
        tp.setParams(180, 180);
        tp.setTopology(p);

    }
}




