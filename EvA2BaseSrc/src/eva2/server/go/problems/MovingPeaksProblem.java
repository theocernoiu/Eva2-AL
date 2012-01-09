package eva2.server.go.problems;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Random;

import eva2.gui.Plot;
import eva2.server.go.PopulationInterface;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.populations.Population;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.math.Mathematics;


/**
 * This class creates a moving peaks landscape. Earlier code version was by
 * J.Branke.
 *
 * @author Geraldine Hopf
 * @date 25.06.2007
 */
public class MovingPeaksProblem extends AbstractSynchronousOptimizationProblem implements InterfaceProblemDouble, InterfaceMultimodalProblemKnown, Interface2DBorderProblem, Serializable {

    /*
      * Scenario 1 of the Moving Peak Benchmark by Juergen Branke
      *
      * alles these variables can be changed by the user
      */

    /* correlation value: 0.0 moving is at random, 1.0 moving is totally correlated to the previous moving */
    private double lambda = 0.0;
    /* the problem itself */
    private MovingPeaksPeakShape peakShape = MovingPeaksPeakShape.shapeF1;
    /* creation of the landscape, assume its always quadratic */
    private int numberOfPeaks = 5;
    private int dimension = 5;
    private double lowerBound = 0.0;
    private double upperBound = 100.0;
    private double minHeight = 10; //30.0;
    private double maxHeight = 20; //70.0;
    private double standardHeight = 15; //50.0;
    private double heightSeverity = 7.0;
    private double minWidth = 0.0001;
    private double maxWidth = 0.2;
    private double standardWidth = 0.1;
    private double widthSeverity = 0.01;
    /* for the random number generator, so that every solution is traceable */
    private int seed = 23;
    // bound within which an optimum is considered found
    private double epsilonPeakFound = 0.05;

    /* the random number */
    private Random rand = new Random(seed);
    /* the landscape, a matrix with NumberOfPeaks rows and Dimension columns */
    private double[][] peaks;
    private double[][] previousMovement;
    private double maxPeakHeight;
    /* the offline error */
    private double offlineError;
    private boolean showOfflineError = false;
    private boolean bExtraPlot = false;
    private AbstractEAIndividual bestIndividual = null;
    private double evaluations = 0.0;
    // a population containing the peaks for the InterfaceMultimodalProblemKnown
    private transient Population peakPop = null;
    private boolean peakPopValid = false;
    // adding a base function (e.g. multi-dim sine) to make the problem harder
    private MovingPeaksBaseFuncEnum baseFuncType = MovingPeaksBaseFuncEnum.none;
    private double baseParam = 5.; //	parameter for the base function


    /* for the output file */
    private int changeCounter;
    private Writer fw = null;
    private String s = "";

    private static final long serialVersionUID = -2818109778140371906L;

    /* the constructor */
    public MovingPeaksProblem() {
        super();
        m_Template = new ESIndividualDoubleData();
        initialize(getStartTime(), 1.0, 0.0);
        setFrequencyRelative(true);
    }

    /* a copy constructor */
    public MovingPeaksProblem(MovingPeaksProblem other) {
        lambda = other.lambda;
        peakShape = other.peakShape;
        numberOfPeaks = other.numberOfPeaks;
        dimension = other.dimension;
        lowerBound = other.lowerBound;
        upperBound = other.upperBound;
        minHeight = other.minHeight;
        maxHeight = other.maxHeight;
        standardHeight = other.standardHeight;
        heightSeverity = other.heightSeverity;
        minWidth = other.minWidth;
        maxWidth = other.maxWidth;
        standardWidth = other.standardWidth;
        widthSeverity = other.widthSeverity;
        /* for the random number generator, so that every solution is traceable */
        seed = other.seed;
        // bound within which an optimum is considered found
        epsilonPeakFound = other.epsilonPeakFound;

        rand = other.rand;

        showOfflineError = other.showOfflineError;
        bExtraPlot = other.bExtraPlot;
        bestIndividual = null;
        evaluations = 0.0;
        // a population containing the peaks for the InterfaceMultimodalProblemKnown
        peakPop = null;
        peakPopValid = false;
    }

