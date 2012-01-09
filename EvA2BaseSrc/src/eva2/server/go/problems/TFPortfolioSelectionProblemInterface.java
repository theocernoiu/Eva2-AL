package eva2.server.go.problems;

import eva2.gui.PropertyOptimizationObjectives;
import eva2.server.go.populations.Population;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 18.02.2005
 * Time: 17:10:10
 * To change this template use File | Settings | File Templates.
 */
public interface TFPortfolioSelectionProblemInterface {

    /**
     * This method returns the current optimization targets
     *
     * @return The optimization targets
     */
    public PropertyOptimizationObjectives getOptimizationTargets();

    /**
     * Returns the list of asset returns
     *
     * @return
     */
    public double[] getAssetReturn();

    /**
     * Returns the list of asset risks
     *
     * @return
     */
    public double[] getAssetRisk();

    /**
     * Returns the matrix of asset correlations
     *
     * @return
     */
    public double[][] getAssetCorrelation();

    /**
     * Get the local pareto-front
     *
     * @return
     */
    public Population getLocalParetoFront();
}
