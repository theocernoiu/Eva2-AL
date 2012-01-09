package eva2.server.go.problems;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.util.ArrayList;

import eva2.gui.Plot;
import eva2.gui.PropertyFilePath;
import eva2.tools.math.RNG;
import eva2.tools.matlab.JMatLink;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 17.02.2005
 * Time: 12:59:06
 * To change this template use File | Settings | File Templates.
 */
public class TFPortfolioSelectionTestingMATLABStuff {

    transient protected JMatLink m_Engine;
    transient protected int m_PID;

    transient private BufferedWriter m_ReturnFile, m_RiskFile;

    transient private Plot m_Plot;

    private String base = System.getProperty("user.dir");
    private String FS = System.getProperty("file.separator");
    private PropertyFilePath m_InputFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/port3.txt");
    private PropertyFilePath m_SolutionFilePath = PropertyFilePath.getFilePathFromResource("resources/PortfolioSelection/portef3.txt");
    private double[][] m_ReferenceSolution;
    private int m_HighestReturnAsset;
    private double m_LowestEffReturn = 0;
    public String[] m_AssetName;
    public double[] m_AssetReturn;
    public double[] m_AssetRisk;
    public double[][] m_AssetCorrelation;
    private int m_ProblemDimension;

    public TFPortfolioSelectionTestingMATLABStuff() {
        this.initMatlab();
        this.loadProblemData();
        this.drawProblem();
        this.useMatlab();
    }

//    private void exportToFiles() {
//        try {
//            this.m_ReturnFile = new BufferedWriter(new OutputStreamWriter (new FileOutputStream ("Return.dat")));
//            this.m_RiskFile = new BufferedWriter(new OutputStreamWriter (new FileOutputStream ("Risk.dat")));
//        } catch (FileNotFoundException e) {
//            System.out.println("Could not open output file! Filename: ... grml something");
//        }
//        String ret, ris;
//        for (int i = 0; i < this.m_AssetReturn.length; i++)  {
//            ret = ""+this.m_AssetReturn;
//            for (int j = 0; j < this.m_AssetCorrelation[i].)
//
//        }
//
//    }

//    /** This method writes Data to file.
//     * @param line      The line that is to be added to the file
//     */
//    private void writeToFile(String line) {
//        String write = line + "\n";
//        if (this.m_OutputFile == null) return;
//        try {
//            this.m_OutputFile.write(write, 0, write.length());
//            this.m_OutputFile.flush();
//        } catch (IOException e) {
//            System.out.println("Problems writing to output file!");
//        }
//    }

    private void useMatlab() {
        boolean[] b = new boolean[this.m_AssetReturn.length];
        int dim = this.m_AssetReturn.length;
        double[][] Risk = new double[dim][dim];
        double[] Return = new double[dim];
        for (int i = 0; i < Return.length; i++) {
            Return[i] = this.m_AssetReturn[i];
            for (int j = 0; j < Risk[i].length; j++) {
                Risk[i][j] = this.m_AssetRisk[i] * this.m_AssetRisk[j] * this.m_AssetCorrelation[i][j];
            }
        }
        // fist send the data
        this.m_Engine.engPutArray(this.m_PID, "Risk", Risk);
        this.m_Engine.engPutArray(this.m_PID, "Return", Return);
        this.m_Engine.engEvalString(this.m_PID, "Return = Return'");
        int iter = 0, runs = 0;
        double step = 0.01;
        double lambda = 0;
        double sum = 0;
        double[] fit, last = null;
        double[][] result, tmpExit;
        double[] x, lastX = null;
        x = new double[dim];
        for (int i = 0; i < x.length; i++) {
            x[i] = RNG.randomDouble(0, 1);
            sum += x[i];
        }
        for (int i = 0; i < x.length; i++) {
            x[i] = x[i] / sum;
        }
        double dist = 0;
        while (lambda <= 1) {
            runs++;
            this.m_Engine.engPutArray(this.m_PID, "lambda", lambda);
            this.m_Engine.engPutArray(this.m_PID, "x0", x);
            this.m_Engine.engEvalString(this.m_PID, "H      = 2*(1-lambda)*Risk;");
            this.m_Engine.engEvalString(this.m_PID, "f      = -lambda*Return;");
            this.m_Engine.engEvalString(this.m_PID, "A      = eye(length(Return));");
            this.m_Engine.engEvalString(this.m_PID, "b      = ones(1,length(Return));");
            this.m_Engine.engEvalString(this.m_PID, "Aeq    = ones(1,length(Return));");
            this.m_Engine.engEvalString(this.m_PID, "beq    = 1");
            this.m_Engine.engEvalString(this.m_PID, "lb     = zeros(length(Return), 1);");
            this.m_Engine.engEvalString(this.m_PID, "ub     = ones(length(Return), 1);");
            //this.m_Engine.engEvalString(this.m_PID, "[x, fval, exitfalg, output, lambdaT] = quadprog(H,f,A,b,Aeq,beq,lb,ub,x0);");
            this.m_Engine.engEvalString(this.m_PID, "[x, fval, exitfalg, output, lambdaT] = quadprog(H,f,A,b,Aeq,beq,lb,ub);");
            this.m_Engine.engEvalString(this.m_PID, "iter   = output.iterations;");
            result = this.m_Engine.engGetArray("x");
            tmpExit = this.m_Engine.engGetArray("exitfalg");
            if (tmpExit[0][0] > 0) {
                for (int i = 0; i < x.length; i++) {
                    x[i] = result[i][0];
                }
                iter += (int) this.m_Engine.engGetArray("iter")[0][0];
                fit = this.calcuateRiskReturn(x);
                this.m_Plot.setUnconnectedPoint(fit[0], fit[1], 10);
                if (lambda == 1) break;
                if (last != null) {
                    if (Math.sqrt(Math.pow((fit[0] - last[0]), 2) + Math.pow((fit[1] - last[1]), 2)) > 0.1) {
                        step = step / 2.0;
                    }
                    if (Math.sqrt(Math.pow((fit[0] - last[0]), 2) + Math.pow((fit[1] - last[1]), 2)) < 0.001) {
                        step = step * 2.0;
                    }
                    if (step < 0.0000000000001) step = 0.01;
                    // interpolate
                    double[] tmpX = new double[x.length];
                    double[] tmpFit;
                    int interSteps = 5;
                    for (int k = 1; k < interSteps; k++) {
                        sum = 0;
                        for (int i = 0; i < x.length; i++) {
                            tmpX[i] = ((k / ((double) interSteps)) * x[i] + (1 - (k / ((double) interSteps))) * lastX[i]) / 2.0;
                            sum += tmpX[i];
                        }
                        for (int i = 0; i < tmpX.length; i++) tmpX[i] = tmpX[i] / sum;
                        tmpFit = this.calcuateRiskReturn(tmpX);
                        this.m_Plot.setUnconnectedPoint(tmpFit[0], tmpFit[1], 10);

                    }
                    iter += interSteps - 1;
                    lambda += step;
                } else {
                    lambda += step;
                }
                last = new double[2];
                last[0] = fit[0];
                last[1] = fit[1];
                lastX = new double[x.length];
                for (int i = 0; i < x.length; i++) lastX[i] = x[i];
            } else {
                x = new double[dim];
                for (int i = 0; i < x.length; i++) {
                    x[i] = RNG.randomDouble(0, 1);
                    sum += x[i];
                }
                for (int i = 0; i < x.length; i++) {
                    x[i] = x[i] / sum;
                }
            }
            if (lambda > 1) lambda = 1;
        }
        System.out.println("Iterations  : " + iter);
        System.out.println("Runs        : " + runs);
        System.out.println("Ratio       : " + (iter / runs));
    }

