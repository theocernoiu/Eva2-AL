package eva2.server.go.problems.portfolio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eva2.gui.FunctionArea;
import eva2.gui.GraphPointSet;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.operators.archiving.ArchivingAllDominating;
import eva2.server.go.operators.archiving.ArchivingNSGA;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.TFPortfolioSelection;
import eva2.tools.chart2d.Chart2DDPointIconCircle;
import eva2.tools.chart2d.Chart2DDPointIconCross;
import eva2.tools.chart2d.DPoint;
import eva2.tools.chart2d.DPointIcon;
import eva2.tools.chart2d.DRectangle;
import eva2.tools.chart2d.ScaledBorder;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 07.04.2005
 * Time: 16:02:07
 * To change this template use File | Settings | File Templates.
 */
public class ViewerPortfolioSelection {

    private TFPortfolioSelection m_Problem;
    private Population m_Population;
    private Population m_ParetoFront;
    private JFrame m_Frame;
    private JPanel m_Main, m_Buttons, m_Right, m_DisplayParam;
    private JComboBox m_Mode;
    private JCheckBox m_PANormalize;
    private FunctionArea m_Area;
    private InterfacePortfolioSelectionObjective[] m_PossibleObjectives;
    private JComboBox[] m_Axisis;
    private int[] m_SelectedObjectives;
    private boolean m_CloseAll = true;

    private int m_ParetoFrontSize = 250;

    public ViewerPortfolioSelection(TFPortfolioSelection b) {
        this.m_Problem = b;
        this.init();
    }

