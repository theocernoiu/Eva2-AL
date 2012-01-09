package eva2.server.go.problems;

import java.util.BitSet;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualBinaryData;
import eva2.server.go.problems.B1Problem;
import eva2.server.go.strategies.InterfaceOptimizer;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 30.06.2005
 * Time: 15:33:50
 * To change this template use File | Settings | File Templates.
 */
public class B3Problem extends B1Problem implements java.io.Serializable {

    public B3Problem() {
        this.m_Template = new GAIndividualBinaryData();
    }

    public B3Problem(B3Problem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        //BMinimizeBits
        this.m_ProblemDimension = b.m_ProblemDimension;
        // BMinimizeBitsInARow
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new B3Problem(this);
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
        double[] result = new double[1];
        int fitness = 0, tmpResult = 0, t;

        for (int k = 0; k < l - 1; k++) {
            tmpResult = 0;
            for (int i = 0; i < l - k; i++) {
                t = 1;
                if (b.get(i)) t *= 1;
                else t *= -1;
                if (b.get(i + k)) t *= 1;
                else t *= -1;
                tmpResult += t;
            }
            fitness += Math.pow(tmpResult, 2);
        }
        if (tmpResult > fitness) fitness = tmpResult;
        result[0] = fitness;
        return result;
    }

    /**
     * This method allows you to output a string that describes a found solution
     * in a way that is most suiteable for a given problem.
     *
     * @param individual The individual that is to be shown.
     * @return The description.
     */
    public String getSolutionRepresentationFor(AbstractEAIndividual individual) {
        this.evaluate(individual);
        String result = "Find low autocorrelation BitSets:\n";
        result += individual.getStringRepresentation() + "\n";
        result += "Has " + (((GAIndividualBinaryData) individual).size() - individual.getFitness(0)) + " zero bits in a row!";
        return result;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "Find low autocorrelation BitSets:\n";
        result += "Parameters:\n";
        result += "Number of Bits: " + this.m_ProblemDimension + "\n";
        //result += "Solution representation:\n";
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
        return "Low Autocorrelation Binary Sequences";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The task is to find low autocorrelation BitSets.";
    }
}