/**
 *
 */
package eva2.server.go.problems;

import java.util.Random;

import eva2.server.go.populations.Population;
import eva2.server.go.problems.F1Problem;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.math.RNG;


/**
 * A dynamically "jumping" F1 problem. The severity gives the length of one jump in problem space, occurring
 * with the given frequency.
 *
 * @author marcekro
 *         <p/>
 *         Jan 9, 2007
 */
public class DynJumpF1Problem extends DynTransF1Problem {
    /**
     * Array of current translation.
     */
    protected double transl[];
    private double tmp[];    // necessary really actually at most once per jvm, still dont make static cause its evil
    private long rndSeed = 1;


    /**
     * A constructor.
     */
    public DynJumpF1Problem() {
        transl = new double[f1.getProblemDimension()];
        tmp = new double[f1.getProblemDimension()];
        RNG.setRandomSeed(rndSeed);

        initialize(0., .1, .1);
        initProblem();
    }

    /**
     * A copy constructor. Be aware that the Random instance might not have the same state as
     * the one of the given instance.
     *
     * @param other the instance to clone
     */
    public DynJumpF1Problem(DynJumpF1Problem other) {
        f1 = (F1Problem) other.getF1Instance().clone();

        transl = new double[f1.getProblemDimension()];
        tmp = new double[f1.getProblemDimension()];

        System.arraycopy(other.transl, 0, transl, 0, f1.getProblemDimension());
        System.arraycopy(other.tmp, 0, tmp, 0, f1.getProblemDimension());

        RNG.setRandomSeed(rndSeed);

        initialize(other.startTime, other.severity, other.frequency);
        setFrequencyRelative(other.isFrequencyRelative());
        initProblem();
    }

    /**
     * Override population evaluation to do some data output.
     */
    public void evaluatePopulationEnd(Population population) {
        double delta = transLength(getCurrentProblemTime());
        if (myplot != null) myplot.setConnectedPoint(population.getFunctionCalls(), delta, 0);
        //myplot.setUnconnectedPoint(population.getFunctionCalls(), population.getPopulationMeasures()[2], 2);
    }

    private double transLength(double time) {
        double ret = 0.;
        for (int i = 0; i < f1.getProblemDimension(); i++) ret += Math.pow(getTranslation(i, time), 2.);
        return Math.sqrt(ret);
    }

    /* (non-Javadoc)
      * @see eva2.server.go.OptimizationProblems.DynTransF1Problem#getTranslation(int, double)
      */
    @Override
    protected double getTranslation(int dim, double time) {
        //if (dim==0) System.out.println("translation at (" + dim + "/" + time + " is " + ampl[dim] * Math.sin(time * freq[dim]));
        return transl[dim];
    }

    /* (non-Javadoc)
      * @see eva2.server.go.OptimizationProblems.AbstractDynamicOptimizationProblem#initProblemAt(double)
      */
    @Override
    public void initProblem() {
        super.initProblem();

        for (int i = 0; i < f1.getProblemDimension(); i++) transl[i] = 0.;

        if (TRACE) System.out.println("rand seeded at " + rndSeed);
        //setSeverity(0.1);
    }

    protected void changeProblemAt(double problemTime) {
        super.changeProblemAt(problemTime);
        jumpTranslation();
    }

    /**
     * Calculates a random vector with a length corresponding to the severity and performs the jump within
     * problem space by resetting the translation.
     */
    protected void jumpTranslation() {
        double normfact = 0.;

        // create a uniform random point on the surface of a hypersphere, method by Muller 1959, Marsaglia 1972
        for (int i = 0; i < f1.getProblemDimension(); i++) {
            tmp[i] = RNG.gaussianDouble(1.);
            normfact += (tmp[i] * tmp[i]);
        }
        normfact = Math.sqrt(normfact); // for normalization

        // normalize, scale with severity and add to translation vector at the same time
        for (int i = 0; i < f1.getProblemDimension(); i++) {
            transl[i] += tmp[i] / normfact * getSeverity();
        }
        if (TRACE) {
            System.out.print("Jumped to ");
            for (int i = 0; i < f1.getProblemDimension(); i++) System.out.print(" " + transl[i]);
            System.out.println();
        }
    }

    /**
     * @return the rndSeed
     */
    public long getRndSeed() {
        return rndSeed;
    }

    /**
     * @param rndSeed the rndSeed to set
     */
    public void setRndSeed(long rndSeed) {
        this.rndSeed = rndSeed;
        RNG.setRandomSeed(rndSeed);
        if (TRACE) System.out.println("rand seeded to " + rndSeed);
    }

    public String rndSeedTipText() {
        return "the seed for the random number generator";
    }

    public String getName() {
        return "Dynamic Jump F1 Problem";
    }

    @Override
    public Object clone() {
        return new DynJumpF1Problem(this);
    }

    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        return "The randomly jumping F1 problem";
    }
}
