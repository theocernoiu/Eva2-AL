package eva2.server.go.problems;


import java.io.FileReader;
import java.io.LineNumberReader;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.ESIndividualDoubleData;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.populations.Population;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.math.Mathematics;
import eva2.tools.math.RNG;

/**
 * User: depaly
 * Date: 24.03.2003
 * Time: 17:58:55
 * To change this template use Options | File Templates.
 */

public class IrrigationProblem extends AbstractMultiObjectiveOptimizationProblem implements InterfaceProblemDouble {

    protected int m_NumberOfDays = 130;
    protected int m_IrrigationInterval = 1;
    protected double m_MaxWater = 46.15;
    protected boolean m_Lamarckian = true;
    protected boolean m_ismultiobjective = false;
    protected boolean doRandomZeros = false;

    static double xx[] = {0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0}; //support vectors
    static double pp[] = {0.88, 0.8, 0.7, 0.6, 0.55, 0.5, 0.45, 0.43, 0.4, 0.0};
    static double[] d = {10.00, 11.43, 12.86, 14.29, 15.71, 17.14, 18.57, 20.00, 21.43, 22.86, 24.29, 25.71, 27.14, 28.57, 30.00, 31.43, 32.86, 34.29, 35.71, 37.14, 38.57, 40.00, 41.43, 42.86, 44.29, 45.71, 47.14, 48.57, 50.00, 51.43, 52.86, 54.29, 55.71, 57.14, 58.57, 60.00, 61.43, 62.86, 64.29, 65.71, 67.14, 68.57, 70.00, 71.43, 72.86, 74.29, 75.71, 77.14, 78.57, 80.00, 81.43, 82.86, 84.29, 85.71, 87.14, 88.57, 90.00, 91.43, 92.86, 94.29, 95.71, 97.14, 98.57, 100.00, 101.43, 102.86, 104.29, 105.71, 107.14, 108.57, 110.00, 111.43, 112.86, 114.29, 115.71, 117.14, 118.57, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00, 120.00}; //root depth
    static double[] pet = {0.15, 0.18, 0.18, 0.19, 0.14, 0.19, 0.17, 0.22, 0.21, 0.2, 0.12, 0.13, 0.22, 0.21, 0.15, 0.22, 0.21, 0.22, 0.21, 0.16, 0.23, 0.2, 0.24, 0.24, 0.2, 0.32, 0.28, 0.28, 0.3, 0.27, 0.1, 0.25, 0.38, 0.28, 0.46, 0.45, 0.43, 0.46, 0.48, 0.38, 0.53, 0.54, 0.6, 0.54, 0.58, 0.45, 0.57, 0.59, 0.86, 0.72, 0.75, 0.68, 0.67, 0.51, 0.74, 0.8, 0.91, 0.9, 0.82, 0.82, 0.85, 0.57, 0.68, 0.65, 0.74, 0.69, 0.77, 0.75, 0.78, 0.59, 0.62, 0.45, 0.75, 0.65, 0.72, 0.95, 0.74, 0.71, 0.78, 0.78, 0.81, 0.33, 0.72, 0.51, 0.54, 0.74, 0.74, 0.72, 0.71, 0.57, 0.57, 0.24, 0.69, 0.65, 0.59, 0.6, 0.69, 0.69, 0.71, 0.11, 0.53, 0.11, 0.17, 0.59, 0.6, 0.58, 0.5, 0.55, 0.5, 0.47, 0.3, 0.12, 0.44, 0.42, 0.34, 0.26, 0.35, 0.28, 0.26, 0.23, 0.18, 0.17, 0.29, 0.16, 0.25, 0.22, 0.19, 0.25, 0.11, 0.14, 0.17, 0.14}; //potential evapotranspiration

    public IrrigationProblem() {
        initProblem();
        this.m_Template = new ESIndividualDoubleData();
    }

    public IrrigationProblem(int numDays, int irrInterval, boolean lamarckian) {
        initProblem();
        this.m_Template = new ESIndividualDoubleData();
        m_NumberOfDays = numDays;
        m_IrrigationInterval = irrInterval;
        m_Lamarckian = lamarckian;
    }

