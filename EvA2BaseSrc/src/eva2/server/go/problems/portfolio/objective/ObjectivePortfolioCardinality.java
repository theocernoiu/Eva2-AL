package eva2.server.go.problems.portfolio.objective;


import java.io.BufferedReader;
import java.io.FileReader;

import eva2.gui.PropertyFilePath;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.problems.InterfaceOptimizationObjective;
import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionObjective;
import eva2.tools.SelectedTag;
import eva2.tools.Tag;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 07.04.2005
 * Time: 12:54:26
 * To change this template use File | Settings | File Templates.
 */
public class ObjectivePortfolioCardinality implements InterfaceOptimizationObjective, InterfacePortfolioSelectionObjective, java.io.Serializable {

    private boolean m_NormalizeTarget = false;
    private int m_NumberOfAssets;
    private PropertyFilePath m_InputFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/Port1_Return.txt");
    private double m_MaxValue = Double.POSITIVE_INFINITY;
    private SelectedTag m_ObjectiveType;

    public ObjectivePortfolioCardinality() {
        // the opt mode
        Tag[] tag = new Tag[3];
        tag[0] = new Tag(0, "Objective");
        tag[1] = new Tag(1, "Objective + Constraint");
        tag[2] = new Tag(2, "Constraint");
        this.m_ObjectiveType = new SelectedTag(0, tag);
        this.loadData();
    }

    public ObjectivePortfolioCardinality(ObjectivePortfolioCardinality p) {
        this.m_MaxValue = p.m_MaxValue;
        this.m_NormalizeTarget = p.m_NormalizeTarget;
        this.m_NumberOfAssets = p.m_NumberOfAssets;
        if (p.m_ObjectiveType != null) {
            this.m_ObjectiveType = (SelectedTag) p.m_ObjectiveType.clone();
        }
    }

    public Object clone() {
        return (Object) new ObjectivePortfolioCardinality(this);
    }

    /**
     * This method will return the performance of a given portfolio in a given environment
     *
     * @param indy The individual, storing the portfolio in the phenotpye.
     * @return The value of the optimization target.
     */
    public double evaluatePortfolio(InterfaceDataTypeDouble indy) {
        double[] w = indy.getDoubleDataWithoutUpdate();
        double result = 0;
        double[] range = this.getObjectiveBoundaries();

        for (int i = 0; i < w.length; i++) if (w[i] > 0) result++;

        switch (this.m_ObjectiveType.getSelectedTag().getID()) {
            case 0: {
                // objective
                ((AbstractEAIndividual) indy).putData(this.getName(), new Double(result));
                if (this.m_NormalizeTarget) {
                    result = (result - range[0]) / (range[1] - range[0]);
                }
                return result;
            }
            case 1: {
                // objective + constraint
                if (result > this.m_MaxValue)
                    ((AbstractEAIndividual) indy).addConstraintViolation(result - this.m_MaxValue);
                ((AbstractEAIndividual) indy).putData(this.getName(), new Double(result));
                if (this.m_NormalizeTarget) {
                    result = (result - range[0]) / (range[1] - range[0]);
                }
                return result;
            }
            case 2: {
                // constraint
                if (result > this.m_MaxValue)
                    ((AbstractEAIndividual) indy).addConstraintViolation(result - this.m_MaxValue);
                ((AbstractEAIndividual) indy).putData(this.getName(), new Double(result));
                return Double.NaN;
            }
            default: {
                // objective
                ((AbstractEAIndividual) indy).putData(this.getName(), new Double(result));
                if (this.m_NormalizeTarget) {
                    result = (result - range[0]) / (range[1] - range[0]);
                }
                return result;
            }
        }
    }

    /**
     * This method will return the upper and the lower bound for this objective
     *
     * @return The value of the optimization target.
     */
    public double[] getObjectiveBoundaries() {
        double[] result = new double[2];
        result[0] = 0;
        result[1] = this.m_NumberOfAssets;
        return result;
    }

    /**
     * This method allows you to set a the name of the input file that is to be used
     *
     * @param filename The primer for the inputfile.
     */
    public void setInputFileName(String filename) {
        this.m_InputFilePath.setCompleteFilePath("resources/PortfolioSelection/" + filename + "_Return.txt");
        this.loadData();
    }

    /**
     * This method loads the objective specific data from a file.
     */
    private void loadData() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.m_InputFilePath.getCompleteFilePath()));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Could not find " + this.m_InputFilePath.getCompleteFilePath());
            return;
        }
        String currentLine;
        try {
            this.m_NumberOfAssets = 0;
            while ((currentLine = reader.readLine()) != null && currentLine.length() != 0) {
                this.m_NumberOfAssets++;
            }
            currentLine = reader.readLine();
            reader.close();
        } catch (java.io.IOException e) {
            System.out.println("Java.io.IOExeption: " + e.getMessage());
        }
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
        String result = "Cardiniality:\n";
        result += " Is to be minizimed = " + this.is2BMinimized() + "\n";
        result += " Constrained        = " + this.m_ObjectiveType.getSelectedTag().getString() + "\n";
        result += " Cosntraint         = " + this.getConstraintGoal() + "\n";
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
        return "Cardinality";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The objective is to minimize the cardinality of the portfolio.";
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

    /**
     * This method allows you to set the path to the data file.
     *
     * @param b File path.
     */
    public void setPortfolioProblem(PropertyFilePath b) {
        this.m_InputFilePath = b;
    }

    public PropertyFilePath getPortfolioProblem() {
        return this.m_InputFilePath;
    }

    public String portfolioProblemTipText() {
        return "Select the portfolio problem by choosing the input file.";
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
