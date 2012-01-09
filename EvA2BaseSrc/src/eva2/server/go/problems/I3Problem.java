package eva2.server.go.problems;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualIntegerData;
import eva2.server.go.individuals.InterfaceDataTypeInteger;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractOptimizationProblem;
import eva2.server.go.strategies.InterfaceOptimizer;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 19.05.2005
 * Time: 12:35:18
 * To change this template use File | Settings | File Templates.
 */
public class I3Problem extends AbstractOptimizationProblem implements java.io.Serializable {

    protected AbstractEAIndividual m_OverallBest = null;
    protected int m_ProblemDimension = 4;
    protected int m_MaxStep = 50;
    protected int m_Range = 10;

    public I3Problem() {
        this.m_Template = new GAIndividualIntegerData();
    }

    public I3Problem(I3Problem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        if (b.m_OverallBest != null)
            this.m_OverallBest = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_OverallBest).clone();
        //I1Problem
        this.m_ProblemDimension = b.m_ProblemDimension;
        this.m_MaxStep = b.m_MaxStep;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new I3Problem(this);
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
        this.m_OverallBest = null;
        int[][] range = new int[this.m_ProblemDimension][2];
        for (int i = 0; i < range.length; i++) {
            range[i][0] = 0;
            range[i][1] = this.m_Range;
        }
        ((InterfaceDataTypeInteger) this.m_Template).setIntegerDataLength(this.m_ProblemDimension);
        ((InterfaceDataTypeInteger) this.m_Template).SetIntRange(range);

        AbstractOptimizationProblem.defaultInitPopulation(population, m_Template, this);
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
            //System.out.println(""+individual.getStringRepresentation());
            this.m_OverallBest = (AbstractEAIndividual) individual.clone();
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
        int[] current = new int[x.length];
        int[] next = new int[x.length];
        System.arraycopy(x, 0, current, 0, x.length);
        this.nextStep(current, next);
        while ((result[0] < this.m_MaxStep) && !(this.isSame(current, next))) {
            result[0]++;
            System.arraycopy(next, 0, current, 0, next.length);
            this.nextStep(current, next);
        }
        result[0] = this.m_MaxStep - result[0];
        return result;
    }

    private void nextStep(int[] cur, int[] next) {
        for (int i = 0; i < cur.length - 1; i++) {
            next[i] = Math.abs(cur[i] - cur[i + 1]);
        }
        next[next.length - 1] = Math.abs(cur[cur.length - 1] - cur[0]);
    }

    private boolean isSame(int[] cur, int[] next) {
        for (int i = 0; i < cur.length; i++) {
            if (cur[i] != next[i]) return false;
        }
        return true;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "I3 Problem:\n";
        result += "Here the individual codes a vector to climb the Gran Tribonacci.\n";
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
        return "I3 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Trying to climb the Gran Tribonacci.";
    }

    /**
     * This method allows you to set the problem dimension.
     *
     * @param n The problem dimension
     */
    public void setProblemDimension(int n) {
        this.m_ProblemDimension = n;
    }

    public int getProblemDimension() {
        return this.m_ProblemDimension;
    }

    public String problemDimensionTipText() {
        return "Length of the x vector at is to be optimized.";
    }

    /**
     * This method allows you to set the max number of steps.
     *
     * @param n The max numbers of steps
     */
    public void setMaxStep(int n) {
        this.m_MaxStep = n;
    }

    public int getMaxStep() {
        return this.m_MaxStep;
    }

    public String maxStepTipText() {
        return "The maximum number of steps allowed, isn't that optimistic.";
    }

    /**
     * This method allows you to set the max range.
     *
     * @param n The max range
     */
    public void setRange(int n) {
        this.m_Range = n;
    }

    public int getRange() {
        return this.m_Range;
    }

    public String rangeTipText() {
        return "Gives the range for the variables [0 - range].";
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
}