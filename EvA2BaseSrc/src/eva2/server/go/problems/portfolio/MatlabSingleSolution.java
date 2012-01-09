package eva2.server.go.problems.portfolio;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 24.02.2005
 * Time: 11:15:30
 * To change this template use File | Settings | File Templates.
 */
public class MatlabSingleSolution {
    // exitFlag > 0         The function converged to a solution x
    // exitFlag = 0         The maximum number of function evaulation or iterations was exceeded
    // exitFlag < 0         The function did not converge to a solution
    public int exitFlag;
    public int iterations;
    public double[] fitness;
    public double[] weights;
}
