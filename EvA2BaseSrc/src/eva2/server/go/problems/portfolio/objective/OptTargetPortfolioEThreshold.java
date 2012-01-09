package eva2.server.go.problems.portfolio.objective;

import eva2.gui.PropertyOptimizationObjectivesWithParam;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.problems.InterfaceOptimizationObjective;
import eva2.server.go.problems.InterfaceOptimizationTarget;
import eva2.server.go.problems.TFPortfolioSelectionProblem;
import eva2.server.go.problems.TFPortfolioSelectionProblemInterface;
import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionTarget;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 19.01.2005
 * Time: 15:12:03
 * To change this template use File | Settings | File Templates.
 */
public class OptTargetPortfolioEThreshold implements InterfaceOptimizationObjective, InterfacePortfolioSelectionTarget, java.io.Serializable {

    private PropertyOptimizationObjectivesWithParam m_OptimizationTargets;
    private int m_Level;

    public OptTargetPortfolioEThreshold() {
        this.m_Level = 0;
        // The opt targets
        InterfaceOptimizationObjective[] tmpList;
        tmpList = new InterfaceOptimizationObjective[3];
        tmpList[0] = new OptTargetPortfolioReturn();
        tmpList[1] = new OptTargetPortfolioRisk();
        tmpList[2] = new OptTargetPortfolioCardinality();
        this.m_OptimizationTargets = new PropertyOptimizationObjectivesWithParam(tmpList);
        tmpList = new InterfaceOptimizationObjective[2];
        tmpList[0] = new OptTargetPortfolioReturn();
        tmpList[1] = new OptTargetPortfolioRisk();
        this.m_OptimizationTargets.setSelectedTargets(tmpList);
        this.m_OptimizationTargets.setDescriptiveString("Choose Threshold values for each objective.");
        this.m_OptimizationTargets.setWeightsLabel("Threshold value");
        this.m_OptimizationTargets.enableNormalization(false);
    }

    public OptTargetPortfolioEThreshold(int l) {
        this.m_Level = l - 1;
        // The opt targets
        InterfaceOptimizationObjective[] tmpList;
        if (this.m_Level > 0) {
            tmpList = new InterfaceOptimizationObjective[5];
            tmpList[0] = new OptTargetPortfolioReturn();
            tmpList[1] = new OptTargetPortfolioRisk();
            tmpList[2] = new OptTargetPortfolioCardinality();
            tmpList[3] = new OptTargetPortfolioWeightedFitness(this.m_Level);
            tmpList[4] = new OptTargetPortfolioGoal(this.m_Level);
        } else {
            tmpList = new InterfaceOptimizationObjective[3];
            tmpList[0] = new OptTargetPortfolioReturn();
            tmpList[1] = new OptTargetPortfolioRisk();
            tmpList[2] = new OptTargetPortfolioCardinality();
        }
        this.m_OptimizationTargets = new PropertyOptimizationObjectivesWithParam(tmpList);
        tmpList = new InterfaceOptimizationObjective[2];
        tmpList[0] = new OptTargetPortfolioReturn();
        tmpList[1] = new OptTargetPortfolioRisk();
        this.m_OptimizationTargets.setSelectedTargets(tmpList);
        this.m_OptimizationTargets.setDescriptiveString("Choose Threshold values for each objective.");
        this.m_OptimizationTargets.setWeightsLabel("Threshold value");
        this.m_OptimizationTargets.enableNormalization(false);
    }

    public OptTargetPortfolioEThreshold(OptTargetPortfolioEThreshold p) {
        this.m_Level = p.m_Level;
        if (p.m_OptimizationTargets != null) {
            this.m_OptimizationTargets = (PropertyOptimizationObjectivesWithParam) p.m_OptimizationTargets.clone();
        }
    }

    public Object clone() {
        return (Object) new OptTargetPortfolioEThreshold(this);
    }

