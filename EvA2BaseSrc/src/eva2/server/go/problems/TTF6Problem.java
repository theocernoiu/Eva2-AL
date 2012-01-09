package eva2.server.go.problems;


import java.util.ArrayList;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.operators.constraint.InterfaceConstraint;
import eva2.server.go.operators.moso.InterfaceMOSOConverter;
import eva2.server.go.operators.paretofrontmetrics.InterfaceParetoFrontMetric;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.TF1Problem;
import eva2.tools.math.RNG;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 06.10.2004
 * Time: 13:20:34
 * To change this template use File | Settings | File Templates.
 */
public class TTF6Problem extends TF1Problem implements java.io.Serializable {

    private double[] m_Loss;
    private double[][] m_Risk;
    private int m_CardinalityConst = 2;
    private int m_PortSize = 5;
    private boolean m_UseLamarckism = true;
    private boolean m_UseAdditionalParameters = true;
    private boolean m_UseLocalizedParameters = true;

    public TTF6Problem() {
        super(1);
    }

    public TTF6Problem(TTF6Problem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        //AbstractMultiObjectiveOptimizationProblem
        if (b.m_MOSOConverter != null)
            this.m_MOSOConverter = (InterfaceMOSOConverter) b.m_MOSOConverter.clone();
        if (b.m_Metric != null)
            this.m_Metric = (InterfaceParetoFrontMetric) b.m_Metric.clone();
        if (b.m_ParetoFront != null)
            this.m_ParetoFront = (Population) b.m_ParetoFront.clone();
        if (b.m_Border != null) {
            this.m_Border = new double[b.m_Border.length][2];
            for (int i = 0; i < this.m_Border.length; i++) {
                this.m_Border[i][0] = b.m_Border[i][0];
                this.m_Border[i][1] = b.m_Border[i][1];
            }
        }
        if (b.m_AreaConst4Parallelization != null) {
            this.m_AreaConst4Parallelization = new ArrayList();
            for (int i = 0; i < b.m_AreaConst4Parallelization.size(); i++) {
                this.m_AreaConst4Parallelization.add(((InterfaceConstraint) b.m_AreaConst4Parallelization.get(i)).clone());
            }
        }
        // TF1Problem
        this.m_ProblemDimension = b.m_ProblemDimension;
        this.m_OutputDimension = b.m_OutputDimension;
        this.m_Noise = b.m_Noise;
        this.m_XOffSet = b.m_XOffSet;
        this.m_YOffSet = b.m_YOffSet;
        // TTF6Problem
        if (b.m_Loss != null) {
            this.m_Loss = new double[b.m_Loss.length];
            System.arraycopy(b.m_Loss, 0, this.m_Loss, 0, b.m_Loss.length);
        }
        if (b.m_Risk != null) {
            this.m_Risk = new double[b.m_Risk.length][];
            for (int i = 0; i < this.m_Risk.length; i++) {
                this.m_Risk[i] = new double[b.m_Risk[i].length];
                System.arraycopy(b.m_Risk[i], 0, this.m_Risk[i], 0, b.m_Risk[i].length);
            }
        }
        this.m_CardinalityConst = b.m_CardinalityConst;
        this.m_PortSize = b.m_PortSize;
        this.m_UseLamarckism = b.m_UseLamarckism;
        this.m_UseAdditionalParameters = b.m_UseAdditionalParameters;
        this.m_UseLocalizedParameters = b.m_UseLocalizedParameters;
    }

