package eva2.server.go.problems;

import java.util.BitSet;

import eva2.gui.Plot;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualBinaryData;
import eva2.server.go.problems.B1Problem;
import eva2.server.go.strategies.InterfaceOptimizer;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 06.06.2003
 * Time: 13:53:10
 * To change this template use Options | File Templates.
 */
public class BLOTZ extends B1Problem implements java.io.Serializable {

    private Plot m_Plot;
    transient private boolean m_Show = false;

    public BLOTZ() {
        this.m_Template = new GAIndividualBinaryData();
    }

    public BLOTZ(BLOTZ b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        this.m_Show = b.m_Show;
        //BMinimizeBits
        this.m_ProblemDimension = b.m_ProblemDimension;
        // BLOTZ
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new BLOTZ(this);
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
        double[] result = new double[2];
        int tmpR1 = 0, tmpR2 = 0;

        tmpR1 = l;
        for (int j = 0; j < l; j++) {
            if (b.get(j)) tmpR1--;
            else break;
        }
        tmpR2 = l;
        for (int j = l; j > 0; j--) {
            if (!b.get(j - 1)) tmpR2--;
            else break;
        }
        result[0] = tmpR1;
        result[1] = tmpR2;
        return result;
    }

    /**
     * This method evaluate a single individual and sets the fitness values
     *
     * @param individual The individual that is to be evalutated
     */
    public void evaluate(AbstractEAIndividual individual) {
        GAIndividualBinaryData tmpIndy;
        BitSet tmpBitSet;
        double fitness = 0, tmpR1, tmpR2;

        tmpIndy = (GAIndividualBinaryData) individual;

        tmpBitSet = tmpIndy.getBinaryData();
        fitness = 0;
        tmpR1 = tmpIndy.getGenotypeLength();
        for (int j = 0; j < tmpIndy.getGenotypeLength(); j++) {
            if (tmpBitSet.get(j)) tmpR1--;
            else break;
        }
        tmpR2 = tmpIndy.getGenotypeLength();
        for (int j = tmpIndy.getGenotypeLength(); j > 0; j--) {
            if (!tmpBitSet.get(j - 1)) tmpR2--;
            else break;
        }
        tmpIndy.SetFitness(0, tmpR1);
        tmpIndy.SetFitness(1, tmpR2);

        if (this.m_Show) {
            if (this.m_Plot == null) this.initProblemFrame();
            this.m_Plot.setUnconnectedPoint(tmpR1, tmpR2, 0);
            this.m_Plot.setUnconnectedPoint(0, 0, 0);
        }
    }

    public void initProblemFrame() {
        double[] tmpD = new double[2];
        tmpD[0] = 0;
        tmpD[1] = 0;
        //test
        m_Plot = new Plot("LOTZ", "Leading Ones", "Trailing Zeros", tmpD, tmpD);
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
        String result = "Leading Ones Trailing Zeros problem:\n";
        result += individual.getStringRepresentation() + "\n";
        result += "Has a fitness of " + (((GAIndividualBinaryData) individual).size() - individual.getFitness(0));
        return result;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "Leading Ones Trailing Zeros:\n";
        result += "The task in this problem is to maximize the number of leading ones and/or trailing zeros in the given bit string.\n";
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
        return "Leading Ones Trailing Zeros";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The task in this problem is to maximize the number of leading ones and/or trailing zeros.";
    }

    /**
     * This method allows you to toggle path visualisation on and off.
     *
     * @param b True if the path is to be shown.
     */
    public void setShowPath(boolean b) {
        this.m_Show = b;
        if (this.m_Show) this.initProblemFrame();
        else if (this.m_Plot != null) {
            this.m_Plot.dispose();
            this.m_Plot = null;
        }
    }

    public boolean getShowPath() {
        return this.m_Show;
    }

    public String showPathTipText() {
        return "Toggles the path visualisation.";
    }
}
