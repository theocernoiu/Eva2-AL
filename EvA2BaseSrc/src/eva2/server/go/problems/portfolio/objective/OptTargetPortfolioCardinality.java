package eva2.server.go.problems.portfolio.objective;

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
 * Time: 14:11:15
 * To change this template use File | Settings | File Templates.
 */
public class OptTargetPortfolioCardinality implements InterfaceOptimizationObjective, InterfacePortfolioSelectionTarget, java.io.Serializable {

    private boolean m_NormalizeTarget = false;

//    private SelectedTag m_OptType;
//    private int         m_TargetValue       = 3;

    public OptTargetPortfolioCardinality() {
//        Tag[] tag = new Tag[4];
//        tag[0] = new Tag(0, "Minimize");
//        tag[1] = new Tag(1, "= Target Value");
//        tag[2] = new Tag(1, "> Target Value");
//        tag[3] = new Tag(2, "< Target Value");
//        this.m_OptType = new SelectedTag(0, tag);
    }

    public OptTargetPortfolioCardinality(OptTargetPortfolioCardinality p) {
        this.m_NormalizeTarget = p.m_NormalizeTarget;
//        if (p.m_OptType != null)
//            this.m_OptType = (SelectedTag)p.m_OptType.clone();
//        this.m_TargetValue          = p.m_TargetValue;
    }

    public Object clone() {
        return (Object) new OptTargetPortfolioCardinality(this);
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
        double[] range = this.getBoundaries(prob);

        for (int i = 0; i < w.length; i++) {
            if (w[i] > 0) result++;
        }

        if (this.m_NormalizeTarget) {
            result = (result - range[0]) / (range[1] - range[0]);
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

    private double[] getBoundaries(TFPortfolioSelectionProblemInterface prob) {
        double[] result = new double[2];
        result[0] = 0;
        result[1] = (prob.getAssetReturn().length + 1);
        return result;
    }

    /**
     * This method will return the upper and the lower bound for this objective
     *
     * @param prob The portfolio selection problem
     * @return The value of the optimization target.
     */
    public double[] getObjectiveBoundaries(TFPortfolioSelectionProblemInterface prob) {
        if (this.m_NormalizeTarget) {
            double[] result = new double[2];
            result[0] = 0;
            result[1] = 1;
            return result;
        } else {
            return this.getBoundaries(prob);
        }
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
        return "Cardinality";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Try to minimize the cardinality.";
    }

    /**
     * This method will allow you to toggle Targetvalue normalization
     *
     * @param bit Toggel the optimization mode.
     */
    public void setNormalizeTarget(boolean bit) {
        this.m_NormalizeTarget = bit;
    }

    public boolean getNormalizeTarget() {
        return this.m_NormalizeTarget;
    }

    public String normalizeTargetTipText() {
        return "Toogle target value normalization.";
    }

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
//    public void setTargetValue(int b) {
//        if (b < 0) b = 0;
//        this.m_TargetValue = b;
//    }
//    public int getTargetValue() {
//        return this.m_TargetValue;
//    }
//    public String targetValueTipText() {
//        return "The target value.";
//    }
}