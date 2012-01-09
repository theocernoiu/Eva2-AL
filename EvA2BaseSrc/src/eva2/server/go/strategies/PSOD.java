package eva2.server.go.strategies;

import eva2.gui.GenericObjectEditor;
import eva2.server.go.PopulationInterface;
import eva2.server.go.operators.distancemetric.EuclideanMetric;
import eva2.server.go.operators.distancemetric.PhenotypeMetric;
import eva2.server.go.populations.InterfaceSolutionSet;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractOptimizationProblem;
import eva2.server.go.problems.InterfaceOptimizationProblem;
import eva2.tools.SelectedTag;
import eva2.tools.math.RNG;

/**
 * Created by IntelliJ IDEA.
 * User: andrei.lihu
 * Date: Jan 24, 2011
 * Time: 10:42:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class PSOD extends ParticleSwarmOptimization {
    private static final long serialVersionUID = -149996122795669590L;

    protected SelectedTag algoTypePsod;
    protected SelectedTag algoTypeSimplified;

    protected final String strAlgoTypeSixSigma = "SixSigma";
    protected final String strAlgoTypeStagnationRiot = "RiotOnStagnation";

    private SelectedTag stagnationMeasure;
    private SelectedTag convergenceCondition;
    protected double convThresh = 0.005;
    protected int m_stagTime = 5;
    protected int popFitCalls = 1000;
    protected int popGens = 1000;
    protected boolean firstTime = true;
    protected double[] oldFit;
    protected double oldNorm;
    public boolean riotReady = false;
    PhenotypeMetric pMetric = null;

    protected final String strNo = "No";
    protected final String strYes = "Yes";
    public double execCursor;

    protected double sigmaFilter = 0.0;

    protected double lowLimit = 1.5;
    protected double upLimit = 2.5;

    public int extreme_disagreements = 0;
    public int partial_disagreements = 0;


    public PSOD() {
        super();

        algoTypePsod = new SelectedTag(strAlgoTypeSixSigma, strAlgoTypeStagnationRiot);
        algoTypePsod.setSelectedTag(strAlgoTypeSixSigma);

        algoTypeSimplified = new SelectedTag(strNo, strYes);
        algoTypeSimplified.setSelectedTag(0);

        stagnationMeasure = new SelectedTag("Fitness calls", "Generations");
        this.pMetric = new PhenotypeMetric();
        this.stagnationMeasure.setSelectedTag(1);

        convergenceCondition = new SelectedTag("Relative", "Absolute");
        this.convergenceCondition.setSelectedTag(0);

        sigmaFilter = 0.7;
        lowLimit = 1;
        upLimit = 2;
    }

    public PSOD(PSOD a) {
        super(a);

        if (a.algoTypePsod != null)
            this.algoTypePsod = (SelectedTag) a.algoTypePsod.clone();

        if (a.algoTypeSimplified != null)
            this.algoTypeSimplified = (SelectedTag) a.algoTypeSimplified.clone();

        this.convThresh = a.convThresh;
        this.m_stagTime = a.m_stagTime;

        if (a.pMetric != null)
            pMetric = a.pMetric;
        else
            pMetric = new PhenotypeMetric();

        if (a.stagnationMeasure != null)
            stagnationMeasure = a.stagnationMeasure;
        if (a.convergenceCondition != null)
            convergenceCondition = a.stagnationMeasure;
        sigmaFilter = a.sigmaFilter;
        lowLimit = a.lowLimit;
        upLimit = a.upLimit;
    }

    public void init() {
        super.init();
        firstTime = true;
    }

    public void doHiding() {
        GenericObjectEditor.setShowProperty(ParticleSwarmOptimization.class, "phi1", algoTypeSimplified.isSelectedString(strNo));
        GenericObjectEditor.setShowProperty(getClass(), "convergenceCondition", algoTypePsod.isSelectedString(strAlgoTypeStagnationRiot));
        GenericObjectEditor.setShowProperty(getClass(), "stagnationMeasure", algoTypePsod.isSelectedString(strAlgoTypeStagnationRiot));
        GenericObjectEditor.setShowProperty(getClass(), "stagnationTime", algoTypePsod.isSelectedString(strAlgoTypeStagnationRiot));
        GenericObjectEditor.setShowProperty(getClass(), "convergenceThreshold", algoTypePsod.isSelectedString(strAlgoTypeStagnationRiot));
        GenericObjectEditor.setShowProperty(getClass(), "sigmaFilter", algoTypePsod.isSelectedString(strAlgoTypeSixSigma));
    }

    public Object clone() {
        return (Object) new PSOD(this);
    }

    public String globalInfo() {
        doHiding();
        return "Particle Swarm Optimization with Disagreements.";
    }

    /**
     * This method will return a naming String
     *
     * @return The name of the algorithm
     */
    public String getName() {
        String title = "PSOD-";

        String cognitivePart = "";
        if (algoTypeSimplified.isSelectedString(strNo)) {
            cognitivePart = getPhi1() + "_";
        } else {
            title += "VG-";
        }

        String typePart = "";
        if (algoTypePsod.isSelectedString(strAlgoTypeSixSigma)) {
            typePart = "6o_";
        }

        if (algoTypePsod.isSelectedString(strAlgoTypeStagnationRiot)) {
            typePart = "RS.";
            if (stagnationMeasure.isSelectedString("Generations"))
                typePart += "Gn.";
            else
                typePart += "Ft.";
            if (convergenceCondition.isSelectedString("Relative"))
                typePart += "Rel.";
            else
                typePart += "Abs.";

            typePart += getStagnationTime() + "_";
            typePart += getConvergenceThreshold() + "_";
        }

        return title + typePart + getTopology() + getTopologyRange() + "_" + cognitivePart + getPhi2();
    }

    /**
     * This method will return a string describing all properties of the optimizer
     * and the applied methods.
     *
     * @return A descriptive string
     */
    public String getStringRepresentation() {
        StringBuilder strB = new StringBuilder(200);
        strB.append("Particle Swarm Optimization with Disagreements:\nOptimization Problem: ");
        strB.append(this.m_Problem.getStringRepresentationForProblem(this));
        strB.append("\n");
        strB.append(this.m_Population.getStringRepresentation());
        return strB.toString();
    }

    public void SetProblem(InterfaceOptimizationProblem problem) {
        super.SetProblem(problem);
        if (problem instanceof AbstractOptimizationProblem) {
            ((AbstractOptimizationProblem) problem).informAboutOptimizer(this);
        }
    }

    public double getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(double lowLimit) {
        this.lowLimit = lowLimit;
    }

    public double getUpLimit() {
        return upLimit;
    }

    public void setUpLimit(double upLimit) {
        this.upLimit = upLimit;
    }

    public double getSigmaFilter() {
        return sigmaFilter;
    }

    public void setSigmaFilter(double sigmaFilter) {
        this.sigmaFilter = sigmaFilter;
    }

    public String sigmaFilterTipText() {
        return "Sigma filtering.";
    }

    public String upLimitTipText() {
        return "Upper limit for disagreements.";
    }

    public String lowLimitTipText() {
        return "Low limit for disagreements.";
    }

    public SelectedTag getAlgoTypePsod() {
        return algoTypePsod;
    }

    public void setAlgoTypePsod(SelectedTag algoTypePsod) {
        this.algoTypePsod = algoTypePsod;
        doHiding();
    }

    public String algoTypePsodTipText() {
        return "PSOD type";
    }

    public SelectedTag getAlgoTypeSimplified() {
        return algoTypeSimplified;
    }

    public void setAlgoTypeSimplified(SelectedTag algoTypeSimplified) {
        this.algoTypeSimplified = algoTypeSimplified;
        if (algoTypeSimplified.isSelectedString(strYes)) {
            algType.setSelectedTag(0); // inertness
            m_Phi1 = 0.0;
            m_Phi2 = 1.49445;
        } else {
            algType.setSelectedTag(1); // constriction
            m_Phi1 = 2.05;
            setPhi2(2.05);
        }
        GenericObjectEditor.setShowProperty(ParticleSwarmOptimization.class, "phi1", algoTypeSimplified.isSelectedString(strNo));
        GenericObjectEditor.setShowProperty(ParticleSwarmOptimization.class, "algoType", algoTypeSimplified.isSelectedString(strNo));
    }

    public String algoTypeSimplifiedTipText() {
        return "Specifies whether the personal component is not taken into consideration (Pedersen 2010)";
    }

    /**
     *
     */
    public void setConvergenceThreshold(double x) {
        convThresh = x;
    }

    /**
     *
     */
    public double getConvergenceThreshold() {
        return convThresh;
    }

    public String convergenceThresholdTipText() {
        return "Disagree if the fitness has not improved by this percentage / absolute value for a whole stagnation time period";
    }

    /**
     *
     */
    public void setStagnationTime(int k) {
        m_stagTime = k;
    }

    /**
     *
     */
    public int getStagnationTime() {
        return m_stagTime;
    }

    public String stagnationTimeTipText() {
        return "Disagree if the population has not improved for this time";
    }

    /**
     * @return the stagnationTimeIn
     */
    public SelectedTag getStagnationMeasure() {
        return stagnationMeasure;
    }

    /**
     * @param stagnationTimeIn the stagnationTimeIn to set
     */
    public void setStagnationMeasure(SelectedTag stagnationTimeIn) {
        this.stagnationMeasure = stagnationTimeIn;
    }

    public String stagnationMeasureTipText() {
        return "Stagnation time is measured in fitness calls or generations, to be selected here.";
    }

    /**
     * @return the convergenceCondition
     */
    public SelectedTag getConvergenceCondition() {
        return convergenceCondition;
    }

    /**
     * @param convergenceCondition the convergenceCondition to set
     */
    public void setConvergenceCondition(SelectedTag convergenceCondition) {
        this.convergenceCondition = convergenceCondition;
    }

    public String convergenceConditionTipText() {
        return "Select between absolute and relative convergence condition";
    }

    public void saveState(PopulationInterface Pop) {
        oldFit = Pop.getBestFitness().clone();
        oldNorm = PhenotypeMetric.norm(oldFit);
        popFitCalls = Pop.getFunctionCalls();
        popGens = Pop.getGeneration();
        firstTime = false;
    }

    public boolean shouldDisagree() {
        return isTerminated(getAllSolutions());
    }

    public boolean isTerminated(InterfaceSolutionSet solSet) {
        return isTerminated(solSet.getCurrentPopulation());
    }

    public boolean isTerminated(PopulationInterface Pop) {
        if (!firstTime && isStillConverged(Pop)) {
            if (stagnationTimeHasPassed(Pop)) {
                // population hasnt changed much for max time, criterion is met
                return true;
            } else {
                // population hasnt changed much for i<max time, keep running
                return false;
            }
        } else {
            // first call at all - or population improved more than "allowed" to terminate
            saveState(Pop);

            return false;
        }
    }

    /**
     * Return true if |oldFit - curFit| < |oldFit| * thresh (relative case)
     * and if |oldFit - curFit| < thresh (absolute case).
     *
     * @param
     * @return
     */
    protected boolean isStillConverged(PopulationInterface pop) {
        double[] curFit = pop.getBestFitness();
        double dist = EuclideanMetric.euclideanDistance(oldFit, curFit);
        boolean ret;
        if (convergenceCondition.isSelectedString("Relative")) {
            ret = (dist < (oldNorm * convThresh));
        } else {
            ret = (dist < convThresh);
        }
        return ret;
    }

    private boolean stagnationTimeHasPassed(PopulationInterface pop) {
        if (stagnationMeasure.isSelectedString("Fitness calls")) { // by fitness calls
//			System.out.println("stagnationTimeHasPassed returns " + ((pop.getFunctionCalls() - popFitCalls) >= m_stagTime) + " after " + (pop.getFunctionCalls() - popFitCalls));
            return (pop.getFunctionCalls() - popFitCalls) >= m_stagTime;
        } else {// by generation
//			System.out.println("stagnationTimeHasPassed returns " + ((pop.getFunctionCalls() - popGens) >= m_stagTime) + " after " + (pop.getFunctionCalls() - popGens));
            return (pop.getGeneration() - popGens) >= m_stagTime;
        }
    }

    protected double[] getAcceleration(double[] personalBestPos, double[] neighbourBestPos, double[] curPosition, double[][] range) {
        double[] accel = new double[curPosition.length];
        double chi;

        RNG.setRandomSeed();

        boolean bIsAlgoSixSigma = algoTypePsod.isSelectedString(strAlgoTypeSixSigma);
        boolean bIsStagOnRiot = algoTypePsod.isSelectedString(strAlgoTypeStagnationRiot);

        double gen = 0;
        boolean is_extreme = false;
        boolean is_partial = false;
        if (bIsAlgoSixSigma) {
            gen = RNG.gaussianDouble(sigmaFilter);

            //double chance_of_disagreements = RNG.randomDouble(0, 1);
            //if (execCursor < chance_of_disagreements) {
            if ((-2 < gen && gen <= -1) || (1 < gen && gen < 2)) // +- 2 sigma
            {
                partial_disagreements++;
                is_partial = true;
            } else {
                if (gen <= -2 || gen >= 2) {
                    extreme_disagreements++;
                    is_extreme = true;
                }
            }
            //}
        } else {
            if (bIsStagOnRiot && riotReady) {
                extreme_disagreements++;
                is_extreme = true;
            }
        }


        for (int i = 0; i < personalBestPos.length; i++) {
            // the component from the old velocity
            accel[i] = 0;

            if (algType.getSelectedTag().getID() == 1) chi = m_InertnessOrChi;
            else chi = 1.;

            // the component from the social model
            double agreedAccel = neighbourBestPos[i] - curPosition[i];  // common PSO case

            if (bIsAlgoSixSigma) {
                if (is_partial) // +- 2 sigma
                {
                    // make partial disagreements
                    double partialDisagreement = RNG.randomDouble(-1.0 * lowLimit, lowLimit);
                    agreedAccel *= partialDisagreement;
                } else {
                    if (is_extreme) {
                        double extremeDisagreement = RNG.randomDouble(-1.0 * lowLimit, lowLimit);
                        //agreedAccel *= extremeDisagreement;
                        if (extremeDisagreement < 0)
                            agreedAccel *= (extremeDisagreement - (upLimit - lowLimit)); // -2 to -1
                        else
                            agreedAccel *= (extremeDisagreement + (upLimit - lowLimit)); // 1 to 2*/
                    }
                }
            } else {
                if (bIsStagOnRiot && is_extreme) {
                        double extremeDisagreement = RNG.randomDouble(-1.0 * lowLimit, lowLimit);
                        if (extremeDisagreement < 0)
                            agreedAccel *= (extremeDisagreement - (upLimit - lowLimit)); // -2 to -1
                        else
                            agreedAccel *= (extremeDisagreement + (upLimit - lowLimit)); // 1 to 2
                }
            }

            // the component from the cognition model (invalid in Pederson case)
            if (algoTypeSimplified.isSelectedString(strNo))
                accel[i] = this.m_Phi1 * chi * RNG.randomDouble(0, 1) * (personalBestPos[i] - curPosition[i]);
            // the component from social model
            accel[i] += this.m_Phi2 * chi * RNG.randomDouble(0, 1) * agreedAccel;
        }
        return accel;
    }
}