    /**
     * this function initialises the landscape
     *
     * @param peaks            to be initialized randomly
     * @param previousMovement to be initialized randomly, is needed for movePeaks
     * @param rand             the random number generator
     */
    private void initPeaks(double[][] peaks, double[][] previousMovement, Random rand) {
        /* initialize peaks and previous Movement with NumberOfPeaks and Dimension */
        for (int i = 0; i < getPeakCount(); ++i) {
            double norm = 0.0;
            for (int j = 0; j < getProblemDimension(); ++j) {
                /* peaks has random values between Min and Max */
                peaks[i][j] = (getRangeUpperBound(j) - getRangeLowerBound(j)) * rand.nextDouble() + getRangeLowerBound(j);
                /* previousMovement has values between -0.5 and 0.5 -> direction of movement */
                previousMovement[i][j] = rand.nextDouble() - 0.5;
                /* needed to standardize to moveLength */
                norm += Math.pow(previousMovement[i][j], 2);
            }

            /* (a[]/ |a[]|) = length(1)
                * (a[]/ |a[]|) * moveLength = length(moveLength)
                * |a[]| = sqare_root( (sum (a_i)^2) )
                * norm = moveLength / |a[]|
                * moveLength = getSeverity
                */
            if (norm > 0.0) {
                norm = getSeverity() / Math.sqrt(norm);
            } else {
                norm = 0.0; // rounding errors
            }
            /* now previousMovement is standardized */
            for (int j = 0; j < getProblemDimension(); ++j) {
                previousMovement[i][j] = norm * previousMovement[i][j];
            }

            /* peaks has 2 more columns for widht and height, seems stupid makes calculation a lot easier */
            int widhtIndex = getProblemDimension();
            peaks[i][widhtIndex] = updateWidth(getWidthStandard());

            int heightIndex = getProblemDimension() + 1;
            peaks[i][heightIndex] = updateHeight(getHeightStandard());
        }
//		myPrint(peaks);
        if (TRACE) writeFile();
        peakPopValid = false;
    }

    /**
     * this function changes the landscape depending on lambda and previousMovement
     *
     * @param peaks            to be changed
     * @param previousMovement to be changed
     * @param rand             always the same seed, so that it is traceable
     */
    private void movePeaks(double[][] peaks, double[][] previousMovement, Random rand) {
        /* to calculate offlineError */
        bestIndividual = null;

        /* peaks will be shifted with shift, creation is the same like previousMovement*/
        double shift[] = new double[getProblemDimension()];
        for (int i = 0; i < getPeakCount(); ++i) {
            double norm = 0.0;
            for (int j = 0; j < getProblemDimension(); ++j) {
                shift[j] = rand.nextDouble() - 0.5;
                norm += Math.pow(shift[j], 2);
            }
            if (norm > 0.0) {
                norm = getSeverity() / Math.sqrt(norm);
            } else {
                norm = 0.0; // rounding errors
            }
            double norm2 = 0.0;
            for (int j = 0; j < getProblemDimension(); ++j) {
                /* crux: if lambda = 1, only previousMovement is calculated
                     *       if lambda = 0, only random is calculated
                     *       other          rand and previousMovement are weighted
                     */
                shift[j] = (1 - getLambda()) * (norm * shift[j])
                        + getLambda() * previousMovement[i][j];
                norm2 += Math.pow(shift[j], 2);
            }
            if (norm2 > 0.0) {
                norm2 = getSeverity() / Math.sqrt(norm2);
            } else {
                norm2 = 0.0;
            }

            for (int j = 0; j < getProblemDimension(); ++j) {
                shift[j] = norm2 * shift[j];
                /* test if peaks is still between boundaries, if not bounce it off like a pool ball */
                if (peaks[i][j] + shift[j] < getRangeLowerBound(j)) {
                    peaks[i][j] = 2.0 * getRangeLowerBound(j) - peaks[i][j] - shift[j];
                    shift[j] *= -1.0;
                } else if (peaks[i][j] + shift[j] > getRangeUpperBound(j)) {
                    peaks[i][j] = 2.0 * getRangeUpperBound(j) - peaks[i][j] - shift[j];
                    shift[j] *= -1.0;
                } else {
                    peaks[i][j] += shift[j];
                }
                /* Update previousMovement */
                previousMovement[i][j] = shift[j];
            }

            /* change width */
            int widthIndex = getProblemDimension();
            peaks[i][widthIndex] = updateWidth(peaks[i][widthIndex]);

            /* change height */
            int heightIndex = getProblemDimension() + 1;
            peaks[i][heightIndex] = updateHeight(peaks[i][heightIndex]);
        }
        peakPopValid = false;
    }

