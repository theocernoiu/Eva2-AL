package eva2.server.go.problems;


import javax.swing.*;

import eva2.gui.JEFrame;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GIOBGAIndividualIntegerPermutationData;
import eva2.server.go.individuals.InterfaceDataTypeInteger;
import eva2.server.go.individuals.InterfaceDataTypePermutation;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractOptimizationProblem;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.math.RNG;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 13.05.2004
 * Time: 12:48:15
 * To change this template use File | Settings | File Templates.
 */
public class ITSP1Problem extends AbstractOptimizationProblem implements java.io.Serializable {

    protected AbstractEAIndividual m_OverallBest = null;
    protected int m_ProblemDimension = 4;
    protected int m_TargetElements = 8;
    protected int m_LengthPermutation = 8;
    protected double m_Punishment = 100;
    protected boolean m_AlternatingSign = true;

    public ITSP1Problem() {
        this.m_Template = new GIOBGAIndividualIntegerPermutationData();
    }

    public ITSP1Problem(ITSP1Problem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        if (b.m_OverallBest != null)
            this.m_OverallBest = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_OverallBest).clone();
        //ITSP1Problem
        this.m_ProblemDimension = b.m_ProblemDimension;
        this.m_TargetElements = b.m_TargetElements;
        this.m_LengthPermutation = b.m_LengthPermutation;
        this.m_Punishment = b.m_Punishment;
        this.m_AlternatingSign = b.m_AlternatingSign;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new ITSP1Problem(this);
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
            range[i][0] = 1;
            range[i][1] = this.m_LengthPermutation - 1;
        }
        ((InterfaceDataTypeInteger) this.m_Template).SetIntRange(range);

        int[] permRange = new int[this.m_ProblemDimension];
        int[] firstIndex = new int[this.m_ProblemDimension];
        for (int i = 0; i < permRange.length; i++) {
            permRange[i] = this.m_LengthPermutation;
            firstIndex[i] = 1;
        }
        ((InterfaceDataTypePermutation) this.m_Template).setPermutationDataLength(permRange);
        ((InterfaceDataTypePermutation) this.m_Template).setFirstindex(firstIndex);
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
        int[][] perm;
        double[] fitness;

        fitness = new double[1];
        fitness[0] = 0;

        x = new int[((InterfaceDataTypeInteger) individual).getIntegerData().length];
        System.arraycopy(((InterfaceDataTypeInteger) individual).getIntegerData(), 0, x, 0, x.length);
        perm = ((InterfaceDataTypePermutation) individual).getPermutationData();

        for (int i = 0; i < x.length; i++) {
            fitness[0] += x[i];
        }
        fitness[0] = this.m_Punishment * Math.abs(fitness[0] - this.m_TargetElements);

        int p;
        for (int i = 0; i < x.length; i += 2) {
            p = i + 1;
            for (int j = 0; j < x[i]; j++) {
                fitness[0] += Math.pow(perm[i][j], p);
            }
            if (i + 1 < x.length) {
                for (int j = 0; j < x[i + 1]; j++) {
                    if (this.m_AlternatingSign) {
                        fitness[0] -= Math.pow(perm[i + 1][j], p) - Math.pow(this.m_LengthPermutation, p);
                    } else {
                        fitness[0] += Math.pow(perm[i + 1][j], p);
                    }
                }
            }
        }
        for (int i = 0; i < fitness.length; i++) {
            // set the fitness of the individual
            individual.SetFitness(i, fitness[i]);
        }
        //this.showIndividual(individual);

        if ((this.m_OverallBest == null) || (this.m_OverallBest.getFitness(0) > individual.getFitness(0))) {
            this.m_OverallBest = (AbstractEAIndividual) individual.clone();
        }
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

    public String getSolutionRepresentationFor(AbstractEAIndividual indy) {
        String result = "";
        int[] it = ((InterfaceDataTypeInteger) indy).getIntegerData();
        int[][] perm = ((InterfaceDataTypePermutation) indy).getPermutationData();
        result += "Full individual: \n";
        String tmp = "{";
        for (int i = 0; i < it.length; i++) {
            tmp += it[i];
            if (i < it.length - 1) tmp += "; ";
        }
        tmp += "}";
        result += "  Integer vector: " + tmp + "\n";
        for (int j = 0; j < perm.length; j++) {
            tmp = "{";
            for (int i = 0; i < perm[j].length; i++) {
                tmp += perm[j][i];
                if (i < perm[j].length - 1) tmp += "; ";
            }
            tmp += "}";
            result += "  Permutation " + j + ": " + tmp + "\n";
        }
        result += "This results in a selection of:\n";
        tmp = "{";
        for (int i = 0; i < it.length; i++) {
            for (int j = 0; j < it[i]; j++) {
                tmp += perm[i][j];
                if (j < it[i] - 1) tmp += "; ";
            }
            if (i < it.length - 1) tmp += "/ ";
        }
        tmp += "}";
        result += "  " + tmp + "\n";
        result += "Resulting in fitness " + indy.getFitness(0) + "\n";
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
        return "ITSP1 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Darn this is complicated, select elements from a list to mininze f=Sum(x1)-Sum(x2)+sum(x3+x3)..., while fitting a target number of elements.";
    }

    /**
     * This method allows you to set the problem dimension.
     *
     * @param n The problem dimension
     */
    public void setProblemDimension(int n) {
        this.m_ProblemDimension = Math.max(2, n);
    }

    public int getProblemDimension() {
        return this.m_ProblemDimension;
    }

    public String problemDimensionTipText() {
        return "The number of lists to select from (>2).";
    }

    /**
     * This method allows you to set the problem dimension.
     *
     * @param n The problem dimension
     */
    public void setTargetElements(int n) {
        this.m_TargetElements = n;
    }

    public int getTargetElements() {
        return this.m_TargetElements;
    }

    public String targetElementsTipText() {
        return "Target number of elements to select.";
    }

    /**
     * This method allows you to set the problem dimension.
     *
     * @param n The problem dimension
     */
    public void setLengthPermutation(int n) {
        this.m_LengthPermutation = n;
    }

    public int getLengthPermutation() {
        return this.m_LengthPermutation;
    }

    public String lengthPermutationTipText() {
        return "Number of elements per list to select from.";
    }

    /**
     * This method allows you to set the problem dimension.
     *
     * @param n The problem dimension
     */
    public void setPunishment(double n) {
        this.m_Punishment = Math.max(0, n);
    }

    public double getPunishment() {
        return this.m_Punishment;
    }

    public String punishmentTipText() {
        return "Choose the punishment for disobeying target elements.";
    }

    /**
     * This method allows you to set the problem dimension.
     *
     * @param n The problem dimension
     */
    public void setAlternatingSign(boolean n) {
        this.m_AlternatingSign = n;
    }

    public boolean getAlternatingSign() {
        return this.m_AlternatingSign;
    }

    public String alternatingSignTipText() {
        return "Toggel the use of alternating signs for the fitness function.";
    }

    /**
     * This method allows you to choose the EA individual
     *
     * @param indy The EAIndividual type
     */
    public void setEAIndividual(GIOBGAIndividualIntegerPermutationData indy) {
        this.m_Template = (AbstractEAIndividual) indy;
    }

    public GIOBGAIndividualIntegerPermutationData getEAIndividual() {
        return (GIOBGAIndividualIntegerPermutationData) this.m_Template;
    }
}