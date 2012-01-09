package eva2.server.go.problems;

import java.util.Vector;

import eva2.gui.BeanInspector;
import eva2.server.go.enums.PostProcessMethod;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.operators.postprocess.PostProcess;
import eva2.server.go.operators.terminators.EvaluationTerminator;
import eva2.server.go.populations.Population;

/**
 * This implementation of the Shubert function with "known" optima
 * defines only the 18 global optima as known, lesser local are not recognized.
 *
 * @author M.Aschoff, mkron
 */
public class FM9Problem extends AbstractMultiModalProblemKnown
        implements InterfaceMultimodalProblemKnown {
    double epsilon = 0, tmpMax = Double.NEGATIVE_INFINITY;
    private transient Vector<double[]> optCollection = null;
    private int maxDimForFullOptimaListing = 3;

    public FM9Problem() {
        if (m_ProblemDimension == 0) m_ProblemDimension = 2;
        setDefaultRange(10);
        initTemplate();
    }

    public FM9Problem(FM9Problem b) {
        super(b);
    }

    public int getProblemDimension() {
        return m_ProblemDimension;
    }

    public void setProblemDimension(int dim) {
        m_ProblemDimension = dim;
    }

    public String problemDimensionTipText() {
        return "Set Shubert's function's dimensionionality.";
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FM9Problem(this);
    }

    /**
     * This method returns the unnormalized function value
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] evalUnnormalized(double[] x) {
        double[] result = new double[1];
        double sum = 0;

        result[0] = 1.;
        for (int i = 0; i < x.length; i++) {
            sum = 0;
            for (int j = 1; j <= 5; j++) {
                sum += j * Math.cos(((j + 1) * x[i]) + j);
            }
            result[0] *= sum;
        }
        result[0] = -result[0]; // evalUnnormalized muss ein Maximierungsproblem liefern
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
        return "M9-Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Shubert function. In n dimensions it has n*pow(3,n) global solutions which are unevenly spaced.";
    }

    /**
     * *******************************************************************************************************************
     * Multimodal-Known
     */

    @Override
    public void initListOfOptima() {
//		System.out.println("******************");
        initGeneralOpts();
        // these are only the 18 global optima (found with anpso and postprocessing) for 2D
        // the fitness values all match -186.7309088****
        // so they should at least be accurate within 10^-7
//		this.add2DOptimum(5.482864221694671, 4.858056877357749);
//		this.add2DOptimum(5.48286420452448, -1.4251284367999872);
//		this.add2DOptimum(5.482864208743702, -7.708313736320234);
//		this.add2DOptimum(4.858056878393983, 5.482864204233237);
//		this.add2DOptimum(4.858056877622959, -0.8003211020588199);
//		this.add2DOptimum(4.858059851152184, -7.083507181863828);
//		
//		this.add2DOptimum(-0.8003210880022311, 4.858056874386085);
//		this.add2DOptimum(-0.8003211024667899, -1.4251284262603625);
//		this.add2DOptimum(-0.8003197801033374, -7.708314386066821);
//		this.add2DOptimum(-1.4251284422722779, 5.482864208785137);
//		this.add2DOptimum(-1.4251284281170153, -7.083506407995033);
//		this.add2DOptimum(-1.4251280681833138, -0.8003210826190527);
//		
//		this.add2DOptimum(-7.083506403332486, 4.8580568747060235);
//		this.add2DOptimum(-7.083506489964152, -1.4251284375009345);
//		this.add2DOptimum(-7.083506348919829, -7.708313733758234);
//		this.add2DOptimum(-7.708312885178544, -7.0835035587194755);
//		this.add2DOptimum(-7.70831367711736, 5.482864017383136);
//		this.add2DOptimum(-7.7083137508783945, -0.8003211114688269);
    }

    /**
     * Initialize FM9 optima for any dimension... this does a refining with NelderMead so it may
     * take some time.
     */
    private void initGeneralOpts() {
        int dim = getProblemDimension();
        if (dim > maxDimForFullOptimaListing) {
            if (optCollection != null) optCollection.clear();
            if (super.m_ListOfOptima != null) super.m_ListOfOptima.clear();
        } else {
            if ((optCollection == null) || (optCollection.size() != (dim * Math.pow(3, dim)))) {
                //		double[] vals1 = { 5.4828642066, -0.800321100, -7.083506406 };
                //		double[] vals2 = { 4.858056877, -1.42512843, -7.708313735};
                double[] vals = {5.4828642066, 4.858056877, -0.800321100, -1.42512843, -7.083506406, -7.708313735};
                //			int d = 3;
                double[] pos = new double[getProblemDimension()];
                optCollection = new Vector<double[]>();
                tmpMax = Double.NEGATIVE_INFINITY;
                epsilon = 1e-5;
                addSubOptima(vals, pos, getProblemDimension() - 1);
                //			System.out.println("optColl size " + optCollection.size());
                for (int i = 0; i < optCollection.size(); i++) super.addOptimum(optCollection.get(i));
                //			System.out.println(getRealOptima().getStringRepresentation());

                // refining the optima with NM
                Population opts = getRealOptima();
                PostProcess.processSingleCandidatesNMCMA(PostProcessMethod.nelderMead, opts, new EvaluationTerminator(20 * getProblemDimension()), 0.00001, this);
                m_ListOfOptima.clear();
                for (int i = 0; i < opts.size(); i++) {
                    super.addOptimum(AbstractEAIndividual.getDoublePosition(opts.getEAIndividual(i)));
                }
                double[] measures = getRealOptima().getPopulationMeasures();
                if (measures[1] < 0.000001) {
                    System.err.println("Error in initGeneralOpts! Equal optima shouldnt occur...");
                }
                //			System.out.println(getRealOptima().getStringRepresentation());
                //			System.out.println(BeanInspector.toString(getRealOptima().getPopulationMeasures()));
                //			System.exit(1);
            } else {
                //			System.out.println("optColl size " + optCollection.size());
                for (int i = 0; i < optCollection.size(); i++) super.addOptimum(optCollection.get(i));
            }
        }
    }

    private void addOpt(double[] pos) {
        double[] fit = evalUnnormalized(pos);
        if (fit.length > 1) System.err.println("error in FM9Problem");
        double diff = Math.abs(fit[0] - tmpMax);
        if (fit[0] > (tmpMax + epsilon)) {
            tmpMax = fit[0];
            optCollection.clear();
            optCollection.add(pos.clone());
//			System.out.println("new max: " + BeanInspector.toString(pos) + " " + fit[0]);
        } else if (diff < epsilon) {
//			System.out.println("opt: " + BeanInspector.toString(pos) + " " + fit[0]);
            optCollection.add(pos.clone());
        } else {
            // seems a local opt.
//			if (diff < 100.) System.out.println(" ? "+ BeanInspector.toString(pos) + " " + fit[0] + " diff only " + diff);
        }
    }

    /**
     * A nice recursive style function. Add all optima of the sine function up to a given dimension
     * using recursion for lower dimensions. The pos vector will be overwritten (reused)
     *
     * @param pos
     * @param dim
     */
    public void addSubOptima(double[] vals, double[] pos, int dim) {
        for (int i = 0; i < vals.length; i++) {
            pos[dim] = vals[i];
            if (dim == 0) addOpt(pos);
            else addSubOptima(vals, pos, dim - 1);
        }
    }
}
