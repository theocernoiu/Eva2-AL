package eva2.server.go.problems;


import java.util.BitSet;
import java.util.ArrayList;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualBinaryData;
import eva2.server.go.individuals.InterfaceDataTypeBinary;
import eva2.server.go.operators.constraint.InterfaceConstraint;
import eva2.server.go.operators.moso.InterfaceMOSOConverter;
import eva2.server.go.operators.paretofrontmetrics.InterfaceParetoFrontMetric;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiObjectiveOptimizationProblem;
import eva2.server.go.strategies.InterfaceOptimizer;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 10.05.2004
 * Time: 18:06:30
 * To change this template use File | Settings | File Templates.
 */
public class TF5Problem extends AbstractMultiObjectiveOptimizationProblem implements java.io.Serializable {

    protected int m_ProblemDimension = 11;

    public TF5Problem() {
        super(1.);
    }

    public TF5Problem(TF5Problem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        //AbstractMultiObjectiveOptimizationProblem
        if (b.m_MOSOConverter != null)
            this.m_MOSOConverter = (InterfaceMOSOConverter) b.m_MOSOConverter.clone();
        if (b.m_Metric != null)
            this.m_Metric = (InterfaceParetoFrontMetric) b.m_Metric.clone();
        if (b.m_ParetoFront != null)
            this.m_ParetoFront = (Population) b.m_ParetoFront.clone();
        if (b.m_Border != null) {
            this.m_Border = new double[b.m_Border.length][2];
            for (int i = 0; i < this.m_Border.length; i++) {
                this.m_Border[i][0] = b.m_Border[i][0];
                this.m_Border[i][1] = b.m_Border[i][1];
            }
        }
        if (b.m_AreaConst4Parallelization != null) {
            this.m_AreaConst4Parallelization = new ArrayList();
            for (int i = 0; i < b.m_AreaConst4Parallelization.size(); i++) {
                this.m_AreaConst4Parallelization.add(((InterfaceConstraint) b.m_AreaConst4Parallelization.get(i)).clone());
            }
        }
        // TB5Problem
        this.m_ProblemDimension = b.m_ProblemDimension;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new TF5Problem(this);
    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        // nothing to do here
    }

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        int dim = 30 + (this.m_ProblemDimension - 1) * 5;
        ((InterfaceDataTypeBinary) this.m_Template).setBinaryDataLength(dim);

        AbstractOptimizationProblem.defaultInitPopulation(population, m_Template, this);
    }

    /**
     * This method evaluate a single individual and sets the fitness values
     *
     * @param individual The individual that is to be evalutated
     */
    public void evaluate(AbstractEAIndividual individual) {
        BitSet tmpBitSet;
        double[] result;
        InterfaceDataTypeBinary tmpIndy;

        // collect the data
        tmpIndy = (InterfaceDataTypeBinary) individual;
        tmpBitSet = tmpIndy.getBinaryData();
        // evalutate the fitness
        result = this.evaluate(tmpBitSet, tmpIndy.size());
        // set the fitness
        individual.SetFitness(result);
        individual.checkAreaConst4Parallelization(this.m_AreaConst4Parallelization);
    }

    /**
     * This is a simple method that evaluates a given Individual. The fitness
     * values of the individual will be set inside this method.
     *
     * @param b The BitSet that is to be evaluated.
     * @param l The length of the BitSet.
     * @return Double[]
     */
    public double[] evaluate(BitSet b, int l) {
        double[] result = new double[2];
        int[] u = new int[this.m_ProblemDimension];
        double g;

        for (int i = 0; i < 30; i++) if (b.get(i)) u[0]++;
        for (int i = 1; i < this.m_ProblemDimension; i++) {
            for (int j = 0; j < 5; j++) {
                if (b.get(30 + (i - 1) * 5 + j)) u[i]++;
            }
        }

        result[0] = 1 + u[0];
        g = this.g(u);
        result[1] = g * this.h(result[0], g);

        return result;
    }

    /**
     * The g function
     *
     * @param u The decision variables.
     * @return Objective variable.
     */
    private double g(int[] u) {
        double result = 0;

        for (int i = 1; i < u.length; i++) {
            if (u[i] < 5) result += 2 + u[i];
            if (u[i] == 5) result += 1;
        }

        return result;
    }

    /**
     * The h function
     *
     * @param x The decision variables.
     * @return Objective variable.
     */
    private double h(double x, double y) {
        double result = 0;

        result = 1 / x;
        return result;
    }

    /**
     * This method calculates the number of true bits in a given bitset of
     * length l
     *
     * @param b The BitSet
     * @param l The length of the BitSet
     * @return The number of true bits.
     */
    public int getNumberOfTrueBits(BitSet b, int l) {
        int result = 0;

        for (int i = 0; i < l; i++) if (b.get(i)) l++;

        return result;
    }

//    /** This method will give a String for a given BitSet
//     * @param b     The BitSet.
//     * @param l     The length that is to be printed.
//     * @return string
//     */
//    public String getStringRepresentation(BitSet b, int l) {
//        String result = "{";
//        for (int i = 0; i < l; i++) {
//            if (b.get(i)) result += "1";
//            else result += "0";
//        }
//        result += "}";
//        return result;
//    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "The Multiobjective binary F5 Problem:\n";
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
        return "T5 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "T5(x) = x is to be minimized.";
    }

    /**
     * This method allows you to set the number of mulitruns that are to be performed,
     * necessary for stochastic optimizers to ensure reliable results.
     *
     * @param multiruns The number of multiruns that are to be performed
     */
    public void setProblemDimension(int multiruns) {
        this.m_ProblemDimension = multiruns;
    }

    public int getProblemDimension() {
        return this.m_ProblemDimension;
    }

    public String multiRunsTipText() {
        return "Length of the BitSet that is to be optimized.";
    }

    /**
     * This method allows you to choose the EA individual
     *
     * @param indy The EAIndividual type
     */
    public void setIndividualTemplate(InterfaceDataTypeBinary indy) {
        this.m_Template = (AbstractEAIndividual) indy;
    }
}