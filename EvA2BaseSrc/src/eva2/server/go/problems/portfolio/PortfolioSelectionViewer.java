package eva2.server.go.problems.portfolio;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eva2.gui.FunctionArea;
import eva2.gui.GraphPointSet;
import eva2.server.go.individuals.InterfaceDataTypeDouble;
import eva2.server.go.operators.archiving.ArchivingAllDominating;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.InterfaceOptimizationObjective;
import eva2.server.go.problems.TFPortfolioSelectionProblem;
import eva2.server.go.problems.TFPortfolioSelectionProblemInterface;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioReturn;
import eva2.server.go.problems.portfolio.objective.OptTargetPortfolioRisk;
import eva2.tools.chart2d.Chart2DDPointIconCircle;
import eva2.tools.chart2d.Chart2DDPointIconCross;
import eva2.tools.chart2d.DPoint;
import eva2.tools.chart2d.DPointIcon;
import eva2.tools.chart2d.ScaledBorder;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 21.01.2005
 * Time: 14:42:39
 * To change this template use File | Settings | File Templates.
 */
public class PortfolioSelectionViewer {

    private TFPortfolioSelectionProblemInterface m_Problem;
    private JFrame m_Frame;
    private JPanel m_Main, m_Buttons, m_Axis;
    private FunctionArea m_Area;
    private InterfacePortfolioSelectionTarget[] m_PossibleObjectives;
    private JComboBox[] m_Axisis;
    private int[] m_SelectedObjectives;
    private boolean m_CloseAll = true;
    private Population m_Population;
    private Population m_ParetoFront;
    private double[][] m_ReferenceBeasley;
    private BufferedWriter m_OutputFile;

    public PortfolioSelectionViewer(TFPortfolioSelectionProblemInterface b) {
        this.m_Problem = b;
        this.initView();
    }

    public void dispose() {
        if (this.m_Frame != null) {
            this.m_Frame.dispose();
            this.m_Frame = null;
        }
    }

