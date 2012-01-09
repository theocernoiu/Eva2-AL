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


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 10.05.2004
 * Time: 18:08:27
 * To change this template use File | Settings | File Templates.
 */
public class TF7Problem extends TF1Problem implements java.io.Serializable {

    protected int m_ProblemDimension = 20;
    protected int m_OutputDimension = 2;

    public TF7Problem() {
        super(1.);
    }

    public TF7Problem(TF7Problem b) {
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
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new TF7Problem(this);
    }

    @Override
    protected double[][] makeRange() {
        return super.makeRange(-1000, 1000);
    }

    /**
     * Ths method allows you to evaluate a simple bit string to determine the fitness
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] doEvaluation(double[] x) {
        double[] result = new double[this.m_OutputDimension];

        for (int i = 0; i < this.m_OutputDimension; i++) {
            result[i] = Math.pow((x[i] - 1), 2);
            for (int j = 0; j < this.m_ProblemDimension; j++) {
                if (i != j) result[i] += Math.pow(x[j], 2);
            }
        }

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
        return "SPH-m";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Schaffer 85";
    }

    /**
     * This method allows you to set the number of objective variables
     *
     * @param a The number of objective variables
     */
    public void setOutputDimension(int a) {
        this.m_OutputDimension = a;
        this.m_Border = new double[this.m_OutputDimension][2];
        for (int i = 0; i < this.m_Border.length; i++) {
            this.m_Border[i][0] = 0;
            this.m_Border[i][1] = 5;
        }
    }

    public int getOutputDimension() {
        return this.m_OutputDimension;
    }

    public String outputDimensionTipText() {
        return "Number of objective variables.";
    }
}