    /**
     * Updating Height and Width with bouncing off, like a pool ball
     * example : h = height, o = offset, min = minHeigth, max = maxHeight, of = overflow
     * h + o > maxHeight :
     * of = (h + o) - max
     * h' = max - of
     * h' = max - [(h + o) - max]
     * h' = max - h - o + max
     * h' = 2 * max - h - o
     */
    private double updateWidth(double width) {
        double offset = rand.nextGaussian() * getWidthSeverity();
        return Mathematics.reflectValue(width, offset, getWidthMin(), getWidthMax());
    }

    private double updateHeight(double height) {
        double offset = rand.nextGaussian() * getHeightSeverity();
        return Mathematics.reflectValue(height, offset, getHeightMin(), getHeightMax());
    }

    /**
     * this function reconstruct the peaks
     * this is important if you want the problem to be at a certain given time!
     *
     * @param peaks
     * @param previousMovement
     * @param time             in this case the evaluations
     */
    protected void reconstructPeaks(double[][] peaks, double[][] previousMovement, double time) {
        peaks = new double[getPeakCount()][];
        previousMovement = new double[getPeakCount()][];
        for (int i = 0; i < getPeakCount(); ++i) {
            peaks[i] = new double[getProblemDimension() + 2];
            previousMovement[i] = new double[getProblemDimension()];
        }
        rand = new Random(getSeed());
        initPeaks(peaks, previousMovement, rand);
        for (int i = 0; i < time; ++i) {
            if (time % getFrequency() == 0) {
                movePeaks(peaks, previousMovement, rand);
            }
        }
    }

    /* this is where the frequency of changes come in */
    protected void changeProblemAt(double time) {
        super.changeProblemAt(time);
        movePeaks(peaks, previousMovement, rand);
        /* a little speed up */
        maxPeakHeight = getHeightMax() + 0.1;
        maxPeakHeight = getMaxPeakHeight();
        /* prooving results */
//		myPrint(peaks);
        if (TRACE) writeFile();
    }

    /**
     * this function evaluates every individual in a population
     *
     * @param individual the individal to be evaluated
     * @param time       is not need for synchronous problems
     */

    public void evaluateAt(AbstractEAIndividual individual, double time) {
        double fit = calcFitness(((InterfaceDataTypeDouble) individual).getDoubleData());
        /* fitness is an array, but with this function call, you can fill the array */
        individual.SetFitness(0, fit);
        if (showOfflineError == true)
            calculateOfflineError(individual);
    }

    /**
     * This returns the fitness as a minimization problem.
     *
     * @param position
     * @return
     */
    private double calcFitness(double[] position) {
        return (getMaxPeakHeight() - calcFitnessUnNormalized(position));
    }

    /**
     * Calculate the fitness as a maximization problem.
     *
     * @param position
     * @return
     */
    private double calcFitnessUnNormalized(double[] position) {
        /* calculate maximum of fitness values */
        double maxFit = calculateFitness(position, 0);
        for (int i = 1; i < getPeakCount(); ++i) {
            double fitness = calculateFitness(position, i);
            if (fitness > maxFit)
                maxFit = fitness;
        }
        if (baseFuncType != MovingPeaksBaseFuncEnum.none) {
            // 	check the base function and use it
            maxFit = addBaseFunctionMaximization(position, maxFit);
        }
//		System.out.println("pos maximized fit is " + maxFit);
        return maxFit;
    }

    /**
     * Calc. an n-dim sine between [1,-1];
     *
     * @param x
     * @return
     */
    private double funcSin(double[] x) {
        // an n-dimensional sine
        double sum = 0;
        double rangeX = upperBound - lowerBound;

        for (int i = 0; i < x.length; i++) {
            sum += Math.sin(2 * Math.PI * (x[i] * (5. / rangeX)));
        }
        if (Math.abs(sum / x.length) > 1) System.out.println("!!! sum is " + sum);
        return (sum / x.length);
    }

