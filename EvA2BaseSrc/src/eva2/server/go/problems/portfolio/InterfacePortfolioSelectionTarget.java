package eva2.server.go.problems.portfolio;

import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.problems.TFPortfolioSelectionProblem;
import eva2.server.go.problems.TFPortfolioSelectionProblemInterface;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 17.01.2005
 * Time: 16:31:51
 * To change this template use File | Settings | File Templates.
 */
public interface InterfacePortfolioSelectionTarget {

    /**
     * This method will return the performance of a given portfolio in a given environment
     *
     * @param indy The individual, storing the portfolio in the phenotpye.
     * @param prob The portfolio selection problem
     * @return The value of the optimization target.
     */
    public double evaluatePortfolio(InterfaceDataTypeDouble indy, TFPortfolioSelectionProblemInterface prob);

    /**
     * This method will return the performance of a given portfolio in a given environment
     *
     * @param indy The individual, storing the portfolio in the phenotpye.
     * @param prob The portfolio selection problem
     * @return The value of the optimization target.
     */
    public double getPlotValue(InterfaceDataTypeDouble indy, TFPortfolioSelectionProblemInterface prob);

    /**
     * This Method returns the name for the optimization target
     *
     * @return the name
     */
    public String getName();

    /**
     * This method will return the upper and the lower bound for this objective
     *
     * @param prob The portfolio selection problem
     * @return The value of the optimization target.
     */
    public double[] getObjectiveBoundaries(TFPortfolioSelectionProblemInterface prob);

    /**
     * This method will return wether or not the objective is to be maximized
     * (which is typically solved by inversion)
     *
     * @return true
     */
    public boolean isTargetToBeMaximized();
}
