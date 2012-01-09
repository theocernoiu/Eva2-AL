package eva2.server.go.problems;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.problems.AbstractDynTransProblem;
import eva2.server.go.strategies.InterfaceOptimizer;


/**
 * A dynamically "rotating" problem. The severity gives the angle of the rotation
 * in problem space, occurring with the given frequency around the origin in
 * the first 2 dimensions.
 *
 * @author Geraldine Hopf
 * @date 25.06.2007
 */
public class DynRotationProblem extends AbstractDynTransProblem {

    private static final long serialVersionUID = -1111685518764324935L;
    protected double[] randomNumber;
    private double severity;
    private double noise;

    /* for the output file */
    private int changeCounter;
    private Writer fw = null;
    private String s = "";
    private double evaluations = 0.0;
    private double currentAngle = 0.0;

    public DynRotationProblem() {
        super();
        randomNumber = new double[getProblemDimension()];
        setNoise(0.0);
        initialize(0.0, 10.0, 0.01);
        changeCounter = 0;
    }

    public DynRotationProblem(DynRotationProblem other) {
        other.clone();
    }

    /**
     * not needed here
     */
    protected double getTranslation(int dim, double time) {
        return 0.0;
    }

    /**
     * rotation of individual in x,y-space around the origin
     */
    protected void transform(AbstractEAIndividual individual, double time) {
        double[] indyData = ((InterfaceDataTypeDouble) individual).getDoubleData();
        double[] indyXY = new double[2];
        indyXY[0] = indyData[0];
        indyXY[1] = indyData[1];
        double[] indyRP = transform2Polar(indyXY);
        indyRP[1] -= currentAngle;
        while (indyRP[1] <= -(Math.PI)) indyRP[1] += (2 * Math.PI);
        indyXY = transform2Cartesian(indyRP);
        indyData[0] = indyXY[0];
        indyData[1] = indyXY[1];
        ((InterfaceDataTypeDouble) individual).SetDoubleGenotype(indyData);
    }

    //	private double[] transform2Polar(double[] kartesian) {
//		double[] polar = new double[2];
//		polar[0] = Math.sqrt(Math.pow(kartesian[0],2) + Math.pow(kartesian[1],2));
//		polar[1] = Math.atan2(kartesian[1], kartesian[0]);
//		return polar;
//	}
//	
    private double[] transform2Polar(double[] kartesian) {
        double[] polar = new double[2];
        polar[0] = Math.sqrt(Math.pow(kartesian[0], 2) + Math.pow(kartesian[1], 2));
        if (kartesian[0] == 0 && kartesian[1] > 0)
            polar[1] = Math.PI / 2;
        if (kartesian[0] == 0 && kartesian[1] < 0)
            polar[1] = -Math.PI / 2;
        else
            polar[1] = Math.atan2(kartesian[1], kartesian[0]);
        return polar;
    }

    private double[] transform2Cartesian(double[] polar) {
        double[] kartesian = new double[2];
        kartesian[0] = polar[0] * Math.cos(polar[1]);
        kartesian[1] = polar[0] * Math.sin(polar[1]);
        return kartesian;
    }

    protected void changeProblemAt(double problemTime) {
        super.changeProblemAt(problemTime);
        double om = rand.nextDouble() * 2.0 - 1.0;
        currentAngle += severity * (1.0 + om * getNoise() / 100.0);
//		System.out.println("time = " + problemTime + 
//						   "\n angle = " + currentAngle +
//						   "\n rand = " + om);
        /* proving results */
        if (TRACE) writeFile();
        ++changeCounter;
    }

    protected void countEvaluation() {
        super.countEvaluation();
        evaluations += 1.;
    }

    public void initProblem() {
        super.initProblem();
        evalsSinceChange = 0.0;
        evaluations = 0.0;
        changeCounter = 0;
        currentAngle = 0;
        changeProblemAt(0);
    }

    public AbstractEAIndividual getCurrentOptimum() {
        return null;
    }

    public Object clone() {
        return new DynRotationProblem(this);
    }

    /**
     * ***********************************************************************
     * These are for the GUI
     */

    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        return "DynRotaionProblem";
    }

    public String getName() {
        return "DynRotationProblem";
    }

    public String globalInfo() {
        return "A real valued problem rotation around the origin";
    }

    public double getNoise() {
        return noise;
    }

    public void setNoise(double noise) {
        this.noise = noise;
    }

    public String noiseTipText() {
        return "noise of the problem, maximum deviation of rotation angle (severity) in procent";
    }

    public double getSeverity() {
        return (360.0 / (2 * Math.PI) * severity);
    }

    public void setSeverity(double severity) {
        this.severity = 2 * Math.PI * severity / 360.0;
    }

    public String severityTipText() {
        return "roation angle, in degree not rad!";
    }

    /**
     * ***********************************************************************
     * These are for debugging and determing the output file
     */

    public void myPrint(double[][] toPrint) {
        for (int i = 0; i < toPrint.length; i++) {
            for (int j = 0; j < toPrint[i].length; ++j) {
                System.out.print(toPrint[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public void myPrint(double[] toPrint) {
        for (int i = 0; i < toPrint.length; ++i) {
            System.out.print(toPrint[i] + " ");
        }
        System.out.println("");
    }

    public void writeFile() {
        if (fw == null) {
            try {
                fw = new FileWriter("DynRotationProblem.txt");
            } catch (IOException e) {
                System.err.println("Konnte Datei nicht erstellen");
            }
        } else {
            try {
                fw.write("Problem wurde " + changeCounter + " mal geaendert!\n");
                fw.write(evaluations + " Evaluierungen wurden gemacht\n");
                fw.write(Double.toString(currentAngle));

            } catch (IOException e) {
            } finally {
                if (fw != null)
                    try {
                        fw.flush();
                    } catch (IOException e) {
                    }
            }
        }
    }

    public String myPrints(double[][] toPrint) {
        for (int i = 0; i < toPrint.length; i++) {
            for (int j = 0; j < toPrint[i].length; ++j) {
                if (j != getProblemDimension())
                    s += toPrint[i][j] + "\t";
            }
            s += "\n";
        }
        s += "\n";
        return s;
    }

    public String myPrints(double[] toPrint) {
        for (int i = 0; i < toPrint.length; i++) {
            s += toPrint[i] + "\t";
            s += "\n";
        }
        s += "\n";
        return s;
    }
}