    /**
     * This method inits the Problem to log multiruns for the s-Metric it
     * is necessary to give the border to get reliable results.
     * also it is necessary to init the local Pareto-Front and the
     * problem frame (i'll provide a default implementation here.
     */
    public void initProblem() {
        this.m_Border = new double[2][2];
        for (int i = 0; i < this.m_Border.length; i++) {
            this.m_Border[i][0] = 0;
            this.m_Border[i][1] = 5;
        }
        this.m_ParetoFront = new Population();

        this.m_Loss = new double[this.m_PortSize];
        this.m_Loss[0] = 0.0;
        this.m_Loss[1] = 1.0;
        this.m_Loss[2] = 0.2;
        this.m_Loss[3] = 0.5;
        this.m_Loss[4] = 0.7;

        this.m_Risk = new double[this.m_PortSize][this.m_PortSize];
        this.m_Risk[0][0] = 1.0;
        this.m_Risk[1][1] = 0.0;
        this.m_Risk[2][2] = 0.7;
        this.m_Risk[3][3] = 0.5;
        this.m_Risk[4][4] = 0.2;

        this.m_Risk[0][1] = 0.00;
        this.m_Risk[0][2] = 0.10;
        this.m_Risk[0][3] = 0.00;
        this.m_Risk[0][4] = 0.30;

        this.m_Risk[1][2] = 0.00;
        this.m_Risk[1][3] = 0.00;
        this.m_Risk[1][4] = 0.00;

        this.m_Risk[2][3] = 0.30;
        this.m_Risk[2][4] = -0.10;

        this.m_Risk[3][4] = 0.00;

        for (int i = 0; i < this.m_Risk.length; i++) {
            for (int j = i + 1; j < this.m_Risk.length; j++) {
                this.m_Risk[j][i] = this.m_Risk[i][j];
            }
        }
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new TTF6Problem(this);
    }

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        this.m_ParetoFront = new Population();

        double[][] newRange = new double[this.m_ProblemDimension][2];
        for (int i = 0; i < this.m_ProblemDimension; i++) {
            newRange[i][0] = 0;
            newRange[i][1] = 1;
        }

        ((InterfaceDataTypeDouble) this.m_Template).setDoubleDataLength(this.m_ProblemDimension);
        ((InterfaceDataTypeDouble) this.m_Template).SetDoubleRange(newRange);

