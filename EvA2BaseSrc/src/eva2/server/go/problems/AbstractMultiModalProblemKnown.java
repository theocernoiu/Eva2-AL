package eva2.server.go.problems;

import java.util.List;

import eva2.server.go.PopulationInterface;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.operators.distancemetric.InterfaceDistanceMetric;
import eva2.server.go.operators.distancemetric.PhenotypeMetric;
import eva2.server.go.operators.postprocess.PostProcess;
import eva2.server.go.populations.Population;

import eva2.server.go.problems.Interface2DBorderProblem;
import eva2.tools.EVAERROR;

public abstract class AbstractMultiModalProblemKnown extends AbstractProblemDouble implements Interface2DBorderProblem, InterfaceMultimodalProblemKnown {
    protected static InterfaceDistanceMetric m_Metric = new PhenotypeMetric();
    private double m_GlobalOpt = 0;
    protected Population m_ListOfOptima;
    protected double m_Epsilon = 0.05;
    //	protected double[][]                m_Range;
//	protected double[]                  m_Extrema;
    protected int m_ProblemDimension = 2;
    // if the global optimum is zero and we want to see logarithmic plots, the offset must be a little lower. see addOptimum()
    protected boolean makeGlobalOptUnreachable = false;

    public AbstractMultiModalProblemKnown() {
        this.m_ProblemDimension = 2;
        this.m_Template = new ESIndividualDoubleData();
//		this.m_Extrema          = new double[2];
//		this.m_Range            = makeRange();
//		this.m_Extrema[0]       = -2;
//		this.m_Extrema[1]       = 6;
    }

    protected void cloneObjects(AbstractMultiModalProblemKnown b) {
        super.cloneObjects(b);
        if (b.m_ListOfOptima != null)
            this.m_ListOfOptima = (Population) ((Population) b.m_ListOfOptima).clone();
//		if (b.m_Range != null) {
//			this.m_Range          = new double[b.m_Range.length][b.m_Range[0].length];
//			for (int i = 0; i < this.m_Range.length; i++) {
//				for (int j = 0; j < this.m_Range[i].length; j++) {
//					this.m_Range[i][j] = b.m_Range[i][j];
//				}
//			}
//		}		
        this.m_ProblemDimension = b.m_ProblemDimension;
        this.m_GlobalOpt = b.m_GlobalOpt;
        this.m_Epsilon = b.m_Epsilon;
//		if (b.m_Extrema != null) {
//		this.m_Extrema          = new double[b.m_Extrema.length];
//		for (int i = 0; i < this.m_Extrema.length; i++) {
//			this.m_Extrema[i] = b.m_Extrema[i];
//		}
//	}
    }

    public AbstractMultiModalProblemKnown(AbstractMultiModalProblemKnown b) {
        cloneObjects(b);
    }

//	/** This method returns a deep clone of the problem.
//	 * @return  the clone
//	 */
//	public Object clone() {
//		return (Object) new AbstractMultiModalProblem(this);
//	}

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        AbstractEAIndividual tmpIndy;

        population.clear();

//		this.m_ProblemDimension = 2;
        ((InterfaceDataTypeDouble) this.m_Template).setDoubleDataLength(this.m_ProblemDimension);
        ((InterfaceDataTypeDouble) this.m_Template).SetDoubleRange(makeRange());
        for (int i = 0; i < population.getTargetSize(); i++) {
            tmpIndy = (AbstractEAIndividual) ((AbstractEAIndividual) this.m_Template).clone();
            tmpIndy.init(this);
            population.add(tmpIndy);
        }
        // population init must be last
        // it set's fitcalls and generation to zero
        population.init();
        if (m_ListOfOptima == null) {
            this.m_GlobalOpt = Double.NEGATIVE_INFINITY;
            m_ListOfOptima = new Population();
            this.initListOfOptima();
        }
    }

    public void initProblem() {
        super.initProblem();
        this.m_GlobalOpt = Double.NEGATIVE_INFINITY;
        m_ListOfOptima = new Population();
        this.initListOfOptima();
        if (!fullListAvailable() && (Double.isInfinite(m_GlobalOpt))) m_GlobalOpt = 0;
    }

    /**
     * Ths method allows you to evaluate a simple bit string to determine the fitness
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] eval(double[] x) {
        x = rotateMaybe(x);
        double[] result = new double[1];
        result[0] = this.m_GlobalOpt - evalUnnormalized(x)[0];
        return result;
    }

    /**
     * This method returns the unnormalized (and unrotated!) function value for an maximization problem.
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public abstract double[] evalUnnormalized(double[] x);

    /**
     * This method returns the header for the additional data that is to be written into a file
     *
     * @param pop The population that is to be refined.
     * @return String
     */
    public String getAdditionalFileStringHeader(PopulationInterface pop) {
        return "#Optima found \tMaximum Peak Ratio \t" + super.getAdditionalFileStringHeader(pop);
    }

    /**
     * This method returns the additional data that is to be written into a file
     *
     * @param pop The population that is to be refined.
     * @return String
     */
    public String getAdditionalFileStringValue(PopulationInterface pop) {
        String result = "";
//		result += AbstractEAIndividual.getDefaultDataString(pop.getBestIndividual()) +"\t";
        result += this.getNumberOfFoundOptima((Population) pop) + "\t";
        result += this.getMaximumPeakRatio((Population) pop);
        return result + "\t" + super.getAdditionalFileStringValue(pop);
    }
