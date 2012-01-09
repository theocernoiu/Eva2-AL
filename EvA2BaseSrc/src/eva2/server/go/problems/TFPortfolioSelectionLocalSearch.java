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
import eva2.server.go.operators.selection.MOMultipleSolutions;
import eva2.server.go.operators.selection.MOSolution;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionTarget;
import eva2.server.go.problems.portfolio.MatlabQuadProgLocalSearch;
import eva2.server.go.problems.portfolio.MatlabSingleSolution;
import eva2.server.go.problems.portfolio.PortfolioSelectionViewer;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioReturn;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioRisk;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.SelectedTag;
import eva2.tools.Tag;
import eva2.tools.math.RNG;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 18.02.2005
 * Time: 17:06:53
 * To change this template use File | Settings | File Templates.
 */
public class TFPortfolioSelectionLocalSearch extends AbstractMultiObjectiveOptimizationProblem implements TFPortfolioSelectionProblemInterface, java.io.Serializable {

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
    transient private MatlabQuadProgLocalSearch m_Matlab = null;

    //private boolean             m_ShowBeasleyMetric     = false;
    //transient private eva2.gui.Plot    m_Plot;

    private boolean m_UseBitMask = false;
    private boolean m_UseLamarckism = false;
    private boolean m_UseCardInit = false;
    private int m_Cardinality = 0;
    private double m_UpperBound = 0;
    private int m_Exceptions = 0;
    private SelectedTag m_LocalSearchType;

    public TFPortfolioSelectionLocalSearch() {
        Tag[] tag = new Tag[3];
        tag[0] = new Tag(0, "No Local Search");
        tag[1] = new Tag(1, "Local Search, single solution");
        tag[2] = new Tag(2, "Local Search, multiple solution");
        this.m_LocalSearchType = new SelectedTag(0, tag);
        // first load the data
        this.m_Template = new ESIndividualDoubleData();
        this.loadProblemData();
        // set the targets
        InterfaceOptimizationObjective[] tmpList = new InterfaceOptimizationObjective[2];
        tmpList[0] = new OptTargetPortfolioReturn();
        tmpList[1] = new OptTargetPortfolioRisk();

        this.m_OptimizationTargets = new PropertyOptimizationObjectives(tmpList);
        tmpList = new InterfaceOptimizationObjective[2];
        tmpList[1] = new OptTargetPortfolioReturn();
//        ((OptTargetPortfolioReturn)tmpList[1]).setNormalizeTarget(true);
        tmpList[0] = new OptTargetPortfolioRisk();
//        ((OptTargetPortfolioRisk)tmpList[0]).setNormalizeTarget(true);
        this.m_OptimizationTargets.setSelectedTargets(tmpList);
        // init the problem frame
        if (isShowParetoFront()) this.initProblemFrame();
        this.m_Template = new GAESIndividualBinaryDoubleData();
        ((GAESIndividualBinaryDoubleData) this.m_Template).setNumbers(new ESIndividualDoubleData());
        this.m_Cardinality = 0;
        this.m_UseBitMask = true;
        this.m_UseLamarckism = true;
    }

