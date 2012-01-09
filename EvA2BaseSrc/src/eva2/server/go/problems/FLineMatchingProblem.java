/*
 * Created on Oct 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package eva2.server.go.problems;

/**
 * @author sehnke
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractOptimizationProblem;
import eva2.server.go.problems.linematching.SingletonPicture;
import eva2.server.go.problems.linematching.SingletonWhitePointList;
import eva2.server.go.strategies.InterfaceOptimizer;

/**
 * Created by IntelliJ IDEA. User: streiche Date: 24.03.2003 Time: 17:58:55 To
 * change this template use Options | File Templates.
 */
public class FLineMatchingProblem extends AbstractOptimizationProblem implements
        java.io.Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    protected AbstractEAIndividual m_OverallBest = null;

    protected int m_ProblemDimension = 11;

    protected int m_ShowGenerations = 200;

    protected static double overallFit = 0;

    private boolean m_ProblemShow = false;

    private int m_Generation = 0;

    private double m_Threshold = 8.0;

    private boolean m_ShowMedian = false;
    private int m_LazyEval = 1;

    // Standard Constructor
    public FLineMatchingProblem() {
        this.m_Template = new ESIndividualDoubleData();
    }

    // Copy Constructor
    public FLineMatchingProblem(FLineMatchingProblem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template)
                    .clone();
        //Find BlueProblem
        if (b.m_OverallBest != null)
            this.m_OverallBest = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_OverallBest)
                    .clone();
        this.m_ProblemDimension = b.m_ProblemDimension;
    }

    public double getOverallFitnes() {

        return FLineMatchingProblem.overallFit;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FLineMatchingProblem(this);
    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        this.m_OverallBest = null;
        SingletonWhitePointList.instance();
    }

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        this.m_OverallBest = null;

        ((InterfaceDataTypeDouble) this.m_Template)
                .setDoubleDataLength(this.m_ProblemDimension);
        double[][] range = new double[SingletonWhitePointList.getDimension()][2];
        double[] param = new double[SingletonWhitePointList.getDimension()];

        for (int i = 0; i < SingletonWhitePointList.getDimension(); i++) {
            range[i][0] = SingletonWhitePointList.getParameterRange(i)[0];
            range[i][1] = SingletonWhitePointList.getParameterRange(i)[1];
        }
        ((InterfaceDataTypeDouble) this.m_Template).SetDoubleRange(range);

        for (int j = 0; j < SingletonWhitePointList.getDimension(); j++) {
            param[j] = SingletonWhitePointList.getParameter(j);
        }

        AbstractOptimizationProblem.defaultInitPopulation(population, m_Template, this);
    }

