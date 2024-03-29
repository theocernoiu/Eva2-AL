package eva2.server.go.operators.crossover;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.InterfaceESIndividual;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.InterfaceOptimizationProblem;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 03.12.2003
 * Time: 14:27:59
 * To change this template use Options | File Templates.
 */
public class CrossoverESIntermediate implements InterfaceCrossover, java.io.Serializable {
    private InterfaceOptimizationProblem m_OptimizationProblem;

    public CrossoverESIntermediate() {

    }

    public CrossoverESIntermediate(CrossoverESIntermediate c) {
        this.m_OptimizationProblem = c.m_OptimizationProblem;
    }

    /**
     * This method will enable you to clone a given mutation operator
     *
     * @return The clone
     */
    public Object clone() {
        return new CrossoverESIntermediate(this);
    }

    /**
     * This method performs crossover on two individuals. If the individuals do
     * not implement InterfaceGAIndividual, then nothing will happen.
     *
     * @param indy1    The first individual
     * @param partners The second individual
     */
    public AbstractEAIndividual[] mate(AbstractEAIndividual indy1, Population partners) {
        AbstractEAIndividual[] result = null;
        double[][] parents, children;

        result = new AbstractEAIndividual[partners.size() + 1];
        result[0] = (AbstractEAIndividual) (indy1).clone();
        for (int i = 0; i < partners.size(); i++) {
            result[i + 1] = (AbstractEAIndividual) ((AbstractEAIndividual) partners.get(i)).clone();
        }
        //for (int i = 0; i < result.length; i++) System.out.println("Before Crossover: " +result[i].getSolutionRepresentationFor());

        if ((indy1 instanceof InterfaceESIndividual) && (partners.get(0) instanceof InterfaceESIndividual)) {
            double intermediate;
            parents = new double[partners.size() + 1][];
            children = new double[partners.size() + 1][];
            for (int i = 0; i < result.length; i++) {
                parents[i] = new double[((InterfaceESIndividual) result[i]).getDGenotype().length];
                children[i] = new double[parents[i].length];
                System.arraycopy(((InterfaceESIndividual) result[i]).getDGenotype(), 0, parents[i], 0, parents[i].length);
                System.arraycopy(((InterfaceESIndividual) result[i]).getDGenotype(), 0, children[i], 0, parents[i].length);
            }

            for (int i = 0; i < children[0].length; i++) {
                intermediate = 0;
                for (int j = 0; j < parents.length; j++) intermediate += parents[j][i];
                intermediate = intermediate / parents.length;
                for (int j = 0; j < children.length; j++) children[j][i] = intermediate;
            }
            // write the result back
            for (int i = 0; i < result.length; i++) ((InterfaceESIndividual) result[i]).SetDGenotype(children[i]);
        }
        //in case the crossover was successfull lets give the mutation operators a chance to mate the strategy parameters
        for (int i = 0; i < result.length; i++)
            result[i].getMutationOperator().crossoverOnStrategyParameters(indy1, partners);
        //for (int i = 0; i < result.length; i++) System.out.println("After Crossover: " +result[i].getSolutionRepresentationFor());
        return result;
    }

    /**
     * This method allows you to evaluate wether two crossover operators
     * are actually the same.
     *
     * @param crossover The other crossover operator
     */
    public boolean equals(Object crossover) {
        if (crossover instanceof CrossoverESIntermediate) return true;
        else return false;
    }

    /**
     * This method will allow the crossover operator to be initialized depending on the
     * individual and the optimization problem. The optimization problem is to be stored
     * since it is to be called during crossover to calculate the exogene parameters for
     * the offsprings.
     *
     * @param individual The individual that will be mutated.
     * @param opt        The optimization problem.
     */
    public void init(AbstractEAIndividual individual, InterfaceOptimizationProblem opt) {
        this.m_OptimizationProblem = opt;
    }

    public String getStringRepresentation() {
        return this.getName();
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
        return "ES intermediate crossover";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "This is an intermediate crossover between m ES individuals.";
    }
}
