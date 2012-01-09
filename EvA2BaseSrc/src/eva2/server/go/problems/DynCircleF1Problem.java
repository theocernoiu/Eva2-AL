/**
 *
 */
package eva2.server.go.problems;

import eva2.gui.Graph;
import eva2.gui.GraphWindow;
import eva2.gui.Plot;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.F1Problem;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.math.RNG;
import eva2.server.stat.StatisticsWithGUI;


/**
 * A simple dynamic variant of the F1 optimization benchmark function.
 * Dynamics are based on a hyper-circular translation in all dimensions. The severity of
 * fitness changes is influenced by the circle radius as well as the stepsize when walking
 * along the circle and the frequency of steps.
 *
 * @author Marcel Kronfeld
 * @date 12/14/2006
 */
public class DynCircleF1Problem extends DynTransF1Problem implements java.io.Serializable {
    private double[] radius;
    private double[] sinFreq;
    /// how many steps does the circle round trip consist of
    private double piecesOfCake;
    //double 		maxAmp;	// this is now identified with severity
    //public boolean variateOneDimOnly;

    private static final long serialVersionUID = 17234537L;

    /**
     * A constructor.
     */
    public DynCircleF1Problem() {
        sinFreq = new double[f1.m_ProblemDimension];
        radius = new double[f1.m_ProblemDimension];

        initialize(0., .1, .1);

        // some defaults
        setPiecesOfCake(36.);
        f1.setXOffSet(getSeverity() / (-2.));    // so we circle around the coord. center
    }

    /**
     * The copy constructor.
     *
     * @param other valid instance of a DynF1Problem
     */
    public DynCircleF1Problem(DynCircleF1Problem other) {
        if (other == null) initProblem();
        else {
            f1 = (F1Problem) other.getF1Instance().clone();

            sinFreq = new double[f1.m_ProblemDimension];
            radius = new double[f1.m_ProblemDimension];

            System.arraycopy(other.sinFreq, 0, sinFreq, 0, f1.m_ProblemDimension);
            System.arraycopy(other.radius, 0, radius, 0, f1.m_ProblemDimension);

            initialize(other.getStartTime(), other.getSeverity(), other.getFrequency());
            setPiecesOfCake(other.getPiecesOfCake());

            setCurrentProblemTime(other.getCurrentProblemTime());
        }
    }

    /**
     * Override population evaluation to do some data output.
     */
    public void evaluatePopulationEnd(Population population) {
        // the translation on the first two dimensions
        //double delta = Math.sqrt(Math.pow(getTranslation(0, getCurrentProblemTime()), 2.) + Math.pow(getTranslation(1, getCurrentProblemTime()), 2.));
        double delta = getTranslation(0, getCurrentProblemTime()) + getTranslation(1, getCurrentProblemTime());
        if (myplot != null) myplot.setConnectedPoint(population.getFunctionCalls(), delta, 0);
        //myplot.setUnconnectedPoint(population.getFunctionCalls(), population.getPopulationMeasures()[2], 2);
    }

    /**
     * Returns the translation in the given dimension at the given time, in this case
     * a 2-dimensional circle in the first two dimensions.
     *
     * @param dim  the dimension
     * @param time the simulation time
     * @return the translation in the given dimension at the given time
     */
    protected double getTranslation(int dim, double time) {
        //if (dim==0) System.out.println("translation at (" + dim + "/" + time + " is " + ampl[dim] * Math.sin(time * freq[dim]));
        //return radius[dim] * Math.sin(time * sinFreq[dim]);
        double ret;
        if (dim == 0) ret = radius[dim] * Math.sin(time * sinFreq[dim]);
        else if (dim == 1) ret = radius[dim] * Math.cos(time * sinFreq[dim]);
        else ret = 0;
        return ret;
    }

    /**
     * This actually sets the circle radius responsible for the target translation. Should be
     * smaller than the problem space range.
     *
     * @param sev severity (or circle radius) for this problem
     */
    public void setSeverity(double sev) {
        super.setSeverity(sev);
        for (int i = 0; i < f1.m_ProblemDimension; i++) {
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
        this.piecesOfCake = piecesOfCake;
        for (int i = 0; i < f1.m_ProblemDimension; i++) {
            sinFreq[i] = 2 * Math.PI / piecesOfCake;
        }
    }

    public String piecesOfCakeTipText() {
        return "number of steps one circle round trip consists of, usually > 1. Corresponds to the severity.";
    }

    /* (non-Javadoc)
      * @see eva2.server.go.OptimizationProblems.AbstractOptimizationProblem#clone()
      */
    @Override
    public Object clone() {
        return new DynCircleF1Problem(this);
    }

    /* (non-Javadoc)
      * @see eva2.server.go.OptimizationProblems.InterfaceOptimizationProblem#getStringRepresentationForProblem(eva2.server.go.Strategies.InterfaceOptimizer)
      */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        return "Dynamic Circle " + f1.getStringRepresentationForProblem(opt);
    }

    /**
     * This method allows the CommonJavaObjectEditorPanel to read the
     * name to the current object.
     *
     * @return The name.
     */
    public String getName() {
        return "Dynamic Circle " + f1.getName();
    }

    public String globalInfo() {
        return "The F1 problem wandering on a circle around the origin";
    }
}