//
//	/** This method returns a string describing the optimization problem.
//	 * @return The description.
//	 */
//	public String getStringRepresentation() {
//		String result = "";
//
//		result += "M0 function:\n";
//		result += "This problem has one global and one local optimum.\n";
//		result += "Parameters:\n";
//		result += "Dimension   : " + this.m_ProblemDimension +"\n";
//		result += "Noise level : " + this.getNoise() + "\n";
//		result += "Solution representation:\n";
//		//result += this.m_Template.getSolutionRepresentationFor();
//		return result;
//	}

    /**********************************************************************************************************************
     * Implementation of InterfaceMultimodalProblemKnown
     */

    /**
     * This method allows you to add a 2d optima to the list of optima
     *
     * @param x
     * @param y
     */
    protected void add2DOptimum(double x, double y) {
        double[] point = new double[2];
        point[0] = x;
        point[1] = y;
        addOptimum(point);
    }

    /**
     * This method allows you to add a 2d optima to the list of optima
     *
     * @param x
     * @param y
     */
    protected void addOptimum(double[] point) {
        InterfaceDataTypeDouble tmpIndy;
        tmpIndy = (InterfaceDataTypeDouble) ((AbstractEAIndividual) this.m_Template).clone();
        tmpIndy.SetDoubleGenotype(point);
        ((AbstractEAIndividual) tmpIndy).SetFitness(evalUnnormalized(point));
        if (((AbstractEAIndividual) tmpIndy).getFitness(0) >= m_GlobalOpt) {
            m_GlobalOpt = ((AbstractEAIndividual) tmpIndy).getFitness(0);
            if (makeGlobalOptUnreachable) {
                double tmp = m_GlobalOpt;
                double dx = 1e-30;
                while (tmp == m_GlobalOpt) {
                    // this increases the optimum until there is a real difference.
                    // tries to avoid zero y-values which break the logarithmic plot
                    tmp += dx;
                    dx *= 10;
                }
                m_GlobalOpt = tmp;
            }
        }
        if (isDoRotation()) {
            point = inverseRotateMaybe(point); // theres an inverse rotation required
            tmpIndy.SetDoubleGenotype(point);
        }
        this.m_ListOfOptima.add(tmpIndy);
    }

    /**
     * This method will prepare the problem to return a list of all optima
     * if possible and to return quality measures like NumberOfOptimaFound and
     * the MaximumPeakRatio. When implementing, use the addOptimum(double[])
     * method for every optimum, as it keeps track the global optimum.
     * This method will be called on initialization.
     */
    public abstract void initListOfOptima();

    /**
     * This method returns a list of all optima as population
     *
     * @return population
     */
    public Population getRealOptima() {
        return this.m_ListOfOptima;
    }

    /**
     * Return true if the full list of optima is available, else false.
     *
     * @return
     */
    public boolean fullListAvailable() {
        return ((getRealOptima() != null) && (getRealOptima().size() > 0));
    }

    /**
     * This method returns the Number of Identified optima
     *
     * @param pop A population of possible solutions.
     * @return int
     */
    public int getNumberOfFoundOptima(Population pop) {
        return getNoFoundOptimaOf(this, pop);
    }

    public static int getNoFoundOptimaOf(InterfaceMultimodalProblemKnown mmProb, Population pop) {
        List<AbstractEAIndividual> sols = PostProcess.getFoundOptima(pop, mmProb.getRealOptima(), mmProb.getDefaultAccuracy(), true);
        return sols.size();
    }

    /**
     * This method returns the maximum peak ratio, which is the ratio of found fitness values corresponding to
     * known optima with the internal epsilon criterion and the sum of all fitness values seen as maximization.
     * Thus, if all optima are perfectly found, 1 is returned. If no optimum is found, zero is returned.
     * A return value of 0.5 may mean, e.g., that half of n similar optima have been found perfectly, or that 1 major
     * optimum of equal weight than all the others has been found perfectly, or that all optima have been found
     * with about 50% accuracy, etc.
     *
     * @param pop A population of possible solutions.
     * @return double
     */
    public double getMaximumPeakRatio(Population pop) {
        return getMaximumPeakRatio(this, pop, m_Epsilon);
    }

    /**
     * Returns -1 if the full list is not available. Otherwise calculates the maximum peak ratio
     * based on the full list of known optima.
     *
     * @param mmProb
     * @param pop
     * @param epsilon
     * @return
     */
    public static double getMaximumPeakRatio(InterfaceMultimodalProblemKnown mmProb, Population pop, double epsilon) {
        double foundInvertedSum = 0, sumRealMaxima = 0;
        if (!mmProb.fullListAvailable()) return -1;
        Population realOpts = mmProb.getRealOptima();
        double tmp, maxOpt = realOpts.getEAIndividual(0).getFitness(0);
        sumRealMaxima = maxOpt;
        for (int i = 1; i < realOpts.size(); i++) {
            // search for the maximum fitness (for the maximization problem)
            // also sum up the fitness values
            maxOpt = Math.max(maxOpt, realOpts.getEAIndividual(i).getFitness(0));
            sumRealMaxima += realOpts.getEAIndividual(i).getFitness(0);
            if (realOpts.getEAIndividual(i).getFitness(0) < 0)
                EVAERROR.errorMsgOnce("Warning: avoid negative maxima in AbstractMultiModalProblemKnown!");
        }
        AbstractEAIndividual[] optsFound = PostProcess.getFoundOptimaArray(pop, realOpts, epsilon, true);
        for (int i = 0; i < realOpts.size(); i++) {
            // sum up the found optimal fitness values
            if (optsFound[i] != null) {
                tmp = (maxOpt - optsFound[i].getFitness(0));
                if (tmp < 0)
                    EVAERROR.errorMsgOnce("warning: for the MPR calculation, negative fitness values may disturb the allover result (AbstractMultiModalProblemKnown)");
                foundInvertedSum += Math.max(0., tmp);
//				System.out.println("foundInvertedSum = " + foundInvertedSum);
            }
        }
//		System.out.println("foundSum: " + foundInvertedSum + " realsum: " + sumRealMaxima + " ratio: " + foundInvertedSum/sumRealMaxima);
        return foundInvertedSum / sumRealMaxima;
    }