    public IrrigationProblem(IrrigationProblem b) {
        initProblem();
        this.m_NumberOfDays = b.m_NumberOfDays;
        this.m_MaxWater = b.m_MaxWater;
        this.m_IrrigationInterval = b.m_IrrigationInterval;
//		this.d=b.d.clone();
//		this.pet=b.pet.clone();
        this.m_ismultiobjective = b.m_ismultiobjective;
        this.m_Lamarckian = b.m_Lamarckian;

    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new IrrigationProblem(this);
    }

    public void loadClimate() {
        d = new double[m_NumberOfDays];
        pet = new double[m_NumberOfDays];

        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader("D.txt"));
            String currentLine = reader.readLine();
            while (currentLine != null && reader.getLineNumber() <= m_NumberOfDays) {
                d[reader.getLineNumber() - 1] = new Double(currentLine).doubleValue();
                currentLine = reader.readLine();
            }
            reader.close();
        } catch (java.io.IOException e) {
            System.out.println("File for D values not found");
        }

        try {
            reader = new LineNumberReader(new FileReader("PET.txt"));
            String currentLine = reader.readLine();
            while (currentLine != null && reader.getLineNumber() <= m_NumberOfDays) {
                pet[reader.getLineNumber() - 1] = new Double(currentLine).doubleValue();
                currentLine = reader.readLine();
            }
            reader.close();
        } catch (java.io.IOException e) {
            System.out.println("File for PET values not found");
        }


    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        super.initProblem();
    }

    public void setTemplate(InterfaceDataTypeDouble m_Template) {
        this.m_Template = (AbstractEAIndividual) m_Template;
    }

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        AbstractEAIndividual tmpIndy;


        population.clear();

        ((InterfaceDataTypeDouble) this.m_Template).setDoubleDataLength(getProblemDimension());
        ((InterfaceDataTypeDouble) this.m_Template).SetDoubleRange(makeRange());

        for (int i = 0; i < population.getTargetSize(); i++) {
            tmpIndy = (AbstractEAIndividual) ((AbstractEAIndividual) this.m_Template).clone();
            tmpIndy.init(this);


            double[] x = new double[((InterfaceDataTypeDouble) tmpIndy).getDoubleData().length];
            System.arraycopy(((InterfaceDataTypeDouble) tmpIndy).getDoubleData(), 0, x, 0, x.length);


//			force maxwater constraint
            double sumwater = 0;
            for (int l = 0; l < x.length; l++) {
                sumwater += x[l];
            }
            for (int l = 0; l < x.length; l++) {
                x[l] *= m_MaxWater / sumwater;
            }

            ((InterfaceDataTypeDouble) tmpIndy).SetDoubleGenotype(x);


            population.add(tmpIndy);
        }
        // population init must be last
        // it set's fitcalls and generation to zero
        population.init();
    }

    public int getProblemDimension() {
        return this.m_NumberOfDays / this.m_IrrigationInterval;
    }

    /**
     * This method inits the range for single irrigations between 0 and MaxWater
     */
    public double[][] makeRange() {
        double[][] range = new double[getProblemDimension()][2];
        for (int i = 0; i < range.length; i++) {
            range[i][0] = getRangeLowerBound(i);
            range[i][1] = getRangeUpperBound(i);
        }
        return range;
    }

    public double getRangeLowerBound(int dim) {
        return 0;
    }

    public double getRangeUpperBound(int dim) {
        return m_MaxWater;
    }

    /**
     * This method calculates the depletion factors fror a given etp
     *
     * @param etp evapotranspiration
     * @return depletion factor
     */
    protected double dp_dpl_factor(double etp) {
        //%storage depletion factor in cm

//		%for cotton, maize*-, olive, safflower, sorghum, soybean, sugarbeet, 
//		%    sugarcane, tobacco
//		$    	pp=[0.88,0.8,0.7,0.6,0.55,0.5,0.45,0.43,0.4,0.0];

        /*%for onion, pepper, potato
      %PP=[0.50 0.425 0.35 0.30 0.25 0.225 0.20 0.20 0.175 0.0];

      %banana, cabbage, grape, pea, tomato
      %PP=[0.675 0.575 0.475 0.40 0.35 0.325 0.275 0.25 0.225 0.0];

      %alfalfa, bean, citrus, groundnut, pineapple, sunflower, watermelon, wheat
      %PP=[0.80 0.70 0.60 0.50 0.45 0.425 0.375 0.35 0.30 0.0];
           */

        double p = 0;


        if (etp <= xx[0]) {
            p = pp[0];
        } else {
            for (int n = 1; n < 9; n++) {
                if (xx[n - 1] < etp && etp <= xx[n])
                    p = pp[n - 1] + (pp[n - 1] - pp[n]) / (xx[n - 1] - xx[n]) * (etp - xx[n - 1]);

            }
        }

        return p;
    }

    /**
     * This method calculates the deep perkulation for a day
     *
     * @param theta  current soil moisture contet
     * @param irr    irrigation volume
     * @param d      root depth
     * @param fc     field capacity
     * @param thetar reserved for future use
     * @return deep perkulation for a day
     */
    protected double dp_pk(double theta, double irr, double d, double fc, double thetar) {
        //actual  percolation
        double pk = 0;
        if (irr >= (fc - theta) * d) {
            pk = irr - (fc - theta) * d;
        }
        return pk;
    }

    /**
     * This method calculates the actual evapotranspiration for a day
     *
     * @param theta current soil moisture contet
     * @param pet   potential evapotranspirtion for the current day
     * @param p     precipitation for the current day
     * @param d     root depth
     * @param fc    field capacity
     * @param pwp   permanent wilting point
     * @return actual evapotanspiration for a day
     */
    protected double dp_aet(double theta, double pet, double p, double d, double fc, double pwp) {
        //%actual evapotranspiration
        //%asw ist eine absolute Groesse die Wurzeltiefe ist schon eingerechnet
        double aet = 0;
        if (theta * d >= ((1 - p) * (fc - pwp) * d)) {
            //%    aet=round(pet);
            aet = pet;
        } else {
            //%    aet=round(theta*d*pet/((1-p)*asw));
            aet = theta * d * pet / ((1 - p) * (fc - pwp) * d);
        }
        return aet;
    }

    /**
     * This method calculates the achieved yield from the actual evapotanspiration
     *
     * @param pet  vector of the potential evapotranspiration for every day of th growing period
     * @param aet  vector of the actual evapotranspiration for every day of th growing period
     * @param mean switches beteween differen yield models false for a 4 stage model true for a simpler averaging model
     * @return yield
     */
    protected double dp_yield_production(double pet[], double aet[], boolean mean) {

        double relY = 1;
        /*%CROP PARAMETER (Reference: CROPWAT 4 Windows):
      %stages ... start of the 2nd, 3rd and 4th crop growth stage
      %ky ... mean crop sensitivity coefficient (linear case)
      %KY ... sensitivity coefficients per crop growth stage (non-linear case)
      %
      %Maize*/
        //stages=[25 40 40];
//		ky=1.25;
        //KY=[0.4 0.4 1.3 0.5];
//		KY=[0.0167 0.3990    1.2206    0.3917];
        /*	%KY=[0.0167 0.0167 0.0167 0.3990 0.3990 0.3990 0.3990   1.2206 1.2206 1.2206 1.2206   0.3917];
      %Cotton
      %stages=[30 50 60]
      %ky=0.85;
      %KY=[0.4 0.4 0.5 0.4];*/
//		stages=[0 cumsum(stages) length(PET)];
        double ky[] = {0.4, 0.4, 1.3, 0.5};
        double mky = 1.25;
        int stages[] = {0, 25, 65, 105, pet.length};


        if (!mean) {
            for (int i = 0; i < ky.length; i++) {
                double aeti = 0;
                double peti = 0;
                for (int j = stages[i]; j < stages[i + 1]; j++) {
                    aeti += aet[j];
                    peti += pet[j];

                }
                relY = (1 - ky[i] * (1 - aeti / peti)) * relY;
            }
        } else {
            double aeti = 0;
            double peti = 0;
            for (int j = 0; j < pet.length && j < this.m_NumberOfDays; j++) {
                aeti += aet[j];
                peti += pet[j];

            }
            relY = (1 - mky * (1 - aeti / peti));

        }
        return relY;
    }

    /**
     * This method provides the outer loop for the water balance model
     *
     * @param irr vector of irrigation volumes per day
     * @return yield for a given irrigation schedule
     */
    public double model(double[] irr) {


        double theta0 = 0.1;

        //Soil parameter
        double fc = 0.4;
        double pwp = 0.05;

        double[] aet = new double[m_NumberOfDays]; //actual evapotranspiration

        double[] p = new double[m_NumberOfDays]; //soil water depletion factor
        double[] asw = new double[m_NumberOfDays]; //available storage water
        double[] pk = new double[m_NumberOfDays];//percoltation
        double[] theta = new double[m_NumberOfDays];//relative soil moisture

        if (d == null || pet == null) {
            loadClimate();
        }


        for (int i = 0; i < pet.length && i < this.m_NumberOfDays; i++) {
            p[i] = dp_dpl_factor(pet[i]);
            asw[i] = d[i] * (fc - pwp);
        }


        for (int day = 0; day < m_NumberOfDays; day++) {
            if (day == 0) {
                pk[day] = dp_pk(theta0, irr[day], d[day], fc, pwp);
                aet[day] = dp_aet(theta0, pet[day], p[day], d[day], fc, pwp);
                if (aet[day] > theta0 * d[day] + irr[day] - pk[day]) {//limit aet to the available water
                    aet[day] = theta0 * d[day] + irr[day] - pk[day];
                }
                theta[day] = (theta0 * (d[day]) + irr[day] - aet[day] - pk[day]) / d[day];

            } else {
                pk[day] = dp_pk(theta[day - 1], irr[day], d[day], fc, pwp);

                double aet_old = 0.0;
                aet[day] = pet[day];
                while (Math.abs(aet[day] - aet_old) > 0.0001) {
                    theta[day] = Math.min(fc, Math.max(pwp, (theta[day - 1] * d[day - 1] + irr[day] - aet_old - pk[day] + theta0 * (d[day] - d[day - 1])) / d[day]));
                    aet_old = aet[day];
                    aet[day] = dp_aet(theta[day], pet[day], p[day], d[day], fc, pwp);
                    theta[day] = Math.min(fc, Math.max(pwp, (theta[day - 1] * d[day - 1] + irr[day] - aet[day] - pk[day] + theta0 * (d[day] - d[day - 1])) / d[day]));
                }
            }

        }


        double yield = dp_yield_production(pet, aet, true);
        return yield;
    }


    /** This method evaluates a given population and set the fitness values
     * accordingly
     * @param population    The population that is to be evaluated.
     */
    /*  public void evaluate(Population population) {
        AbstractEAIndividual    tmpIndy;
        //System.out.println("Population size: " + population.size());
        for (int i = 0; i < population.size(); i++) {
            tmpIndy = (AbstractEAIndividual) population.get(i);
            tmpIndy.resetConstraintViolation();
            this.evaluate(tmpIndy);
            population.incrFunctionCalls();
        }
    }*/


    /**
     * This method evaluate a single individual and sets the fitness values
     *
     * @param individual The individual that is to be evalutated
     */
    public void evaluate(AbstractEAIndividual individual) {
        double[] x;
        double[] fitness;

        x = new double[((InterfaceDataTypeDouble) individual).getDoubleData().length];
        System.arraycopy(((InterfaceDataTypeDouble) individual).getDoubleData(), 0, x, 0, x.length);

        if (!this.m_ismultiobjective) {
            if (doRandomZeros) {
                for (int i = 0; i < x.length; i++) {
                    if (x[i] < 3) {
                        if (RNG.randomDouble() < .2) x[i] = 0;
                    }
                }
            }

//			force maxwater constraint
            double sumwater = Mathematics.sum(x);
            Mathematics.svMult(m_MaxWater / sumwater, x, x);

            //TODO: try different strategies for constraint handling
            if (this.m_Lamarckian) {
                ((InterfaceDataTypeDouble) individual).SetDoubleGenotype(x);
            }
        }


        fitness = this.eval(x);
        individual.SetFitness(fitness);
//		for (int i = 0; i < fitness.length; i++) {
//			// add noise to the fitness
//			//	fitness = this.doEvaluation(x);
//			//     fitness[i] += RNG.gaussianDouble(this.m_Noise);
//
//			// set the fitness of the individual
//			individual.SetFitness(i, fitness[i]);
//		}


        //for later use to determine the best individual
        /*     if ((this.m_OverallBest == null) || (this.m_OverallBest.getFitness(0) > individual.getFitness(0))) {
            this.m_OverallBest = (AbstractEAIndividual)individual.clone();
        }*/
    }

    /**
     * This method does the actual fitness calculation based on a water balance model
     *
     * @param x The n-dimensional input vector with n as the number of days
     * @return The m-dimensional output vector. vector length is limited to 1 in the current version but may be extended to incorporate various other scheduling criteria
     */
    public double[] eval(double[] x) {
        double[] result;
        if (this.m_ismultiobjective) {
            result = new double[2];
        } else {
            result = new double[1];
        }
        double[] irr = new double[this.m_NumberOfDays];
        double sumwater = 0;
        for (int i = 0; i < x.length; i++) {
            sumwater += x[i];
            irr[i * this.m_IrrigationInterval] = x[i];
        }
        result[0] = 1 - model(irr);
        if (this.m_ismultiobjective) {
            result[1] = sumwater;
        }
        //System.out.println(sumwater+" ; "+result[0]);
        //if (result[0] < 0.05) {
        //	System.err.println("quak!");
        //}
        return result;
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += "User Problem:\n";
        result += "Parameters:\n";
        result += "days   : " + this.m_NumberOfDays + "\n";
        result += "Solution representation:\n";
        //result += this.m_Template.getSolutionRepresentationFor();
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
        return "Irrigation-Problem_" + getMaxWater();
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "This is a water distribution problem based on a simple water balance model for a whole growing season.";
    }

    /**
     * Length of the growing period in days that is to be optimized
     *
     * @param t number of days in the growing period
     */
    public void setNumberOfDays(int t) {
        this.m_NumberOfDays = t;
    }

    public int getNumberOfDays() {
        return this.m_NumberOfDays;
    }

    public String numberOfDaysTipText() {
        return "Length of the x vector at is to be optimized.";
    }

    /**
     * Interval between 2 irrigations
     *
     * @param t number of days between 2 irrigations
     */
    public void setIrrigationInterval(int t) {
        this.m_IrrigationInterval = t;
    }

    public int getIrrigationInterval() {
        return this.m_IrrigationInterval;
    }

    public String irrigationIntervalTipText() {
        return "Interval between 2 subsequent irrigations in days";
    }

    /**
     * maximum amount of water for the entire growing period
     *
     * @param t available water volume
     */
    public void setMaxWater(double t) {
        this.m_MaxWater = t;
    }

    public double getMaxWater() {
        return this.m_MaxWater;
    }

    public String maxWaterTipText() {
        return "Available Water Volume";
    }


    /**
     * sets the constraint handling method
     *
     * @param t true for Lamarckian Evolution
     */
    public void setLamarckian(boolean t) {
        this.m_Lamarckian = t;
    }

    public boolean getLamarckian() {
        return this.m_Lamarckian;
    }

    public String lamarckianTipText() {
        return "Lamarckism with local search to improve individuals during lifetime";
    }

    /**
     * sets the constraint handling method
     *
     * @param t true for water usage as 2nd objective
     */
    public void setIsMultiObjective(boolean t) {
        this.m_ismultiobjective = t;
    }

    public boolean getIsMultiObjective() {
        return this.m_ismultiobjective;
    }

    public String isMultiObjectiveTipText() {
        return "Treads water usage as a 2nd Onjective instead as a constraint";
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

    public boolean isDoRandomZeros() {
        return doRandomZeros;
    }

    public void setDoRandomZeros(boolean doRandomZeros) {
        this.doRandomZeros = doRandomZeros;
    }

}
