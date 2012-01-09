package eva2.server.go.problems.tsputil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import eva2.gui.JEFrame;
import eva2.client.EvAClient;

/**
 * <p>Title: EvA2</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author planatsc
 * @version 1.0
 */

public class TSPProblemViewer extends JEFrame {

    TSPProblemViewerPanel TSPpanel;
    TSPProblemViewerPanel TSPpanelSmall;
    JPanel rightPanel, leftPanel;
    JPanel infoPanel;
    InterfaceTSPInstance tspinst;
    JLabel namelabel, commentlabel, metriclabel, bestfoundlabel;
    JLabel helplabel;
    double bestfound;


    /**
     * TSPProblemViewer displays a instance of the Travelling Salesman Problem.
     *
     * @param tspinst InterfaceTSPInstance
     */
    public TSPProblemViewer(InterfaceTSPInstance tspinst) {
        bestfound = Double.MAX_VALUE;
        TSPpanel = new TSPProblemViewerPanelGraph();
        TSPpanelSmall = new TSPProblemViewerPanelPolygon();
//    TSPpanel = new TSPProblemViewerPanelPolygon();
        infoPanel = new JPanel();
        rightPanel = new JPanel();
        namelabel = new JLabel();
        commentlabel = new JLabel();
        metriclabel = new JLabel();
        helplabel = new JLabel();
        bestfoundlabel = new JLabel();
        namelabel.setBorder(javax.swing.BorderFactory.createTitledBorder("Instance Name"));
        commentlabel.setBorder(javax.swing.BorderFactory.createTitledBorder("Comment"));
        metriclabel.setBorder(javax.swing.BorderFactory.createTitledBorder("Metric"));
        bestfoundlabel.setBorder(javax.swing.BorderFactory.createTitledBorder("best found"));
        helplabel.setBorder(javax.swing.BorderFactory.createTitledBorder("Shortcuts"));
        helplabel.setBackground(null);
        TSPpanelSmall.setBorder(javax.swing.BorderFactory.createTitledBorder("Polygonal View"));
        infoPanel.setLayout(new GridLayout(4, 0, 5, 5));
        infoPanel.add(namelabel);
        infoPanel.add(commentlabel);
        infoPanel.add(metriclabel);
        infoPanel.add(bestfoundlabel);

        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(infoPanel, BorderLayout.NORTH);
        rightPanel.add(helplabel, BorderLayout.CENTER);

//    rightPanel.add(TSPpanelSmall);
        rightPanel.add(TSPpanelSmall, BorderLayout.SOUTH);
        TSPpanelSmall.setMinimumSize(new Dimension(200, 200));
        TSPpanelSmall.setPreferredSize(new Dimension(200, 200));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(TSPpanel, BorderLayout.CENTER);
        this.getContentPane().add(rightPanel, BorderLayout.EAST);
        this.setSize(800, 600);
        this.setVisible(true);
        TSPpanel.setVisible(true);
        TSPpanelSmall.setVisible(true);

        this.setTspinst(tspinst);
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK),
                "ctrlPluspressed"
        );
        this.getRootPane().getActionMap().put(
                "ctrlPluspressed",
                new AbstractAction("ctrlPluspressed") {
                    public void actionPerformed(ActionEvent actionEvent) {
                        ((TSPProblemViewerPanelGraph) TSPpanel).zoomfac = ((TSPProblemViewerPanelGraph) TSPpanel).zoomfac * 1.01;
                        TSPpanel.repaint();
                    }
                }
        );
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK),
                "ctrlMinuspressed"
        );
        this.getRootPane().getActionMap().put(
                "ctrlMinuspressed",
                new AbstractAction("ctrlMinuspressed") {
                    public void actionPerformed(ActionEvent actionEvent) {
                        ((TSPProblemViewerPanelGraph) TSPpanel).zoomfac = ((TSPProblemViewerPanelGraph) TSPpanel).zoomfac * 0.99;
                        TSPpanel.repaint();
                    }
                }
        );
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK),
                "ctrl1pressed"
        );
        this.getRootPane().getActionMap().put(
                "ctrl1pressed",
                new AbstractAction("ctrl1pressed") {
                    public void actionPerformed(ActionEvent actionEvent) {
                        ((TSPProblemViewerPanelGraph) TSPpanel).noderadius = ((TSPProblemViewerPanelGraph) TSPpanel).noderadius - 2;
                        TSPpanel.repaint();
                    }
                }
        );
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK),
                "ctrl2pressed"
        );
        this.getRootPane().getActionMap().put(
                "ctrl2pressed",
                new AbstractAction("ctrl2pressed") {
                    public void actionPerformed(ActionEvent actionEvent) {
                        ((TSPProblemViewerPanelGraph) TSPpanel).noderadius = ((TSPProblemViewerPanelGraph) TSPpanel).noderadius + 2;
                        TSPpanel.repaint();
                    }
                }
        );
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK),
                "ctrlSpressed"
        );
        this.getRootPane().getActionMap().put(
                "ctrlSpressed",
                new AbstractAction("ctrl2Sressed") {
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (((TSPProblemViewerPanelGraph) TSPpanel).linecolor == Color.yellow) {
                            ((TSPProblemViewerPanelGraph) TSPpanel).setBackground(Color.white);
                            ((TSPProblemViewerPanelGraph) TSPpanel).linecolor = Color.black;
                        } else {
                            ((TSPProblemViewerPanelGraph) TSPpanel).setBackground(Color.darkGray);
                            ((TSPProblemViewerPanelGraph) TSPpanel).linecolor = Color.yellow;
                        }
                        TSPpanel.repaint();
                    }
                }
        );
    }


    /**
     * setTspinst sets the TSP-instance to display.
     *
     * @param tspinst InterfaceTSPInstance
     */
    public void setTspinst(InterfaceTSPInstance tspinst) {
        bestfound = Double.MAX_VALUE;
        this.tspinst = tspinst;
        this.setTitle(EvAClient.getProductName() + " TSP-Viewer - " + tspinst.getName());
        namelabel.setText(tspinst.getName());
        commentlabel.setText(tspinst.getComment());
        metriclabel.setText(tspinst.getMetric());
        String newline = "<br>";//System.getProperty("line.separator");
        helplabel.setText("<html>Ctrl-s: toogle Colors " + newline + "Ctrl-1: nodesize- " + newline + "Ctrl-2: nodesize+" + newline + "Ctrl-+: zoom in" + newline + "Ctrl--: zoom out </html>");
        TSPpanel.drawCities(tspinst.getCoordinates());
        TSPpanelSmall.drawCities(tspinst.getCoordinates());
    }


    /**
     * draw updates the drwan tour and the tour length.
     *
     * @param tour   int[]
     * @param length double
     */
    public void draw(int[] tour, double length) {
        TSPpanel.drawTour(tour);
        TSPpanelSmall.drawTour(tour);
        if (length < bestfound) {
            bestfound = length;
            bestfoundlabel.setText(String.valueOf(length));
        }
    }

    public static void main(String args[]) {
        RandomTSPInstance ri = new RandomTSPInstance();
        ri.setSize(100);
        TSPProblemViewer test = new TSPProblemViewer(ri);
        int[] tour = new int[ri.size - 1];
        for (int i = 1; i < ri.size; i++) {
            tour[i - 1] = i;
        }
        test.draw(tour, 100);
    }
}
