package eva2.server.go.problems;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.problems.F1Problem;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 20.01.2005
 * Time: 10:31:14
 * To change this template use File | Settings | File Templates.
 */
public class FUserProblem extends AbstractProblemDoubleOffset implements java.io.Serializable {

    public FUserProblem() {
        this.m_Template = new ESIndividualDoubleData();
    }

    public FUserProblem(FUserProblem b) {
        super(b);
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FUserProblem(this);
    }

    /**
     * Ths method allows you to evaluate a double[] to determine the fitness
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] eval(double[] x) {
        x = rotateMaybe(x);
//****************************************************************************************************
//      Please limit to editing this section, otherwise it gets really complicated ;-)
        double[] result = new double[1];
        result[0] = 0;
        for (int i = 0; i < x.length - 1; i++) {
            result[0] += x[i];
        }
        result[0] += 1;
//****************************************************************************************************
        return result;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @return The description.
     */
    public String getStringRepresentationForProblem() {
        String result = "";

        result += "User Problem:\n";
        result += "Parameters:\n";
        result += "Dimension   : " + this.m_ProblemDimension + "\n";
        result += "Noise level : " + this.getNoise() + "\n";
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
        return "User Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "This is a user defined problem, in case it doesn't work: please remove FUserProblem.class.";
    }

}