    /**
     * Remember this is still maximization case, so to make it worse, make it smaller.
     * The returned value must be smaller than the maximum peak height for consistency.
     *
     * @param x
     * @param value
     * @return
     */
    private double addBaseFunctionMaximization(double[] x, double value) {
        switch (baseFuncType) {
            case zeroCut:
                return Math.max(0., value);
            case sineAdditive:
//			System.out.println("val was " + value);
                if (Math.abs(funcSin(x)) > 1.) {
                    System.err.println("!!! sin func is " + funcSin(x));
                }
                return value + (getHeightStandard() * (baseParam / 100.)) * (funcSin(x) - 1.);
            case sineCut:
                return Math.max(value, funcSin(x) - 1);
            default:
                System.err.println("Invalid base function type!");
            case none:
                return value;
        }
    }

    /**
     * This implements InterfaceProblemDouble and does not influence dynamics.
     */
    public double[] eval(double[] x) {
        return new double[]{calcFitness(x)};
    }

    private double getMaxPeakHeight() {
        if (maxPeakHeight > getHeightMax()) {
            int heightIndex = getProblemDimension() + 1;
            maxPeakHeight = peaks[0][heightIndex];
            for (int i = 1; i < getPeakCount(); ++i) {
                if (peaks[i][heightIndex] > maxPeakHeight) {
                    maxPeakHeight = peaks[i][heightIndex];
                }
            }
        }
        return maxPeakHeight;
    }

    private void calculateOfflineError(AbstractEAIndividual individual) {
        if (bestIndividual == null || individual.getFitness(0) < bestIndividual.getFitness(0)) {
            bestIndividual = (AbstractEAIndividual) individual.clone();
        }
        offlineError += bestIndividual.getFitness(0);
    }

    private double getOfflineError() {
        return offlineError / evaluations;
    }

    protected void countEvaluation() {
        super.countEvaluation();
        evaluations += 1.;
    }

    /**
     * Override population evaluation to do some data output.
     * in this case the offline error
     */
    public void evaluatePopulationEnd(Population population) {
        super.evaluatePopulationEnd(population);

        if (showOfflineError == true) {
            setExtraPlot(true);
            if (myplot != null) {
                myplot.jump();
            } else {
                if (TRACE) System.out.println("creating myplot instance");
                this.myplot = new Plot("offline Error", "FunctionCalls", "offline Error");
            }
            myplot.setConnectedPoint(population.getFunctionCalls(), getOfflineError(), 0);
        } else
            myplot = null;
    }

    protected void setExtraPlot(boolean doPlot) {
        if (bExtraPlot && !doPlot) {
            myplot = null;
        } else if (!bExtraPlot && doPlot) {
            if (myplot != null) {
                myplot.jump();
            } else makePlot();
        }
        bExtraPlot = doPlot;
    }

    protected boolean isExtraPlot() {
        return bExtraPlot;
    }

    private void makePlot() {
        if (TRACE) System.out.println("creating myplot instance");
        this.myplot = new Plot("offline Error", "FunctionCalls", "offline Error");
    }


    /**
     * This function calculates the fitness regarding a single peak.
     *
     * @param individual
     * @param peakNumber
     * @return fitness
     */
    public double calculateFitness(double[] position, int peakNumber) {
        double distance = 0.0;
        double fitness = 1000000000;
        for (int i = 0; i < getProblemDimension(); ++i) {
            distance += Math.pow((position[i] - peaks[peakNumber][i]), 2);
        }
        double peakHeight = peaks[peakNumber][getProblemDimension() + 1];
        double peakWidth = peaks[peakNumber][getProblemDimension()];
        int heightIndex = getProblemDimension() + 1;
        int widhtIndex = getProblemDimension();

        switch (getPeakShape()) {
            case shapeF1:/* F1 */
                fitness = peakHeight / (1 + distance * peakWidth); // always positive
                break;
            case shapeCone:/* Cone */
                fitness = peakHeight - peakWidth * Math.sqrt(distance);
                break;
            case shapeHill:    /* Hill */
                fitness = peakHeight - peakWidth * distance - 0.01 * Math.sin(20.0 * distance);
                break;
            case shapeSphere:/* Sphere */ // width is irrelevant??
                fitness = peakHeight - distance;
                break;
            case shapeTwin:/* Twin */
                /* difference to first peak */
                /*static*/
                double[] twinPeak = {1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0};
                distance = peaks[peakNumber][heightIndex] - peaks[peakNumber][widhtIndex] * distance;
                fitness = distance;
                for (int i = 0; i < getProblemDimension(); ++i) {
                    distance = Math.pow(position[i] - peaks[getPeakCount()][i] + twinPeak[i], 2);
                }
                distance = peaks[peakNumber][heightIndex] + twinPeak[heightIndex]
                        - (peaks[peakNumber][widhtIndex] + twinPeak[widhtIndex]);
                if (distance > fitness)
                    fitness = distance;
                break;
        }
        return fitness;
    }