    /**
     * This method will init the viewer
     */
    public void init() {
        this.m_ParetoFront = new Population();
        this.m_Population = new Population();
        Dimension d = new Dimension(700, 450);
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

        // the right panel
        this.m_Right = new JPanel();
        this.m_Right.setLayout(new BorderLayout());
        String[] elements = {"Pareto-Front", "Parallel Coordinates", "Covariance Matrix"};
        this.m_Mode = new JComboBox(elements);
        this.m_Mode.setSelectedIndex(0);
        this.m_Mode.addActionListener(changeMode);
        this.m_Right.add(this.m_Mode, BorderLayout.NORTH);
        this.m_DisplayParam = new JPanel();
        this.m_Right.add(this.m_DisplayParam, BorderLayout.CENTER);
        this.m_Main.add(this.m_Right, BorderLayout.EAST);

        // the button panel
        this.m_Buttons = new JPanel();
        this.m_Buttons.setLayout(new GridLayout(3, 1));
        JButton recalButton = new JButton("Recalculate Population");
        recalButton.addActionListener(recalculatePopulation);
        this.m_Buttons.add(recalButton);
        JButton saveButton = new JButton("Save Solution");
        //saveButton.addActionListener(saveSolutionStreichert);
        this.m_Buttons.add(saveButton);
        JButton loadReference = new JButton("Load Reference");
        //loadReference.addActionListener(loadReferenceBeasley);
        this.m_Buttons.add(loadReference);
        this.m_Main.add(this.m_Buttons, BorderLayout.SOUTH);

        // The main frame
        this.m_Frame = new JFrame();
        this.m_Frame.setTitle("Portfolio Selection Problem");
        this.m_Frame.setSize(700, 450);
        this.m_Frame.setLocation(240, 550);
        this.m_Frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                dispose();
            }
        });
        this.m_Frame.getContentPane().add(this.m_Main);
        this.m_Frame.setVisible(true);
        this.m_Frame.show();
        this.update(null);
    }

    /**
     * This method will update the viewer
     */
    public void update(Population p) {

        // Secondly, update the axis elements
        // i will only perform a update in case there is no pop
        if (p == null) this.updateDisplay();

        InterfacePortfolioSelectionObjective[] list = this.m_Problem.getObjectives().getSelectedTargets();
        this.m_PossibleObjectives = new InterfacePortfolioSelectionObjective[list.length];
        for (int i = 0; i < this.m_PossibleObjectives.length; i++)
            this.m_PossibleObjectives[i] = (InterfacePortfolioSelectionObjective) list[i];
        this.updateSelectedObjectives();

        // Finally, draw the actual pop data
        if (this.m_PossibleObjectives.length != this.m_SelectedObjectives.length) this.updateDisplay();
        else {
            if ((p != null) && (p.size() > 0)) {
                if ((this.m_PossibleObjectives.length != ((AbstractEAIndividual) p.get(0)).getFitness().length) ||
                        (this.m_SelectedObjectives.length != ((AbstractEAIndividual) p.get(0)).getFitness().length))
                    this.updateDisplay();
            }
        }
        this.updateArea(p);

        this.m_Frame.validate();
        this.m_Frame.repaint();

    }

    private void updateDisplay() {
        this.m_DisplayParam.removeAll();

        switch (this.m_Mode.getSelectedIndex()) {
            case 0: {
                this.updateDisplayParamPF();
                break;
            }
            case 1: {
                this.updateDisplayParamPA();
                break;
            }
            case 2: {
                this.updateDisplayParamCV();
                break;
            }
            default: {
                this.updateDisplayParamPF();
            }
        }
    }

    /**
     * This method will solely update the area and nothing else
     */
    private void updateArea(Population pop) {
        if (pop != null) {
            this.m_Population = ((Population) pop.clone());
            if (pop.getArchive() != null) this.m_Population.addPopulation((Population) pop.getArchive().clone());

            if (true) {
                ArchivingAllDominating tmpArch = new ArchivingAllDominating();
                tmpArch.addElementsToArchive(this.m_Population);
                this.m_ParetoFront = this.m_Population.getArchive();
            } else {
                ArchivingNSGA tmpArch = new ArchivingNSGA();
                this.m_ParetoFront.SetArchive(new Population());
                this.m_ParetoFront.getArchive().setTargetSize(this.m_ParetoFrontSize);
                this.m_ParetoFront.addPopulation(this.m_Population);
                tmpArch.addElementsToArchive(this.m_ParetoFront);
                this.m_ParetoFront = this.m_ParetoFront.getArchive();
            }
            switch (this.m_Mode.getSelectedIndex()) {
                case 0: {
                    this.updatePlotPF();
                    break;
                }
                case 1: {
                    this.updatePlotPA();
                    break;
                }
                case 2: {
                    this.updatePlotCV();
                    break;
                }
                default: {
                    this.updatePlotPF();
                }
            }
        }
    }

    /**
     * This method will draw the pareto front in the traditional way
     */
    private void updatePlotPF() {
        // parameterize the area
        this.m_Area.removeAllDElements();
        double x, y, xw, yw;
        x = this.m_PossibleObjectives[this.m_SelectedObjectives[0]].getObjectiveBoundaries()[0];
        xw = this.m_PossibleObjectives[this.m_SelectedObjectives[0]].getObjectiveBoundaries()[1] - this.m_PossibleObjectives[this.m_SelectedObjectives[0]].getObjectiveBoundaries()[0];
        y = this.m_PossibleObjectives[this.m_SelectedObjectives[1]].getObjectiveBoundaries()[0];
        yw = this.m_PossibleObjectives[this.m_SelectedObjectives[1]].getObjectiveBoundaries()[1] - this.m_PossibleObjectives[this.m_SelectedObjectives[1]].getObjectiveBoundaries()[0];
        // So die idee waere jetzt ja einfach 10% links und rechts drauf zu tun
        double x10, y10;
        x10 = xw * 0.1;
        y10 = yw * 0.1;
        this.m_Area.setVisibleRectangle(x - x10, y - y10, xw + (2 * x10), yw + (2 * y10));
        ((ScaledBorder) this.m_Area.getBorder()).x_label = this.m_PossibleObjectives[this.m_SelectedObjectives[0]].getName();
        ((ScaledBorder) this.m_Area.getBorder()).y_label = this.m_PossibleObjectives[this.m_SelectedObjectives[1]].getName();

        // Draw some references *****************************************************************
//            if (this.m_ReferenceBeasley != null) {
//                if (this.m_SelectedObjectives.length == 2) {
//                    if (((this.m_PossibleObjectives[this.m_SelectedObjectives[0]] instanceof OptTargetPortfolioReturn) ||
//                        (this.m_PossibleObjectives[this.m_SelectedObjectives[0]] instanceof OptTargetPortfolioRisk)) &&
//                        ((this.m_PossibleObjectives[this.m_SelectedObjectives[1]] instanceof OptTargetPortfolioReturn) ||
//                        (this.m_PossibleObjectives[this.m_SelectedObjectives[1]] instanceof OptTargetPortfolioRisk))) {
//                        GraphPointSet mySetRef = new GraphPointSet(1, this.m_Area);
//                        mySetRef.setConnectedMode(true);
//                        mySetRef.setColor(Color.GREEN);
//                        double[]    tmpVal = new double[2];
//                        for (int i = 0; i < this.m_ReferenceBeasley.length; i++) {
//                            tmpVal = new double[2];
//                            if (this.m_PossibleObjectives[this.m_SelectedObjectives[0]] instanceof OptTargetPortfolioReturn) {
//                                tmpVal[0] = this.m_ReferenceBeasley[i][1];
//                            } else {
//                                tmpVal[0] = this.m_ReferenceBeasley[i][0];
//                            }
//                            if (this.m_PossibleObjectives[this.m_SelectedObjectives[1]] instanceof OptTargetPortfolioReturn) {
//                                tmpVal[1] = this.m_ReferenceBeasley[i][1];
//                            } else {
//                                tmpVal[1] = this.m_ReferenceBeasley[i][0];
//                            }
//                            mySetRef.addDPoint(tmpVal[0], tmpVal[1]);
//                        }
//                    }
//                }
//            }
        //***************************************************************************************

        // first draw all the individuals
        double[][] ranges;
        double[][] valuesPop, valuesArch;
        double[] violPop, violArch;
        ranges = new double[this.m_PossibleObjectives.length][2];
        for (int i = 0; i < ranges.length; i++) {
            ranges[i] = this.m_PossibleObjectives[this.m_SelectedObjectives[i]].getObjectiveBoundaries();
        }
        valuesPop = new double[this.m_Population.size()][this.m_PossibleObjectives.length];
        valuesArch = new double[this.m_ParetoFront.size()][this.m_PossibleObjectives.length];
        violPop = new double[this.m_Population.size()];
        violArch = new double[this.m_ParetoFront.size()];
        for (int i = 0; i < this.m_Population.size(); i++) {
            violPop[i] = ((AbstractEAIndividual) this.m_Population.get(i)).getConstraintViolation();
            for (int j = 0; j < this.m_PossibleObjectives.length; j++) {
                // @todo in case the objectives changed a reevaluation is necessary
                valuesPop[i][j] = ((Double) ((AbstractEAIndividual) this.m_Population.get(i)).getData(this.m_PossibleObjectives[this.m_SelectedObjectives[j]].getName())).doubleValue();
            }
        }

        for (int i = 0; i < this.m_ParetoFront.size(); i++) {
            violArch[i] = ((AbstractEAIndividual) this.m_ParetoFront.get(i)).getConstraintViolation();
            for (int j = 0; j < this.m_PossibleObjectives.length; j++) {
                // @todo in case the objectives changed a reevaluation is necessary
                valuesArch[i][j] = ((Double) ((AbstractEAIndividual) this.m_ParetoFront.get(i)).getData(this.m_PossibleObjectives[this.m_SelectedObjectives[j]].getName())).doubleValue();
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
        mySet1.setColor(Color.DARK_GRAY);

        // Then draw the pareto-front
        int minSize = 2, maxSize = 10;
        int minCol = 50, maxCol = 200;
        mySet3 = new GraphPointSet(13, this.m_Area);
        mySet3.setConnectedMode(false);
        for (int i = 0; i < valuesArch.length; i++) {
            point = new DPoint(valuesArch[i][0], valuesArch[i][1]);
            if (valuesArch[i].length > 2) {
                icon = new Chart2DDPointIconCircle();
                if (violArch[i] > 0) ((Chart2DDPointIconCircle) icon).setBorderColor(Color.BLACK);
                else ((Chart2DDPointIconCircle) icon).setBorderColor(Color.RED);
                ((Chart2DDPointIconCircle) icon).setFillColor(Color.WHITE);
                ((Chart2DDPointIconCircle) icon).setSize(minSize);

                double min2 = this.m_PossibleObjectives[this.m_SelectedObjectives[2]].getObjectiveBoundaries()[0];
                double max2 = this.m_PossibleObjectives[this.m_SelectedObjectives[2]].getObjectiveBoundaries()[1];
                int size = (maxSize - minSize);
                size = (int) (maxSize - size * ((valuesArch[i][2] - min2) / (max2 - min2)));
                ((Chart2DDPointIconCircle) icon).setSize(size);
                if (valuesPop[i].length > 3) {
                    double min3 = this.m_PossibleObjectives[this.m_SelectedObjectives[3]].getObjectiveBoundaries()[0];
                    double max3 = this.m_PossibleObjectives[this.m_SelectedObjectives[3]].getObjectiveBoundaries()[1];
                    int col = (maxCol - minCol);
                    col = (int) (minCol + col * ((valuesPop[i][3] - min3) / (max3 - min3)));
                    ((Chart2DDPointIconCircle) icon).setFillColor(new Color(col, 30, 70));

                }
            } else {
                icon = new Chart2DDPointIconCross();
                if (violArch[i] > 0) ((Chart2DDPointIconCross) icon).setColor(Color.BLACK);
                else ((Chart2DDPointIconCross) icon).setColor(Color.RED);
            }
            point.setIcon(icon);
            mySet3.addDPoint(point);
        }
    }

    /**
     * This method will update the display parameters for the Pareto-Front Plot
     */
    private void updateDisplayParamPF() {
        InterfacePortfolioSelectionObjective[] list = this.m_Problem.getObjectives().getSelectedTargets();
        this.m_PossibleObjectives = new InterfacePortfolioSelectionObjective[list.length];
        for (int i = 0; i < this.m_PossibleObjectives.length; i++)
            this.m_PossibleObjectives[i] = (InterfacePortfolioSelectionObjective) list[i];
        int x = Math.min(4, this.m_PossibleObjectives.length);
        this.m_DisplayParam.setLayout(new GridLayout(2 * x, 1));
        JLabel tmpLabel;
        String[] tmpList = new String[x];
        for (int i = 0; i < this.m_PossibleObjectives.length; i++) tmpList[i] = this.m_PossibleObjectives[i].getName();
        this.m_Axisis = new JComboBox[x];
        if (x > 0) {
            // the x-Axis
            tmpLabel = new JLabel("X-Axis");
            this.m_DisplayParam.add(tmpLabel);
            this.m_Axisis[0] = new JComboBox(tmpList);
            this.m_Axisis[0].setSelectedIndex(0);
            this.m_Axisis[0].addActionListener(updateAxis);
            this.m_DisplayParam.add(this.m_Axisis[0]);
        }
        if (x > 1) {
            // the y-Axis
            tmpLabel = new JLabel("Y-Axis");
            this.m_DisplayParam.add(tmpLabel);
            this.m_Axisis[1] = new JComboBox(tmpList);
            this.m_Axisis[1].setSelectedIndex(1);
            this.m_Axisis[1].addActionListener(updateAxis);
            this.m_DisplayParam.add(this.m_Axisis[1]);
        }
        if (x > 2) {
            // the o-Axis
            tmpLabel = new JLabel("O-Axis");
            this.m_DisplayParam.add(tmpLabel);
            this.m_Axisis[2] = new JComboBox(tmpList);
            this.m_Axisis[2].setSelectedIndex(2);
            this.m_Axisis[2].addActionListener(updateAxis);
            this.m_DisplayParam.add(this.m_Axisis[2]);
        }
        if (x > 3) {
            // the color-Axis
            tmpLabel = new JLabel("C-Axis");
            this.m_DisplayParam.add(tmpLabel);
            this.m_Axisis[3] = new JComboBox(tmpList);
            this.m_Axisis[3].setSelectedIndex(3);
            this.m_Axisis[3].addActionListener(updateAxis);
            this.m_DisplayParam.add(this.m_Axisis[3]);
        }
        this.m_DisplayParam.validate();
        this.updateSelectedObjectives();
    }

    /**
     * This method will draw the *how knows the name*
     * type of graphic
     */
    private void updatePlotPA() {
        // parameterize the area
        this.m_Area.removeAllDElements();
        if (this.m_PANormalize.isSelected()) {
            double x, xw;
            x = 0.5;
            xw = this.m_PossibleObjectives.length;
            this.m_Area.setVisibleRectangle(x, 0, xw, 1);
        } else {
            double x, y, xw, yw;
            x = 0.5;
            xw = this.m_PossibleObjectives.length;
            double ymin = Double.POSITIVE_INFINITY;
            double ymax = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < this.m_PossibleObjectives.length; i++) {
                ymin = Math.min(ymin, this.m_PossibleObjectives[i].getObjectiveBoundaries()[0]);
                ymax = Math.max(ymax, this.m_PossibleObjectives[i].getObjectiveBoundaries()[1]);
            }
            y = ymin;
            yw = ymax - ymin;
            // So die idee waere jetzt ja einfach 10% links und rechts drauf zu tun
            double y10;
            y10 = yw * 0.1;
            this.m_Area.setVisibleRectangle(x, y - y10, xw, yw + (2 * y10));
        }
        ((ScaledBorder) this.m_Area.getBorder()).x_label = "Objectives";
        ((ScaledBorder) this.m_Area.getBorder()).y_label = "Achived Values";

        // now draw all parteooptimal solutions in this grid
        GraphPointSet mySet;
        AbstractEAIndividual indy;
        double[] point, range;
        for (int i = 0; i < this.m_ParetoFront.size(); i++) {
            indy = (AbstractEAIndividual) this.m_ParetoFront.get(i);
            mySet = new GraphPointSet(i + 10, this.m_Area);
            if (indy.getConstraintViolation() > 0) mySet.setColor(Color.RED);
            else mySet.setColor(Color.BLACK);
            mySet.setConnectedMode(true);
            mySet.setIcon(new Chart2DDPointIconCross());
            for (int j = 0; j < this.m_PossibleObjectives.length; j++) {
                point = new double[2];
                point[0] = j + 1;
                point[1] = ((Double) (indy.getData(this.m_PossibleObjectives[j].getName()))).doubleValue();
                if (this.m_PANormalize.isSelected()) {
                    range = this.m_PossibleObjectives[j].getObjectiveBoundaries();
                    point[1] = (point[1] - range[0]) / (range[1] - range[0]);
                }
                mySet.addDPoint(point[0], point[1]);
                mySet.addDPoint(point[0], point[1]);
            }
            this.m_Area.addGraphPointSet(mySet);
        }
    }

    /**
     * This method will update the display parameters for the Pareto-Front Plot
     */
    private void updateDisplayParamPA() {
        this.m_DisplayParam.setLayout(new GridBagLayout());
        GridBagConstraints gbConst = new GridBagConstraints();

        JLabel label = new JLabel("Norm. Fitness: ");
        gbConst = new GridBagConstraints();
        gbConst.anchor = GridBagConstraints.NORTHWEST;
        gbConst.fill = GridBagConstraints.BOTH;
        gbConst.gridy = 0;
        gbConst.gridx = 0;
        gbConst.weightx = 100;
        this.m_DisplayParam.add(label, gbConst);
        if (this.m_PANormalize == null) {
            this.m_PANormalize = new JCheckBox();
            this.m_PANormalize.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    updatePlotPA();
                }
            });
            this.m_PANormalize.setSelected(true);
        }
        gbConst = new GridBagConstraints();
        gbConst.anchor = GridBagConstraints.NORTHWEST;
        gbConst.fill = GridBagConstraints.REMAINDER;
        gbConst.gridy = 0;
        gbConst.gridx = 1;
        gbConst.weightx = 100;
        this.m_DisplayParam.add(this.m_PANormalize, gbConst);
        this.m_DisplayParam.validate();
    }

    /**
     * This method will display the covariance between multiple
     * goals
     */
    private void updatePlotCV() {
        // parameterize the area
        this.m_Area.removeAllDElements();
        this.m_Area.setVisibleRectangle(0, 0, this.m_PossibleObjectives.length, this.m_PossibleObjectives.length);
        ((ScaledBorder) this.m_Area.getBorder()).x_label = "Objectives";
        ((ScaledBorder) this.m_Area.getBorder()).y_label = "Objectives";

        if (this.m_ParetoFront.size() < 2) return;

        // now draw all parteooptimal solutions in this grid
        double[][] values = new double[this.m_ParetoFront.size()][];
        double[] range, fitness;
        AbstractEAIndividual indy;
        for (int i = 0; i < this.m_ParetoFront.size(); i++) {
            indy = (AbstractEAIndividual) this.m_ParetoFront.get(i);
            fitness = indy.getFitness();
            values[i] = new double[fitness.length];
            for (int j = 0; j < this.m_PossibleObjectives.length; j++) {
                if (true) {
                    values[i][j] = fitness[j];
                } else {
                    values[i][j] = ((Double) (indy.getData(this.m_PossibleObjectives[j].getName()))).doubleValue();
//                    if (this.m_Normalize) {
//                        range = this.m_PossibleObjectives[j].getObjectiveBoundaries();
//                        values[i][j] = (values[i][j] - range[0])/(range[1]- range[0]);
//                    }
                }
            }
        }
        // first calculate the mean
        double[] mean = new double[values[0].length];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                mean[j] += values[i][j];
            }
        }
        for (int i = 0; i < mean.length; i++) mean[i] = mean[i] / ((double) values.length);
        // now calculate the covariance
        double[][] covar = new double[mean.length][mean.length];
        for (int i = 0; i < mean.length; i++) {
            for (int j = 0; j < mean.length; j++) {
                for (int k = 0; k < values.length; k++) {
                    covar[i][j] += (values[k][i] - mean[i]) * (values[k][j] - mean[j]);
                }
            }
        }

        // norm and plot the stuff
        DRectangle rect;
        Color color;
        for (int i = 0; i < mean.length; i++) {
            for (int j = 0; j < mean.length; j++) {
                covar[i][j] = covar[i][j] / ((double) values.length);
                rect = new DRectangle(i, j, 1, 1);
                if (covar[i][j] < 0) {
                    color = new Color(Math.min(250, (int) (50 - (covar[i][j] * 1000))), 0, 0);
                } else {
                    color = new Color(0, Math.min(250, (int) (50 + (covar[i][j] * 1000))), 0);
                }
                if (covar[i][j] == 0) color = Color.GRAY;
                if (i == j) {
                    color = new Color(Math.min(250, (int) (50 + (covar[i][j] * 1000))), Math.min(250, (int) (50 + (covar[i][j] * 1000))), Math.min(250, (int) (50 + (covar[i][j] * 1000))));
                }
                rect.setColor(Color.BLACK);
                rect.setFillColor(color);
                this.m_Area.addDElement(rect);
            }
        }
    }

    /**
     * This method will update the display parameters for the Pareto-Front Plot
     */
    private void updateDisplayParamCV() {
        this.m_DisplayParam.validate();
    }

    /**
     * This method will dispose the viewer
     */
    public void dispose() {
        if (this.m_Frame != null) {
            this.m_Frame.dispose();
            this.m_Frame = null;
            this.m_Area = null;
            this.m_PossibleObjectives = null;
            this.m_Problem.m_Viewer = null;
        }
    }

    private void updateSelectedObjectives() {
        this.m_SelectedObjectives = new int[this.m_Axisis.length];
        for (int i = 0; i < this.m_Axisis.length; i++) {
            this.m_SelectedObjectives[i] = this.m_Axisis[i].getSelectedIndex();
        }
    }

    /**
     */
    ActionListener recalculatePopulation = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            m_Population.addPopulation(m_ParetoFront);
            // in case the problem has changed
            m_Problem.initProblem();
            m_Problem.evaluate(m_Population);
            updateArea(m_Population);
        }
    };
    /**
     */
    ActionListener changeMode = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateDisplay();
            updateArea(m_Population);
        }
    };

    /**
     */
    ActionListener updateAxis = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateSelectedObjectives();
            updateArea(m_Population);
        }
    };
}
