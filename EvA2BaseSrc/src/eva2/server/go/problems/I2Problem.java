package eva2.server.go.problems;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualIntegerData;
import eva2.server.go.individuals.GIIndividualIntegerData;
import eva2.server.go.individuals.InterfaceDataTypeInteger;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractOptimizationProblem;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.math.RNG;
import eva2.tools.EVAERROR;
import eva2.tools.SelectedTag;
import eva2.tools.Tag;


import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 18.05.2005
 * Time: 17:12:27
 * To change this template use File | Settings | File Templates.
 */

public class I2Problem extends AbstractOptimizationProblem implements java.io.Serializable {

    protected AbstractEAIndividual m_OverallBest = null;
    protected int m_ProblemLength = 10;
    protected int m_charCnt = 26;
    private SelectedTag m_ProblemDimension;
    private SelectedTag m_ProblemType;
    private String m_TargetString = "Lorem Ipsum";

    transient private boolean m_Show = true;
    transient private JFrame m_Frame;
    transient private JPanel m_Panel;

    public I2Problem() {
        this.m_Template = new GIIndividualIntegerData();
        Tag[] tag = new Tag[2];
        tag[0] = new Tag(0, "Fixed Length");
        tag[1] = new Tag(1, "Variable Length");
        this.m_ProblemDimension = new SelectedTag(0, tag);
        tag = new Tag[2];
        tag[0] = new Tag(0, "Ordinal");
        tag[1] = new Tag(1, "Nominal");
        this.m_ProblemType = new SelectedTag(0, tag);
    }

