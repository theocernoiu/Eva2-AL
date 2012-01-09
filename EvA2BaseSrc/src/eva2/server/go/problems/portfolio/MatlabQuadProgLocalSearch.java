package eva2.server.go.problems.portfolio;

import eva2.server.go.operators.selection.MOMultipleSolutions;
import eva2.server.go.operators.selection.MOSolution;
import eva2.tools.math.RNG;
import eva2.tools.matlab.JMatLink;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 24.02.2005
 * Time: 10:58:11
 * To change this template use File | Settings | File Templates.
 */
public class MatlabQuadProgLocalSearch {

    transient protected JMatLink m_Engine;
    transient protected int m_PID;


    public MatlabQuadProgLocalSearch() {
        this.initMatlab();
    }

    /**
     * This method will perform a single local optimization step for the portfolio optimization step
     * in the lambda direction
     *
     * @param lambda     The direction [0-1] of the local search
     * @param x          A possible initial value for x
     * @param risk       The risk for the assets
     * @param ret        The return for the assets
     * @param cov        The covariance of the assets
     * @param upperBound The upper bound for each asset
     * @param useInit    Flag to indicate whether or not to use the x as inital value
     * @return A single matlab solution
     */
    public MatlabSingleSolution performLocalSerach(double lambda, double[] x, double[] ret, double[] risk, double[][] cov, double[] upperBound, boolean useInit) {
        MatlabSingleSolution result = new MatlabSingleSolution();

        // First build the reduced matrix
        int dim = ret.length;
        double[][] Risk = new double[dim][dim];
        double[] Return = new double[dim];
        for (int i = 0; i < Return.length; i++) {
            Return[i] = ret[i];
            for (int j = 0; j < Risk[i].length; j++) {
                Risk[i][j] = risk[i] * risk[j] * cov[i][j];
            }
        }
        // Then send the data
        this.m_Engine.engPutArray(this.m_PID, "lambda", lambda);
        this.m_Engine.engPutArray(this.m_PID, "x0", x);
        this.m_Engine.engPutArray(this.m_PID, "Risk", Risk);
        this.m_Engine.engPutArray(this.m_PID, "Return", Return);
        this.m_Engine.engPutArray(this.m_PID, "ub", upperBound);
        this.m_Engine.engEvalString(this.m_PID, "Return = Return'");
        this.m_Engine.engEvalString(this.m_PID, "H      = 2*(1-lambda)*Risk;");
        this.m_Engine.engEvalString(this.m_PID, "f      = -lambda*Return;");
        this.m_Engine.engEvalString(this.m_PID, "A      = eye(length(Return));");
        this.m_Engine.engEvalString(this.m_PID, "b      = ones(1,length(Return));");
        this.m_Engine.engEvalString(this.m_PID, "Aeq    = ones(1,length(Return));");
        this.m_Engine.engEvalString(this.m_PID, "beq    = 1");
        this.m_Engine.engEvalString(this.m_PID, "lb     = zeros(length(Return), 1);");
        //this.m_Engine.engEvalString(this.m_PID, "ub     = ones(length(Return), 1);");
        // now perform the local search
        if (useInit)
            this.m_Engine.engEvalString(this.m_PID, "[x, fval, exitfalg, output, lambdaT] = quadprog(H,f,A,b,Aeq,beq,lb,ub,x0);");
        else
            this.m_Engine.engEvalString(this.m_PID, "[x, fval, exitfalg, output, lambdaT] = quadprog(H,f,A,b,Aeq,beq,lb,ub);");
        // now evaluate the result of the local search
        result.exitFlag = (int) this.m_Engine.engGetArray("exitfalg")[0][0];
        // i guess i can ignore lambdaT and make my own check for validity
        double[][] tmpA = this.m_Engine.engGetArray("x");
        result.weights = new double[tmpA.length];
        for (int i = 0; i < tmpA.length; i++) result.weights[i] = tmpA[i][0];
        this.m_Engine.engEvalString(this.m_PID, "iter   = output.iterations;");
        result.iterations = (int) this.m_Engine.engGetArray("iter")[0][0];
        // finally calculate the fitness just to check
        result.fitness = this.calcuateRiskReturn(result.weights, ret, risk, cov);

        return result;
    }

