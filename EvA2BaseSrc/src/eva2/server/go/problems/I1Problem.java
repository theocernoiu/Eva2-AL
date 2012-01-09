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
 * Date: 13.05.2004
 * Time: 12:48:15
 * To change this template use File | Settings | File Templates.
 */
public class I1Problem extends AbstractOptimizationProblem implements java.io.Serializable {

    protected AbstractEAIndividual m_OverallBest = null;
    protected int m_ProblemDimension = 10;

    public I1Problem() {
        this.m_Template = new GAIndividualIntegerData();
    }

    public I1Problem(I1Problem b) {
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
        return (Object) new I1Problem(this);
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
        ((InterfaceDataTypeInteger) this.m_Template).setIntegerDataLength(this.m_ProblemDimension);
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
        for (int i = 0; i < x.length; i++) {
            result[0] += Math.pow(x[i], 2);
        }
        result[0] += 1;
        return result;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "I1 Problem:\n";
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
        return "I1 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "I1(x) = x^2 is to be minimized.";
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