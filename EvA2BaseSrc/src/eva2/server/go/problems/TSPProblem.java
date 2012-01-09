package eva2.server.go.problems;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.InterfaceDataTypePermutation;
import eva2.server.go.individuals.OBGAIndividualPermutationData;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractOptimizationProblem;
import eva2.server.go.problems.tsputil.InterfaceTSPInstance;
import eva2.server.go.problems.tsputil.InterfaceTSPLocalSearch;
import eva2.server.go.problems.tsputil.TSPLibTSPInstance;
import eva2.server.go.problems.tsputil.TSPLocalSearch2OPT;
import eva2.server.go.problems.tsputil.TSPProblemViewer;
import eva2.server.go.strategies.InterfaceOptimizer;

/**
 * <p>Title: EvA2</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author planatsc
 * @version 1.0
 */

public class TSPProblem extends AbstractOptimizationProblem implements java.io.Serializable {

    private InterfaceTSPInstance TSPInstance;
    // private AbstractEAIndividual
    private double bestfitness;
    private int[] besttour;
    private transient TSPProblemViewer tspviewer;
    private boolean doLocalSearch;
    private boolean showViewer;
    private InterfaceTSPLocalSearch localSearchOperator;
    //private int sleepTime;

    public TSPProblem() {
        this.m_Template = new OBGAIndividualPermutationData();
        TSPInstance = new TSPLibTSPInstance();
        showViewer = false;
        if (showViewer) tspviewer = new TSPProblemViewer(TSPInstance);
        bestfitness = Double.MAX_VALUE;
        doLocalSearch = true;
        localSearchOperator = new TSPLocalSearch2OPT();
        initProblem();
    }

    public TSPProblem(TSPProblem b) {
        //AbstractOptimizationProblem
        this.TSPInstance = b.getTSPInstance();
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        //TSPProblem
        this.doLocalSearch = b.doLocalSearch;
        this.showViewer = b.showViewer;
        this.bestfitness = b.bestfitness;
        System.out.println("TSPProblem: Warning can't clone localSearchOperator!");
        this.localSearchOperator = b.localSearchOperator;

    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new TSPProblem(this);
    }

    public void initProblem() {
        //System.out.println("INITPROBLEM");
        bestfitness = Double.MAX_VALUE;
        if (this.showViewer) {
            if (tspviewer == null) {
                tspviewer = new TSPProblemViewer(TSPInstance);
            } else {
                tspviewer.setTspinst(TSPInstance);
            }
        }
        //@todo Implement this eva2.server.go.OptimizationProblems.InterfaceOptimizationProblem method*/
    }

    public void initPopulation(Population population) {
        initProblem();
        //InterfaceMutation tmutop = new MutateOBGAInversion();
        ((InterfaceDataTypePermutation) m_Template).setPermutationDataLength(new int[]{this.TSPInstance.getSize() - 1});
        ((InterfaceDataTypePermutation) m_Template).setFirstindex(new int[]{1});

        AbstractOptimizationProblem.defaultInitPopulation(population, m_Template, this);
    }

    //    public void evaluate(Population population) {
    //        AbstractEAIndividual    tmpIndy;
    //        for (int i = 0; i < population.size(); i++) {
    //            tmpIndy = (AbstractEAIndividual) population.get(i);
    //            tmpIndy.resetConstraintViolation();
    //            this.evaluate(tmpIndy);
    //            population.incrFunctionCalls();
    //        }
    //		//if (sleepTime > 0 ) try { Thread.sleep(sleepTime); } catch(Exception e) {}
    //    }

    public void evaluate(AbstractEAIndividual individual) {
        if (individual instanceof InterfaceDataTypePermutation) {
            int[] perm = ((InterfaceDataTypePermutation) individual).getPermutationData()[0];
            double tourlength = 0;
            tourlength += TSPInstance.getDistance(0, perm[0]);
            for (int i = 1; i < perm.length; i++) {
                tourlength += TSPInstance.getDistance(perm[i - 1], perm[i]);
            }
            tourlength += TSPInstance.getDistance(perm[perm.length - 1], 0);
            individual.SetFitness(new double[]{tourlength});
            if (tourlength < bestfitness) {
                bestfitness = tourlength;
                besttour = perm;
                if (this.doLocalSearch)
                    this.localSearchOperator.doLocalSearch(this.TSPInstance, (InterfaceDataTypePermutation) individual);
                if (this.showViewer) {
                    if (tspviewer == null) tspviewer = new TSPProblemViewer(TSPInstance);
                    tspviewer.draw(perm, tourlength);
                }
            }
        }
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        return "";
    }

    public String getStringRepresentationForProblem() {
        return "";
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
        return "Traveling Salesman Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Given a finite number of 'cities' along with the distance between each pair of them, find the cheapest way of visiting all the cities and returning to your starting point";
    }

    public InterfaceTSPInstance getTSPInstance() {
        return TSPInstance;
    }

    public void setTSPInstance(InterfaceTSPInstance m_TSPInstance) {
        this.TSPInstance = m_TSPInstance;
        initProblem();
    }

    public String TSPInstanceTipText() {
        return "Instance of a TSP";
    }

    public InterfaceTSPLocalSearch getLocalSearchOperator() {
        return localSearchOperator;
    }

    public void setLocalSearchOperator(InterfaceTSPLocalSearch m_localSearchOperator) {
        this.localSearchOperator = m_localSearchOperator;
    }

    public String localSearchOperatorTipText() {
        return "The operator used by the local search.";
    }

    public boolean isDoLocalSearch() {
        return doLocalSearch;
    }

    public void setDoLocalSearch(boolean doLocalSearch) {
        this.doLocalSearch = doLocalSearch;
    }

    public String doLocalSearchTipText() {
        return "Enables/Disables the local search";
    }

    public boolean isShowViewer() {
        return showViewer;
    }

    public void setShowViewer(boolean showViewer) {
        if (showViewer) {
            if (tspviewer == null) {
                tspviewer = new TSPProblemViewer(TSPInstance);
            } else {
                tspviewer.setTspinst(this.TSPInstance);
                tspviewer.setVisible(true);
            }
        } else {
            if (tspviewer != null) {
                tspviewer.setVisible(false);
                tspviewer.dispose();
                tspviewer = null;
            }
        }
        this.showViewer = showViewer;
    }

    public String showViewerTipText() {
        return "Displays a viewer for TSP-Problem.";
    }

    public InterfaceDataTypePermutation getEAIndividual() {
        return (InterfaceDataTypePermutation) m_Template;
    }

    public void setEAIndividual(InterfaceDataTypePermutation m_Template) {
        this.m_Template = (AbstractEAIndividual) m_Template;
    }

}