    public MOMultipleSolutions performGlobalSerach(double[] x, double[] ret, double[] risk, double[][] cov, double[] upperBound, boolean useInit) {
        MOMultipleSolutions result = new MOMultipleSolutions();

        // First build the reduced matrix
        int dim = ret.length;
        double[] tmpW;
        double[][] Risk = new double[dim][dim];
        double[] Return = new double[dim];
        for (int i = 0; i < Return.length; i++) {
            Return[i] = ret[i];
            for (int j = 0; j < Risk[i].length; j++) {
                Risk[i][j] = risk[i] * risk[j] * cov[i][j];
            }
        }
        this.m_Engine.engPutArray(this.m_PID, "x0", x);
        this.m_Engine.engPutArray(this.m_PID, "Risk", Risk);
        this.m_Engine.engPutArray(this.m_PID, "Return", Return);
        this.m_Engine.engPutArray(this.m_PID, "ub", upperBound);
        this.m_Engine.engEvalString(this.m_PID, "A      = eye(length(Return));");
        this.m_Engine.engEvalString(this.m_PID, "b      = ones(1,length(Return));");
        this.m_Engine.engEvalString(this.m_PID, "Aeq    = ones(1,length(Return));");
        this.m_Engine.engEvalString(this.m_PID, "beq    = 1");
        this.m_Engine.engEvalString(this.m_PID, "lb     = zeros(length(Return), 1);");

        // now iterate over multiple lambda values
        double lambda = 0;
        double step = 0.01;
        double lowerStep = 0.0000001;
        double[][] tmpD;
        double[][] tmpExit;
        double[] cFitness, oFitness = null;
        MOSolution tmpSol;
        while (lambda <= 1) {
            // init the stuff depending on lamdba
            this.m_Engine.engPutArray(this.m_PID, "lambda", lambda);
            this.m_Engine.engEvalString(this.m_PID, "H      = 2*(1-lambda)*Risk;");
            this.m_Engine.engEvalString(this.m_PID, "f      = -lambda*Return;");

            // optimize
            if (useInit)
                this.m_Engine.engEvalString(this.m_PID, "[x, fval, exitfalg, output, lambdaT] = quadprog(H,f,A,b,Aeq,beq,lb,ub,x0);");
            else
                this.m_Engine.engEvalString(this.m_PID, "[x, fval, exitfalg, output, lambdaT] = quadprog(H,f,A,b,Aeq,beq,lb,ub);");

            // now fetch the results
            this.m_Engine.engEvalString(this.m_PID, "iter   = output.iterations;");
            tmpD = this.m_Engine.engGetArray("x");
            tmpExit = this.m_Engine.engGetArray("exitfalg");
            if (tmpExit[0][0] > 0) {
                tmpW = new double[x.length];
                for (int i = 0; i < x.length; i++) {
                    tmpW[i] = tmpD[i][0];
                }
                cFitness = this.calcuateRiskReturn(tmpW, ret, risk, cov);
                tmpSol = new MOSolution(cFitness, tmpW);
                result.add(tmpSol);
                result.m_Iterations += (int) this.m_Engine.engGetArray("iter")[0][0];
                if (lambda == 1) break;
                if (oFitness != null) {
                    if (Math.sqrt(Math.pow((cFitness[0] - oFitness[0]), 2) + Math.pow((cFitness[1] - oFitness[1]), 2)) > 0.01) {
                        step = step / 2.0;
                    }
                    if (Math.sqrt(Math.pow((cFitness[0] - oFitness[0]), 2) + Math.pow((cFitness[1] - oFitness[1]), 2)) < 0.0001) {
                        step = step * 2.0;
                    }
                    if (step < lowerStep) step = lowerStep;
                    lambda += step;
                } else {
                    lambda += step;
                }
                oFitness = new double[2];
                oFitness[0] = cFitness[0];
                oFitness[1] = cFitness[1];
            } else {
                double sum = 0;
                x = new double[dim];
                for (int i = 0; i < x.length; i++) {
                    x[i] = RNG.randomDouble(0, 1);
                    sum += x[i];
                }
                for (int i = 0; i < x.length; i++) {
                    x[i] = x[i] / sum;
                }
                useInit = true;
            }
            if (lambda > 1) lambda = 1;
        }
        return result;
    }


    /**
     * This method calculates the risk return for a given portfolio
     *
     * @param x The portfolio which must be normatted
     * @return double[] risk/return
     */
    private double[] calcuateRiskReturn(double[] x, double[] ret, double[] ris, double[][] cov) {
        double Return = 0, Risk = 0;
        double[] result = new double[2];
//*******************************************************************************************************************
        // calculate the return on investment
        for (int i = 0; i < x.length; i++) {
            // at the very end of the sourceMatrix is the return of an asset
            Return += x[i] * ret[i];
        }

        // calculate the risk of the allocation
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {
                Risk += x[i] * x[j] * ris[i] * ris[j] * cov[i][j];
            }
        }
        Risk = Math.sqrt(Risk);
//*******************************************************************************************************************
        result[0] = Risk;
        result[1] = Return;
        return result;
    }


    /**
     * If necessary the matlab engine can be initialized
     */
    private void initMatlab() {
        this.m_Engine = JMatLink.getInstance();
        this.m_PID = this.m_Engine.engOpen();
        String ClassPath = System.getProperty("java.class.path");
        String ToolBoxPath = ClassPath + "/matlab/";
        this.m_Engine.engEvalString(this.m_PID, "addpath " + ToolBoxPath);
        System.out.println("-->" + this.m_Engine.engOutputBuffer(this.m_PID));
        this.m_Engine.engEvalString(this.m_PID, "path ");
        System.out.println("-->" + this.m_Engine.engOutputBuffer(this.m_PID));
    }
}
