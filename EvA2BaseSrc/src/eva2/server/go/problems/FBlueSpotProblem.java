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
import eva2.server.go.strategies.InterfaceOptimizer;

/**
 * Created by IntelliJ IDEA. User: streiche Date: 24.03.2003 Time: 17:58:55 To
 * change this template use Options | File Templates.
 */
public class FBlueSpotProblem extends AbstractOptimizationProblem implements
        java.io.Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    protected AbstractEAIndividual m_OverallBest = null;

    protected int m_ProblemDimension = 2;

    protected int m_ShowGenerations = 498;

    protected static double overallFit = 0;

    private boolean m_ProblemShow = false;

    // Standard Constructor
    public FBlueSpotProblem() {
        this.m_Template = new ESIndividualDoubleData();
    }

    // Copy Constructor
    public FBlueSpotProblem(FBlueSpotProblem b) {
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

        return FBlueSpotProblem.overallFit;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new FBlueSpotProblem(this);
    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        this.m_OverallBest = null;
        SingletonPicture.instance();
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
        double[][] range = new double[2][2];
        range[0][0] = 0;
        range[0][1] = SingletonPicture.PIC_WIDTH - 1;
        range[1][0] = 0;
        range[1][1] = SingletonPicture.PIC_HEIGHT - 1;
        ((InterfaceDataTypeDouble) this.m_Template).SetDoubleRange(range);

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
////		evalPopStart überall rein
////		overallFit=0;
//		for (int i = 0; i < population.size(); i++) {
//			tmpIndy = (AbstractEAIndividual) population.get(i);
//            tmpIndy.resetConstraintViolation();
//			this.evaluate(tmpIndy);
////			overallFit+=tmpIndy.getFitness(0);
//			population.incrFunctionCalls();
//		}
//		evaluatePopulationEnd(population);
//	} MOVED TO SUPERCLASS	

    public void evaluatePopulationEnd(Population population) {
        overallFit = 0;
        for (int i = 0; i < population.size(); i++) overallFit += population.getEAIndividual(i).getFitness(0);
        AbstractEAIndividual tmpIndy;
        if ((population.getGeneration() == m_ShowGenerations) && (this.m_ProblemShow)) {
            byte[] cArray = new byte[3];
            cArray[0] = 31;
            cArray[1] = 31;
            cArray[2] = 31;

            double sumX = 0;
            double sumY = 0;

            for (int i = 0; i < population.size(); i++) {
                tmpIndy = (AbstractEAIndividual) population.get(i);
                double xPos = ((InterfaceDataTypeDouble) tmpIndy).getDoubleData()[0];
                double yPos = ((InterfaceDataTypeDouble) tmpIndy).getDoubleData()[1];

                sumX += xPos;
                sumY += yPos;

                for (int k = -1; k < 1; k++) {
                    for (int j = -1; j < 1; j++) {
                        if (((int) xPos + k > 0) && ((int) xPos + k < SingletonPicture.PIC_WIDTH) && ((int) yPos + j > 0) && ((int) yPos + j < SingletonPicture.PIC_HEIGHT))
                            SingletonPicture.instance().setPixel((int) xPos + k, (int) yPos + j, cArray);
                    }
                }
            }
            cArray[1] = 0;
            cArray[2] = 0;

            for (int i = -2; i < 2; i++) {
                for (int j = -2; j < 2; j++) {
                    SingletonPicture.instance().setPixel((int) sumX / population.size() + i, (int) sumY / population.size() + j, cArray);
                }

            }

            SingletonPicture.instance().drawPicture();
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
     * plot. In this Problem this double value is the median fitness of the Population
     *
     * @param pop The population that is to be refined.
     * @return Double value
     */
    public Double getDoublePlotValue(Population pop) {
        return new Double(overallFit / (double) pop.size());
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
        int xCord = (int) ((x[0])); //*SingletonPicture.PIC_WIDTH);
        int yCord = (int) ((x[1])); //*SingletonPicture.PIC_HEIGHT);

        if ((xCord > 0) && (xCord < SingletonPicture.PIC_WIDTH) && (yCord > 0) && (yCord < SingletonPicture.PIC_HEIGHT)) {

            double blue = (double) SingletonPicture.instance().getPixel(xCord, yCord)[2];
            double green = (double) SingletonPicture.instance().getPixel(xCord, yCord)[1];
            double red = (double) SingletonPicture.instance().getPixel(xCord, yCord)[0];

            result[0] = 32.0 / (double) (blue + 1);
        } else {
            result[0] = 32.0;
        }
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
        result += "Dimension   : 2"/**+ this.m_ProblemDimension**/ + "\n";
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
    //public void setProblemDimension(int multiruns) {
    //this.m_ProblemDimension = 2;
    //}
    public int getProblemDimension() {
        return this.m_ProblemDimension;
    }

    //public String problemDimensionTipText() {
    //	return "Length of the x vector at is to be optimized.";
    //}

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
