package eva2.server.go.problems.portfolio.objective;

import eva2.gui.PropertyOptimizationObjectivesWithParam;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.problems.InterfaceOptimizationObjective;
import eva2.server.go.problems.InterfaceOptimizationTarget;
import eva2.server.go.problems.TFPortfolioSelectionProblem;
import eva2.server.go.problems.TFPortfolioSelectionProblemInterface;
import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionTarget;
import eva2.tools.SelectedTag;
import eva2.tools.Tag;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 17.01.2005
 * Time: 18:22:57
 * To change this template use File | Settings | File Templates.
 */
public class OptTargetPortfolioWeightedFitness implements InterfaceOptimizationObjective, InterfacePortfolioSelectionTarget, java.io.Serializable {

    private PropertyOptimizationObjectivesWithParam m_OptimizationTargets;
    //    private SelectedTag                 m_OptType;
//    private double                      m_TargetValue = 0.0;
    private int m_Level;

    public OptTargetPortfolioWeightedFitness() {
        this.m_Level = 0;
        // the opt mode
//        Tag[] tag = new Tag[4];
//        tag[0] = new Tag(0, "Minimize");
//        tag[1] = new Tag(1, "= Target Value");
//        tag[2] = new Tag(1, "> Target Value");
//        tag[3] = new Tag(2, "< Target Value");
//        this.m_OptType = new SelectedTag(0, tag);
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
        this.m_OptimizationTargets.setDescriptiveString("Weighted fitness aggregation, choose proper weights for the individual objectives.");
        this.m_OptimizationTargets.setWeightsLabel("Weigths");
    }

    public OptTargetPortfolioWeightedFitness(int l) {
        this.m_Level = l - 1;
        // the opt mode
//        Tag[] tag = new Tag[4];
//        tag[0] = new Tag(0, "Minimize");
//        tag[1] = new Tag(1, "= Target Value");
//        tag[2] = new Tag(1, "> Target Value");
//        tag[3] = new Tag(2, "< Target Value");
//        this.m_OptType = new SelectedTag(0, tag);
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
        this.m_OptimizationTargets.setDescriptiveString("Weighted fitness aggregation, choose proper weights for the individual objectives.");
        this.m_OptimizationTargets.setWeightsLabel("Weigths");
    }

    public OptTargetPortfolioWeightedFitness(OptTargetPortfolioWeightedFitness p) {
        this.m_Level = p.m_Level;
        if (p.m_OptimizationTargets != null) {
            this.m_OptimizationTargets = (PropertyOptimizationObjectivesWithParam) p.m_OptimizationTargets.clone();
        }
//        this.m_TargetValue      = p.m_TargetValue;
//        if (p.m_OptType != null)
//            this.m_OptType = (SelectedTag)p.m_OptType.clone();
    }

    public Object clone() {
        return (Object) new OptTargetPortfolioWeightedFitness(this);
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
        InterfaceOptimizationObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        double[] weights = this.m_OptimizationTargets.getWeights();

        // calculate the risk of the allocation
        for (int i = 0; i < list.length; i++) {
            result += weights[i] * ((InterfacePortfolioSelectionTarget) list[i]).evaluatePortfolio(indy, prob);
        }

//        // now realize the different optimization options
//        switch (this.m_OptType.getSelectedTag().getID()) {
//            case 0 : {
//                result = result;
//                break;
//            }
//            case 1 : {
//                result = Math.abs(this.m_TargetValue - result);
//                break;
//            }
//            case 2 : {
//                result = this.m_TargetValue - result;
//                if (result < 0) result = 0;
//                break;
//            }
//            case 3 : {
//                result = result - this.m_TargetValue;
//                if (result < 0) result = 0;
//                break;
//            }
//            default : {
//                result = result;
//                break;
//            }
//        }

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
        double[] weights = this.m_OptimizationTargets.getWeights();
        double[] tmpD;
        result[0] = 0;
        result[1] = 0;

        for (int i = 0; i < list.length; i++) {
            tmpD = ((InterfacePortfolioSelectionTarget) list[i]).getObjectiveBoundaries(prob);
            result[0] += weights[i] * tmpD[0];
            result[1] += weights[i] * tmpD[1];
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
        return "Cardinality";
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
        return "Weighted Fitness";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The target value is given by the weigthed aggregation of multiple optimization targets.";
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
//
//    /** This method will allow you to toggle the use of the TargetValue
//     * @param bit     Toggel the optimization mode.
//      */
//    public void setOptType(SelectedTag bit) {
//        this.m_OptType = bit;
//    }
//    public SelectedTag getOptType() {
//        return this.m_OptType;
//    }
//    public String optTypeTipText() {
//        return "Toogle the use of a target value as optimization target.";
//    }
//
//    /** This method will set the target value
//     * @param b     The target value.
//      */
//    public void setTargetValue(double b) {
//        this.m_TargetValue = b;
//    }
//    public double getTargetValue() {
//        return this.m_TargetValue;
//    }
//    public String targetValueTipText() {
//        return "The target value.";
//    }
}