//	public double getMaximumPeakRatio(Population pop) {
//		double                  result = 0, sum = 0;
//		AbstractEAIndividual   posOpt, opt;
//		boolean[]               found = new boolean[this.m_Optima.size()];
//		for (int i = 0; i < found.length; i++) {
//			found[i] = false;
//			sum += ((AbstractEAIndividual)this.m_Optima.get(i)).getFitness(0) ;
//			//System.out.println("Optimum " + i + ".: " + (((AbstractEAIndividual)this.m_Optima.get(i)).getFitness(0)));
//		}
//
//		for (int i = 0; i < pop.size(); i++) {
//			posOpt = (AbstractEAIndividual) pop.get(i);
//			for (int j = 0; j < this.m_Optima.size(); j++) {
//				if (!found[j]) {
//					opt = (AbstractEAIndividual) this.m_Optima.get(j);
//					if (this.m_Metric.distance(posOpt, opt) < this.m_Epsilon) {
//						found[j] = true;
//						result += this.m_GlobalOpt - posOpt.getFitness(0);
//						//System.out.println("Found Optimum " + j + ".: " + (this.m_GlobalOpt - posOpt.getFitness(0)));
//					}
//				}
//			}
//		}
//		return result/sum;
//	}
    /**********************************************************************************************************************
     * These are for GUI
     */

//	/** This method returns this min and may fitness occuring
//	* @return double[]
//	*/
//	public double[] getExtrema() {
//	double[] range = new double[2];
//	range[0] = -5;
//	range[1] = 5;
//	return range;
//	}

//	/**
//	 * @return the m_Epsilon
//	 */
//	public double getEpsilon() {
//		return m_Epsilon;
//	}

    /**
     * @param epsilon the m_Epsilon to set
     */
    public void setDefaultAccuracy(double epsilon) {
        super.SetDefaultAccuracy(epsilon);
    }
//
//	public String epsilonTipText() {
//		return "Epsilon criterion indicating whether an optimum was found";
//	}

    @Override
    public int getProblemDimension() {
        return m_ProblemDimension;
    }
}