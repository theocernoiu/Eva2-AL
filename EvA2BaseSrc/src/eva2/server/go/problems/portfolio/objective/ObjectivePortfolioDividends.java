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
import eva2.tools.math.RNG;
import eva2.tools.SelectedTag;
import eva2.tools.Tag;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 19.04.2005
 * Time: 17:28:44
 * To change this template use File | Settings | File Templates.
 */
public class ObjectivePortfolioDividends implements InterfaceOptimizationObjective, InterfacePortfolioSelectionObjective, java.io.Serializable {

    private boolean m_NormalizeTarget = false;
    private double[] m_Dividends;
    private String base = System.getProperty("user.dir");
    private String FS = System.getProperty("file.separator");
    private PropertyFilePath m_InputFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/Port1_Dividends.txt");
    private double m_MinValue = Double.NEGATIVE_INFINITY;
    private SelectedTag m_ObjectiveType;

    public ObjectivePortfolioDividends() {
        Tag[] tag = new Tag[3];
        tag[0] = new Tag(0, "Objective");
        tag[1] = new Tag(1, "Objective + Constraint");
        tag[2] = new Tag(2, "Constraint");
        this.m_ObjectiveType = new SelectedTag(0, tag);
        this.loadData();
    }

    public ObjectivePortfolioDividends(ObjectivePortfolioDividends p) {
        this.m_MinValue = p.m_MinValue;
        this.m_NormalizeTarget = p.m_NormalizeTarget;
        if (p.m_Dividends != null) {
            this.m_Dividends = new double[p.m_Dividends.length];
            System.arraycopy(p.m_Dividends, 0, this.m_Dividends, 0, p.m_Dividends.length);
        }
        if (p.m_ObjectiveType != null) {
            this.m_ObjectiveType = (SelectedTag) p.m_ObjectiveType.clone();
        }
    }

    public Object clone() {
        return (Object) new ObjectivePortfolioDividends(this);
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
            if (w[i] < 0.1) {
                result += w[i] * this.m_Dividends[i];
            } else {
                if (w[i] < 0.5) {
                    result += w[i] * (this.m_Dividends[i] * 1.2);
                } else {
                    result += w[i] * (this.m_Dividends[i] * 1.5);
                }
            }
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

        for (int i = 0; i < this.m_Dividends.length; i++) {
            if (tmpD[0] > this.m_Dividends[i])
                tmpD[0] = this.m_Dividends[i];
            if (tmpD[1] < this.m_Dividends[i] * 2.5)
                tmpD[1] = this.m_Dividends[i] * 2.5;
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
            this.m_Dividends = new double[tmpA.size()];
            for (int i = 0; i < tmpA.size(); i++) {
                this.m_Dividends[i] = ((Double) tmpA.get(i)).doubleValue();
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
        PropertyFilePath fileOutPath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/Port1_Dividends.txt");
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
                //todo hier berechne ich aus dem return die dividends
                tmp = Math.max(0, Math.sqrt(tmp) + RNG.gaussianDouble(0.002));
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
        return "Dividends";
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
        String result = "Dividends:\n";
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
        return "Dividends";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The objective is to minimze (MaxDividends - dividends) [note div. use step function (W[i] < 0.1 | d*1.0; W[i] < 0.6 | d*1.2; else | d*1.5.";
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
        return "Select the portfolio problem by choosing the input file for the dividends.";
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
        return "Choose a min value as a constraint.";
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