    /**
     * This method will return the performance of a given portfolio in a given environment
     *
     * @param indy The individual, storing the portfolio in the phenotpye.
     * @param prob The portfolio selection problem
     * @return The value of the optimization target.
     */
    public double evaluatePortfolio(InterfaceDataTypeDouble indy, TFPortfolioSelectionProblemInterface prob) {
        double[] w = indy.getDoubleDataWithoutUpdate();
        double result = 0;
        double tmpFit;
        InterfaceOptimizationObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        double[] thresh = this.m_OptimizationTargets.getWeights();

        // calculate the risk of the allocation
        for (int i = 0; i < list.length; i++) {
            tmpFit = ((InterfacePortfolioSelectionTarget) list[i]).evaluatePortfolio(indy, prob);
            if (((InterfacePortfolioSelectionTarget) list[i]).isTargetToBeMaximized()) {
                result += Math.max(0, thresh[i] - tmpFit);
            } else {
                result += Math.max(0, tmpFit - thresh[i]);
            }
        }

        return result;
    }

    /**
     * This method will return the performance of a given portfolio in a given environment
     *
     * @param indy The individual, storing the portfolio in the phenotpye.
     * @param prob The portfolio selection problem
     * @return The value of the optimization target.
     */
    public double getPlotValue(InterfaceDataTypeDouble indy, TFPortfolioSelectionProblemInterface prob) {
        return this.evaluatePortfolio(indy, prob);
    }

    /**
     * This method will return the upper and the lower bound for this objective
     *
     * @param prob The portfolio selection problem
     * @return The value of the optimization target.
     */
    public double[] getObjectiveBoundaries(TFPortfolioSelectionProblemInterface prob) {
        double[] result = new double[2];
        InterfaceOptimizationObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        double[] thresh = this.m_OptimizationTargets.getWeights();
        double[] tmpD;
        result[0] = 0;
        result[1] = 0;

        for (int i = 0; i < list.length; i++) {
            tmpD = ((InterfacePortfolioSelectionTarget) list[i]).getObjectiveBoundaries(prob);
            result[0] += 0;
//            if (((InterfacePortfolioSelectionTarget)list[i]).isTargetToBeMaximized()) {
//                result += Math.max(0, thresh[i] - tmpFit);
//            } else {
//                result += Math.max(0, tmpFit - thresh[i]);
//            }
//            result[1] += Math.max(Math.abs(goals[i]-tmpD[0]), Math.abs(goals[i]-tmpD[1]));
        }

        return result;
    }

    /**
     * This method will return wether or not the objective is to be maximized
     * (which is typically solved by inversion)
     *
     * @return true
     */
    public boolean isTargetToBeMaximized() {
        return false;
    }

    /**
     * This method allows you to retrieve the name of the optimization target
     *
     * @return The name
     */
    public String getIdentName() {
        return "EThreshold";
    }

    /**
     * This method allows you to retrieve the current optimization mode
     * The modes include
     * - Objective
     * - Objective + Constraint
     * - Constraint
     * (-Goal !?)
     *
     * @return The mode as string
     */
    public String getOptimizationMode() {
        return null;
    }

    public void SetOptimizationMode(String s) {
    }

    /**
     * This method allows you to retrieve the constraint/goal
     *
     * @return The cosntraint/goal
     */
    public double getConstraintGoal() {
        return Double.NaN;
    }

    public void SetConstraintGoal(double d) {
    }

    /**
     * This method returns whether or not the given objective is to be minimized
     *
     * @return True if to be minimized false else.
     */
    public boolean is2BMinimized() {
        return true;
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
        return "E-Threshold";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The optimzation objectives have meet a given maximum value (minimum value).";
    }

    /**
     * This method allows you to choose the optimization targets.
     *
     * @param b File path.
     */
    public void setOptimizationTargets(PropertyOptimizationObjectivesWithParam b) {
        this.m_OptimizationTargets = b;
    }

    public PropertyOptimizationObjectivesWithParam getOptimizationTargets() {
        return this.m_OptimizationTargets;
    }

    public String optimizationTargetsTipText() {
        return "Choose the optimization Targets.";
    }
}