    public I2Problem(I2Problem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        if (b.m_OverallBest != null)
            this.m_OverallBest = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_OverallBest).clone();
        //I1Problem
        this.m_ProblemDimension = b.m_ProblemDimension;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new I2Problem(this);
    }

    private void initProblemFrame() {
        if (this.m_Frame == null) {
            this.m_Frame = new JFrame();
            this.m_Panel = new JPanel();
            this.m_Panel.setBorder(new LineBorder(Color.BLACK));
            this.m_Frame.getContentPane().add(this.m_Panel);
        }

    }

    private void disposeProblemFrame() {
        if (this.m_Frame != null) this.m_Frame.dispose();
        this.m_Frame = null;
    }

    private void updateProblemFrame() {
        if (this.m_Frame == null) return;
        int[] x = ((InterfaceDataTypeInteger) this.m_OverallBest).getIntegerData();
        this.m_Panel.removeAll();
        if (this.m_ProblemType.getSelectedTag().getID() == 0) this.m_Frame.setTitle("Optimizing x^2");
        else this.m_Frame.setTitle("Searching for \'" + this.m_TargetString + "\'");
        if (this.m_ProblemType.getSelectedTag().getID() == 0) {
            this.m_Panel.setLayout(new GridLayout(0, x.length));
            for (int i = 0; i < x.length; i++) this.m_Panel.add(new JLabel("" + x[i]));
        } else {
            int dist, length;
            JPanel rp;
            String test = "";
            for (int i = 0; i < x.length; i++) {
                test += convertSingleInt(x[i]);
            }
            dist = this.computeLevenshteinDistance(this.m_TargetString.toLowerCase(), test);
            length = Math.max(test.length(), this.m_TargetString.length());
            rp = new JPanel();
            rp.setMinimumSize(new Dimension(300, 50));
            rp.setPreferredSize(new Dimension(300, 50));
            rp.setLayout(new GridLayout(2, length));
            this.m_Panel.add(rp, BorderLayout.CENTER);
            this.m_Panel.add(new JLabel("Distance to target string: " + dist), BorderLayout.SOUTH);
            for (int i = 0; i < length; i++) {
                if (i < test.length()) rp.add(new JLabel("" + test.charAt(i)));
                else rp.add(new JLabel(" "));
            }
            for (int i = 0; i < length; i++) {
                if (i < this.m_TargetString.length()) rp.add(new JLabel("" + this.m_TargetString.charAt(i)));
                else rp.add(new JLabel(" "));
            }
            //System.out.println("Comparing \'"+test+"\' to \'"+this.m_TargetString+"\' resulting in " + this.computeLevenshteinDistance(this.m_TargetString.toLowerCase(), test));
        }
        this.m_Frame.pack();
        this.m_Frame.validate();
        this.m_Frame.setVisible(true);
    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        this.m_OverallBest = null;
    }

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        AbstractEAIndividual tmpIndy;

        this.m_OverallBest = null;

        population.clear();
        if (this.m_ProblemType.getSelectedTag().getID() == 1) this.m_ProblemLength = this.m_TargetString.length();
        int[][] range = new int[this.m_ProblemLength][2];
        for (int i = 0; i < range.length; i++) {
            range[i][0] = 0;
            range[i][1] = m_charCnt;
        }
        ((InterfaceDataTypeInteger) this.m_Template).setIntegerDataLength(this.m_ProblemLength);
        ((InterfaceDataTypeInteger) this.m_Template).SetIntRange(range);

        for (int i = 0; i < population.getTargetSize(); i++) {
            tmpIndy = (AbstractEAIndividual) ((AbstractEAIndividual) this.m_Template).clone();
            if (this.m_ProblemDimension.getSelectedTag().getID() == 1) {
                ((InterfaceDataTypeInteger) tmpIndy).setIntegerDataLength(RNG.randomInt(1, this.m_ProblemLength * 2));
            }
            tmpIndy.init(this);
            population.add(tmpIndy);
        }
        // population init must be last
        // it set's fitcalls and generation to zero
        population.init();
    }

    /**
     * This method evaluate a single individual and sets the fitness values
     *
     * @param individual The individual that is to be evalutated
     */
    public void evaluate(AbstractEAIndividual individual) {
        int[] x;
        double[] fitness;


        x = new int[((InterfaceDataTypeInteger) individual).getIntegerData().length];
        System.arraycopy(((InterfaceDataTypeInteger) individual).getIntegerData(), 0, x, 0, x.length);

        fitness = this.doEvaluation(x);
        for (int i = 0; i < fitness.length; i++) {
            // set the fitness of the individual
            individual.SetFitness(i, fitness[i]);
        }
        if ((this.m_OverallBest == null) || (this.m_OverallBest.getFitness(0) > individual.getFitness(0))) {
            this.m_OverallBest = (AbstractEAIndividual) individual.clone();
            if (this.m_Show) this.updateProblemFrame();
        }
    }

    /**
     * Ths method allows you to evaluate a simple bit string to determine the fitness
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] doEvaluation(int[] x) {
        double[] result = new double[1];
        result[0] = 0;
        if (this.m_ProblemType.getSelectedTag().getID() == 0) {
            // ordinal
            for (int i = 0; i < x.length; i++) {
                result[0] += Math.pow(x[i], 2);
            }
            if (this.m_ProblemDimension.getSelectedTag().getID() == 1) {
                result[0] = result[0] * (1 + Math.abs(this.m_ProblemLength - x.length)) + Math.pow(1 + Math.abs(this.m_ProblemLength - x.length), 3);
            }
            result[0] += 1;
        } else {
            // nominal
            String test = "";
            for (int i = 0; i < x.length; i++) {
                test += convertSingleInt(x[i]);
            }
            //System.out.println("Comparing \'"+test+"\' to \'"+this.m_TargetString+"\' resulting in " + this.computeLevenshteinDistance(this.m_TargetString.toLowerCase(), test));
            result[0] = this.computeLevenshteinDistance(this.m_TargetString.toLowerCase(), test) + 0.1;
        }

        return result;
    }

    private char convertSingleInt(int i) {
        if (i < 0 || (i > m_charCnt)) {
            EVAERROR.errorMsgOnce("Error, invalid encoding in I2Problem, integer out of range: " + i);
            i = Math.abs(i % (m_charCnt + 1));
        }
        if (i == 0) return 'a';
        if (i == 1) return 'b';
        if (i == 2) return 'c';
        if (i == 3) return 'd';
        if (i == 4) return 'e';
        if (i == 5) return 'f';
        if (i == 6) return 'g';
        if (i == 7) return 'h';
        if (i == 8) return 'i';
        if (i == 9) return 'j';
        if (i == 10) return 'k';
        if (i == 11) return 'l';
        if (i == 12) return 'm';
        if (i == 13) return 'n';
        if (i == 14) return 'o';
        if (i == 15) return 'p';
        if (i == 16) return 'q';
        if (i == 17) return 'r';
        if (i == 18) return 's';
        if (i == 19) return 't';
        if (i == 20) return 'u';
        if (i == 21) return 'v';
        if (i == 22) return 'w';
        if (i == 23) return 'x';
        if (i == 24) return 'y';
        if (i == 25) return 'z';
        return ' ';
    }

    private int Minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    private int computeLevenshteinDistance(String s, String t) {
        int d[][]; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s
        char t_j; // jth character of t
        int cost; // cost

        // Step 1
        n = s.length();
        m = t.length();
        if (n == 0) return m;
        if (m == 0) return n;
        d = new int[n + 1][m + 1];

        // Step 2
        for (i = 0; i <= n; i++) d[i][0] = i;
        for (j = 0; j <= m; j++) d[0][j] = j;

        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);
            // Step 4
            for (j = 1; j <= m; j++) {
                t_j = t.charAt(j - 1);
                // Step 5
                if (s_i == t_j) cost = 0;
                else {
                    // @todo
                    cost = 1;
                }
                // Step 6
                d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }
        // Step 7
        return d[n][m];
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "I2 Problem:\n";
        result += "Here the individual codes a vector of int numbers x and F1(x)= x^2 is to be minimized.\n";
        result += "Parameters:\n";
        result += "Dimension   : " + this.m_ProblemDimension + "\n";
        result += "Solution representation:\n";
        //result += this.m_Template.getSolutionRepresentationFor();
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
        return "I2 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The I2 Problem is of fixed/variable size and of ordinal/nominal type.";
    }

    /**
     * This method allows you to set the number of mulitruns that are to be performed,
     * necessary for stochastic optimizers to ensure reliable results.
     *
     * @param n Either the dimension of the problem or the target length
     */
    public void setProblemLength(int n) {
        this.m_ProblemLength = n;
    }

    public int getProblemLength() {
        return this.m_ProblemLength;
    }

    public String problemLengthTipText() {
        return "Either the dimension of the problem or the target length.";
    }

    /**
     * This method allows you to choose between fixed length or
     * dynamic length problem
     *
     * @param n
     */
    public void setProblemDimension(SelectedTag n) {
        this.m_ProblemDimension = n;
    }

    public SelectedTag getProblemDimension() {
        return this.m_ProblemDimension;
    }

    public String problemDimensionTipText() {
        return "Toggel between a fixed or dynamic length optimization problem.";
    }

    /**
     * This method allows you to choose between ordinal or nominal
     * optimization problem
     *
     * @param n
     */
    public void setProblemType(SelectedTag n) {
        this.m_ProblemType = n;
    }

    public SelectedTag getProblemType() {
        return this.m_ProblemType;
    }

    public String problemTypeTipText() {
        return "Toggel between an ordinal or nominal optimization problem.";
    }

    /**
     * This method allows you to choose between ordinal or nominal
     * optimization problem
     *
     * @param n
     */
    public void setTargetString(String n) {
        this.m_TargetString = n;
        if (this.m_ProblemType.getSelectedTag().getID() == 1) this.m_ProblemLength = this.m_TargetString.length();
    }

    public String getTargetString() {
        return this.m_TargetString;
    }

    public String targetStringTipText() {
        return "Choose a target string for nominal optimizatio, but limit to letters and spaces.";
    }

    /**
     * This method allows you to toggle path visualisation on and off.
     *
     * @param b True if the path is to be shown.
     */
    public void setShow(boolean b) {
        this.m_Show = b;
        if (this.m_Show) this.initProblemFrame();
        else this.disposeProblemFrame();
    }

    public boolean getShow() {
        return this.m_Show;
    }

    public String showTipText() {
        return "Toggles the nominal target visualisation.";
    }

    /**
     * This method allows you to choose the EA individual
     *
     * @param indy The EAIndividual type
     */
    public void setEAIndividual(InterfaceDataTypeInteger indy) {
        this.m_Template = (AbstractEAIndividual) indy;
    }

    public InterfaceDataTypeInteger getEAIndividual() {
        return (InterfaceDataTypeInteger) this.m_Template;
    }

    public String[] getGOEPropertyUpdateLinks() {
        return new String[]{"targetString", "problemLength"};
    }
}