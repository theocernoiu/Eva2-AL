package eva2.server.go.problems.portfolio.objective;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

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
 * Time: 12:53:54
 * To change this template use File | Settings | File Templates.
 */
public class ObjectivePortfolioReturn implements InterfaceOptimizationObjective, InterfacePortfolioSelectionObjective, java.io.Serializable {

    private boolean m_NormalizeTarget = false;
    private double[] m_Return;
    private String base = System.getProperty("user.dir");
    private String FS = System.getProperty("file.separator");
    private PropertyFilePath m_InputFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/Port1_Return.txt");
    private double m_MinValue = Double.NEGATIVE_INFINITY;
    private SelectedTag m_ObjectiveType;

    public ObjectivePortfolioReturn() {
        // the opt mode
        Tag[] tag = new Tag[3];
        tag[0] = new Tag(0, "Objective");
        tag[1] = new Tag(1, "Objective + Constraint");
        tag[2] = new Tag(2, "Constraint");
        this.m_ObjectiveType = new SelectedTag(0, tag);
        this.loadData();
    }

    public ObjectivePortfolioReturn(ObjectivePortfolioReturn p) {
        this.m_MinValue = p.m_MinValue;
        this.m_NormalizeTarget = p.m_NormalizeTarget;
        if (p.m_Return != null) {
            this.m_Return = new double[p.m_Return.length];
            System.arraycopy(p.m_Return, 0, this.m_Return, 0, p.m_Return.length);
        }
        if (p.m_ObjectiveType != null) {
            this.m_ObjectiveType = (SelectedTag) p.m_ObjectiveType.clone();
        }
    }

    public Object clone() {
        return (Object) new ObjectivePortfolioReturn(this);
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

        for (int i = 0; i < w.length; i++) {
            result += w[i] * this.m_Return[i];
        }

        switch (this.m_ObjectiveType.getSelectedTag().getID()) {
            case 0: {
                // objective
                ((AbstractEAIndividual) indy).putData(this.getName(), new Double(result));
                if (this.m_NormalizeTarget) {
                    result = (result - range[0]) / (range[1] - range[0]);
                    result = 1 - result;
                } else {
                    result = range[1] - result;
                }
                return result;
            }
            case 1: {
                // objective + constraint
                if (result < this.m_MinValue)
                    ((AbstractEAIndividual) indy).addConstraintViolation(this.m_MinValue - result);
                ((AbstractEAIndividual) indy).putData(this.getName(), new Double(result));
                if (this.m_NormalizeTarget) {
                    result = (result - range[0]) / (range[1] - range[0]);
                    result = 1 - result;
                } else {
                    result = range[1] - result;
                }
                return result;
            }
            case 2: {
                // constraint
                if (result < this.m_MinValue)
                    ((AbstractEAIndividual) indy).addConstraintViolation(this.m_MinValue - result);
                ((AbstractEAIndividual) indy).putData(this.getName(), new Double(result));
                return Double.NaN;
            }
            default: {
                // objective
                ((AbstractEAIndividual) indy).putData(this.getName(), new Double(result));
                if (this.m_NormalizeTarget) {
                    result = (result - range[0]) / (range[1] - range[0]);
                    result = 1 - result;
                } else {
                    result = range[1] - result;
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
        double[] tmpD = new double[2];
        tmpD[0] = Double.MAX_VALUE;
        tmpD[1] = -Double.MAX_VALUE;

        for (int i = 0; i < this.m_Return.length; i++) {
            if (tmpD[0] > this.m_Return[i]) tmpD[0] = this.m_Return[i];
            if (tmpD[1] < this.m_Return[i]) tmpD[1] = this.m_Return[i];
        }
        double[] result = new double[2];
        result[0] = tmpD[1] - tmpD[1];
        result[1] = tmpD[1] - tmpD[0];
        return result;
    }

    /**
     * This method allows you to set a the name of the input file that is to be used
     *
     * @param filename The primer for the inputfile.
     */
    public void setInputFileName(String filename) {
        this.m_InputFilePath.setCompleteFilePath(base + FS + "resources" + FS + "PortfolioSelection" + FS + filename + "_Return.txt");
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
            ArrayList tmpA = new ArrayList();
            while ((currentLine = reader.readLine()) != null && currentLine.length() != 0) {
                currentLine = currentLine.trim();
                tmpA.add(new Double(currentLine));
            }
            this.m_Return = new double[tmpA.size()];
            for (int i = 0; i < tmpA.size(); i++) {
                this.m_Return[i] = ((Double) tmpA.get(i)).doubleValue();
            }
            reader.close();
        } catch (java.io.IOException e) {
            System.out.println("Java.io.IOExeption: " + e.getMessage());
        }
    }

    /**
     * This method can be used to transform a beasley data file into
     * a streichert style data file for the return...
     *
     * @param args
     */
    public static void main(String[] args) {
        String base = System.getProperty("user.dir");
        String FS = System.getProperty("file.separator");
        PropertyFilePath fileInPath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/port1.txt");
        PropertyFilePath fileOutPath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/Port1_Return.txt");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileInPath.getCompleteFilePath()));
            writer = new BufferedWriter(new FileWriter(fileOutPath.getCompleteFilePath()));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Could not find " + fileInPath.getCompleteFilePath());
            return;
        } catch (java.io.IOException ed) {
            System.out.println("Could not open " + fileOutPath.getCompleteFilePath());
            return;
        }
        String currentLine, outline;
        String[] lineComponents;
        double tmp;
        try {
            currentLine = reader.readLine();
            currentLine = currentLine.trim();
            int number = new Integer(currentLine).intValue();
            // First the basic elements like return, risk, [name, ...]
            for (int i = 0; i < number; i++) {
                currentLine = reader.readLine();
                currentLine = currentLine.trim();
                lineComponents = currentLine.split("\t");
                if (lineComponents.length < 2) lineComponents = currentLine.split(" ");
                tmp = new Double(lineComponents[0]).doubleValue();
                outline = "" + tmp;
                if (i < number - 1) outline += "\n";
                writer.write(outline);
            }
            reader.close();
            writer.close();
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
     * This method allows you to retrieve the constraint/goal
     *
     * @return The cosntraint/goal
     */
    public double getConstraintGoal() {
        return this.m_MinValue;
    }

    public void SetConstraintGoal(double d) {
        this.m_MinValue = d;
    }

    /**
     * This method returns whether or not the given objective is to be minimized
     *
     * @return True if to be minimized false else.
     */
    public boolean is2BMinimized() {
        return false;
    }

    /**
     * This method returns a description of the objective
     *
     * @return A String
     */
    public String getStringRepresentation() {
        String result = "Return:\n";
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
        return "Return";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The objective is to minimze (MaxReturn - return).";
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
        this.loadData();
    }

    public PropertyFilePath getPortfolioProblem() {
        return this.m_InputFilePath;
    }

    public String portfolioProblemTipText() {
        return "Select the portfolio problem by choosing the input file.";
    }

    /**
     * This method will allow you set a min value as constraint
     *
     * @param d The upper border.
     */
    public void setMinValue(double d) {
        this.m_MinValue = d;
    }

    public double getMinValue() {
        return this.m_MinValue;
    }

    public String minValueTipText() {
        return "Choose a min value as as constraint.";
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