    /**
     * This method calculates the risk return for a given portfolio
     *
     * @param x The portfolio which must be normatted
     * @return double[] risk/return
     */
    private double[] calcuateRiskReturn(double[] x) {
        double Return = 0, Risk = 0;
        double[] result = new double[2];
//*******************************************************************************************************************
        // calculate the return on investment
        for (int i = 0; i < x.length; i++) {
            // at the very end of the sourceMatrix is the return of an asset
            Return += x[i] * this.m_AssetReturn[i];
        }

        // calculate the risk of the allocation
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {
                Risk += x[i] * x[j] * this.m_AssetRisk[i] * this.m_AssetRisk[j] * this.m_AssetCorrelation[i][j];
            }
        }
        Risk = Math.sqrt(Risk);
//*******************************************************************************************************************
        result[0] = Risk;
        result[1] = Return;
        return result;
    }

    private void drawProblem() {
        this.m_Plot = new Plot("Portfolio Selection Problem MATLAB", "Risk", "Return");

        // The assets
        this.m_Plot.setUnconnectedPoint(0, 0, 0);
        for (int i = 0; i < this.m_AssetReturn.length; i++) {
            if (this.m_AssetReturn[i] > this.m_AssetReturn[this.m_HighestReturnAsset]) this.m_HighestReturnAsset = i;
            this.m_Plot.setUnconnectedPoint(this.m_AssetRisk[i], this.m_AssetReturn[i], 0);
        }
        this.m_Plot.setUnconnectedPoint(this.m_AssetRisk[this.m_HighestReturnAsset] + 0.01, this.m_AssetReturn[this.m_HighestReturnAsset] + 0.001, 0);
        this.m_Plot.setUnconnectedPoint(0, 0, 0);
        System.out.println("Highest Return Asset: " + this.m_HighestReturnAsset + " yielding (" + this.m_AssetRisk[this.m_HighestReturnAsset] + ", " + this.m_AssetReturn[this.m_HighestReturnAsset] + ")");
        System.out.println("Number of Assets:     " + this.m_AssetReturn.length);
        // the reference solution
        for (int i = 0; i < this.m_ReferenceSolution.length; i++) {
            this.m_Plot.setUnconnectedPoint(this.m_ReferenceSolution[i][0], this.m_ReferenceSolution[i][1], 1);
        }

    }

    /**
     * If necessary the matlab engine can be initialized
     */
    private void initMatlab() {
        this.m_Engine = JMatLink.getInstance();
        this.m_PID = this.m_Engine.engOpen();
        //this.m_PID = m_engine.engOpenSingleUse();
        String ClassPath = System.getProperty("java.class.path");
        String ToolBoxPath = ClassPath + "/matlab/";
        this.m_Engine.engEvalString(this.m_PID, "addpath " + ToolBoxPath);
        System.out.println("-->" + this.m_Engine.engOutputBuffer(this.m_PID));
        this.m_Engine.engEvalString(this.m_PID, "path ");
        System.out.println("-->" + this.m_Engine.engOutputBuffer(this.m_PID));
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

    private void closeMatlab() {
        this.m_Engine.engClose();
    }

    public static void main(String[] args) {
        TFPortfolioSelectionTestingMATLABStuff tpm = new TFPortfolioSelectionTestingMATLABStuff();
    }
}
