package eva2.server.go.problems;


import java.util.ArrayList;

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
 * Date: 04.10.2004
 * Time: 16:41:40
 * To change this template use File | Settings | File Templates.
 */
public class TTF5Problem extends TF1Problem implements java.io.Serializable {

    public TTF5Problem() {
        super(1.);
    }

    public TTF5Problem(TTF5Problem b) {
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
        return (Object) new TTF5Problem(this);
    }

    /**
     * The h function
     *
     * @param x The decision variables.
     * @return Objective variable.
     */
    protected double h(double x, double y) {
        double result = 0;
        result = 1 - Math.sqrt(Math.sqrt(Math.sqrt((x / y))));
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
        return "TT2 Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "TT2_1 is to be minimized.";
    }
}