    /**
     * initializes the population, so that the population is adjustet to the problem
     *
     * @param population
     * @param time
     */
    public void initPopulationAt(Population population, double time) {
        /* m_Template is an inherited AbstractEAIndividual, gets casted to the individual adjusted to the problem */
        ((InterfaceDataTypeDouble) m_Template).setDoubleDataLength(getProblemDimension());
        ((InterfaceDataTypeDouble) m_Template).SetDoubleRange(makeRange());

        AbstractOptimizationProblem.defaultInitPopulation(population, m_Template, this);
    }

    /**
     *
     */
    public void initProblem() {
        super.initProblem();
        /* for the offline error */
        evalsSinceChange = 0.0;
        evaluations = 0.0;
        bestIndividual = null;
        offlineError = 0.0;

        peaks = new double[getPeakCount()][];
        previousMovement = new double[getPeakCount()][];
        for (int i = 0; i < getPeakCount(); ++i) {
            peaks[i] = new double[getProblemDimension() + 2];
            previousMovement[i] = new double[getProblemDimension()];
        }
        rand = new Random(getSeed());

        initPeaks(peaks, previousMovement, rand);
        /* a little speed up */
        maxPeakHeight = getHeightMax() + 0.1;
        maxPeakHeight = getMaxPeakHeight();

//		Population pop = getRealOptima();
//		evaluate(pop);
//		for (int i=0; i<pop.size(); i++) {
//			System.out.println(pop.getEAIndividual(i));
//			pop.getEAIndividual(i).setMutationOperator(new MutateESFixedStepSize(0.1));
//			pop.getEAIndividual(i).mutate();
//		}
//		evaluate(pop);
//		System.out.println("-------------------");
//		for (int i=0; i<pop.size(); i++) {
//			System.out.println(pop.getEAIndividual(i));
//		}
//		setPeakShape(MovingPeaksPeakShape.shapeSphere);
//		setBaseFunction(MovingPeaksBaseFuncEnum.baseNone);
//		evaluate(pop);
//		System.out.println("-------------------");
//		for (int i=0; i<pop.size(); i++) {
//			System.out.println(pop.getEAIndividual(i));
//		}
//		System.exit(1);
    }

    /**
     * Whenever the environment (or the time, primarily) has changed, some problem
     * properties (like stored individual fitness) may require updating.
     *
     * @param severity the severity of the change (time measure)
     */
    public void resetProblem(double severity) {
        if ((evaluations == 0.0) && (bestIndividual != null))
            this.evaluateAt(bestIndividual, getCurrentProblemTime());
    }

    /* if an optimum is found, sets the individual to it and returns true */
    public AbstractEAIndividual getCurrentOptimum() {

        AbstractEAIndividual endy = (AbstractEAIndividual) ((AbstractEAIndividual) m_Template).clone();
        InterfaceDataTypeDouble indy = (InterfaceDataTypeDouble) endy;

        indy.setDoubleDataLength(getProblemDimension());
        indy.SetDoubleRange(makeRange());

        int heightIndex = getProblemDimension() + 1;
        int maxPeakIndex = 0;
        double maxPeakHeight = peaks[0][heightIndex];
        for (int i = 1; i < getPeakCount(); ++i) {
            if (peaks[i][heightIndex] > maxPeakHeight) {
                maxPeakHeight = peaks[i][heightIndex];
                maxPeakIndex = i;
            }
        }
        double[] optIndyDoubleData = indy.getDoubleData();
        for (int i = 0; i < getProblemDimension(); ++i) {
            optIndyDoubleData[i] = peaks[maxPeakIndex][i];
        }
        /* sets the optimal data to the individual */
        indy.SetDoubleGenotype(optIndyDoubleData);
        /* evaluates the individual to set the fitness */
        evaluateAt((AbstractEAIndividual) indy, getCurrentProblemTime());
        return endy;
    }

