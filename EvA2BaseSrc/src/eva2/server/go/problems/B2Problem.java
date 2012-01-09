package eva2.server.go.problems;

import java.util.BitSet;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualBinaryData;
import eva2.server.go.problems.B1Problem;
import eva2.server.go.strategies.InterfaceOptimizer;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 21.03.2003
 * Time: 13:56:49
 * To change this template use Options | File Templates.
 */
public class B2Problem extends B1Problem implements java.io.Serializable {

    public B2Problem() {
        this.m_Template = new GAIndividualBinaryData();
    }

    public B2Problem(B2Problem b) {
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
        return (Object) new B2Problem(this);
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
        int fitness = 0, tmpResult = 0;

        for (int j = 0; j < l; j++) {
            if (!b.get(j)) tmpResult++;
            else {
                if (tmpResult > fitness) fitness = tmpResult;
                tmpResult = 0;
            }
        }
        if (tmpResult > fitness) fitness = tmpResult;
        fitness = l - fitness;
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
        String result = "Minimize Number of Bits in a Row problem:\n";
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

        result += "Minimize Bits in a Row Problem:\n";
        result += "The task is to maximize the number of FALSE Bits in a succesive row in the given bit string.\n";
        result += "Parameters:\n";
        result += "Number of Bits: " + this.m_ProblemDimension + "\n";
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
        return "Maximize number of bits in a row";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The task in this problem is to maximize the number of false bits in a row in a BitSet.";
    }
}
