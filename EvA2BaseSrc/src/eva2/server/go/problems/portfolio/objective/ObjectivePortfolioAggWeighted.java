package eva2.server.go.problems.portfolio.objective;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.problems.InterfaceOptimizationObjective;
import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionObjective;
import eva2.tools.SelectedTag;
import eva2.tools.Tag;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 19.04.2005
 * Time: 18:36:15
 * To change this template use File | Settings | File Templates.
 */
public class ObjectivePortfolioAggWeighted implements InterfaceOptimizationObjective, InterfacePortfolioSelectionObjective, java.io.Serializable {

    private PropertyPortfolioSelectionWeightAggregatedObjectives m_OptimizationTargets;
    private double m_MaxValue = Double.POSITIVE_INFINITY;
    private SelectedTag m_ObjectiveType;

    public ObjectivePortfolioAggWeighted() {
        // the opt mode
        Tag[] tag = new Tag[3];
        tag[0] = new Tag(0, "Objective");
        tag[1] = new Tag(1, "Objective + Constraint");
        tag[2] = new Tag(2, "Constraint");
        this.m_ObjectiveType = new SelectedTag(0, tag);
        // The opt targets
        InterfacePortfolioSelectionObjective[] tmpList;
        tmpList = new InterfacePortfolioSelectionObjective[3];
        tmpList[0] = new ObjectivePortfolioReturn();
        tmpList[1] = new ObjectivePortfolioRisk();
        tmpList[2] = new ObjectivePortfolioCardinality();
        this.m_OptimizationTargets = new PropertyPortfolioSelectionWeightAggregatedObjectives(tmpList);
        tmpList = new InterfacePortfolioSelectionObjective[2];
        tmpList[0] = new ObjectivePortfolioReturn();
        tmpList[1] = new ObjectivePortfolioRisk();
        this.m_OptimizationTargets.setSelectedTargets(tmpList);
        this.m_OptimizationTargets.setDescriptiveString("Weighted fitness aggregation, choose proper weights for the individual objectives.");
        this.m_OptimizationTargets.setWeightsLabel("Weigths");
    }


    public ObjectivePortfolioAggWeighted(ObjectivePortfolioAggWeighted p) {
        if (p.m_OptimizationTargets != null) {
            this.m_OptimizationTargets = (PropertyPortfolioSelectionWeightAggregatedObjectives) p.m_OptimizationTargets.clone();
        }
        this.m_MaxValue = p.m_MaxValue;
        if (p.m_ObjectiveType != null) {
            this.m_ObjectiveType = (SelectedTag) p.m_ObjectiveType.clone();
        }
    }

    public Object clone() {
        return (Object) new ObjectivePortfolioAggWeighted(this);
    }

    /**
     * This method will return the performance of a given portfolio in a given environment
     *
     * @param indy The individual, storing the portfolio in the phenotpye.
     * @return The value of the optimization target.
     */
    public double evaluatePortfolio(InterfaceDataTypeDouble indy) {
        double result = 0;
        InterfacePortfolioSelectionObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        double[] weights = this.m_OptimizationTargets.getWeights();
        double tmpD;

        for (int i = 0; i < list.length; i++) {
            tmpD = ((InterfacePortfolioSelectionObjective) list[i]).evaluatePortfolio(indy);
            if (!(new Double(tmpD).isNaN())) {
                result += weights[i] * ((InterfacePortfolioSelectionObjective) list[i]).evaluatePortfolio(indy);
            }
        }

        switch (this.m_ObjectiveType.getSelectedTag().getID()) {
            case 0: {
                // objective
                ((AbstractEAIndividual) indy).putData(this.getIdentName(), new Double(result));
                return result;
            }
            case 1: {
                // objective + constraint
                if (result > this.m_MaxValue)
                    ((AbstractEAIndividual) indy).addConstraintViolation(result - this.m_MaxValue);
                ((AbstractEAIndividual) indy).putData(this.getIdentName(), new Double(result));
                return result;
            }
            case 2: {
                // constraint
                if (result > this.m_MaxValue)
                    ((AbstractEAIndividual) indy).addConstraintViolation(result - this.m_MaxValue);
                ((AbstractEAIndividual) indy).putData(this.getIdentName(), new Double(result));
                return Double.NaN;
            }
            default: {
                // objective
                ((AbstractEAIndividual) indy).putData(this.getIdentName(), new Double(result));
                return result;
            }
        }
    }