    public TFPortfolioSelectionLocalSearch(TFPortfolioSelectionLocalSearch b) {
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
        this.m_UpperBound = b.m_UpperBound;
        this.m_Exceptions = b.m_Exceptions;
        if (b.m_LocalSearchType != null)
            this.m_LocalSearchType = (SelectedTag) b.m_LocalSearchType.clone();
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
        return (Object) new TFPortfolioSelectionLocalSearch(this);
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

//    /** If necessary the matlab engine can be initialized
//     */
//    private void initMatlab() {
//        this.m_Engine   = JMatLink.getInstance();
//        this.m_PID      = this.m_Engine.engOpen();
//        //this.m_PID = m_engine.engOpenSingleUse();
//        String ClassPath = System.getProperty("java.class.path");
//        String ToolBoxPath = ClassPath+"/matlab/dace/";
//        this.m_Engine.engEvalString(this.m_PID, "addpath " + ToolBoxPath);
//        //System.out.println("-->"+this.m_Engine.engOutputBuffer(this.m_PID));
//        this.m_Engine.engEvalString(this.m_PID,"path ");
//        //System.out.println("-->"+this.m_Engine.engOutputBuffer(this.m_PID));
//    }

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
            tmpIndy.m_FunctionCalls = 0;
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
            // @todo This is a potential problem
            population.incrFunctionCalls();
            if (tmpIndy.m_FunctionCalls > 0) {
                // this catches the local search....
                population.incrFunctionCallsBy(tmpIndy.m_FunctionCalls);
                tmpIndy.m_FunctionCalls = 0;
            }
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
        double[] x, tmp, tx;
        double[] fitness;
        BitSet t;

        tmp = ((InterfaceDataTypeDouble) individual).getDoubleData();
        x = new double[tmp.length];
        for (int i = 0; i < tmp.length; i++) x[i] = tmp[i];
        if ((individual instanceof GAESIndividualBinaryDoubleData) && (this.m_UseBitMask)) {
            t = ((GAESIndividualBinaryDoubleData) individual).getBinaryData();
            for (int i = 0; i < x.length; i++) if (!t.get(i)) x[i] = 0;
        }

        // @todo Implement this local search stuff
        switch (this.m_LocalSearchType.getSelectedTag().getID()) {
            case 0:
                fitness = this.doEvaluation(x, (InterfaceDataTypeDouble) individual);
                individual.m_FunctionCalls = 1;
                break;
            case 1:
                fitness = this.doEvaluation(x, (InterfaceDataTypeDouble) individual);
                if (this.m_Matlab == null) this.m_Matlab = new MatlabQuadProgLocalSearch();
                tx = this.prepareWeights(x);
                if (tx.length > 2) {
                    double[] ret = this.prepareReturn(x);
                    double[] ris = this.prepareRisk(x);
                    double[][] cov = this.prepareCov(x);
                    double lambda = RNG.randomDouble(0, 1);
                    if (RNG.flipCoin(0.5)) lambda = Math.pow(lambda, 2);
                    double[] ub = this.prepareUpperBorder(x);
                    MatlabSingleSolution malta = this.m_Matlab.performLocalSerach(lambda, tx, ret, ris, cov, ub, true);
                    if (malta.exitFlag > 0) {
                        x = this.decomposeWeights(x, malta.weights);
                        fitness = this.doEvaluation(x, (InterfaceDataTypeDouble) individual);
                        individual.m_FunctionCalls = malta.iterations;
                    }
                }
                break;
            case 2:
                fitness = this.doEvaluation(x, (InterfaceDataTypeDouble) individual);
                if (this.m_Matlab == null) this.m_Matlab = new MatlabQuadProgLocalSearch();
                tx = this.prepareWeights(x);
                if (tx.length > 2) {
                    double[] ret = this.prepareReturn(x);
                    double[] ris = this.prepareRisk(x);
                    double[][] cov = this.prepareCov(x);
                    double lambda = RNG.randomDouble(0, 1);
                    if (RNG.flipCoin(0.5)) lambda = Math.pow(lambda, 2);
                    double[] ub = this.prepareUpperBorder(x);
                    MOMultipleSolutions malta = this.m_Matlab.performGlobalSerach(tx, ret, ris, cov, ub, true);
                    individual.m_FunctionCalls = malta.m_Iterations;
                    // assume that all entered elements are actually feasible
                    MOSolution tmpS;
                    for (int i = 0; i < malta.size(); i++) {
                        tmpS = (MOSolution) malta.get(i);
                        tmpS.weights = this.decomposeWeights(x, tmpS.weights);
                        tmpS.fitness = this.doEvaluation(tmpS.weights, (InterfaceDataTypeDouble) individual);
                        if (!this.isValid(tmpS.weights)) {
                            malta.remove(i);
                            i--;
                        }
                    }
                }
                break;
            default:
                fitness = this.doEvaluation(x, (InterfaceDataTypeDouble) individual);
        }

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
     * This method makes a sparse vector from the given weights
     * vector x
     *
     * @param x The full weights vector
     * @return A sparse version of the previously full vector
     */
    private double[] prepareWeights(double[] x) {
        ArrayList tmpR = new ArrayList();
        for (int i = 0; i < x.length; i++) if (x[i] > 0) tmpR.add(new Double(x[i]));
        double[] result = new double[tmpR.size()];
        for (int i = 0; i < tmpR.size(); i++) result[i] = ((Double) tmpR.get(i)).doubleValue();
        return result;
    }

    /**
     * This method makes a sparse return vector from the given weights
     * vector x
     *
     * @param x The full weights vector
     * @return A sparse version of the previously full return vector
     */
    private double[] prepareReturn(double[] x) {
        ArrayList tmpR = new ArrayList();
        for (int i = 0; i < x.length; i++) if (x[i] > 0) tmpR.add(new Double(this.m_AssetReturn[i]));
        double[] result = new double[tmpR.size()];
        for (int i = 0; i < tmpR.size(); i++) result[i] = ((Double) tmpR.get(i)).doubleValue();
        return result;
    }

    /**
     * This method makes a sparse risk vector from the given weights
     * vector x
     *
     * @param x The full weights vector
     * @return A sparse version of the previously full risk vector
     */
    private double[] prepareRisk(double[] x) {
        ArrayList tmpR = new ArrayList();
        for (int i = 0; i < x.length; i++) if (x[i] > 0) tmpR.add(new Double(this.m_AssetRisk[i]));
        double[] result = new double[tmpR.size()];
        for (int i = 0; i < tmpR.size(); i++) result[i] = ((Double) tmpR.get(i)).doubleValue();
        return result;
    }

    /**
     * This method makes a sparse cov matrix from the given weights
     * vector x
     *
     * @param x The full weights vector
     * @return A sparse version of the previously full cov Matrix
     */
    private double[][] prepareCov(double[] x) {
        ArrayList tmpR = new ArrayList();
        ArrayList tmpC;
        for (int i = 0; i < x.length; i++) {
            // for each row
            if (x[i] > 0) {
                tmpC = new ArrayList();
                for (int j = 0; j < x.length; j++) {
                    // for each coluum
                    if (x[j] > 0) {
                        tmpC.add(new Double(this.m_AssetCorrelation[i][j]));
                    }
                }
                tmpR.add(tmpC);
            }
        }
        double[][] result = new double[tmpR.size()][];
        for (int i = 0; i < tmpR.size(); i++) {
            tmpC = (ArrayList) tmpR.get(i);
            result[i] = new double[tmpC.size()];
            for (int j = 0; j < tmpC.size(); j++) {
                result[i][j] = ((Double) tmpC.get(j)).doubleValue();
            }
        }
        return result;
    }

    /**
     * This method prepares the upper bounds for the local search method
     *
     * @param x The full weights vector
     * @return The upper bounds for the local search
     */
    private double[] prepareUpperBorder(double[] x) {
        ArrayList tmpR = new ArrayList();
        for (int i = 0; i < x.length; i++) {
            if (x[i] > 0) {
                if (this.m_UpperBound > 0) {
                    // there are upper bounds
                    if (x[i] > this.m_UpperBound) {
                        // this guy seems to be an exception
                        tmpR.add(new Double(1));
                    } else {
                        // this is an ordinary limited guy
                        tmpR.add(new Double(this.m_UpperBound));
                    }
                } else {
                    // there are no upper bounds imposed on this guy
                    tmpR.add(new Double(1));
                }
            }
        }
        double[] result = new double[tmpR.size()];
        for (int i = 0; i < tmpR.size(); i++) result[i] = ((Double) tmpR.get(i)).doubleValue();
        return result;
    }

    /**
     * This method turns the local solution from the local search method back
     * into a global solution by using the original weights vectors
     *
     * @param x  The original weight vector
     * @param tx The local solution
     * @return The global version of the locally optimized solution
     */
    public double[] decomposeWeights(double[] x, double[] tx) {
        double[] result = new double[x.length];
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            if (x[i] > 0) {
                result[i] = tx[index];
                index++;
            } else {
                result[i] = 0;
            }
        }
        return result;
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

        if (this.m_UpperBound > 0) {
            int exp = 0;
            for (int i = 0; i < x.length; i++) {
                if (x[i] > this.m_UpperBound) exp++;
            }
            if (exp > this.m_Exceptions) {
                if (show) System.out.println("To many upper bound exceptions " + exp + " > " + this.m_Exceptions + "!");
                return false;
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

        // take care of the upper bound
        if (this.m_UpperBound > 0) {
            // first i'll try to meet the upper bound constraint allowing n exceptions
            // search for the n biggest which are allowed to exceed the upper bound
            int[] exceptions = new int[this.m_Exceptions];
            boolean isSpace;
            for (int i = 0; i < exceptions.length; i++) exceptions[i] = -1;
            for (int i = 0; i < result.length; i++) {
                if (result[i] > this.m_UpperBound) {
                    // this is a candidate for an exception
                    // check whether or not there is still place for an exception
                    isSpace = false;
                    for (int j = 0; j < exceptions.length; j++) {
                        if (exceptions[j] < 0) {
                            exceptions[j] = i;
                            isSpace = true;
                            j = exceptions.length + 1;
                        }
                    }
                    if (!isSpace) {
                        // there was no space for this candidate
                        // perhaps there is is someone who can be replaced
                        int index = -1;
                        double lower = result[i];
                        for (int j = 0; j < exceptions.length; j++) {
                            if (result[exceptions[j]] < lower) {
                                lower = result[exceptions[j]];
                                index = j;
                            }
                        }
                        if (index >= 0) {
                            // there is some one to replace
                            exceptions[index] = i;
                        }
                    }
                }
            }
            // now i got all possible exceptions
            // lets norm all elements except the exceptions to the upper bound
            for (int i = 0; i < result.length; i++) {
                if (result[i] > this.m_UpperBound) {
                    if (!this.isException(i, exceptions)) result[i] = this.m_UpperBound;
                }
            }
            // now all elements obey the upper bounds except the exceptions of course
            // finally lets norm this stuff
            double sum = 0;
            for (int i = 0; i < result.length; i++) {
                sum += result[i];
            }
            if (sum > 1) {
                // in this case all are too big that is not much of a problem
                for (int i = 0; i < result.length; i++) {
                    result[i] = result[i] / sum;
                }
            } else {
                // in this case the portfolio is too small, darn that is a problem!
                // mmmh shall i increase all if possible? or just the exceptions!?
                // i guess all is better, imagine i have no exceptions
                // but how to increase all? and who i'm writing this too!?
                // simple first get a list of all that can be increased log the amount by
                // which they can be increase, if this amount is smaller than the missing
                // amount we are dammed, else we norm the rest to the remaining amount
                // but still i have to obey the upper bound darn darn darn....
                ArrayList space = new ArrayList();
                for (int i = 0; i < result.length; i++) {
                    if ((result[i] < this.m_UpperBound) || (this.isException(i, exceptions))) {
                        space.add(new Integer(i));
                    }
                }
                int[] spaceGuys = new int[space.size()];
                double[] availableSpace = new double[space.size()];
                double aSum = 0;
                for (int i = 0; i < space.size(); i++) {
                    spaceGuys[i] = ((Integer) space.get(i)).intValue();
                    if (this.isException(i, exceptions)) {
                        availableSpace[i] = 1 - result[spaceGuys[i]];
                    } else {
                        availableSpace[i] = this.m_UpperBound - result[spaceGuys[i]];
                    }
                    aSum += availableSpace[i];
                }
                if (aSum < (1 - sum)) {
                    // this is a severe problem
                    for (int i = 0; i < result.length; i++) result[i] = 0;
                    return result;
                } else {
                    // i guess i should be able to solve this
                    for (int i = 0; i < spaceGuys.length; i++) {
                        result[spaceGuys[i]] += (aSum / (1 - sum)) * availableSpace[i];
                    }
                }
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
        return result;
    }

    private boolean isException(int index, int[] exceptions) {
        for (int i = 0; i < exceptions.length; i++) if (index == exceptions[i]) return true;
        return false;
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
        return "C_Metric";
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

    public PropertyOptimizationObjectives getOptimizationTargets() {
        return this.m_OptimizationTargets;
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
     * This method will set the upper bound for any asset.
     *
     * @param b The upper bound.
     */
    public void setUpperBound(double b) {
        if (b < 0) b = 0;
//        if (this.m_Cardinality > 0) b = Math.max(b, 1/(double)this.m_Cardinality);
        this.m_UpperBound = b;
    }

    public double getUpperBound() {
        return this.m_UpperBound;
    }

    public String upperBoundTipText() {
        return "The minimum value for an asset (0<x<<1).";
    }

    /**
     * This method will set the roundlots
     *
     * @param b The roundlots
     */
    public void setExceptions(int b) {
        // b should not be negative
        if (b < 0) b = 0;
        this.m_Exceptions = b;
    }

    public int getExceptions() {
        return this.m_Exceptions;
    }

    public String exceptionsTipText() {
        return "The number of exceptions for the upper bound.";
    }

    public String showParetFrontTipText() {
        return "Toggles the path visualisation.";
    }

    /**
     * This method allows you to choose the type of local search.
     *
     * @param s The type.
     */
    public void setLocalSearchType(SelectedTag s) {
        this.m_LocalSearchType = s;
    }

    public SelectedTag getLocalSearchType() {
        return this.m_LocalSearchType;
    }

    public String localSearchTypeTipText() {
        return "Choose the type ol local serach (none, single or multiple solutions).";
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