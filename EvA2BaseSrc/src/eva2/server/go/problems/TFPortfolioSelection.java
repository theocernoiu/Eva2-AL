package eva2.server.go.problems;


import javax.swing.*;

import eva2.gui.FunctionArea;
import eva2.gui.GraphPointSet;
import eva2.gui.PropertyFilePath;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.individuals.GAESIndividualBinaryDoubleData;
import eva2.server.go.individuals.InterfaceDataTypeBinary;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.mocco.paretofrontviewer.InterfaceParetoFrontView;
import eva2.server.go.mocco.paretofrontviewer.MOCCOViewer;
import eva2.server.go.operators.archiving.ArchivingAllDominating;
import eva2.server.go.operators.archiving.ArchivingNSGA;
import eva2.server.go.operators.constraint.InterfaceConstraint;
import eva2.server.go.operators.moso.InterfaceMOSOConverter;
import eva2.server.go.operators.moso.MOSONoConvert;
import eva2.server.go.operators.paretofrontmetrics.InterfaceParetoFrontMetric;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractMultiObjectiveOptimizationProblem;
import eva2.server.go.problems.InterfaceMultiObjectiveDeNovoProblem;
import eva2.server.go.problems.InterfaceOptimizationObjective;
import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionObjective;
import eva2.server.go.problems.portfolio.ViewerPortfolioSelection;
import eva2.server.go.problems.portfolio.objective.ObjectivePortfolioAggWeighted;
import eva2.server.go.problems.portfolio.objective.ObjectivePortfolioCardinality;
import eva2.server.go.problems.portfolio.objective.ObjectivePortfolioDividends;
import eva2.server.go.problems.portfolio.objective.ObjectivePortfolioReturn;
import eva2.server.go.problems.portfolio.objective.ObjectivePortfolioRisk;
import eva2.server.go.problems.portfolio.objective.PropertyPortfolioSelectionObjectives;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.math.RNG;


import java.util.ArrayList;
import java.util.BitSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.*;

import eva2.tools.chart2d.DRectangle;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 07.04.2005
 * Time: 11:32:02
 * To change this template use File | Settings | File Templates.
 */
public class TFPortfolioSelection extends AbstractMultiObjectiveOptimizationProblem implements InterfaceMultiObjectiveDeNovoProblem, java.io.Serializable {

    private int m_NumberOfAssets;
    private PropertyFilePath m_InputFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/Port1_Return.txt");
    private PropertyPortfolioSelectionObjectives m_Objectives;
    private boolean m_UseBitMask = false;
    private boolean m_UseLamarckism = false;
    private boolean m_UseLocalSearch = false;
    private boolean m_UseCardInit = false;
    private int m_Cardinality = 0;
    private double m_BuyInThreshold = 0.0;
    private double m_RoundLots = 0.0;

    transient public ViewerPortfolioSelection m_Viewer;

    public TFPortfolioSelection() {
        // set the targets
        InterfacePortfolioSelectionObjective[] tmpList = new InterfacePortfolioSelectionObjective[5];
        tmpList[0] = new ObjectivePortfolioReturn();
        tmpList[1] = new ObjectivePortfolioRisk();
        tmpList[2] = new ObjectivePortfolioCardinality();
        tmpList[3] = new ObjectivePortfolioDividends();
        tmpList[4] = new ObjectivePortfolioAggWeighted();
        this.m_Objectives = new PropertyPortfolioSelectionObjectives(tmpList);
        tmpList = new InterfacePortfolioSelectionObjective[3];
        tmpList[1] = new ObjectivePortfolioReturn();
        tmpList[0] = new ObjectivePortfolioRisk();
        tmpList[2] = new ObjectivePortfolioDividends();
        this.m_Objectives.setSelectedTargets(tmpList);
        // init the problem frame
        if (isShowParetoFront()) this.initProblemFrame();
        // set the template
        this.m_Template = new GAESIndividualBinaryDoubleData();
        ((GAESIndividualBinaryDoubleData) this.m_Template).setNumbers(new ESIndividualDoubleData());
        // set reasonable default values
        this.m_Cardinality = 0;
        this.m_UseBitMask = true;
        this.m_UseLamarckism = true;
        this.initProblem();
    }

