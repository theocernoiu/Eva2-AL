/**
 *
 */
package eva2.server.go.problems;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractSynchronousOptimizationProblem;
import eva2.server.go.problems.F1Problem;
import eva2.tools.math.RNG;

/**
 * An abstract class that represents a translated F1 problem. This makes it easier to produce different
 * kinds of translations.
 *
 * @author marcekro
 *         <p/>
 *         Jan 8, 2007
 */
public abstract class DynTransF1Problem extends AbstractSynchronousOptimizationProblem {
    /**
     * The F1 problem instance serving as base problem.
     */
    protected F1Problem f1;

    /**
     * A constructor.
     */
    public DynTransF1Problem() {
        f1 = new F1Problem();
        m_Template = new ESIndividualDoubleData();
    }

    /**
     * Returns the F1 instance of the object.
     *
     * @return the F1 problem instance of the object.
     */
    protected F1Problem getF1Instance() {
        return f1;
    }

    /**
     * Evaluate the F1 function at the individuals position using an arbitrary translation which may be dynamically changing.
     *
     * @param individual the individual to be evaluated
     * @param t          timestamp of the evaluation
     */
    public void evaluateAt(AbstractEAIndividual individual, double t) {
        double[] x;
        double[] fitness;

        x = new double[((InterfaceDataTypeDouble) individual).getDoubleData().length];
        System.arraycopy(((InterfaceDataTypeDouble) individual).getDoubleData(), 0, x, 0, x.length);

        //System.out.println("translation at (" + getProblemTime() + " is " + getTranslation(0, getProblemTime()));

        for (int i = 0; i < x.length; i++) {
            // the actual dynamics comes in through time-dependent translation
            x[i] = x[i] + f1.getXOffSet() + getTranslation(i, t);
        }
        fitness = f1.eval(x);
        for (int i = 0; i < fitness.length; i++) {
            // add noise to the fitness
            fitness[i] += RNG.gaussianDouble(f1.getNoise());
            fitness[i] += f1.getYOffSet();

            // set the fitness of the individual
            individual.SetFitness(i, fitness[i]);
        }
//        if (f1.m_UseTestConstraint) {
//            if (x[0] < 1) individual.addConstraintViolation(1-x[0]);
//        }
//        if ((f1.m_OverallBest == null) || (f1.m_OverallBest.getFitness(0) > individual.getFitness(0))) {
//        	if ((f1.m_OverallBest != null) &&  TRACE ) System.out.println("found new best: " + individual.getFitness(0));
//            f1.m_OverallBest = (AbstractEAIndividual)individual.clone();
//        }
    }

    /**
     * Returns the translation in the given dimension at the given time.
     *
     * @param dim  the dimension
     * @param time the simulation time
     * @return the translation in the given dimension at the given time
     */
    protected abstract double getTranslation(int dim, double time);

    /**
     * Returns the individuum representing the current optimum of the time-variable F1 problem.
     *
     * @return an optimal individuum
     */
    public AbstractEAIndividual getCurrentOptimum() {
        AbstractEAIndividual indy = new ESIndividualDoubleData();
        int n = f1.getProblemDimension();
        ((InterfaceDataTypeDouble) indy).setDoubleDataLength(n);
        ((InterfaceDataTypeDouble) indy).SetDoubleRange(f1.get2DBorder());
        double[] optVect = ((InterfaceDataTypeDouble) indy).getDoubleData();
        for (int i = 0; i < optVect.length; i++) optVect[i] = getTranslation(i, getCurrentProblemTime());
        ((InterfaceDataTypeDouble) indy).SetDoubleGenotype(optVect);
        return indy;
    }

    /**
     * Initializes the underlying F1 problem.
     */
    public void initProblem() {
        super.initProblem();
        f1.initProblem();
    }

    /**
     * This recalculates the overall best individual stored within the F1 problem instance.
     */
    public void resetProblem(double sev) {
//		if ((f1 != null) && (f1.m_OverallBest != null)) {
//			//System.out.println(getSimTime() + ": best was " + f1.m_OverallBest.getFitness(0));
//			this.evaluateAt(f1.m_OverallBest, getCurrentProblemTime());
//			//System.out.println("best is now " + f1.m_OverallBest.getFitness(0));
//		}
    }

    /* (non-Javadoc)
      * @see eva2.server.go.OptimizationProblems.AbstractDynamicOptimizationProblem#initPopulationAt(eva2.server.go.Populations.Population, double)
      */
    @Override
    public void initPopulationAt(Population population, double time) {
        if (TRACE) System.out.println("DynTransF1Problem at " + this + " initPop, f1 is " + f1);
        f1.initPopulation(population);
        //population.setResetFitOnAdd(true);
        for (int i = 0; i < population.size(); i++) ((AbstractEAIndividual) population.get(i)).SetAge(0);
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
