package eva2.server.go.problems;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.BitSet;

import eva2.gui.PropertyFilePath;
import eva2.gui.PropertyOptimizationObjectives;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.individuals.GAESIndividualBinaryDoubleData;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.operators.archiving.ArchivingAllDominating;
import eva2.server.go.operators.archiving.ArchivingNSGA;
import eva2.server.go.operators.constraint.InterfaceConstraint;
import eva2.server.go.operators.moso.InterfaceMOSOConverter;
import eva2.server.go.operators.paretofrontmetrics.InterfaceParetoFrontMetric;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiObjectiveOptimizationProblem;
import eva2.server.go.problems.InterfaceOptimizationObjective;
import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionTarget;
import eva2.server.go.problems.portfolio.PortfolioSelectionViewer;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioCardinality;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioGoal;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioReturn;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioRisk;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioWeightedFitness;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.math.RNG;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 03.06.2003
 * Time: 17:03:10
 * To change this template use Options | File Templates.
 */
public class TFPortfolioSelectionProblem extends AbstractMultiObjectiveOptimizationProblem implements TFPortfolioSelectionProblemInterface, java.io.Serializable {

    private String base = System.getProperty("user.dir");
    private String FS = System.getProperty("file.separator");
    private PropertyFilePath m_InputFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/port1.txt");
    private PropertyFilePath m_SolutionFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/portef1.txt");
    private double[][] m_ReferenceSolution;
    private int m_HighestReturnAsset;
    private double m_LowestEffReturn = 0;
    public String[] m_AssetName;
    public double[] m_AssetReturn;
    public double[] m_AssetRisk;
    public double[][] m_AssetCorrelation;
    private int m_ProblemDimension;

    private PropertyOptimizationObjectives m_OptimizationTargets;
    transient private PortfolioSelectionViewer m_Frame;
    public static boolean hideFromGOE = true; // TODO the class seems incompatible in that is cannot be switched away from


    //private boolean             m_ShowBeasleyMetric     = false;
    //transient private eva2.gui.Plot    m_Plot;

    private boolean m_UseBitMask = false;
    private boolean m_UseLamarckism = false;
    private boolean m_UseCardInit = false;
    private int m_Cardinality = 0;
    private double m_BuyInThreshold = 0.0;
    private double m_RoundLots = 0.0;

    // local serach
    private boolean m_UseLocalSearch = false;

    public TFPortfolioSelectionProblem() {
        // first load the data
        this.m_Template = new ESIndividualDoubleData();
        this.loadProblemData();
        // set the targets
        InterfaceOptimizationObjective[] tmpList = new InterfaceOptimizationObjective[5];
        tmpList[0] = new OptTargetPortfolioReturn();
        tmpList[1] = new OptTargetPortfolioRisk();
        tmpList[2] = new OptTargetPortfolioCardinality();
        tmpList[3] = new OptTargetPortfolioWeightedFitness(2);
        tmpList[4] = new OptTargetPortfolioGoal(2);

        this.m_OptimizationTargets = new PropertyOptimizationObjectives(tmpList);
        tmpList = new InterfaceOptimizationObjective[2];
        tmpList[1] = new OptTargetPortfolioReturn();
        tmpList[0] = new OptTargetPortfolioRisk();
        this.m_OptimizationTargets.setSelectedTargets(tmpList);
        // init the problem frame
        if (isShowParetoFront()) this.initProblemFrame();
        this.m_Template = new GAESIndividualBinaryDoubleData();
        ((GAESIndividualBinaryDoubleData) this.m_Template).setNumbers(new ESIndividualDoubleData());
        this.m_Cardinality = 0;
        this.m_UseBitMask = true;
        this.m_UseLamarckism = true;
    }

    public TFPortfolioSelectionProblem(TFPortfolioSelectionProblem b) {
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
        // TFPortfolioSelectionProblem
        this.m_UseBitMask = b.m_UseBitMask;
        this.m_UseLamarckism = b.m_UseLamarckism;
        this.m_Cardinality = b.m_Cardinality;
        this.m_BuyInThreshold = b.m_BuyInThreshold;
        this.m_RoundLots = b.m_RoundLots;
        this.m_UseLocalSearch = b.m_UseLocalSearch;
        //
        this.m_LowestEffReturn = b.m_LowestEffReturn;
        this.m_HighestReturnAsset = b.m_HighestReturnAsset;
        this.m_ProblemDimension = b.m_ProblemDimension;
        if (b.m_OptimizationTargets != null) {
            this.m_OptimizationTargets = (PropertyOptimizationObjectives) b.m_OptimizationTargets.clone();
        }
        if (b.m_AssetName != null) {
            this.m_AssetName = new String[b.m_AssetName.length];
            System.arraycopy(b.m_AssetName, 0, this.m_AssetName, 0, this.m_AssetName.length);
        }
        if (b.m_AssetReturn != null) {
            this.m_AssetReturn = new double[b.m_AssetReturn.length];
            System.arraycopy(b.m_AssetReturn, 0, this.m_AssetReturn, 0, this.m_AssetReturn.length);
        }
        if (b.m_AssetRisk != null) {
            this.m_AssetRisk = new double[b.m_AssetRisk.length];
            System.arraycopy(b.m_AssetRisk, 0, this.m_AssetRisk, 0, this.m_AssetRisk.length);
        }
        if (b.m_ReferenceSolution != null) {
            this.m_ReferenceSolution = new double[b.m_ReferenceSolution.length][b.m_ReferenceSolution[0].length];
            for (int i = 0; i < this.m_ReferenceSolution.length; i++) {
                for (int j = 0; j < this.m_ReferenceSolution[i].length; j++) {
                    this.m_ReferenceSolution[i][j] = b.m_ReferenceSolution[i][j];
                }
            }
        }
        if (b.m_AssetCorrelation != null) {
            this.m_AssetCorrelation = new double[b.m_AssetCorrelation.length][b.m_AssetCorrelation[0].length];
            for (int i = 0; i < this.m_AssetCorrelation.length; i++) {
                for (int j = 0; j < this.m_AssetCorrelation[i].length; j++) {
                    this.m_AssetCorrelation[i][j] = b.m_AssetCorrelation[i][j];
                }
            }
        }
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new TFPortfolioSelectionProblem(this);
    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        this.loadProblemData();
        InterfaceOptimizationObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        this.m_Border = new double[list.length][2];
        for (int i = 0; i < this.m_Border.length; i++) {
            this.m_Border[i] = ((InterfacePortfolioSelectionTarget) list[i]).getObjectiveBoundaries(this);
        }
        this.m_ParetoFront = new Population();
        if (isShowParetoFront()) this.initProblemFrame();
    }