    public TFPortfolioSelection(TFPortfolioSelection b) {
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
        if (b.m_Objectives != null) {
            this.m_Objectives = (PropertyPortfolioSelectionObjectives) b.m_Objectives.clone();
        }

        // TFPortfolioSelection
        this.m_UseBitMask = b.m_UseBitMask;
        this.m_UseLamarckism = b.m_UseLamarckism;
        this.m_Cardinality = b.m_Cardinality;
        this.m_BuyInThreshold = b.m_BuyInThreshold;
        this.m_RoundLots = b.m_RoundLots;
        this.m_UseLocalSearch = b.m_UseLocalSearch;
        this.m_NumberOfAssets = b.m_NumberOfAssets;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new TFPortfolioSelection(this);
    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        this.determineNumberOfAssets();
        InterfacePortfolioSelectionObjective[] list = this.m_Objectives.getSelectedTargets();
        this.m_Border = new double[list.length][2];
        for (int i = 0; i < this.m_Border.length; i++) {
            this.m_Border[i] = ((InterfacePortfolioSelectionObjective) list[i]).getObjectiveBoundaries();
        }
        this.m_ParetoFront = new Population();
        if ((isShowParetoFront()) && (this.m_Viewer == null)) this.initProblemFrame();
    }

    /**
     * This method loads the objective specific data from a file.
     */
    private void determineNumberOfAssets() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.m_InputFilePath.getCompleteFilePath()));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Could not find " + this.m_InputFilePath.getCompleteFilePath());
            return;
        }
        String currentLine;
        try {
            this.m_NumberOfAssets = 0;
            while ((currentLine = reader.readLine()) != null && currentLine.length() != 0) {
                this.m_NumberOfAssets++;
            }
            currentLine = reader.readLine();
            reader.close();
        } catch (java.io.IOException e) {
            System.out.println("Java.io.IOExeption: " + e.getMessage());
        }
    }

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        InterfacePortfolioSelectionObjective[] list = this.m_Objectives.getSelectedTargets();
        this.m_Border = new double[list.length][2];
        for (int i = 0; i < this.m_Border.length; i++) {
            this.m_Border[i] = ((InterfacePortfolioSelectionObjective) list[i]).getObjectiveBoundaries();
        }
        double[] tmpData, newData;
        int tmpIndex, tmpCard;
        if (isShowParetoFront()) this.initProblemFrame();

        AbstractEAIndividual tmpIndy;
        double[][] newRange = new double[this.m_NumberOfAssets][2];

        for (int i = 0; i < this.m_NumberOfAssets; i++) {
            newRange[i][0] = 0;
            newRange[i][1] = 1;
        }

        population.clear();

        ((InterfaceDataTypeBinary) this.m_Template).setBinaryDataLength(this.m_NumberOfAssets);
        ((InterfaceDataTypeDouble) this.m_Template).setDoubleDataLength(this.m_NumberOfAssets);
        ((InterfaceDataTypeDouble) this.m_Template).SetDoubleRange(newRange);

        for (int i = 0; i < population.getTargetSize(); i++) {
            tmpIndy = (AbstractEAIndividual) ((AbstractEAIndividual) this.m_Template).clone();
            tmpIndy.init(this);
            if ((this.m_UseCardInit) && (this.m_Cardinality > 0)) {
                tmpData = ((InterfaceDataTypeDouble) tmpIndy).getDoubleData();
                tmpCard = RNG.randomInt(0, this.m_Cardinality);
                newData = new double[tmpData.length];
                for (int j = 0; j < tmpCard; j++) {
                    tmpIndex = RNG.randomInt(0, this.m_NumberOfAssets - 1);
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
        if ((isShowParetoFront()) && (this.m_Viewer != null)) this.m_Viewer.update(null);
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
                // @todo here the boder is changing
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

        if (isShowParetoFront()) this.updateProblemFrame(population);

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

        individual.resetConstraintViolation();
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
        InterfacePortfolioSelectionObjective[] targets;
        targets = this.m_Objectives.getSelectedTargets();

        // norm the portfolio
        x = this.normPortfolio(x);
        indy.SetDoublePhenotype(x);

        // fetch the optimization target values
        result = new double[targets.length];
        if (this.isValid(x)) {
            for (int i = 0; i < result.length; i++) {
                result[i] = ((InterfacePortfolioSelectionObjective) targets[i]).evaluatePortfolio(indy);
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                result[i] = ((InterfacePortfolioSelectionObjective) targets[i]).getObjectiveBoundaries()[1];
            }
        }

        // remove constraints for this this, i.e. constraints would set
        // the result to NaN to intendify as constraint
        boolean constraint = false;
        ArrayList tmpL = new ArrayList();
        for (int i = 0; i < result.length; i++) {
            if (!(new Double(result[i])).isNaN()) {
                tmpL.add(new Double(result[i]));
            } else {
                constraint = true;
            }
        }
        if (constraint) {
            result = new double[tmpL.size()];
            for (int i = 0; i < tmpL.size(); i++) {
                result[i] = ((Double) tmpL.get(i)).doubleValue();
            }
        }

        return result;
    }

    /**
     * This method allows you to request a graphical representation for a given
     * individual.
     *
     * @return JComponent
     */
    public JComponent drawIndividual(AbstractEAIndividual indy) {
        JPanel result = new JPanel();
        DRectangle rect;
        result.setLayout(new BorderLayout());
        double[] tmp = ((InterfaceDataTypeDouble) indy).getDoubleData();
        double[] x = new double[tmp.length];
        for (int i = 0; i < tmp.length; i++) x[i] = tmp[i];
        if ((indy instanceof GAESIndividualBinaryDoubleData) && (this.m_UseBitMask)) {
            BitSet t = ((GAESIndividualBinaryDoubleData) indy).getBinaryData();
            for (int i = 0; i < x.length; i++) if (!t.get(i)) x[i] = 0;
        }
        FunctionArea area = new FunctionArea("Asset", "Weight");
        result.add(area, BorderLayout.CENTER);
        for (int i = 0; i < x.length; i++) {
            rect = new DRectangle(i, 0, 1, x[i]);
            rect.setColor(Color.BLACK);
            rect.setFillColor(Color.RED);
            area.addDElement(rect);
        }

        GraphPointSet mySet = new GraphPointSet(0, area);
        mySet.setConnectedMode(false);
        mySet.addDPoint(0, 0);
        mySet.addDPoint(x.length + 1, 1);

        // give the specs in the lower panel
        JPanel specs = new JPanel();
        specs.setLayout(new GridLayout(this.getObjectives().getSelectedTargets().length, 2));
        InterfacePortfolioSelectionObjective[] targets;
        targets = this.m_Objectives.getSelectedTargets();
        for (int i = 0; i < targets.length; i++) {
            specs.add(new JLabel(targets[i].getName() + ":"));
            specs.add(new JLabel("" + ((Double) indy.getData(((InterfaceOptimizationObjective) targets[i]).getIdentName())).doubleValue()));
        }
        result.add(specs, BorderLayout.SOUTH);
        result.setPreferredSize(new Dimension(400, 300));
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

        result += this.calculateMetric(tmpPop) + "\t";
        // now add the efficiency front
        if (false) {
            Population archive = tmpPop.getArchive();
            result += "{";
            double[][] values = new double[archive.size()][2];
            AbstractEAIndividual indy;
            int tmpIndex;

            for (int i = 0; i < archive.size(); i++) {
                indy = (AbstractEAIndividual) archive.get(i);
                values[i][0] = indy.getFitness(0);
                values[i][1] = -indy.getFitness(1);
            }
            for (int i = 0; i < values.length; i++) {
                tmpIndex = 0;
                for (int j = 1; j < values.length; j++) {
                    if (values[tmpIndex][0] < values[j][0]) tmpIndex = j;
                }
                if (values[tmpIndex][1] > 0) result += "(" + values[tmpIndex][0] + ", " + values[tmpIndex][1] + "); ";
                values[tmpIndex][0] = -1;
            }
            result += "}";
        }

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
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";
        InterfaceOptimizationObjective[] obj = this.getProblemObjectives();
        result += "Portfolio Optimization Problem:\n";
        result += "Using " + this.m_InputFilePath.getCompleteFilePath() + " as input.\n";
        result += "With " + obj.length + " objectives.\n";
        for (int i = 0; i < obj.length; i++) {
            result += ((InterfacePortfolioSelectionObjective) obj[i]).getStringRepresentation();
        }
        result += "MOSO Converter = " + this.m_MOSOConverter.getName() + "\n";
        result += this.m_MOSOConverter.getStringRepresentation();
        result += "Constraints: \n";
        result += " Cardinality = " + this.m_Cardinality + "\n";
        result += " Buy-In      = " + this.m_BuyInThreshold + "\n";
        result += " Roundlot    = " + this.m_RoundLots + "\n";
        result += "Algorithm parameters: \n";
        result += " BitMask      = " + this.m_UseBitMask + "\n";
        result += " Lamarckism   = " + this.m_UseLamarckism + "\n";
        result += " Local Search = " + this.m_UseLocalSearch + "\n";
        return result;
    }

    /**
     * This method will init the problem specific visualisation of the problem
     */
    public void initProblemFrame() {
        super.initProblemFrame();
        if (this.m_Viewer == null) {
            this.m_Viewer = new ViewerPortfolioSelection(this);
            this.m_Viewer.update(this.m_ParetoFront);
        }
    }

    /**
     * This method will dispose the current problem frame
     */
    public void disposeProblemFrame() {
        if (this.m_Viewer != null) this.m_Viewer.dispose();
    }

    /**
     * This method will draw the current state of the optimization process
     *
     * @param p The current population
     */
    public void updateProblemFrame(Population p) {
        if (this.m_Viewer != null) this.m_Viewer.update(p);
    }

    /**
     * This method will report whether or not this optimization problem is truly
     * multi-objective
     *
     * @return True if multi-objective, else false.
     */
    public boolean isMultiObjective() {
        InterfacePortfolioSelectionObjective[] targets = this.m_Objectives.getSelectedTargets();
        int dim = 0;
        for (int i = 0; i < targets.length; i++) {
            if (((InterfaceOptimizationObjective) targets[i]).getOptimizationMode().indexOf("Objective") >= 0) {
                dim++;
            }
        }
        if (dim > 1) {
            if (((AbstractMultiObjectiveOptimizationProblem) this).getMOSOConverter() instanceof MOSONoConvert) {
                return true;
            } else {
                return false;
            }
        } else return false;
    }


    /**********************************************************************************************************************
     * These are for the InterfaceMultiObjectiveDeNovoProblem
     */

    /**
     * This method allows you to recieve all the optimization
     * objectives
     *
     * @return A list of optimization objectives
     */
    public InterfaceOptimizationObjective[] getProblemObjectives() {
        InterfacePortfolioSelectionObjective[] tmp = this.m_Objectives.getSelectedTargets();
        InterfaceOptimizationObjective[] result = new InterfaceOptimizationObjective[tmp.length];
        for (int i = 0; i < tmp.length; i++) result[i] = (InterfaceOptimizationObjective) tmp[i];
        return result;
    }

    /**
     * This method will generate a problem specific view on the Pareto
     * front. Nice idea isn't it. This idea was by Jochen, the teamleader
     * of the CombiChem guys at ALTANA Pharma Konstanz.
     *
     * @return the Panel
     */
    public InterfaceParetoFrontView getParetoFrontViewer4MOCCO(MOCCOViewer t) {
        // pff ??
        return null;
    }

    /**
     * This method allows MOCCO to deactivate the representation editior
     * if and only if the specific editor reacts to this signal. This signal
     * cannot be deactivated!
     */
    public void deactivateRepresentationEdit() {
        // ignore as long as there is no editor
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
        if (b < 0) b = 0;
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
        if (b < 0) b = 0;
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
     * This method allows you to choose the optimization targets.
     *
     * @param b File path.
     */
    public void setObjectives(PropertyPortfolioSelectionObjectives b) {
        this.m_Objectives = b;
        if (this.m_Viewer != null) this.m_Viewer.update(null);
    }

    public PropertyPortfolioSelectionObjectives getObjectives() {
        return this.m_Objectives;
    }

    public String objectivesTipText() {
        return "Choose the optimization objectives.";
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