//	/**
//	 * This method evaluates a given population and set the fitness values
//	 * accordingly
//	 * 
//	 * @param population
//	 *            The population that is to be evaluated.
//	 */
//	public void evaluate(Population population) {
//		AbstractEAIndividual tmpIndy;
//
//		//System.out.println("Generation: " + population.getGeneration());
////		overallFit = 0;
//		//if ((this.m_OverallBest!=null) && (population.getGeneration()>2)) population.add(0,this.m_OverallBest);
//		this.m_OverallBest=null;
//		for (int i = 0; i < population.size(); i++) {
//			tmpIndy = (AbstractEAIndividual) population.get(i);
//            tmpIndy.resetConstraintViolation();
//			this.evaluate(tmpIndy);
////			overallFit += tmpIndy.getFitness(0);
//			//System.out.println(tmpIndy.getFitness(0));
//			population.incrFunctionCalls();
//		}
//		
//		evaluatePopulationEnd(population);
//	}

    public void evaluatePopulationEnd(Population population) {
        m_Generation = population.getGeneration() * population.size() / 100;
        overallFit = 0;
        for (int i = 0; i < population.size(); i++) overallFit += population.getEAIndividual(i).getFitness(0);

        double xPoint = 0;
        double yPoint = 0;
        byte[] joByte = new byte[3];
        joByte[0] = 31;
        joByte[1] = 15;
        joByte[2] = 15;

        if ((population.getGeneration() == m_ShowGenerations)
                && (this.m_ProblemShow)) {
            SingletonPicture.instance().drawPicture();
            SingletonWhitePointList.instance().showWorld(m_Generation);
            SingletonPicture.instance().drawPicture();
        }

        double[] bestSol;
        if (m_OverallBest instanceof InterfaceDataTypeDouble) {
            bestSol = ((InterfaceDataTypeDouble) m_OverallBest).getDoubleData();
        } else {
            System.err.println("Double valued data type required!");
            bestSol = AbstractEAIndividual.getDoublePosition(m_OverallBest);
        }
        if ((population.getGeneration() % m_ShowGenerations == 1)
                && (this.m_ProblemShow)) {
            SingletonWhitePointList.instance().showWorld(m_Generation);
            for (int i = 0; i < m_ProblemDimension; i++) {
                System.out.println(bestSol[i]);
            }

        }
        if (this.m_ProblemShow) {
            for (int i = 0; i < SingletonWhitePointList.getNumbOfWhitePoints(); i += m_LazyEval) {
                xPoint = SingletonWhitePointList.transform(
                        SingletonWhitePointList.getPoint(i)[0]
                                - SingletonWhitePointList.PIC_WIDTH / 2,
                        SingletonWhitePointList.getPoint(i)[1]
                                - SingletonWhitePointList.PIC_HEIGHT / 2,
                        bestSol)[0];
                yPoint = SingletonWhitePointList.transform(
                        SingletonWhitePointList.getPoint(i)[0]
                                - SingletonWhitePointList.PIC_WIDTH / 2,
                        SingletonWhitePointList.getPoint(i)[1]
                                - SingletonWhitePointList.PIC_HEIGHT / 2,
                        bestSol)[1];

                SingletonPicture.instance().setPixel(
                        (int) xPoint + SingletonWhitePointList.PIC_WIDTH / 2,
                        (int) yPoint + SingletonWhitePointList.PIC_HEIGHT / 2,
                        joByte);

            }
        }

    }

    /**
     * This method evaluate a single individual and sets the fitness values
     *
     * @param individual The individual that is to be evaluated
     */
    public void evaluate(AbstractEAIndividual individual) {
        double[] x;
        double[] fitness;

        x = new double[((InterfaceDataTypeDouble) individual).getDoubleData().length];

        System.arraycopy(
                ((InterfaceDataTypeDouble) individual).getDoubleData(), 0, x,
                0, x.length);
        double[][] r = ((InterfaceDataTypeDouble) individual).getDoubleRange();
        //for (int i=0; i<11; i++)
        //System.out.print(x[i] + " ["+r[i][0]+", " +r[i][1]+"] ");

        //System.out.println();

        fitness = this.doEvaluation(x);
        for (int i = 0; i < fitness.length; i++) {
            // set the fitness of the individual
            individual.SetFitness(i, fitness[i]);
        }
        if ((this.m_OverallBest == null)
                || (this.m_OverallBest.getFitness(0) > individual.getFitness(0))) {
            this.m_OverallBest = (AbstractEAIndividual) individual.clone();
        }
    }

    /**
     * This method returns a double value that will be displayed in a fitness
     * plot. In this Problem this double value is the median fitness of the
     * Population
     *
     * @param pop The population that is to be refined.
     * @return Double value
     */
    public Double getDoublePlotValue(Population pop) {
        if (m_ShowMedian) {
            return new Double(overallFit / (double) pop.size());
        } else {
            return new Double((double) ((AbstractEAIndividual) (pop.get(pop.getIndexOfBestIndividualPrefFeasible()))).getFitness(0));
        }
    }

    /**
     * Ths method allows you to evaluate a simple bit string to determine the
     * fitness
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] doEvaluation(double[] x) {

        double[] result = new double[1];
        result[0] = 0.0;
        double xPoint = 0;
        double yPoint = 0;
        int[] skill = new int[2];
        double[] point = new double[2];
        double maxscore = 0;

        for (int i = 0; i < SingletonWhitePointList.getNumbOfWhitePoints(); i += m_LazyEval) {
            point = SingletonWhitePointList.getPoint(i);
            skill = SingletonWhitePointList.transform(point[0]
                    - SingletonWhitePointList.PIC_WIDTH / 2, point[1]
                    - SingletonWhitePointList.PIC_HEIGHT / 2, x);
            xPoint = skill[0];
            yPoint = skill[1];

            double dist = Math.sqrt(point[0] * point[0] + point[1] * point[1]);
            maxscore += dist;
            if (SingletonWhitePointList.onLine(xPoint, yPoint, m_Generation))
                result[0] += dist;
        }
        result[0] = maxscore / result[0] - m_Threshold;
        return result;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "Problem to find a blue square in a noise picture:\n";
        result += "Here the individual codes a pixel in a picture. For the individual the goal is to find a pixel with maximum intensiti in the blue channel.\n";
        result += "Parameters:\n";
        result += "Dimension   : 2"/** + this.m_ProblemDimension **/ + "\n";
        return result;
    }

    /***************************************************************************
     * These are for GUI
     */
    /**
     * This method allows the CommonJavaObjectEditorPanel to read the name to
     * the current object.
     *
     * @return The name.
     */
    public String getName() {
        return "Line Matching Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Problem is to find the center of a blue Square in a noise-picture.";
    }

    /**
     * This method allows you to set the number of mulitruns that are to be
     * performed, necessary for stochastic optimizers to ensure reliable
     * results.
     *
     * @param multiruns The number of multiruns that are to be performed
     */
    public void setProblemDimension(int multiruns) {
        this.m_ProblemDimension = 11;
    }

    public int getProblemDimension() {
        return this.m_ProblemDimension;
    }

    public String problemDimensionTipText() {
        return "Length of the x vector at is to be optimized.";
    }

    /**
     * This method allows you to choose if the Picture is shown
     */
    public void setShow(boolean show) {
        this.m_ProblemShow = show;
    }

    public boolean getShow() {
        return this.m_ProblemShow;
    }

    public String showTipText() {
        return "Picture will be shown";
    }

    public void setShowMedian(boolean showMedian) {
        this.m_ShowMedian = showMedian;
    }

    public boolean getShowMedian() {
        return this.m_ShowMedian;
    }

    public String showMedianTipText() {
        return "Plot the median fitness of the whole Population";
    }

    public void setLazyEval(int lazyEval) {
        this.m_LazyEval = lazyEval;
    }

    public int getLazyEval() {
        return this.m_LazyEval;
    }

    public String lazyEvalTipText() {
        return "Only every x. point will be used for the evaluation";
    }

    public void setThresh(double thresh) {
        this.m_Threshold = thresh;
    }

    public double getThresh() {
        return this.m_Threshold;
    }

    public String threshTipText() {
        return "Amount of whitepointscore of white points that are not from linepoints";
    }

    public void setShowGen(int showGen) {
        this.m_ShowGenerations = showGen;
    }

    public int getShowGen() {
        return this.m_ShowGenerations;
    }

    public String showGenTipText() {
        return "Picture will be shown at this number of generations";
    }

    /**
     * This method allows you to choose the EA individual
     *
     * @param indy The EAIndividual type
     */
    public void setEAIndividual(InterfaceDataTypeDouble indy) {
        this.m_Template = (AbstractEAIndividual) indy;
    }

    public InterfaceDataTypeDouble getEAIndividual() {
        return (InterfaceDataTypeDouble) this.m_Template;
    }
}
