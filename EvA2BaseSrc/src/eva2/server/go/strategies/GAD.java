package eva2.server.go.strategies;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.InterfaceESIndividual;
import eva2.server.go.individuals.InterfaceGAIndividual;
import eva2.server.go.operators.mutation.InterfaceMutationGenerational;
import eva2.server.go.populations.Population;
import eva2.tools.SelectedTag;
import eva2.tools.math.RNG;

import java.util.BitSet;

/**
 * Created by IntelliJ IDEA.
 * User: andrei.lihu
 * Date: Mar 26, 2011
 * Time: 9:55:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class GAD extends GeneticAlgorithm {
    private static final long serialVersionUID = -149996122795669595L;
    private SelectedTag algoType;

    protected double lowLimit = 1;
    protected double upLimit = 2;

    public int extreme_disagreements = 0;
    public int partial_disagreements = 0;

    public GAD() {
        super();
        lowLimit = 0.25;
        upLimit = 0.5;
    }

    public GAD(GAD a) {
        super(a);
        lowLimit = a.lowLimit;
        upLimit = a.upLimit;
    }

    public Object clone() {
        return (Object) new GAD(this);
    }

    public String globalInfo() {
        return "Genetic Algorithm with Disagreements.";
    }

    public String getName() {
        return "GAD";
    }

    public String getStringRepresentation() {
        String result = "";
        result += "Genetic Algorithm with Disagreements:\n";
        result += "Using:\n";
        result += " Population Size    = " + this.m_Population.getTargetSize() + "/" + this.m_Population.size() + "\n";
        result += " Parent Selection   = " + this.m_ParentSelection.getClass().toString() + "\n";
        result += " Partner Selection  = " + this.m_PartnerSelection.getClass().toString() + "\n";
        result += " Number of Partners = " + this.m_NumberOfPartners + "\n";
        result += " Elitism            = " + this.m_UseElitism + "\n";
        result += "=> The Optimization Problem: ";
        result += this.m_Problem.getStringRepresentationForProblem(this) + "\n";
        //result += this.m_Population.getStringRepresentation();
        return result;
    }

public double getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(double lowLimit) {
        this.lowLimit = lowLimit;
    }

    public double getUpLimit() {
        return upLimit;
    }

    public void setUpLimit(double upLimit) {
        this.upLimit = upLimit;
    }

     public String upLimitTipText()
    {
        return "Upper limit for disagreements.";
    }

    public String lowLimitTipText()
    {
        return "Low limit for disagreements.";
    }

    public void optimize() {
        optimizeImpl();

        for (int i = 0; i < m_Population.size(); i++) {
            double gen = RNG.gaussianDouble(1);

            AbstractEAIndividual individual = m_Population.getEAIndividual(i);
            if (individual instanceof InterfaceESIndividual) {
                double[] x = ((InterfaceESIndividual) individual).getDGenotype();
                double[][] range = ((InterfaceESIndividual) individual).getDoubleRange();

                for (int j = 0; j < x.length; j++) {
                    if ((-1 >= gen && gen > -2) || (1 <= gen && gen < 2)) // +- 2 sigma
                    {
                        double dist = (range[j][1] - range[j][0]) / 2;
                        x[j] += dist * RNG.randomDouble(-1.0 * lowLimit, lowLimit);
                        if(j==0)
                            partial_disagreements++;
                        if (range[j][0] > x[j]) x[j] = range[j][0];
                        if (range[j][1] < x[j]) x[j] = range[j][1];
                    } else {
                        if (gen <= -2 || gen >= 2) {
                            double disagreement = 0;
                            double extremeDisagreement = RNG.randomDouble(-1.0 * lowLimit, lowLimit);
                            if(j==0)
                                extreme_disagreements++;
                            if (extremeDisagreement < 0) {
                                disagreement = extremeDisagreement - (upLimit - lowLimit); // -2 to -1
                            } else {
                                disagreement = extremeDisagreement + (upLimit - lowLimit);
                            } // 1 to 2
                            double dist = (range[j][1] - range[j][0]) / 2;
                            x[j] += dist * disagreement;
                            if (range[j][0] > x[j]) x[j] = range[j][0];
                            if (range[j][1] < x[j]) x[j] = range[j][1];
                        }
                    }

                }

                ((InterfaceESIndividual) individual).SetDGenotype(x);
            } else {
                if (individual instanceof InterfaceGAIndividual) {
                    BinaryDisagree2((InterfaceGAIndividual) individual, gen);
                }
            }
        }

        this.firePropertyChangedEvent(Population.nextGenerationPerformed);
    }

    private void BinaryDisagree2(InterfaceGAIndividual individual, double gen) {
        BitSet tmpBitSet = individual.getBGenotype();
        int len = individual.getGenotypeLength();

        if (((0.5 - 0.341 - 0.136) < gen && gen < (0.5 - 0.341)) || ((0.5 + 0.341) < gen && gen < (0.5 + 0.341 + 0.136))) // +- 2 sigma
        {
            for (int i = 0; i < len / 4; i++) {
                int locus = RNG.randomInt(0, len - 1);
                tmpBitSet.flip(locus);
            }

        } else {
            if ((0 <= gen && gen < 0.5 - 0.341 - 0.136) || (0.5 + 0.341 + 0.136 < gen && gen <= 1)) {
                for (int i = 0; i < len / 2; i++) {
                    int locus = RNG.randomInt(0, len - 1);
                    tmpBitSet.flip(locus);
                }
            }
        }
        individual.SetBGenotype(tmpBitSet);
    }

    private void BinaryDisagree(InterfaceGAIndividual individual) {
        BitSet tmpBitSet = individual.getBGenotype();
        boolean enforceNext = false;
        boolean enforcedValue = false;
        for (int j = 0; j < individual.getGenotypeLength(); j++) {
            double gen2 = 0.5 + RNG.gaussianDouble(0.12);
            if (enforceNext) {
                enforceNext = false;

                if (RNG.randomBoolean() == true) {
                    tmpBitSet.set(j, enforcedValue);
                    continue;
                }
            }

            if (((0.5 - 0.341 - 0.136) < gen2 && gen2 < (0.5 - 0.341)) || ((0.5 + 0.341) < gen2 && gen2 < (0.5 + 0.341 + 0.136))) // +- 2 sigma
            {
                tmpBitSet.flip(j);
            } else {
                if ((0 <= gen2 && gen2 < 0.5 - 0.341 - 0.136) || (0.5 + 0.341 + 0.136 < gen2 && gen2 <= 1)) {
                    tmpBitSet.flip(j);
                    enforcedValue = tmpBitSet.get(j);
                    enforceNext = true;
                }
            }
        }
        individual.SetBGenotype(tmpBitSet);
    }
}
