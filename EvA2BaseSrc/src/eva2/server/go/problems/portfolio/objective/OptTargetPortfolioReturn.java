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
 * Time: 14:10:26
 * To change this template use File | Settings | File Templates.
 */
public class OptTargetPortfolioReturn implements InterfaceOptimizationObjective, InterfacePortfolioSelectionTarget, java.io.Serializable {

    private boolean m_NormalizeTarget = false;
//    private double      m_TargetValue       = 1.0;
//    private SelectedTag m_OptType;

    public OptTargetPortfolioReturn() {
//        Tag[] tag = new Tag[4];
//        tag[0] = new Tag(0, "Maximize");
//        tag[1] = new Tag(1, "= Target Value");
//        tag[2] = new Tag(1, "> Target Value");
//        tag[3] = new Tag(2, "< Target Value");
//        this.m_OptType = new SelectedTag(0, tag);
    }

    public OptTargetPortfolioReturn(OptTargetPortfolioReturn p) {
        this.m_NormalizeTarget = p.m_NormalizeTarget;
//        this.m_TargetValue          = p.m_TargetValue;
//        if (p.m_OptType != null)
//            this.m_OptType = (SelectedTag)p.m_OptType.clone();
    }

    public Object clone() {
        return (Object) new OptTargetPortfolioReturn(this);
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
        double[] retA, riskA;

        retA = prob.getAssetReturn();
        riskA = prob.getAssetRisk();

        for (int i = 0; i < w.length; i++) {
            result += w[i] * retA[i];
        }
        result = range[1] - result;
        if (this.m_NormalizeTarget) {
            result = (result - range[0]) / (range[1] - range[0]);
        }
//        // now realize the different optimization options
//        switch (this.m_OptType.getSelectedTag().getID()) {
//            case 0 : {
//                result = -result;
//                break;
//            }
//            case 1 : {
//                result = -Math.abs(this.m_TargetValue - result);
//                break;
//            }
//            case 2 : {
//                result = -(this.m_TargetValue - result);
//                if (result < 0) result = 0;
//                break;
//            }
//            case 3 : {
//                result = -(result - this.m_TargetValue);
//                if (result < 0) result = 0;
//                break;
//            }
//            default : {
//                result = -result;
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
        double result = 0;
        double[] w = indy.getDoubleDataWithoutUpdate();
        double[] range = this.getBoundaries(prob);
        double[] retA, riskA;

        retA = prob.getAssetReturn();
        riskA = prob.getAssetRisk();

        for (int i = 0; i < w.length; i++) {
            // at the very end of the sourceMatrix is the return of an asset
            result += w[i] * retA[i];
        }

        if (this.m_NormalizeTarget) {
            result = (result - range[0]) / (range[1] - range[0]);
        }

        return result;
    }

    private double[] getBoundaries(TFPortfolioSelectionProblemInterface prob) {
        double[] tmpD = new double[2];
        tmpD[0] = Double.MAX_VALUE;
        tmpD[1] = -Double.MAX_VALUE;
        double[] retA, riskA;

        retA = prob.getAssetReturn();
        riskA = prob.getAssetRisk();

        for (int i = 0; i < retA.length; i++) {
            if (tmpD[0] > retA[i]) tmpD[0] = retA[i];
            if (tmpD[1] < retA[i]) tmpD[1] = retA[i];
        }

        double[] result = new double[2];
        result[0] = tmpD[1] - tmpD[1];
        result[1] = tmpD[1] - tmpD[0];
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
        return true;
    }

    /**
     * This method allows you to retrieve the name of the optimization target
     *
     * @return The name
     */
    public String getIdentName() {
        return "Return";
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
        return false;
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
        return "Return";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Try to minimize the negative return.";
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