    /**
     * This method will load the data
     */
    private void loadProblemData() {
        this.loadInputFile(this.m_InputFilePath.getCompleteFilePath());
        if ((this.m_SolutionFilePath != null) && (this.m_SolutionFilePath.getCompleteFilePath() != null))
            this.loadInputSolutionFile(this.m_SolutionFilePath.getCompleteFilePath());
    }

    private void loadInputFile(String inputFile) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Could not find " + inputFile);
            return;
        }
        String currentLine;
        String[] lineComponents;
        int tmpI, tmpJ;
        try {
            currentLine = reader.readLine();
            currentLine = currentLine.trim();
            this.m_ProblemDimension = new Integer(currentLine).intValue();
            this.m_AssetName = new String[this.m_ProblemDimension];
            this.m_AssetReturn = new double[this.m_ProblemDimension];
            this.m_AssetRisk = new double[this.m_ProblemDimension];
            this.m_AssetCorrelation = new double[this.m_ProblemDimension][this.m_ProblemDimension];
            // First the basic elements like return, risk, [name, ...]
            for (int i = 0; i < this.m_ProblemDimension; i++) {
                currentLine = reader.readLine();
                currentLine = currentLine.trim();
                lineComponents = currentLine.split("\t");
                if (lineComponents.length < 2) lineComponents = currentLine.split(" ");
                this.m_AssetReturn[i] = new Double(lineComponents[0]).doubleValue();
                this.m_AssetRisk[i] = new Double(lineComponents[1]).doubleValue();
                if (lineComponents.length >= 3)
                    this.m_AssetName[i] = lineComponents[2];
            }
            // Now the correlation matrix
            for (int i = 0; i < this.m_ProblemDimension; i++) {
                for (int j = 0; j < this.m_ProblemDimension; j++) {
                    this.m_AssetCorrelation[i][j] = 0.0;
                }
                this.m_AssetCorrelation[i][i] = 1.0;
            }
            while ((currentLine = reader.readLine()) != null && currentLine.length() != 0) {
                currentLine = currentLine.trim();
                //System.out.println("Trying to parse : " + currentLine);
                lineComponents = currentLine.split("\t");
                if (lineComponents.length < 3) lineComponents = currentLine.split(" ");
                //for (int i = 0; i < lineComponents.length; i++) System.out.println("LineElement " + i+ " : "+lineComponents[i]);
                tmpI = new Integer(lineComponents[0]).intValue() - 1;
                tmpJ = new Integer(lineComponents[1]).intValue() - 1;
                this.m_AssetCorrelation[tmpI][tmpJ] = new Double(lineComponents[2]).doubleValue();
                this.m_AssetCorrelation[tmpJ][tmpI] = new Double(lineComponents[2]).doubleValue();
            }
        } catch (java.io.IOException e) {
            System.out.println("Java.io.IOExeption: " + e.getMessage());
        }
    }

    /**
     * This method load the unconstrained input solution
     *
     * @param inputFile The name of the input solution
     */
    private void loadInputSolutionFile(String inputFile) {
        BufferedReader reader = null;
        ArrayList tmpInput = new ArrayList();
        try {
            reader = new BufferedReader(new FileReader(inputFile));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Could not find " + inputFile);
            return;
        }
        String currentLine;
        String[] lineComponents;
        int tmpI, tmpJ;
        this.m_HighestReturnAsset = 0;
        try {
            currentLine = reader.readLine();
            currentLine = currentLine.trim();
            double[] tmpD;
            while ((currentLine = reader.readLine()) != null && currentLine.length() != 0) {
                currentLine = currentLine.trim();
                lineComponents = currentLine.split(" ");
                //for (int i = 0; i < lineComponents.length; i++) System.out.println("LineElement " + i+ " : "+lineComponents[i]);
                tmpD = new double[2];
                tmpD[1] = new Double(lineComponents[0]).doubleValue();
                tmpD[0] = Math.sqrt(new Double(lineComponents[2]).doubleValue());
                tmpInput.add(tmpD);
            }
        } catch (java.io.IOException e) {
            System.out.println("Java.io.IOExeption: " + e.getMessage());
        }
        this.m_ReferenceSolution = new double[tmpInput.size()][2];
        this.m_LowestEffReturn = Double.POSITIVE_INFINITY;
        double[][] values = new double[tmpInput.size()][2];
        for (int i = 0; i < tmpInput.size(); i++) {
            this.m_ReferenceSolution[i] = (double[]) tmpInput.get(i);
            values[i][0] = this.m_ReferenceSolution[i][0];
            values[i][1] = this.m_ReferenceSolution[i][1];
            if (this.m_ReferenceSolution[i][1] > this.m_ReferenceSolution[this.m_HighestReturnAsset][1])
                this.m_HighestReturnAsset = i;
            if (this.m_ReferenceSolution[i][1] < this.m_LowestEffReturn)
                this.m_LowestEffReturn = this.m_ReferenceSolution[i][1];
        }
    }

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        InterfaceOptimizationObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        this.m_Border = new double[list.length][2];
        for (int i = 0; i < this.m_Border.length; i++) {
            this.m_Border[i] = ((InterfacePortfolioSelectionTarget) list[i]).getObjectiveBoundaries(this);
        }
        // reload the data since the Generic Object Editor won't tell
        // when the InputFile is altered
        double[] tmpData, newData;
        int tmpIndex, tmpCard;
        this.loadProblemData();
        if (isShowParetoFront()) this.initProblemFrame();

        AbstractEAIndividual tmpIndy;
        double[][] newRange = new double[this.m_ProblemDimension][2];

        for (int i = 0; i < this.m_ProblemDimension; i++) {
            newRange[i][0] = 0;
            newRange[i][1] = 1;
        }

        population.clear();

        ((InterfaceDataTypeDouble) this.m_Template).setDoubleDataLength(this.m_ProblemDimension);
        ((InterfaceDataTypeDouble) this.m_Template).SetDoubleRange(newRange);
        for (int i = 0; i < population.getTargetSize(); i++) {
            tmpIndy = (AbstractEAIndividual) ((AbstractEAIndividual) this.m_Template).clone();
            tmpIndy.init(this);
            if ((this.m_UseCardInit) && (this.m_Cardinality > 0)) {
                tmpData = ((InterfaceDataTypeDouble) tmpIndy).getDoubleData();
                tmpCard = RNG.randomInt(0, this.m_Cardinality);
                newData = new double[tmpData.length];
                for (int j = 0; j < tmpCard; j++) {
                    tmpIndex = RNG.randomInt(0, this.m_ProblemDimension - 1);
                    newData[tmpIndex] = tmpData[tmpIndex];
                }
                if (tmpIndy instanceof GAESIndividualBinaryDoubleData) {
                    Object[] obj = new Object[2];
                    BitSet newMask = new BitSet(newData.length);
                    for (int j = 0; j < newData.length; j++) {
                        if (newData[j] > 0) newMask.set(j);
                        else newMask.clear(i);
                    }
                    obj[0] = newData;
                    obj[1] = newMask;
                    tmpIndy.initByValue(obj, this);
                } else {
                    tmpIndy.initByValue(newData, this);
                }
            }
            population.add(tmpIndy);
        }
        // population init must be last
        // it set's fitcalls and generation to zero
        population.init();
        if ((isShowParetoFront()) && (this.m_Frame != null)) this.m_Frame.updateView(null);
    }

    /**
     * This method evaluates a given population and set the fitness values
     * accordingly
     *
     * @param population The population that is to be evaluated.
     */
    public void evaluate(Population population) {
        AbstractEAIndividual tmpIndy;
        double[] fitness;

        evaluatePopulationStart(population);

        // first evaluate the population
        for (int i = 0; i < population.size(); i++) {
            tmpIndy = (AbstractEAIndividual) population.get(i);
            tmpIndy.resetConstraintViolation();
            this.evaluate(tmpIndy);
            fitness = tmpIndy.getFitness();
            // check and update border if necessary
            if (fitness.length != this.m_Border.length) {
                //System.out.println("AbstractMOOptimizationProblem: Warning fitness.length("+fitness.length+") doesn't fit border.length("+this.m_Border.length+")");
                //System.out.println("Resetting the border!");
                this.m_Border = new double[fitness.length][2];
            }
            for (int j = 0; j < fitness.length; j++) {
//                if ((this.m_Border[j][0] > fitness[j]) || (this.m_Border[j][1] < fitness[j])) {
//                    System.out.println("border... " + j);
//                    System.out.println(this.m_Border[j][0]+" > "+fitness[j]);
//                    System.out.println(this.m_Border[j][1]+" < "+fitness[j]);
//                }
                this.m_Border[j][0] = Math.min(this.m_Border[j][0], fitness[j]);
                this.m_Border[j][1] = Math.max(this.m_Border[j][1], fitness[j]);
            }
            population.incrFunctionCalls();
        }

        // So what is the problem:
        // on the one hand i want to log the pareto-front in the
        // multiobjective case
        // but on the other hand i also need to log the pareto-front
        // in the single objective case if a MOSOConverter is used,
        // here i want to log all found pareto-optimal solutions, which
        // could be pretty many

        // currently the problem should be multi-criteria
        // log the pareto-front
        if (this.isPopulationMultiObjective(population)) {
            if (this.m_ParetoFront == null) this.m_ParetoFront = new Population();
            if (this.m_ParetoFront.getArchive() == null) {
                Population archive = new Population();
                archive.setTargetSize(100);
                this.m_ParetoFront.SetArchive(archive);
            }
            this.m_ParetoFront.addPopulation((Population) population.getClone());
            ArchivingNSGA archiving = new ArchivingNSGA();
            archiving.addElementsToArchive(this.m_ParetoFront);
            this.m_ParetoFront = this.m_ParetoFront.getArchive();
        }

        // Sometimes you want to transform a multiobjective optimization problem
        // into a single objective one, this way single objective optimization
        // algorithms can be applied more easily
        this.m_MOSOConverter.convertMultiObjective2SingleObjective(population);

        evaluatePopulationEnd(population);
    }

    /**
     * This method evaluate a single individual and sets the fitness values
     *
     * @param individual The individual that is to be evalutated
     */
    public void evaluate(AbstractEAIndividual individual) {
        double[] x, tmp;
        double[] fitness;
        BitSet t;

        //System.out.println("Individual " + individual.getSolutionRepresentationFor());
        tmp = ((InterfaceDataTypeDouble) individual).getDoubleData();
        x = new double[tmp.length];
        for (int i = 0; i < tmp.length; i++) x[i] = tmp[i];
        if ((individual instanceof GAESIndividualBinaryDoubleData) && (this.m_UseBitMask)) {
            t = ((GAESIndividualBinaryDoubleData) individual).getBinaryData();
            for (int i = 0; i < x.length; i++) if (!t.get(i)) x[i] = 0;
        }

        fitness = this.doEvaluation(x, (InterfaceDataTypeDouble) individual);

        // Write back the data so that it can be read from other elements
        x = this.normPortfolio(x);
        if (this.isValid(x)) {
            if (this.m_UseLamarckism) ((InterfaceDataTypeDouble) individual).SetDoubleGenotype(x);
            else ((InterfaceDataTypeDouble) individual).SetDoublePhenotype(x);
            if ((individual instanceof GAESIndividualBinaryDoubleData) && (this.m_UseBitMask)) {
                t = ((GAESIndividualBinaryDoubleData) individual).getBinaryData();
                for (int i = 0; i < x.length; i++) {
                    if (x[i] == 0) t.set(i, false);
                    else t.set(i, true);
                }
                if (this.m_UseLamarckism) ((GAESIndividualBinaryDoubleData) individual).SetBinaryGenotype(t);
                else ((GAESIndividualBinaryDoubleData) individual).SetBinaryPhenotype(t);
            }
        }

        // set the fitness of the individual
        individual.SetFitness(fitness);
        individual.checkAreaConst4Parallelization(this.m_AreaConst4Parallelization);
    }

    /**
     * This method calulates if a given portfolio is too big
     *
     * @param x The portfolio
     * @return true if valid false if too big
     */
    private boolean validCardinality(double[] x) {
        int counter = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] > 0) counter++;
            if (counter > this.m_Cardinality) return false;
        }
        return true;
    }

    /**
     * This debug method will print a given Portfolio
     *
     * @param x The portfolio
     */
    private void printPortfolio(double[] x) {
        String tmpStr = "Portfolio: (";
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            tmpStr += x[i] + "; ";
            sum += x[i];
        }
        tmpStr += ") Summe : " + sum;
        System.out.println(tmpStr);
    }

    /**
     * This method will determine if a given portfolio is actually valid
     *
     * @param x The portfolio
     * @return boolean
     */
    private boolean isValid(double[] x) {
        boolean show = false;
        double tmpSum = 0;

        for (int i = 0; i < x.length; i++) {
            if (Double.isNaN(x[i])) {
                if (show) System.out.println("NaN");
                return false;
            }
        }

        for (int i = 0; i < x.length; i++) {
            if ((x[i] < 0 - 0.0000001) || (x[i] > 1 + 0.0000001)) {
                if (show) System.out.println("Invalid bounds");
                return false;
            }
            tmpSum += x[i];
        }
        if (tmpSum > 1 + 0.0000001) {
            if (show) System.out.println("Invalid sum " + tmpSum);
            return false;
        }
        if (tmpSum < 1 - 0.0000001) {
            if (show) System.out.println("Invalid sum " + tmpSum);
            return false;
        }

        if (this.m_Cardinality > 0) {
            int cardcounter = 0;
            for (int i = 0; i < x.length; i++) if (x[i] > 0) cardcounter++;
            if (cardcounter > this.m_Cardinality) {
                if (show) System.out.println("Invalid Cardinality");
                return false;
            }
        }

        if (this.m_BuyInThreshold > 0) {
            for (int i = 0; i < x.length; i++) {
                if ((x[i] > 0) && (x[i] + 0.0000001 < this.m_BuyInThreshold)) {
                    if (show) System.out.println("Invalid Buy-in Thresholds " + x[i]);
                    return false;
                }
            }
        }

        if (this.m_RoundLots > 0) {
            for (int i = 0; i < x.length; i++) {
                if ((x[i] > 0) && (((x[i] + 0.0000001) % this.m_RoundLots) > 0.0000002)) {
                    if (show) System.out.println("Invalid Roundlots " + (x[i] % this.m_RoundLots));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method will normate the portfolio so that it fits the constraints
     *
     * @param tx The input portfolio
     * @return The normated portfolio
     */
    private double[] normPortfolio(double[] tx) {
        double[] result;
        result = new double[tx.length];
        for (int i = 0; i < tx.length; i++) result[i] = tx[i];

        // take care of cardinality
        if (this.m_Cardinality > 0) {
            // remove the smallest values from the portfolio
            while (!this.validCardinality(result)) {
                // remove the smallest asset
                int tmpIndex = -1;
                double tmpValue = 2;
                for (int i = 0; i < result.length; i++) {
                    if ((result[i] > 0) && (result[i] < tmpValue)) {
                        tmpIndex = i;
                        tmpValue = result[i];
                    }
                }
                if (tmpIndex >= 0) result[tmpIndex] = 0;
            }
        }

        // take care of the buy in threshhold
        if (this.m_BuyInThreshold > 0) {
            double[] bitx = new double[result.length];
            double[] tmpResult = new double[result.length];
            double bsum = 0, remainder, tSum;

            for (int i = 0; i < result.length; i++) {
                bitx[i] = this.m_BuyInThreshold;
                if (result[i] > 0) {
                    tmpResult[i] = Math.max(0, result[i] - bitx[i]);
                    bsum += bitx[i];
                } else {
                    tmpResult[i] = 0;
                    result[i] = 0;
                }
            }
            int repeats = result.length;
            while ((bsum > 1) && (repeats > 0)) {
                repeats--;
                int index = -1;
                double smallest = Double.POSITIVE_INFINITY;
                for (int i = 0; i < result.length; i++) {
                    if ((result[i] > 0) && (result[i] < smallest)) {
                        smallest = result[i];
                        index = i;
                    }
                }
                if (index >= 0) {
                    bsum -= bitx[index];
                    result[index] = 0;
                    tmpResult[index] = 0;
                }
            }
            remainder = 1 - bsum;
            tSum = 0;
            for (int i = 0; i < result.length; i++) tSum += tmpResult[i];
            for (int i = 0; i < result.length; i++) tmpResult[i] = (remainder * tmpResult[i]) / tSum;

            for (int i = 0; i < result.length; i++) {
                if (result[i] > 0) result[i] = bitx[i] + tmpResult[i];
            }
        } else {
            // this is a simple normation
            double tmpSum = 0;
            for (int i = 0; i < result.length; i++) tmpSum += result[i];
            if (tmpSum > 0) for (int i = 0; i < result.length; i++) result[i] = result[i] / tmpSum;
            else {
                for (int i = 0; i < result.length; i++) result[i] = 0;
                return result;
            }
        }
//        System.out.println("Portfolio after card & Buy-in");
//        this.printPortfolio(result);
        // now take care of the roundlots
        if (this.m_RoundLots > 0) {
            double rsum = 0;
            double[] remainingDesire = new double[result.length];
            for (int i = 0; i < result.length; i++) {
                if (result[i] > 0) {
                    remainingDesire[i] = result[i] % this.m_RoundLots;
                    result[i] -= remainingDesire[i];
                    rsum += remainingDesire[i];
                } else {
                    remainingDesire[i] = 0;
                }
            }
//            this.printPortfolio(result);
            // rsum can/must be redistributed
            boolean tryToRedistribute = true;
            int biggestDesire;
            double currentDesire = 0;
            while (tryToRedistribute) {
                tryToRedistribute = false;
                biggestDesire = -1;
                currentDesire = 0;

                // clear the desire from all that can't be satisfied anyway
                for (int i = 0; i < remainingDesire.length; i++) {
                    // this could be dependant on the roundlot
                    if (this.m_RoundLots > rsum) remainingDesire[i] = 0;
                }

                // search for the biggest desire
                for (int i = 0; i < remainingDesire.length; i++) {
                    if ((result[i] > 0) && (remainingDesire[i] > currentDesire)) {
                        biggestDesire = i;
                        currentDesire = remainingDesire[i];
                    }
                }
                if (biggestDesire >= 0) {
                    // the roundlot could be dependant on the asset
                    //if (rsum > this.m_RoundLots[biggestDesire]) {
                    if (rsum > this.m_RoundLots) {
                        rsum -= this.m_RoundLots;
                        result[biggestDesire] += this.m_RoundLots;
                        tryToRedistribute = true;
                    } else {
                        // if the roundlot was dependent on the asset
                        // tryToRedistribute = true;
                        // would be set since other assets could
                        // have a roundlot smaller than rsum
                        remainingDesire[biggestDesire] = 0;
                    }
                }
            }
        }
//        System.out.println("Portfolio after roundlot");
//        this.printPortfolio(result);
        return result;
    }

    /**
     * Ths method allows you to evaluate a simple bit string to determine the fitness
     *
     * @param x The n-dimensional input vector
     * @return The m-dimensional output vector.
     */
    public double[] doEvaluation(double[] x, InterfaceDataTypeDouble indy) {
        double[] result;
        InterfaceOptimizationObjective[] targets;
        targets = this.m_OptimizationTargets.getSelectedTargets();

        // norm the portfolio
        x = this.normPortfolio(x);
        indy.SetDoublePhenotype(x);

        // fetch the optimization target values
        result = new double[targets.length];
        if (this.isValid(x)) {
            for (int i = 0; i < result.length; i++) {
                result[i] = ((InterfacePortfolioSelectionTarget) targets[i]).evaluatePortfolio(indy, this);
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                result[i] = ((InterfacePortfolioSelectionTarget) targets[i]).getObjectiveBoundaries(this)[1]; // - Math.abs(RNG.gaussianDouble(0.01));
            }
        }

        return result;
    }

    /**
     * This method will init the problem specific visualisation of the problem
     */
    public void initProblemFrame() {
        if (this.m_Frame == null) {
            this.m_Frame = new PortfolioSelectionViewer(this);
        }
    }

    /**
     * This method will dispose the current problem frame
     */
    public void disposeProblemFrame() {
        if (this.m_Frame != null) this.m_Frame.dispose();
    }

    /**
     * This method will draw the current state of the optimization process
     *
     * @param p The current population
     */
    public void drawProblem(Population p) {
        if (this.m_Frame != null) this.m_Frame.updateView(p);
    }

//    /** This method calculates the risk return for a given portfolio
//     * @param x The portfolio which must be normatted
//     * @return double[] risk/return
//     */
//    private double[] calcuateRiskReturn(double[] x) {
//        double  Return = 0, Risk = 0;
//        double[] result = new double[2];
////*******************************************************************************************************************
//        // calculate the return on investment
//        for (int i = 0; i < x.length; i++) {
//            // at the very end of the sourceMatrix is the return of an asset
//            Return += x[i]*this.m_AssetReturn[i];
//        }
//
//        // calculate the risk of the allocation
//        for (int i = 0; i < x.length; i++) {
//            for (int j = 0; j < x.length; j++) {
//                Risk += x[i]*x[j]*this.m_AssetRisk[i]*this.m_AssetRisk[j]*this.m_AssetCorrelation[i][j];
//            }
//        }
//        Risk = Math.sqrt(Risk);
////*******************************************************************************************************************
//        result[0] = Risk;
//        result[1] = Return;
//        return result;
//    }
//
//    /** Ths method allows you to evaluate a simple bit string to determine the fitness
//     * @param x     The n-dimensional input vector
//     * @return  The m-dimensional output vector.
//     */
//    public double[] doEvaluation(double[] x) {
//        double[]    result = new double[2], tmpResult;
//        double      Return = 0, Risk = 0;
//
//        x = this.normPortfolio(x);
//
//        tmpResult   = this.calcuateRiskReturn(x);
//        Risk        = tmpResult[0];
//        Return      = tmpResult[1];
//        //System.out.println("Result : ("+ Risk +", " + Return +")");
//
////        // here i could perform a local search
////        if (this.m_UseLocalSearch) {
////            int         dim = this.computeCardinality(x);
////            if (this.m_Engine == null) this.initMatlab();
////            double[][]  H = new double[dim][dim];
////            double[]    f = new double[dim];
////            double[][]  A = new double[dim][1];
////            double[]    b = {-Return};
////            double[][]  Aeq = new double[dim][1];
////            double[]    beq = {1};
////            double[]    lb = new double[dim], ub = new double[dim];
////            double[]    x0 = new double[dim];
////
////            int ti = 0, tj = 0;
////            for (int i = 0; i < x.length; i++){
////                if (x[i] > 0) {
////                    f[ti]       = 0;
////                    lb[ti]      = 0;
////                    ub[ti]      = 1;
////                    Aeq[ti][0]  = 1;
////                    x0[ti]      = x[i];
////                    A[ti][0]    = -this.m_AssetReturn[i];
////                    tj          = 0;
////                    for (int j = 0; j < x.length; j++) {
////                        if (x[j] > 0) {
////                            H[ti][tj] = this.m_AssetRisk[i]*this.m_AssetRisk[j]*this.m_AssetCorrelation[i][j];
////                            tj++;
////                        }
////                    }
////                    ti++;
////                }
////            }
////            this.m_Engine.engPutArray(this.m_PID, "H", H);
////            this.m_Engine.engPutArray(this.m_PID, "f", f);
////            this.m_Engine.engPutArray(this.m_PID, "A", A);
////            this.m_Engine.engPutArray(this.m_PID, "b", b);
////            this.m_Engine.engPutArray(this.m_PID, "Aeq", Aeq);
////            this.m_Engine.engPutArray(this.m_PID, "beq", beq);
////            this.m_Engine.engPutArray(this.m_PID, "lb", lb);
////            this.m_Engine.engPutArray(this.m_PID, "ub", ub);
////            this.m_Engine.engPutArray(this.m_PID, "x0", x0);
////            this.m_Engine.engEvalString(this.m_PID,"x = quadprog(H,f',A',b',Aeq',beq',lb',ub',x0');");
//////            this.m_Engine.engEvalString(this.m_PID,"bri = sqrt(x0*H*x0');");
//////            this.m_Engine.engEvalString(this.m_PID,"bre = sum(x0*A);");
//////            this.m_Engine.engEvalString(this.m_PID,"ari = sqrt(x'*H*x);");
//////            this.m_Engine.engEvalString(this.m_PID,"are = sum(x'*A);");
//////            double[][] bri = this.m_Engine.engGetArray(this.m_PID,"bri");
//////            double[][] bre = this.m_Engine.engGetArray(this.m_PID,"bre");
//////            double[][] ari = this.m_Engine.engGetArray(this.m_PID,"ari");
//////            double[][] are = this.m_Engine.engGetArray(this.m_PID,"are");
//////            System.out.println("Mathlab results : Before ("+bri[0][0]+", " + bre[0][0] + ") After: (" + ari[0][0] + ", " + are[0][0] +")");
////            double[][] x1 = this.m_Engine.engGetArray(this.m_PID,"x");
////            if (x1 != null) {
////                //x   = new double[x1.length];
////                ti = 0;
////                for (int i = 0; i < x.length; i++) {
////                    if (x[i] > 0 ) {
////                        x[i] = x1[ti][0];
////                        ti++;
////                    }
////                    //x[i] = x1[i][0];
////                }
////                x   = this.normPortfolio(x);
////                tmpResult   = this.calcuateRiskReturn(x);
////                Risk        = tmpResult[0];
////                Return      = tmpResult[1];
////                //System.out.println("Result After local search: ("+ Risk +", " + Return +")");
////            }
////        }
//
//        // Test if portfolio is valid
////        boolean valid = false;
////        for (int i = 0; i < x.length; i++) if (x[i] > 0) valid = true;
//        if (this.isValid(x)) {
//            result[0]       = Risk;
//            result[1]       = -Return;
//            //System.out.println("Result valid: ("+ result[0] +", " + -result[1] +")");
//        } else {
//            result[0] = this.m_AssetRisk[this.m_HighestReturnAsset]+RNG.randomDouble(0, 0.00001);
//            result[1] = 0+RNG.randomDouble(0, 0.00001);
//            //System.out.println("Result invalid: ("+ result[0] +", " + -result[1] +")");
//        }
//
//        return result;
//    }
//
//    /** This method returns a double value that will be displayed in a fitness
//     * plot. A fitness that is to be minimized with a global min of zero
//     * would be best, since log y can be used. But the value can depend on the problem.
//     */
//    public Double getDoublePlotValue(Population pop) {
//        // In this case i will try to return the distance to the UEF according to Beasley
//        Population archive = pop.getArchive();
//
//        if ((this.m_ReferenceSolution != null) && (archive != null)) {
//            if (this.m_ShowBeasleyMetric) return new Double(this.calculateBMetric(archive));
//            else return new Double(this.calculateSMetric(archive));
//        } else {
//            return new Double(pop.getBestEAIndividual().getFitness(0));
//        }
//    }
//
//    /** This method will init the problem specific visualisation of the problem
//     */
//    public void initProblemFrame() {
//        double[] tmpD = new double[2];
//        tmpD[0] = 0;
//        tmpD[1] = 0;
//        if (this.m_Plot == null) m_Plot = new eva2.gui.Plot("Portfolio Selection", "Risk", "Return", tmpD, tmpD);
//
//        // plot the assets
//        this.m_Plot.clearGraph(0);
//        this.m_Plot.setUnconnectedPoint(0, 0, 0);
//        for (int i = 0; i < this.m_ProblemDimension; i++) {
//            //System.out.println(this.m_AssetName[i] + " ("+this.m_AssetReturn[i] + ";"+ this.m_AssetRisk[i] +")");
//            this.m_Plot.setUnconnectedPoint(this.m_AssetRisk[i], this.m_AssetReturn[i], 0);
//        }
//
//        // plot the reference solution
//        if (this.m_ReferenceSolution != null) {
//            this.m_Plot.clearGraph(1);
//            for (int i = 0; i < this.m_ReferenceSolution.length; i++) {
//                this.m_Plot.setConnectedPoint(this.m_ReferenceSolution[i][0], this.m_ReferenceSolution[i][1], 1);
//            }
//        }
//    }
//
//    /** This method will draw the current state of the optimization process
//     * @param p     The current population
//     */
//    public void drawProblem(Population p) {
//        if (p.getGeneration() > 2) {
//            AbstractEAIndividual indy;
//
//            // plot the current population
//            this.m_Plot.clearGraph(2);
//            for (int i = 0; i < p.size(); i++) {
//                 indy = (AbstractEAIndividual)p.get(i);
//                 this.m_Plot.setUnconnectedPoint(indy.getFitness(0), -indy.getFitness(1), 2);
//            }
//
//            // plot the archive
//            Population tmpP = p.getArchive();
//            ArchivingNSGA archiving = new ArchivingNSGA();
//            archiving.addElementsToArchive(tmpP);
//            tmpP = tmpP.getArchive();
//            if (tmpP != null) {
//                double[][]  values = new double[tmpP.size()][2];
//                double[]    lastValue = new double[2];
//                int         tmpIndex;
//                this.m_Plot.clearGraph(3);
//
//                for (int i = 0; i < tmpP.size(); i++) {
//                    indy = (AbstractEAIndividual)tmpP.get(i);
//                    values[i][0] = indy.getFitness(0);
//                    values[i][1] = -indy.getFitness(1);
//                }
//                lastValue[0] = this.m_ReferenceSolution[this.m_HighestReturnAsset][0];
//                lastValue[1] = this.m_ReferenceSolution[this.m_HighestReturnAsset][0];
//                for (int i = 0; i < values.length; i++) {
//                    tmpIndex = 0;
//                    for (int j = 1; j < values.length; j++) {
//                        if (values[tmpIndex][0] < values[j][0]) tmpIndex = j;
//                    }
//                    if (values[tmpIndex][0] < 0) return;
//                    if (values[tmpIndex][1] > 0 ) {
//                        this.m_Plot.setConnectedPoint(lastValue[0], values[tmpIndex][1], 3);
//                        this.m_Plot.setConnectedPoint(values[tmpIndex][0], values[tmpIndex][1], 3);
//                    }
//
//                    lastValue[0] = values[tmpIndex][0];
//                    lastValue[1] = values[tmpIndex][1];
//                    values[tmpIndex][0] = -1;
//                }
//            }
//        }
//    }

    /**
     * This method will calculate the Beasley Metric
     *
     * @param archive The parteofront
     * @return result
     */
    private double calculateBMetric(Population archive) {
        double result = 0;
        double[] portfolio = new double[2], tmp;
        int number = 0;

//        if (this.m_Show) this.m_Plot.clearGraph(4);
        for (int i = 0; i < archive.size(); i++) {
            tmp = ((AbstractEAIndividual) archive.get(i)).getFitness();
            portfolio[0] = tmp[0];
            portfolio[1] = -tmp[1];
            result += this.calculateBeasleyDistanceToFront(portfolio);
            number++;
        }
        return result / (double) number;
    }

    /**
     * This method will calculate the distance from a given point to the reference
     * solution according to Beasley et. al.
     *
     * @param p The given point
     * @return result
     */
    private double calculateBeasleyDistanceToFront(double[] p) {
        double result = 0;

        if (this.m_ReferenceSolution == null) return result;
        // First regarding the x axis
        // find upper and lower bounding points
        int upperix = -1, lowerix = -1;
        double distupx = 1000, distlox = 1000;
        int upperiy = -1, loweriy = -1;
        double distupy = 1000, distloy = 1000;
        double interx, intery;
        double ax, ay;

        for (int i = 0; i < this.m_ReferenceSolution.length; i++) {
            if ((this.m_ReferenceSolution[i][0] > p[0]) && (Math.abs(this.m_ReferenceSolution[i][0] - p[0]) < distupx)) {
                distupx = Math.abs(this.m_ReferenceSolution[i][0] - p[0]);
                upperix = i;
            }
            if ((this.m_ReferenceSolution[i][0] < p[0]) && (Math.abs(this.m_ReferenceSolution[i][0] - p[0]) < distlox)) {
                distlox = Math.abs(this.m_ReferenceSolution[i][0] - p[0]);
                lowerix = i;
            }
        }

        if ((upperix >= 0) && (lowerix >= 0)) {
            // We got two bounding values regarding the x axis
            ax = (this.m_ReferenceSolution[upperix][1] - this.m_ReferenceSolution[lowerix][1]) / (this.m_ReferenceSolution[upperix][0] - this.m_ReferenceSolution[lowerix][0]);
            intery = (p[0] - this.m_ReferenceSolution[lowerix][0]) * ax + this.m_ReferenceSolution[lowerix][1];
            result += 100 * Math.abs(intery - p[1]) / intery;
        }

        for (int i = 0; i < this.m_ReferenceSolution.length; i++) {
            if ((this.m_ReferenceSolution[i][1] > p[1]) && (Math.abs(this.m_ReferenceSolution[i][1] - p[1]) < distupy)) {
                distupy = Math.abs(this.m_ReferenceSolution[i][1] - p[1]);
                upperiy = i;
            }
            if ((this.m_ReferenceSolution[i][1] < p[1]) && (Math.abs(this.m_ReferenceSolution[i][1] - p[1]) < distloy)) {
                distloy = Math.abs(this.m_ReferenceSolution[i][1] - p[1]);
                loweriy = i;
            }
        }

        if ((upperiy >= 0) && (loweriy >= 0)) {
            // We got two bounding values regarding the y axis
            ay = (this.m_ReferenceSolution[upperiy][0] - this.m_ReferenceSolution[loweriy][0]) / (this.m_ReferenceSolution[upperiy][1] - this.m_ReferenceSolution[loweriy][1]);
            interx = (p[1] - this.m_ReferenceSolution[loweriy][1]) * ay + this.m_ReferenceSolution[loweriy][0];
            result += 100 * Math.abs(interx - p[0]) / interx;
        }
        return result;
    }

    /**
     * This method allows you to output a string that describes a found solution
     * in a way that is most suiteable for a given problem.
     *
     * @param individual The individual that is to be shown.
     * @return The description.
     */
    public String getSolutionRepresentationFor(AbstractEAIndividual individual) {
        this.evaluate(individual);
        String result = "Portfolio Selection problem:\n";
        result += individual.getStringRepresentation() + "\n";
        result += "Y = (Risk:" + individual.getFitness(0) + "; Return: " + (-individual.getFitness(1));
        return result;
    }

    /**
     * This method returns the header for the additional data that is to be written into a file
     *
     * @param pop The population that is to be refined.
     * @return String
     */
    public String getAdditionalFileStringHeader(Population pop) {
        //return "Beasley_Metric \t C_Metric \t Portfolio";
        return "S_Metric \t S_Metric_Rel";
    }

    /**
     * This method returns the additional data that is to be written into a file
     *
     * @param pop The population that is to be refined.
     * @return String
     */
    public String getAdditionalFileStringValue(Population pop) {

        String result = "";
        Population tmpPop = (Population) pop.clone();
        ArchivingAllDominating arch = new ArchivingAllDominating();
        arch.addElementsToArchive(tmpPop);

        //result      += this.calculateBMetric(archive) +"\t";
        result += this.calculateMetric(tmpPop) + "\t";
        // now add the efficiency front
//        result += "{";
//        double[][]              values = new double[archive.size()][2];
//        AbstractEAIndividual    indy;
//        int         tmpIndex;
//
//        for (int i = 0; i < archive.size(); i++) {
//            indy = (AbstractEAIndividual)archive.get(i);
//            values[i][0] = indy.getFitness(0);
//            values[i][1] = -indy.getFitness(1);
//        }
//        for (int i = 0; i < values.length; i++) {
//            tmpIndex = 0;
//            for (int j = 1; j < values.length; j++) {
//                if (values[tmpIndex][0] < values[j][0]) tmpIndex = j;
//            }
//            if (values[tmpIndex][1] > 0 ) result += "("+values[tmpIndex][0] + ", " + values[tmpIndex][1] + "); ";
//            values[tmpIndex][0] = -1;
//        }
//        result += "}";

        return result;
    }

    /**
     * This method allows you to output a string that describes a found solution
     * in a way that is most suiteable for a given problem.
     *
     * @param optimizer The individual that is to be shown.
     * @return The description.
     */
    public String getFinalReportOn(InterfaceOptimizer optimizer) {
        String result = "This is the final Pareto-front:\n";
        Population archive = optimizer.getPopulation().getArchive();
        if (archive != null) {
            result += "Risk \t Return \t Portfolio\n";
            AbstractEAIndividual tmpIndy;
            double[] portfolio;
            for (int i = 0; i < archive.size(); i++) {
                tmpIndy = (AbstractEAIndividual) archive.get(i);
                result += tmpIndy.getFitness(0) + "\t";
                result += -tmpIndy.getFitness(1);
                portfolio = ((InterfaceDataTypeDouble) tmpIndy).getDoubleData();
                portfolio = this.normPortfolio(portfolio);
                for (int j = 0; j < portfolio.length; j++) {
                    result += "\t" + portfolio[j];
                }
                result += "\n";
            }

        } else {
            result += "No Pareto-front";
        }
        ;
        return result;
    }

    /**
     * Returns the list of asset returns
     *
     * @return
     */
    public double[] getAssetReturn() {
        return this.m_AssetReturn;
    }

    /**
     * Returns the list of asset risks
     *
     * @return
     */
    public double[] getAssetRisk() {
        return this.m_AssetRisk;
    }

    /**
     * Returns the matrix of asset correlations
     *
     * @return
     */
    public double[][] getAssetCorrelation() {
        return this.m_AssetCorrelation;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "Portfolio Optimization Problem:\n";
        result += this.getFinalReportOn(opt);
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
        return "Portfolio Selection Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "An Asset Allocation problem.";
    }

    /**
     * This method will allow you to toggle the use of the BitMask
     *
     * @param bit The new representation for the inner constants.
     */
    public void setUseBitMask(boolean bit) {
        this.m_UseBitMask = bit;
    }

    public boolean getUseBitMask() {
        return this.m_UseBitMask;
    }

    public String useBitMaskTipText() {
        return "Toogle the use of an additional BitMask for the double values.";
    }

    /**
     * This method will allow you to toggle the use of the Lamarckism
     *
     * @param bit The new representation for the inner constants.
     */
    public void setUseLamarckism(boolean bit) {
        this.m_UseLamarckism = bit;
    }

    public boolean getUseLamarckism() {
        return this.m_UseLamarckism;
    }

    public String useLamarckismTipText() {
        return "Toogle the use of an additional BitMask for the double values.";
    }

    /**
     * With this method you can set the target cardinality. Zero denotes no cardinality
     * constraint at all.
     *
     * @param b The target cardinality.
     */
    public void setCardinality(int b) {
        this.m_Cardinality = b;
    }

    public int getCardinality() {
        return this.m_Cardinality;
    }

    public String cardinalityTipText() {
        return "Set the cardinality constraints. If zero, no card. const. will be applied.";
    }

    /**
     * This method will set the buy in threshhold.
     *
     * @param b The buy in threshhold factor.
     */
    public void setBuyInThreshold(double b) {
        // b should not be negative
        if (b < 0) b = 0;
        // b should not be bigger than 1/number of Assets otherwise it becomes a knapsackproblem
        //if (b > 1/(double)this.m_AssetReturn.length) b = 1/(double)this.m_AssetReturn.length;
        // b should not interfere with the RoundLots
        //if (b % this.m_RoundLots > 0) b += b % this.m_RoundLots;
        this.m_BuyInThreshold = b;
    }

    public double getBuyInThreshold() {
        return this.m_BuyInThreshold;
    }

    public String buyInThresholdTipText() {
        return "The minimum value for an asset (0<x<<1).";
    }

    /**
     * This method will set the roundlots
     *
     * @param b The roundlots
     */
    public void setRoundLots(double b) {
        // b should not be negative
        if (b < 0) b = 0;
        // b should not be bigger than 1/number of Assets otherwise it becomes a knapsackproblem
        //if (b > 1/(double)this.m_AssetReturn.length) b = 1/(double)this.m_AssetReturn.length;
        // b should not interfere with the RoundLots
        //if (this.m_BuyInThreshold % b > 0) this.m_BuyInThreshold += this.m_BuyInThreshold % b;
        this.m_RoundLots = b;
    }

    public double getRoundLots() {
        return this.m_RoundLots;
    }

    public String roundLotsTipText() {
        return "The lot size for an asset (0<x<<1).";
    }

    public String showParetoFrontTipText() {
        return "Toggles the path visualisation.";
    }

//    /** This method allows you to toggle between Beasley and S-Metric.
//     * @param b     True if the path is to be shown.
//     */
//    public void setShowBeasleyMetric(boolean b) {
//        this.m_ShowBeasleyMetric = b;
//    }
//    public boolean getShowBeasleyMetric() {
//        return this.m_ShowBeasleyMetric;
//    }
//    public String showBeasleyMetricTipText() {
//        return "Toggles between the Beasley/S-Metric fitness display.";
//    }

    /**
     * This method allows you to activate an additional local search.
     *
     * @param b True if the path is to be shown.
     */
    public void setUseLocalSearch(boolean b) {
        this.m_UseLocalSearch = b;
    }

    public boolean getUseLocalSearch() {
        return this.m_UseLocalSearch;
    }

    public String useLocalSearchTipText() {
        return "A matlab quadrog search is applied on every individual.";
    }

    /**
     * This method allows you to activate cardinality init
     *
     * @param b True if card init is to be used.
     */
    public void setUseCardInit(boolean b) {
        this.m_UseCardInit = b;
    }

    public boolean getUseCardInit() {
        return this.m_UseCardInit;
    }

    public String useCardInitTipText() {
        return "Use a cardinality conserving init.";
    }

    /**
     * This method allows you to set the path to the data file.
     *
     * @param b File path.
     */
    public void setPortfolioProblem(PropertyFilePath b) {
        this.m_InputFilePath = b;
    }

    public PropertyFilePath getPortfolioProblem() {
        return this.m_InputFilePath;
    }

    public String portfolioProblemTipText() {
        return "Select the portfolio problem by choosing the input file.";
    }

    /**
     * This method allows you to choose a path to a reference.
     *
     * @param b File path.
     */
    public void setPortfolioReference(PropertyFilePath b) {
        this.m_SolutionFilePath = b;
    }

    public PropertyFilePath getPortfolioReference() {
        return this.m_SolutionFilePath;
    }

    public String portfolioReferenceTipText() {
        return "Select the reference solution for the portfolio problem.";
    }

    /**
     * This method allows you to choose the optimization targets.
     *
     * @param b File path.
     */
    public void setOptimizationTargets(PropertyOptimizationObjectives b) {
        this.m_OptimizationTargets = b;
        System.out.println("HERERE");
        if (this.m_Frame != null) this.m_Frame.updateView(null);
    }

    public PropertyOptimizationObjectives getOptimizationTargets() {
        return this.m_OptimizationTargets;
    }

    public String optimizationTargetsTipText() {
        return "Choose the optimization Targets.";
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