    public double[][] makeRange() {
        double[][] range = new double[getProblemDimension()][2];
        for (int i = 0; i < getProblemDimension(); ++i) {
            range[i][0] = getRangeLowerBound(i);
            range[i][1] = getRangeUpperBound(i);
        }
        return range;
    }

    public Object clone() {
        return new MovingPeaksProblem(this);
    }

    /**
     * *******************************************************************************************************************
     * These are for GUI
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        return getName();
    }

    public String getName() {
        return "Moving-Peaks-Problem(" + getPeakCount() + "_Peaks)";
    }

    public String globalInfo() {
        return "The Moving Peaks Benchmark";
    }

    public void setEAIndividual(InterfaceDataTypeDouble indy) {
        this.m_Template = (AbstractEAIndividual) indy;
    }

    public InterfaceDataTypeDouble getEAIndividual() {
        return (InterfaceDataTypeDouble) this.m_Template;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        if (lambda > 1.0)
            this.lambda = 1.0;
        else if (lambda < 0.0)
            this.lambda = 0.0;
        else
            this.lambda = lambda;
    }

    public String lambdaTipText() {
        return "direction of movement: [0,1] 0 = random, 1 = depending";
    }

    public int getPeakCount() {
        return numberOfPeaks;
    }

    public void setPeakCount(int numberOfPeaks) {
        if ((numberOfPeaks > 0) && (numberOfPeaks != this.numberOfPeaks)) {
            this.numberOfPeaks = numberOfPeaks;
            peakPopValid = false;
        }
    }

    public String peakCountTipText() {
        return "number of peaks in Moving Peaks landscape";
    }

    public MovingPeaksPeakShape getPeakShape() {
        return peakShape;
    }

    public void setPeakShape(MovingPeaksPeakShape peak_shape) {
        this.peakShape = peak_shape;
        peakPopValid = false;
    }

    public String peakShapeTipText() {
        return "Shape of the peaks, F1, cone, hill, sphere, or twin";
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
        this.rand = new Random(seed);
        peakPopValid = false;
    }

    public String seedTipText() {
        return "seed for landscape creation";
    }

    public int getProblemDimension() {
        return dimension;
    }

    public void setProblemDimension(int dimension) {
        if ((dimension > 0) && (dimension != this.dimension)) {
            this.dimension = dimension;
            peakPopValid = false;
        }
    }

    public String problemDimensionTipText() {
        return "problem dimension in Moving Peaks landscape";
    }

    public double getRangeUpperBound(int dim) {
        return upperBound;
    }

    public void setRangeUpperBound(double maxBoundary) {
        if (maxBoundary > upperBound) {
            this.upperBound = maxBoundary;
            peakPopValid = false;
        }
    }

    public String rangeUpperBoundTipText() {
        return "max value of Moving Peaks landscape";
    }

    public double getRangeLowerBound(int dim) {
        return lowerBound;
    }

    public void setRangeLowerBound(double minBoundary) {
        if (minBoundary < upperBound) {
            this.lowerBound = minBoundary;
            peakPopValid = false;
        }
    }

    public String rangeLowerBoundTipText() {
        return "min value of Moving Peaks landscape";
    }

    public double getHeightMax() {
        return maxHeight;
    }

    public void setHeightMax(double maxHeight) {
        if (maxHeight > getHeightMin() && maxHeight > getHeightStandard())
            this.maxHeight = maxHeight;
    }

    public String heightMaxTipText() {
        return "max height of peaks in Moving Peaks landscape";
    }

    public double getHeightMin() {
        return minHeight;
    }

    public void setHeightMin(double minHeight) {
        if (minHeight < getHeightMax() && minHeight < getHeightStandard())
            this.minHeight = minHeight;
    }

    public String heightMinTipText() {
        return "min height of peaks in Moving Peaks landscape";
    }

    public double getHeightStandard() {
        return standardHeight;
    }

    public void setHeightStandard(double standardHeight) {
        if (standardHeight < getHeightMax() && standardHeight > getHeightMin()) {
            this.standardHeight = standardHeight;
            peakPopValid = false;
        }
    }

    public String heightStandardTipText() {
        return "standart height of peaks in Moving Peaks landscape";
    }

    public double getHeightSeverity() {
        return heightSeverity;
    }

    public void setHeightSeverity(double heightSeverity) {
        if (heightSeverity > 0.0)
            this.heightSeverity = heightSeverity;
    }

    public String heightSeverityTipText() {
        return "standart deviation of changes in height of peaks";
    }

    public double getWidthMax() {
        return maxWidth;
    }

    public void setWidthMax(double maxWidth) {
        if (maxWidth > getWidthMin() && maxWidth > 0.0 && maxWidth > getWidthStandard())
            this.maxWidth = maxWidth;
    }

    public String widthMaxTipText() {
        return "max width of peaks in Moving Peaks landscape";
    }

    public double getWidthMin() {
        return minWidth;
    }

    public void setWidthMin(double minWidth) {
        if (minWidth < getWidthMax() && minWidth > 0.0 && minWidth < getWidthStandard())
            this.minWidth = minWidth;
    }

    public String widthMinTipText() {
        return "min width of peaks in Moving Peaks landscape";
    }

    public double getWidthStandard() {
        return standardWidth;
    }

    public void setWidthStandard(double standardWidth) {
        if (standardWidth < getWidthMax() && standardWidth > getWidthMin()) {
            this.standardWidth = standardWidth;
            peakPopValid = false;
        }
    }

    public String widthStandardTipText() {
        return "standart width of peaks in Moving Peaks landscape";
    }

    public double getWidthSeverity() {
        return widthSeverity;
    }

    public void setWidthSeverity(double widthSeverity) {
        if (widthSeverity > 0.0)
            this.widthSeverity = widthSeverity;
    }

    public String widthSeverityTipText() {
        return "standard deviation of changes in width of peaks";
    }

    public void setSeverity(double severity) {
        super.setSeverity((severity > 0.0) ? severity : 1.0);
    }

    public String severityTipText() {
        return "length of changes";
    }

    public boolean isShowOfflineError() {
        return showOfflineError;
    }

    public void setShowOfflineError(boolean showOfflineError) {
        this.showOfflineError = showOfflineError;
    }

    public String showOfflineErrorTipText() {
        return "shows offline error, makes evaluation slow";
    }

    /**
     * ***********************************************************************
     * These are for debugging and determining the output file
     */

