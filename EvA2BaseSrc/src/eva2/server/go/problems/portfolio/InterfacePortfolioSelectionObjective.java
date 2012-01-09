package eva2.server.go.problems.portfolio;

import eva2.server.go.individuals.InterfaceDataTypeDouble;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 07.04.2005
 * Time: 13:00:08
 * To change this template use File | Settings | File Templates.
 */
public interface InterfacePortfolioSelectionObjective {

    /**
     * This method allows you to clone the object
     */
    public Object clone();

    /**
     * This method will return the performance of a given portfolio in a given environment
     *
     * @param indy The individual, storing the portfolio in the phenotpye.
     * @return The value of the optimization target.
     */
    public double evaluatePortfolio(InterfaceDataTypeDouble indy);

    /**
     * This method will return the upper and the lower bound for this objective
     *
     * @return The value of the optimization target.
     */
    public double[] getObjectiveBoundaries();

    /**
     * This method allows you to set a the name of the input file that is to be used
     *
     * @param filename The primer for the inputfile.
     */
    public void setInputFileName(String filename);

    /**
     * This method returns the name for the optimization target
     *
     * @return the name
     */
    public String getName();

    /**
     * This method returns a description of the objective
     *
     * @return A String
     */
    public String getStringRepresentation();
}