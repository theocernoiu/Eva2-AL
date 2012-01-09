package eva2.server.go.problems;

import java.util.BitSet;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualBinaryData;
import eva2.server.go.problems.B1Problem;
import eva2.server.go.problems.Interface2DBorderProblem;
import eva2.server.go.problems.InterfaceMultimodalProblem;
import eva2.server.go.strategies.InterfaceOptimizer;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 26.03.2003
 * Time: 18:18:28
 * To change this template use Options | File Templates.
 */
public class BMUnitationProblem extends B1Problem implements java.io.Serializable, InterfaceMultimodalProblem {
    private int m_NumberOfUnitation = 2;

    public BMUnitationProblem() {
        this.m_Template = new GAIndividualBinaryData();
    }

    public BMUnitationProblem(BMUnitationProblem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        //BMinimizeBits
        this.m_ProblemDimension = b.m_ProblemDimension;
        // BMUnitationProblem
        this.m_NumberOfUnitation = b.m_NumberOfUnitation;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new BMUnitationProblem(this);
    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        // nothing to init here
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
        int tmpCounter = 0;
        double tmpFitness, fitness = 0;
        int unitationLength = this.m_ProblemDimension / this.m_NumberOfUnitation;

//        System.out.println("Evaluate: " + this.getStringRepresentation(b,l));
//        System.out.println("NumberOfUnitation: " + this.m_NumberOfUnitation);
//        System.out.println("UnitationLenght  : " + unitationLength);
//        System.out.println("BitString        : " + this.getStringRepresentation(b,l));
        fitness = 0;
        for (int i = 0; i < this.m_NumberOfUnitation; i++) {
            tmpCounter = 0;
            for (int j = 0; j < unitationLength; j++) {
                if (b.get(i * unitationLength + j)) tmpCounter++;
            }
            tmpFitness = Math.abs(tmpCounter - (unitationLength / (float) 2));
            if ((tmpCounter == 0) || (tmpCounter == unitationLength)) {
                tmpFitness = 0;
            } else {
                tmpFitness += 1;
            }
            fitness += (double) tmpFitness;
        }
        result[0] = fitness;
//        System.out.println("Fitness         : " + fitness);
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
        String result = "Unitation problem:\n";
        result += individual.getStringRepresentation() + "\n";
        result += "Scores " + (individual.getFitness(0)) + " zero bits!";
        return result;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "Unitation Problem:\n";
        result += "This problem is multimodal and deceptive. The bit string is separated into " + this.m_NumberOfUnitation + " segments.\n";
        result += "Each segment contains " + this.m_ProblemDimension / this.m_NumberOfUnitation + " bits. For each segment the number of TRUE bits is determined and the fitness calculated.";
        result += "The fitness function resembles a 'W' zero bits and " + this.m_ProblemDimension / this.m_NumberOfUnitation + " yield maximal fitness. But also equal number of FALSE and ";
        result += "TRUE bits yield a relative high fitness, therfore this problem is deceptive.";
        result += "Parameters:\n";
        result += "Number of unitation segments: " + this.m_NumberOfUnitation + "\n";
        result += "Number of Bits per segment  : " + this.m_ProblemDimension / this.m_NumberOfUnitation + "\n";
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
        return "Maximize number of bits";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Currently out of order: The task in this problem is to maximize the number of false bits in a BitSet.";
    }

    /**
     * This method sets the number of unitation problems that are to be merged
     *
     * @param NumberOfUnitation The number of unitation problems
     */
    public void setNumberOfUnitation(int NumberOfUnitation) {
        this.m_NumberOfUnitation = NumberOfUnitation;
    }

    public int getNumberOfUnitation() {
        return this.m_NumberOfUnitation;
    }

    public String numberOfUnitationTipText() {
        return "Number of n-bit unitation segments.";
    }

    public String problemDimensionTipText() {
        return "The length of a single unitation problem.";
    }
}