    public void myPrint(double[][] toPrint) {
        for (int i = 0; i < toPrint.length; i++) {
            for (int j = 0; j < toPrint[i].length; ++j) {
                if (j != 2)
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

    private void writeFile() {
        if (fw == null) {
            try {
                fw = new FileWriter("MovingPeaksBenchmark.txt");
            } catch (IOException e) {
                System.err.println("Konnte Datei nicht erstellen");
            }
        } else {
            try {
                fw.write("Problem wurde " + changeCounter + " mal geaendert!\n");
                fw.write(evaluations + " Evaluierungen wurden gemacht\n");
                fw.write(myPrints(peaks));

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

    private String myPrints(double[][] toPrint) {
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

    public double getEpsilon() {
        return epsilonPeakFound;
    }

    public double getMaximumPeakRatio(Population pop) {
        return AbstractMultiModalProblemKnown.getMaximumPeakRatio(this, pop, getEpsilon());
    }

    public int getNumberOfFoundOptima(Population pop) {
        return AbstractMultiModalProblemKnown.getNoFoundOptimaOf(this, pop);
    }

    /**
     * This may return more optima than can really be found, as
     * peaks may overlap and overshadow each other.
     */
    public Population getRealOptima() {
        if (!peakPopValid || (peakPop == null)) {
            // create indys containing peak coords and add them to new pop
//			if (peakPop == null || peakPop.size() != getNumberOfPeaks()) {
            // fill pop with templates
            peakPop = new Population(getPeakCount());
            AbstractEAIndividual indy;
            for (int i = 0; i < getPeakCount(); i++) {
                indy = (AbstractEAIndividual) getIndividualTemplate().clone();
                peakPop.add(indy);
            }
//			}
            for (int i = 0; i < getPeakCount(); i++) {
                // really set optima
                double[] peakPos = new double[getProblemDimension()];
                System.arraycopy(peaks[i], 0, peakPos, 0, getProblemDimension());
//				AbstractEAIndividual 
                indy = peakPop.getEAIndividual(i);
                ((InterfaceDataTypeDouble) indy).SetDoubleGenotype(peakPos);
                indy.SetFitness(0, calcFitnessUnNormalized(peakPos));
//				System.out.println("Opt at " + BeanInspector.toString(peakPos));
//				System.out.println("Fit is " + BeanInspector.toString(indy.getFitness()));
            }
            peakPopValid = true;
        }
        return peakPop;
    }

    public void initListOfOptima() {
        // nothing to do for our case. peaks are initialized anyways and
        // updated only when requested and necessary.
    }

    /**
     * @return the epsilonPeakFound
     */
    public double getEpsilonPeakFound() {
        return epsilonPeakFound;
    }

    /**
     * @param epsilonPeakFound the epsilonPeakFound to set
     */
    public void setEpsilonPeakFound(double epsilonPeakFound) {
        this.epsilonPeakFound = epsilonPeakFound;
    }

    public String epsilonPeakFound() {
        return "The epsilon distance within which an optimum is considered found";
    }


    // adding a base function (e.g. multi-dim sine) to make the problem harder
    public void setBaseFunction(MovingPeaksBaseFuncEnum funcType) {
        baseFuncType = funcType;
    }

    public MovingPeaksBaseFuncEnum getBaseFunction() {
        return baseFuncType;
    }

    public String baseFunctionTipText() {
        return "This allows setting a basic (minimal) function to be combined with the peaks landscape";
    }

    public double getBaseParam() {
        return baseParam;
    }

    public void setBaseParam(double baseParam) {
        this.baseParam = baseParam;
    }

    public String baseParamTipText() {
        return "For additive sine: the sine amplitude in percent of std. peak height.";
    }

    ///////////////////////
    public double functionValue(double[] point) {
        return calcFitness(project2DPoint(point));
    }

    public double[] project2DPoint(double[] point) {
        return Mathematics.expandVector(point, getProblemDimension(), 0.);
    }

    public double[][] get2DBorder() {
        return makeRange();
    }

    public String toString() {
        return getName();
    }

    public String getAdditionalFileStringHeader(PopulationInterface pop) {
        return "optsFound";
    }

    public String getAdditionalFileStringValue(PopulationInterface pop) {
//		String ret = super.getAdditionalFileStringValue(pop);
        int found = getNumberOfFoundOptima((Population) pop);
        return String.valueOf(found);
    }

    /**
     * Create a Moving Peaks instance.
     *
     * @param dim
     * @param seed
     * @param numPeaks
     * @param baseFunc  so far, 0 is none (peaks only), 1 is minium at zero and 2 is a multidimensional sine
     * @param frequency
     * @param minH
     * @param stdH
     * @param maxH
     * @return
     */
    public static MovingPeaksProblem makePeaksProblem(int dim, int seed, int numPeaks,
                                                      MovingPeaksPeakShape peakShape, MovingPeaksBaseFuncEnum baseFunc, double baseParam,
                                                      double frequency, double minH, double stdH, double maxH) {
        MovingPeaksProblem mpp = new MovingPeaksProblem();
        mpp.setProblemDimension(dim);
        mpp.setFrequency(frequency);
        mpp.setPeakCount(numPeaks);
        mpp.setPeakShape(peakShape);
        mpp.setHeightMin(minH);
        mpp.setHeightStandard(stdH);
        mpp.setHeightMax(maxH);
        mpp.setSeed(seed);
        mpp.setBaseFunction(baseFunc);
        mpp.setBaseParam(baseParam);
        return mpp;
    }

    public boolean fullListAvailable() {
        return true;
    }
}