    private void initView() {
        Dimension d = new Dimension(350, 250);
        this.m_Main = new JPanel();
        this.m_Main.setPreferredSize(d);
        this.m_Main.setMinimumSize(d);
        this.m_Main.setLayout(new BorderLayout());

        // the center area
        this.m_Area = new FunctionArea("?", "?");
        ScaledBorder myBorder = new ScaledBorder();
        myBorder.x_label = "?";
        myBorder.y_label = "?";
        this.m_Area.setBorder(myBorder);
        this.m_Main.add(this.m_Area, BorderLayout.CENTER);

        // the axis panel
        this.m_Axis = new JPanel();
        this.m_Main.add(this.m_Axis, BorderLayout.EAST);

        // the button panel
        this.m_Buttons = new JPanel();
        this.m_Buttons.setLayout(new GridLayout(2, 1));
        JButton saveButton = new JButton("Save Solution");
        saveButton.addActionListener(saveSolutionStreichert);

        this.m_Buttons.add(saveButton);
        JButton loadReference = new JButton("Load Reference");
        loadReference.addActionListener(loadReferenceBeasley);
        this.m_Buttons.add(loadReference);
        this.m_Main.add(this.m_Buttons, BorderLayout.SOUTH);

        // The main frame
        this.m_Frame = new JFrame();
        this.m_Frame.setTitle("Portfolio Selection Problem");
        this.m_Frame.setSize(500, 500);
        this.m_Frame.setLocation(530, 50);
        this.m_Frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                if (m_CloseAll) System.exit(0);
                else dispose();
            }
        });
        this.m_Frame.getContentPane().add(this.m_Main);
        this.m_Frame.setVisible(true);
        this.m_Frame.show();
        this.updateView(null);
    }

    /**
     * This method will update the view on the problem
     * I guess everything is redrawn ranging from the actual
     * data to the potential objective options
     *
     * @param pop The population to draw
     */
    public void updateView(Population pop) {
        // First lets care for the area

        // Secondly, update the axis elements
        // i will only perform a update in case there is no pop
        if (pop == null) {
            this.m_Axis.removeAll();
            InterfaceOptimizationObjective[] list = this.m_Problem.getOptimizationTargets().getSelectedTargets();
            this.m_PossibleObjectives = new InterfacePortfolioSelectionTarget[list.length];
            for (int i = 0; i < this.m_PossibleObjectives.length; i++)
                this.m_PossibleObjectives[i] = (InterfacePortfolioSelectionTarget) list[i];
            int x = Math.min(4, this.m_PossibleObjectives.length);
            this.m_Axis.setLayout(new GridLayout(2 * x, 1));
            JLabel tmpLabel;
            String[] tmpList = new String[x];
            for (int i = 0; i < this.m_PossibleObjectives.length; i++)
                tmpList[i] = this.m_PossibleObjectives[i].getName();
            this.m_Axisis = new JComboBox[x];
            if (x > 0) {
                // the x-Axis
                tmpLabel = new JLabel("X-Axis");
                this.m_Axis.add(tmpLabel);
                this.m_Axisis[0] = new JComboBox(tmpList);
                this.m_Axisis[0].setSelectedIndex(0);
                this.m_Axisis[0].addActionListener(updateAxis);
                this.m_Axis.add(this.m_Axisis[0]);
            }
            if (x > 1) {
                // the y-Axis
                tmpLabel = new JLabel("Y-Axis");
                this.m_Axis.add(tmpLabel);
                this.m_Axisis[1] = new JComboBox(tmpList);
                this.m_Axisis[1].setSelectedIndex(1);
                this.m_Axisis[1].addActionListener(updateAxis);
                this.m_Axis.add(this.m_Axisis[1]);
            }
            if (x > 2) {
                // the o-Axis
                tmpLabel = new JLabel("O-Axis");
                this.m_Axis.add(tmpLabel);
                this.m_Axisis[2] = new JComboBox(tmpList);
                this.m_Axisis[2].setSelectedIndex(2);
                this.m_Axisis[2].addActionListener(updateAxis);
                this.m_Axis.add(this.m_Axisis[2]);
            }
            if (x > 3) {
                // the color-Axis
                tmpLabel = new JLabel("C-Axis");
                this.m_Axis.add(tmpLabel);
                this.m_Axisis[3] = new JComboBox(tmpList);
                this.m_Axisis[3].setSelectedIndex(3);
                this.m_Axisis[3].addActionListener(updateAxis);
                this.m_Axis.add(this.m_Axisis[3]);
            }
            this.updateSelectedObjectives();
        }

        // Finally, draw the actual pop data
        this.updateArea(pop);

        this.m_Frame.validate();
        this.m_Frame.repaint();
    }

    private void updateSelectedObjectives() {
        this.m_SelectedObjectives = new int[this.m_Axisis.length];
        for (int i = 0; i < this.m_Axisis.length; i++) {
            this.m_SelectedObjectives[i] = this.m_Axisis[i].getSelectedIndex();
        }
    }

    /**
     */
    ActionListener updateAxis = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateSelectedObjectives();
            updateArea(m_Population);
        }
    };

    ActionListener loadReferenceBeasley = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File("Z:/Augustus/JOpt/resources/PortfolioSelection"));
            fc.setMultiSelectionEnabled(false);
            int returnVal = fc.showOpenDialog(m_Frame);
            if (returnVal == JFileChooser.APPROVE_OPTION)
                loadInputSolutionFile(fc.getSelectedFile().getAbsolutePath());
            updateArea(m_Population);
        }
    };

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
        this.m_ReferenceBeasley = new double[tmpInput.size()][2];
        for (int i = 0; i < tmpInput.size(); i++) {
            this.m_ReferenceBeasley[i] = (double[]) tmpInput.get(i);
        }
    }

    ActionListener loadReferenceStreichert = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateSelectedObjectives();
            updateArea(m_Population);
        }
    };
    ActionListener saveSolutionStreichert = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File("C:/Dokumente und Einstellungen/streiche/Eigene Dateien"));
            fc.setMultiSelectionEnabled(false);
            int returnVal = fc.showSaveDialog(m_Frame);
            if (returnVal == JFileChooser.APPROVE_OPTION)
                saveSolutionFile(fc.getSelectedFile().getAbsolutePath());
            updateArea(m_Population);
        }
    };

    /**
     * This method will save the current solution file
     * fitness values first into the outputfile
     *
     * @param outputfile The target file.
     */
    private void saveSolutionFile(String outputfile) {
        this.m_OutputFile = null;
        try {
            this.m_OutputFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile)));
        } catch (FileNotFoundException e) {
            System.out.println("Could not open output file! Filename: " + outputfile);
        }
        if (this.m_OutputFile != null) {
            String line;
            // the header
            line = "";
            InterfaceOptimizationObjective[] targets = this.m_Problem.getOptimizationTargets().getSelectedTargets();
            for (int i = 0; i < targets.length; i++) {
                line += targets[i].getName() + "\t";
            }
            for (int i = 0; i < this.m_Problem.getAssetReturn().length; i++) {
                line += "AW_" + (i + 1) + "\t";
            }
            this.writeToFile(line);
            for (int i = 0; i < this.m_ParetoFront.size(); i++) {
                line = "";
                // first the fitness
                double[] fit, w;
                fit = new double[targets.length];
                for (int j = 0; j < targets.length; j++)
                    fit[j] = ((InterfacePortfolioSelectionTarget) targets[j]).getPlotValue(((InterfaceDataTypeDouble) this.m_ParetoFront.get(i)), this.m_Problem);
                for (int j = 0; j < fit.length; j++) line += fit[j] + "\t";
                w = ((InterfaceDataTypeDouble) this.m_ParetoFront.get(i)).getDoubleDataWithoutUpdate();
                for (int j = 0; j < w.length; j++) line += w[j] + "\t";
                this.writeToFile(line);
            }
        }
        try {
            this.m_OutputFile.close();
        } catch (java.io.IOException e) {
            System.out.println("Could not close the file!");
        }
    }

    /**
     * This method writes Data to file.
     *
     * @param line The line that is to be added to the file
     */
    private void writeToFile(String line) {
        String write = line + "\n";
        if (this.m_OutputFile == null) return;
        try {
            this.m_OutputFile.write(write, 0, write.length());
            this.m_OutputFile.flush();
        } catch (IOException e) {
            System.out.println("Problems writing to output file!");
        }
    }

    /**
     * This method will solely update the area and nothing else
     */
    private void updateArea(Population pop) {
        if (pop != null) {
            this.m_Population = pop;
            Population archive, tmpPop;
            tmpPop = new Population();
            tmpPop.addPopulation((Population) pop.clone());
            if (pop.getArchive() != null) tmpPop.addPopulation((Population) pop.getArchive().clone());
            ArchivingAllDominating tmpArch = new ArchivingAllDominating();
            tmpArch.addElementsToArchive(tmpPop);
            archive = tmpPop.getArchive();
            this.m_ParetoFront = (Population) archive.clone();

            // parameterize the area
            this.m_Area.removeAllDElements();
            double x, y, xw, yw;
            x = this.m_PossibleObjectives[this.m_SelectedObjectives[0]].getObjectiveBoundaries(this.m_Problem)[0];
            xw = this.m_PossibleObjectives[this.m_SelectedObjectives[0]].getObjectiveBoundaries(this.m_Problem)[1] - this.m_PossibleObjectives[this.m_SelectedObjectives[0]].getObjectiveBoundaries(this.m_Problem)[0];
            y = this.m_PossibleObjectives[this.m_SelectedObjectives[1]].getObjectiveBoundaries(this.m_Problem)[0];
            yw = this.m_PossibleObjectives[this.m_SelectedObjectives[1]].getObjectiveBoundaries(this.m_Problem)[1] - this.m_PossibleObjectives[this.m_SelectedObjectives[1]].getObjectiveBoundaries(this.m_Problem)[0];
            // So die idee w�re jetzt ja einfach 10% links und rechts drauf zu tun
            double x10, y10;
            x10 = xw * 0.1;
            y10 = yw * 0.1;
            this.m_Area.setVisibleRectangle(x - x10, y - y10, xw + (2 * x10), yw + (2 * y10));

            // Draw some references *****************************************************************
            if (this.m_ReferenceBeasley != null) {
                if (this.m_SelectedObjectives.length == 2) {
                    if (((this.m_PossibleObjectives[this.m_SelectedObjectives[0]] instanceof OptTargetPortfolioReturn) ||
                            (this.m_PossibleObjectives[this.m_SelectedObjectives[0]] instanceof OptTargetPortfolioRisk)) &&
                            ((this.m_PossibleObjectives[this.m_SelectedObjectives[1]] instanceof OptTargetPortfolioReturn) ||
                                    (this.m_PossibleObjectives[this.m_SelectedObjectives[1]] instanceof OptTargetPortfolioRisk))) {
                        GraphPointSet mySetRef = new GraphPointSet(1, this.m_Area);
                        mySetRef.setConnectedMode(true);
                        mySetRef.setColor(Color.GREEN);
                        double[] tmpVal = new double[2];
                        for (int i = 0; i < this.m_ReferenceBeasley.length; i++) {
                            tmpVal = new double[2];
                            if (this.m_PossibleObjectives[this.m_SelectedObjectives[0]] instanceof OptTargetPortfolioReturn) {
                                tmpVal[0] = this.m_ReferenceBeasley[i][1];
                            } else {
                                tmpVal[0] = this.m_ReferenceBeasley[i][0];
                            }
                            if (this.m_PossibleObjectives[this.m_SelectedObjectives[1]] instanceof OptTargetPortfolioReturn) {
                                tmpVal[1] = this.m_ReferenceBeasley[i][1];
                            } else {
                                tmpVal[1] = this.m_ReferenceBeasley[i][0];
                            }
                            mySetRef.addDPoint(tmpVal[0], tmpVal[1]);
                        }
                    }
                }
            }


            //***************************************************************************************

            // first draw all the individuals
            double[][] ranges;
            double[][] valuesPop, valuesArch;
            ranges = new double[this.m_PossibleObjectives.length][2];
            for (int i = 0; i < ranges.length; i++) {
                ranges[i] = this.m_PossibleObjectives[this.m_SelectedObjectives[i]].getObjectiveBoundaries(this.m_Problem);
            }
            valuesPop = new double[tmpPop.size()][this.m_PossibleObjectives.length];
            valuesArch = new double[archive.size()][this.m_PossibleObjectives.length];

            for (int i = 0; i < tmpPop.size(); i++) {
                for (int j = 0; j < this.m_PossibleObjectives.length; j++) {
                    valuesPop[i][j] = this.m_PossibleObjectives[this.m_SelectedObjectives[j]].getPlotValue((InterfaceDataTypeDouble) tmpPop.get(i), this.m_Problem);
                }
            }
            for (int i = 0; i < archive.size(); i++) {
                for (int j = 0; j < this.m_PossibleObjectives.length; j++) {
                    valuesArch[i][j] = this.m_PossibleObjectives[this.m_SelectedObjectives[j]].getPlotValue((InterfaceDataTypeDouble) archive.get(i), this.m_Problem);
                }
            }

            GraphPointSet mySet1, mySet2, mySet3;
            DPoint point;
            DPointIcon icon;
            mySet1 = new GraphPointSet(10, this.m_Area);
            mySet1.setConnectedMode(false);
            for (int i = 0; i < valuesPop.length; i++) {
                mySet1.addDPoint(valuesPop[i][0], valuesPop[i][1]);
            }
            mySet1.setIcon(new Chart2DDPointIconCross());
            mySet1.setColor(Color.BLUE);

            // Then draw the pareto-front
            int minSize = 2, maxSize = 10;
            int minCol = 50, maxCol = 200;
            mySet3 = new GraphPointSet(13, this.m_Area);
            mySet3.setConnectedMode(false);
            for (int i = 0; i < valuesArch.length; i++) {
                point = new DPoint(valuesArch[i][0], valuesArch[i][1]);
                if (valuesArch[i].length > 2) {
                    icon = new Chart2DDPointIconCircle();
                    ((Chart2DDPointIconCircle) icon).setBorderColor(Color.BLACK);
                    ((Chart2DDPointIconCircle) icon).setFillColor(Color.WHITE);
                    ((Chart2DDPointIconCircle) icon).setSize(minSize);

                    double min2 = this.m_PossibleObjectives[this.m_SelectedObjectives[2]].getObjectiveBoundaries(this.m_Problem)[0];
                    double max2 = this.m_PossibleObjectives[this.m_SelectedObjectives[2]].getObjectiveBoundaries(this.m_Problem)[1];
                    int size = (maxSize - minSize);
                    size = (int) (maxSize - size * ((valuesArch[i][2] - min2) / (max2 - min2)));
                    ((Chart2DDPointIconCircle) icon).setSize(size);
                    if (valuesPop[i].length > 3) {
                        double min3 = this.m_PossibleObjectives[this.m_SelectedObjectives[3]].getObjectiveBoundaries(this.m_Problem)[0];
                        double max3 = this.m_PossibleObjectives[this.m_SelectedObjectives[3]].getObjectiveBoundaries(this.m_Problem)[1];
                        int col = (maxCol - minCol);
                        col = (int) (minCol + col * ((valuesPop[i][3] - min3) / (max3 - min3)));
                        ((Chart2DDPointIconCircle) icon).setFillColor(new Color(col, 30, 70));
                    }
                } else {
                    icon = new Chart2DDPointIconCross();
                    ((Chart2DDPointIconCross) icon).setColor(Color.RED);

                }
                point.setIcon(icon);
                mySet3.addDPoint(point);
            }

        }
    }

    public static void main(String[] args) {
        TFPortfolioSelectionProblem prob = new TFPortfolioSelectionProblem();
        prob.setUseCardInit(false);
        prob.getOptimizationTargets().addTarget(prob.getOptimizationTargets().getAvailableTargets()[2]);
        Population pop = new Population();
        pop.setTargetSize(50);
        prob.setShowParetoFront(true);
        prob.initProblem();
        prob.initPopulation(pop);
        prob.evaluate(pop);
    }
}
