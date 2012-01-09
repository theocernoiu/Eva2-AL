package eva2.server.go.problems;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import eva2.gui.Plot;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractDynTransProblem;
import eva2.server.go.strategies.InterfaceOptimizer;


/**
 * A simple dynamic variant of a optimization benchmark function.
 * Dynamics are based on a hyper-circular translation in all dimensions. The severity of
 * fitness changes is influenced by the circle radius as well as the stepsize when walking
 * along the circle and the frequency of steps.
 *
 * @author Geraldine Hopf
 * @date 25.06.2007
 */
public class DynCircleProblem extends AbstractDynTransProblem {

    private static final long serialVersionUID = -4602616190523056591L;

    private double[] radius;
    private double[] sinFreq;
    /// how many steps does the circle round trip consist of
    private double piecesOfCake;
    protected double[] translation;
    /* for the output file */
    private int changeCounter;
    private Writer fw = null;
    private String s = "";
    private double evaluations = 0.0;

    public DynCircleProblem() {
        super();
        sinFreq = new double[getProblemDimension()];
        radius = new double[getProblemDimension()];
        // some defaults
        setPiecesOfCake(36.0);
        initialize(0.0, 2.0, 0.1);
        changeCounter = 0;
    }

    public DynCircleProblem(DynCircleProblem other) {
        other.clone();
    }

    protected void changeProblemAt(double problemTime) {
        super.changeProblemAt(problemTime);
        /* prooving results */
        if (TRACE) writeFile();
        ++changeCounter;
    }

    protected void countEvaluation() {
        super.countEvaluation();
        evaluations += 1.;
    }

    protected double getTranslation(int dim, double time) {
        double ret;
        if (dim == 0)
            ret = radius[dim] * Math.sin(time * sinFreq[dim]);
            // ret = getSeverity() * Math.sin(time * 2 * Math.PI / getPiecesOfCake());
        else if (dim == 1)
            ret = radius[dim] * Math.cos(time * sinFreq[dim]);
        else
            ret = 0;
        return ret;
    }

    /**
     * Override population evaluation to do some data output.
     */
    public void evaluatePopulationEnd(Population population) {
        // the translation on the first two dimensions
        //double delta = Math.sqrt(Math.pow(getTranslation(0, getCurrentProblemTime()), 2.) + Math.pow(getTranslation(1, getCurrentProblemTime()), 2.));
        double delta = getTranslation(0, getCurrentProblemTime()) + getTranslation(1, getCurrentProblemTime());
        if (isExtraPlot() == true) {
            if (myplot != null) {
                myplot.jump();
            } else {
                if (TRACE) System.out.println("creating myplot instance");
                double[] tmpD = new double[2];
                tmpD[0] = 0;
                tmpD[1] = 0;
                // im not really certain about what tmpD is required for
                this.myplot = new Plot("population measures", "x1", "x2", tmpD, tmpD);
            }
            myplot.setConnectedPoint(population.getFunctionCalls(), delta, 0);
            //myplot.setUnconnectedPoint(population.getFunctionCalls(), population.getPopulationMeasures()[2], 2);
        } else
            myplot = null;
    }

    public void initProblem() {
        super.initProblem();
        evalsSinceChange = 0.0;
        evaluations = 0.0;
        changeCounter = 0;
    }

    public Object clone() {
        return new DynCircleProblem(this);
    }

    public AbstractEAIndividual getCurrentOptimum() {
        return null;
    }

    /**************************************************************************
     * These are for the GUI
     *
     */
    /**
     * This actually sets the circle radius responsible for the target translation. Should be
     * smaller than the problem space range.
     *
     * @param sev severity (or circle radius) for this problem
     */
    public void setSeverity(double sev) {
        super.setSeverity(sev);
        for (int i = 0; i < getProblemDimension(); i++) {
            radius[i] = sev;
        }
    }

    public String severityTipText() {
        return "In this case the circle radius.";
    }

    public String frequencyTipText() {
        return "The frequency of changes in the target function, should be <= 1 for the circle";
    }

    /**
     * How many steps does the circle round trip consist of.
     *
     * @return the piecesOfCake
     */
    public double getPiecesOfCake() {
        return piecesOfCake;
    }

    /**
     * Set how many steps does the circle round trip consist of.
     *
     * @param piecesOfCake the piecesOfCake to set
     */
    public void setPiecesOfCake(double piecesOfCake) {
        if (piecesOfCake > 0.0)
            this.piecesOfCake = piecesOfCake;
        for (int i = 0; i < getProblemDimension(); i++) {
            sinFreq[i] = 2 * Math.PI / piecesOfCake;
        }
    }

    public String piecesOfCakeTipText() {
        return "number of steps one circle round trip consists of, usually > 1. Corresponds to the severity.";
    }

    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        return "DynCircleProblem";
    }

    public String getName() {
        return "DynCircleProblem";
    }

    public String globalInfo() {
        return "A real valued problem wandering on a circle around the origin";
    }

    /**
     * ***********************************************************************
     * These are for debugging and determing the output file
     */

    public void myPrint(double[][] toPrint) {
        for (int i = 0; i < toPrint.length; i++) {
            for (int j = 0; j < toPrint[i].length; ++j) {
                System.out.print(toPrint[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public void myPrint(double[] toPrint) {
        for (int i = 0; i < toPrint.length; ++i) {
            System.out.print(toPrint[i] + " ");
        }
        System.out.println("");
    }

    public void writeFile() {
        if (fw == null) {
            try {
                fw = new FileWriter("DynCircleProblem.txt");
            } catch (IOException e) {
                System.err.println("Konnte Datei nicht erstellen");
            }
        } else {
            try {
                fw.write("Problem wurde " + changeCounter + " mal geaendert!\n");
                fw.write(evaluations + " Evaluierungen wurden gemacht\n");
                fw.write(myPrints(translation));

            } catch (IOException e) {
            } finally {
                if (fw != null)
                    try {
                        fw.flush();
                    } catch (IOException e) {
                    }
            }
        }
    }

    public String myPrints(double[][] toPrint) {
        for (int i = 0; i < toPrint.length; i++) {
            for (int j = 0; j < toPrint[i].length; ++j) {
                if (j != getProblemDimension())
                    s += toPrint[i][j] + "\t";
            }
            s += "\n";
        }
        s += "\n";
        return s;
    }

    public String myPrints(double[] toPrint) {
        for (int i = 0; i < toPrint.length; i++) {
            s += toPrint[i] + "\t";
            s += "\n";
        }
        s += "\n";
        return s;
    }
}
