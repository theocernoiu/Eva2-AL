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
public class ObjectivePortfolioDuration implements InterfaceOptimizationObjective, InterfacePortfolioSelectionObjective, java.io.Serializable {

    private boolean m_NormalizeTarget = false;
    private double[] m_Duration;
    private PropertyFilePath m_InputFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/Port1_Duration.txt");
    private double m_MaxValue = Double.NEGATIVE_INFINITY;
    private SelectedTag m_ObjectiveType;

    public ObjectivePortfolioDuration() {
        Tag[] tag = new Tag[3];
        tag[0] = new Tag(0, "Objective");
        tag[1] = new Tag(1, "Objective + Constraint");
        tag[2] = new Tag(2, "Constraint");
        this.m_ObjectiveType = new SelectedTag(0, tag);
        this.loadData();
    }

    public ObjectivePortfolioDuration(ObjectivePortfolioDuration p) {
        this.m_MaxValue = p.m_MaxValue;
        this.m_NormalizeTarget = p.m_NormalizeTarget;
        if (p.m_Duration != null) {
            this.m_Duration = new double[p.m_Duration.length];
            System.arraycopy(p.m_Duration, 0, this.m_Duration, 0, p.m_Duration.length);
        }
        if (p.m_ObjectiveType != null) {
            this.m_ObjectiveType = (SelectedTag) p.m_ObjectiveType.clone();
        }
    }

    public Object clone() {
        return (Object) new ObjectivePortfolioDuration(this);
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
                result += w[i] * this.m_Duration[i];
            } else {
                if (w[i] < 0.5) {
                    result += w[i] * (this.m_Duration[i] * 1.2);
                } else {
                    result += w[i] * (this.m_Duration[i] * 1.5);
                }
            }
        }

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
        double[] tmpD = new double[2];
        tmpD[0] = Double.MAX_VALUE;
        tmpD[1] = -Double.MAX_VALUE;

        for (int i = 0; i < this.m_Duration.length; i++) {
            if (tmpD[0] > this.m_Duration[i])
                tmpD[0] = this.m_Duration[i];
            if (tmpD[1] < this.m_Duration[i])
                tmpD[1] = this.m_Duration[i];
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
            ArrayList tmpA = new ArrayList();
            while ((currentLine = reader.readLine()) != null && currentLine.length() != 0) {
                currentLine = currentLine.trim();
                tmpA.add(new Double(currentLine));
            }
            this.m_Duration = new double[tmpA.size()];
            for (int i = 0; i < tmpA.size(); i++) {
                this.m_Duration[i] = ((Double) tmpA.get(i)).doubleValue();
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
        PropertyFilePath fileOutPath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/Port1_Duration.txt");
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
                tmp = Math.max(0.1, 2 * Math.sin(tmp) + 0.5 + RNG.gaussianDouble(1.2));
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
        return "Duration";
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
        return this.m_MaxValue;
    }

    public void SetConstraintGoal(double d) {
        this.m_MaxValue = d;
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
        String result = "Duration:\n";
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
        return "Duration";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The objective is to minimze the duration.";
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
        return "Select the portfolio problem by choosing the input file for the duration.";
    }

    /**
     * This method will allow you set a min value as constraint
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
        return "Choose a max value as a constraint.";
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