    /**
     * This method allows you to set a the name of the input file that is to be used
     *
     * @param filename The primer for the inputfile.
     */
    public void setInputFileName(String filename) {
        InterfacePortfolioSelectionObjective[] list = this.m_OptimizationTargets.getSelectedTargets();

        for (int i = 0; i < list.length; i++) {
            ((InterfacePortfolioSelectionObjective) list[i]).setInputFileName(filename);
        }
    }

    /**
     * This method will return the upper and the lower bound for this objective
     *
     * @return The value of the optimization target.
     */
    public double[] getObjectiveBoundaries() {
        double[] result = new double[2];
        InterfacePortfolioSelectionObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        double[] weights = this.m_OptimizationTargets.getWeights();
        double[] tmpD;

        for (int i = 0; i < list.length; i++) {
            tmpD = ((InterfacePortfolioSelectionObjective) list[i]).getObjectiveBoundaries();
            result[0] += weights[i] * tmpD[0];
            result[1] += weights[i] * tmpD[1];
        }

        return result;
    }


    /**
     * This method allows you to retrieve the name of the optimization target
     *
     * @return The name
     */
    public String getIdentName() {
        String result = "";
        InterfacePortfolioSelectionObjective[] tmp = this.m_OptimizationTargets.getSelectedTargets();
        double[] tmpD = this.m_OptimizationTargets.getWeights();
        for (int i = 0; i < tmp.length; i++) {
            result += (((int) tmpD[i] * 100) / ((double) 100)) + "*" + ((InterfaceOptimizationObjective) tmp[i]).getIdentName().substring(0, 3);
            if (i < tmp.length - 1) result += "+";
        }
        return result;
    }

    /**
     * This method allows you to retrieve the constraint/goal
     *
     * @return The cosntraint/goal
     */
    public double getConstraintGoal() {
        return this.m_MaxValue;
    }

    public void SetConstraintGoal(double d) {
        this.m_MaxValue = d;
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
        return this.m_ObjectiveType.getTags()[this.m_ObjectiveType.getSelectedTag().getID()].getString();
    }

    public void SetOptimizationMode(String s) {
        Tag[] tags = this.m_ObjectiveType.getTags();
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].getString().equalsIgnoreCase(s)) {
                this.m_ObjectiveType.setSelectedTag(i);
                return;
            }
        }
    }

    /**
     * This method returns whether or not the given objective is to be minimized
     *
     * @return True if to be minimized false else.
     */
    public boolean is2BMinimized() {
        return true;
    }

    /**
     * This method returns a description of the objective
     *
     * @return A String
     */
    public String getStringRepresentation() {
        String result = "Aggregated sum of multiple objectives:\n";
        InterfacePortfolioSelectionObjective[] tmp = this.m_OptimizationTargets.getSelectedTargets();
        double[] tmpD = this.m_OptimizationTargets.getWeights();
        result += " Obj = ";
        for (int i = 0; i < tmp.length; i++) {
            result += tmpD[i] + " * Obj" + (1 + i);
            if (i < (tmp.length - 1)) result += "+";
        }
        result += "\n";
        for (int i = 0; i < tmp.length; i++) {
            result += "Objective " + (i + 1) + "\n";
            result += tmp[i].getStringRepresentation();
        }
        result += "\n";
        return result;
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
        String result = "Weighted Fitness Aggregation";
        return result;
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The objective value is given by the weigthed aggregation of multiple optimization objectives.";
    }

    /**
     * This method allows you to choose the optimization targets.
     *
     * @param b File path.
     */
    public void setOptimizationTargets(PropertyPortfolioSelectionWeightAggregatedObjectives b) {
        this.m_OptimizationTargets = b;
    }

    public PropertyPortfolioSelectionWeightAggregatedObjectives getOptimizationTargets() {
        return this.m_OptimizationTargets;
    }

    public String optimizationTargetsTipText() {
        return "Choose the optimization objectives.";
    }

    /**
     * This method will allow you set a max value as constraint
     *
     * @param d The upper border.
     */
    public void setMaxValue(double d) {
        this.m_MaxValue = d;
    }

    public double getMaxValue() {
        return this.m_MaxValue;
    }

    public String maxValueTipText() {
        return "Choose a max value as as constraint.";
    }

    /**
     * This method will allow you to choose the type of objective
     *
     * @param d The lower border.
     */
    public void setObjectiveType(SelectedTag d) {
        this.m_ObjectiveType = d;
    }

    public SelectedTag getObjectiveType() {
        return this.m_ObjectiveType;
    }

    public String objectiveTypeTipText() {
        return "Choose the type of optimization.";
    }
}