        AbstractOptimizationProblem.defaultInitPopulation(population, m_Template, this);
    }

    /**
     * This method evaluate a single individual and sets the fitness values
     *
     * @param individual The individual that is to be evalutated
     */
    public void evaluate(AbstractEAIndividual individual) {
        double[] x;
        double[] fitness;

        x = new double[((InterfaceDataTypeDouble) individual).getDoubleData().length];
        System.arraycopy(((InterfaceDataTypeDouble) individual).getDoubleData(), 0, x, 0, x.length);

        x = this.repair(x);
        if (this.isValid(x)) {
            fitness = this.doEvaluation(x);
            //        this.showPortfolio(fitness, x);
            if (this.m_UseLamarckism) ((InterfaceDataTypeDouble) individual).SetDoubleGenotype(x);
        } else {
            System.out.println("NotValid");
            fitness = new double[2];
            fitness[0] = 1.5;
            fitness[1] = 1.5;
        }
        for (int i = 0; i < fitness.length; i++) {
            // add noise to the fitness
            fitness[i] += RNG.gaussianDouble(this.m_Noise);
            fitness[i] += this.m_YOffSet;
            // set the fitness of the individual
            individual.SetFitness(i, fitness[i]);
        }
        individual.checkAreaConst4Parallelization(this.m_AreaConst4Parallelization);
    }

    /**
     * Ths method allows you to evaluate a simple bit string to determine the fitness
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] doEvaluation(double[] x) {
        double[] result = new double[2];

        result[0] = 0;  // this is the risk
        result[1] = 0;  // this is the loss

        for (int i = 0; i < this.m_PortSize; i++) {
            result[1] += x[i] * this.m_Loss[i];
            for (int j = 0; j < this.m_PortSize; j++) {
                result[0] += x[i] * x[j] * this.m_Risk[i][j];
            }
        }

        if (this.m_UseAdditionalParameters) {
            for (int i = this.m_PortSize; i < x.length; i++) {
                if (this.m_UseLocalizedParameters) {
                    result[1] += x[i] * x[i] * x[i % this.m_PortSize];
                } else {
                    result[1] += x[i] * x[i];
                }
            }
        }
        return result;
    }

    private void showPortfolio(double[] fit, double[] x) {
        String out = "Risk: " + fit[0] + " / Loss: " + fit[1];
        out += "{";
        for (int i = 0; i < this.m_PortSize; i++) out += x[i] + "; ";
        out += "}";
        System.out.println("" + out);
    }

    private double[] repair(double[] x) {
        int index = 0;
        double[] result = new double[x.length];
        System.arraycopy(x, 0, result, 0, x.length);
        // keep the two biggest values of x[0]-x[4]
        while (!this.validCardinality(result)) {
            // remove the lowest value
            int tmpIndex = -1;
            double tmpValue = 2;
            for (int i = 0; i < this.m_PortSize; i++) {
                if ((result[i] > 0) && (result[i] < tmpValue)) {
                    tmpIndex = i;
                    tmpValue = result[i];
                }
            }
            if (tmpIndex >= 0) result[tmpIndex] = 0;
            index++;
//            if (index > 100) {
//                for (int i = 0; i < this.m_PortSize; i++) {
//                    result[i] = RNG.randomDouble(0.1, 0.9);
//                }
//                index = 0;
//            }
        }
        // now it meets cardinality
        // lets meet sum = 1 too
        double sum = 0;
        for (int i = 0; i < this.m_PortSize; i++) {
            sum += result[i];
        }
        if (sum < 0.00001) {
            for (int i = 0; i < this.m_PortSize; i++) result[i] = 0.0;
            result[RNG.randomInt(0, this.m_PortSize - 1)] = 1.0;
        } else {
            for (int i = 0; i < this.m_PortSize; i++) {
                result[i] = result[i] / sum;
            }
        }
        return result;
    }

    private boolean validCardinality(double[] x) {
        int counter = 0;
        for (int i = 0; i < this.m_PortSize; i++) {
            if (x[i] > 0) counter++;
        }
        if (counter > this.m_CardinalityConst) return false;
        else return true;
    }

    private boolean isValid(double[] x) {
        // check card first
        int card = 0;
        for (int i = 0; i < this.m_PortSize; i++) {
            if (x[i] > 0) card++;
        }
        if (card > this.m_CardinalityConst) return false;
        // check range
        for (int i = 0; i < this.m_PortSize; i++) {
            if ((x[i] < 0) || (x[i] > 1)) return false;
        }
        // check sum
        double sum = 0;
        for (int i = 0; i < this.m_PortSize; i++) {
            sum += x[i];
        }
        if ((sum > 1.0000001) || (sum < 0.999999)) return false;
        else return true;
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
        return "TT6 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "TT6_1 is to be minimized.";
    }

    /**
     * This method allows you to activate Lamarckism.
     *
     * @param b True if the path is to be shown.
     */
    public void setUseLamarckism(boolean b) {
        this.m_UseLamarckism = b;
    }

    public boolean getUseLamarckism() {
        return this.m_UseLamarckism;
    }

    public String useLamarckismTipText() {
        return "Toggle the use of Lamarckism.";
    }

    /**
     * This method allows you to activate additional parameters.
     *
     * @param b True if the path is to be shown.
     */
    public void setUseAdditionalParameters(boolean b) {
        this.m_UseAdditionalParameters = b;
    }

    public boolean getUseAdditionalParameters() {
        return this.m_UseAdditionalParameters;
    }

    public String useAdditionalParametersTipText() {
        return "Toggel the use of additional parameters.";
    }

    /**
     * This method allows you to activate a localized version of
     * additional parameters.
     *
     * @param b True if the path is to be shown.
     */
    public void setUseLocalizedParameters(boolean b) {
        this.m_UseLocalizedParameters = b;
    }

    public boolean getUseLocalizedParameters() {
        return this.m_UseLocalizedParameters;
    }

    public String useLocalizedParametersTipText() {
        return "Localized the additional parameters.";
    }

    /**
     * This method allows you set the cardinality.
     *
     * @param b True if the path is to be shown.
     */
    public void setCardinalityCons(int b) {
        if (b < 2) b = 2;
        if (b > this.m_PortSize) b = this.m_PortSize;
        this.m_CardinalityConst = b;
    }

    public int getCardinalityCons() {
        return this.m_CardinalityConst;
    }

    public String cardinalityConsTipText() {
        return "Choose the cardinality of the portfolio.";
    }
}