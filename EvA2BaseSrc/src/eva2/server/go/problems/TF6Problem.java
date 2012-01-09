package eva2.server.go.problems;


import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;

import eva2.gui.PropertyFilePath;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.operators.constraint.InterfaceConstraint;
import eva2.server.go.operators.moso.InterfaceMOSOConverter;
import eva2.server.go.operators.paretofrontmetrics.InterfaceParetoFrontMetric;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.TF1Problem;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 10.05.2004
 * Time: 18:08:16
 * To change this template use File | Settings | File Templates.
 */
public class TF6Problem extends TF1Problem implements java.io.Serializable {

    protected int m_ProblemDimension = 10;

    public TF6Problem() {
        super(1.);
    }

    public TF6Problem(TF6Problem b) {
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
        return (Object) new TF6Problem(this);
    }

    /**
     * Ths method allows you to evaluate a simple bit string to determine the fitness
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] doEvaluation(double[] x) {
        double[] result = new double[2];
        double g = 0;

        result[0] = 1 - Math.exp(-4 * x[0]) * Math.pow(Math.sin(6 * Math.PI * x[0]), 6);
        g = this.g(x);
        result[1] = g * this.h(result[0], g);

        return result;
    }

    /**
     * The g function
     *
     * @param x The decision variables.
     * @return Objective variable.
     */
    protected double g(double[] x) {
        double result = 0;
        for (int i = 1; i < x.length; i++) result += x[i];
        result = 1 + (x.length - 1) * Math.pow(result / (x.length - 1), 0.25);
        return result;
    }

    /**
     * The h function
     *
     * @param x The decision variables.
     * @return Objective variable.
     */
    protected double h(double x, double y) {
        double result = 0;
        result = 1 - Math.pow((x / y), 2);
        return result;
    }

    public static void main(String[] args) {
        int points = 50;
        PropertyFilePath fileOutPath = PropertyFilePath.getFilePathFromResource("resources/MOPReference/T6_" + points + ".txt");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileOutPath.getCompleteFilePath()));
        } catch (java.io.IOException ed) {
            System.out.println("Could not open " + fileOutPath.getCompleteFilePath());
            return;
        }
        TF6Problem problem = new TF6Problem();
        System.out.println("This method generates a reference set for the T6 problem with " + points + " sample points.");
        double ub = 1, lb = 0;
        double x1, x2;
        String tmpStr;
        tmpStr = "x1 \t x2";
        for (int i = 0; i < points + 1; i++) {
            x1 = (ub - lb) / ((double) points) * i;
            x2 = problem.h(x1, 1);
            tmpStr += "\n" + x1 + "\t" + x2;
        }
        try {
            writer.write(tmpStr);
            writer.close();
        } catch (java.io.IOException e) {
            System.out.println("DAMM IOException " + e);
        }
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
        return "T6 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "T6_1 is to be minimized.";
    }
}

