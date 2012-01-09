package eva2.server.go.problems.tsputil;


import java.awt.Color;
import java.awt.Graphics;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;


/**
 * <p>Title: EvA2</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p/>
 * <p>Company: </p>
 * This is a standard graph view for a TSP-Instance.
 *
 * @author planatsc
 * @version 1.0
 */
public class TSPProblemViewerPanelGraph extends TSPProblemViewerPanel {

    int noderadius;
    Color nodecolor;
    Color linecolor;
    double[][] coordinates;
    int[] tour;
    double zoomfac;

    public TSPProblemViewerPanelGraph() {
        noderadius = 10;
        nodecolor = Color.orange;
        linecolor = Color.yellow;
        zoomfac = 1;
        this.setOpaque(true);
        this.setBackground(Color.darkGray);
    }

    public void normCoordinates(double[][] coord) {
        double minvalx = Double.MAX_VALUE;
        double minvaly = Double.MAX_VALUE;
        for (int i = 0; i < coord.length; i++) {
            if (minvalx > coord[i][0]) minvalx = coord[i][0];
            if (minvaly > coord[i][1]) minvaly = coord[i][1];
        }
        for (int i = 0; i < coord.length; i++) {
            coord[i][0] = coord[i][0] - minvalx;
            coord[i][1] = coord[i][1] - minvaly;
        }
        double maxval = -Double.MAX_VALUE;
        for (int i = 0; i < coord.length; i++) {
            for (int j = 0; j < coord[0].length; j++) {
                if (maxval < coord[i][j]) maxval = coord[i][j];
            }
        }
        for (int i = 0; i < coord.length; i++)
            for (int j = 0; j < coord[0].length; j++)
                coord[i][j] = coord[i][j] / maxval;
    }

    public void drawTSP(double[][] coordinates, int[] tour) {
        drawCities(coordinates);
        drawTour(tour);
    }

    public void drawCities(double[][] coordinates) {
        normCoordinates(coordinates);
        this.coordinates = coordinates;
        this.tour = null;
        this.repaint();
    }

    public void drawTour(int[] tour) {
        this.tour = tour;
        this.repaint();
    }

    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        if (coordinates != null) {
            double height = this.getSize().getHeight();
            double width = this.getSize().getWidth();
            double normfac = width - 20;
            normfac = zoomfac * normfac;
            if (tour != null) {
                gc.setColor(linecolor);
                int x1 = (int) Math.round(coordinates[0][0] * normfac);
                int y1 = (int) Math.round(coordinates[0][1] * normfac);
                int x2 = (int) Math.round(coordinates[tour[0]][0] * normfac);
                int y2 = (int) Math.round(coordinates[tour[0]][1] * normfac);
                gc.drawLine(10 + x1, 10 + y1, 10 + x2, 10 + y2);
                for (int i = 0; i < tour.length - 1; i++) {
                    x1 = (int) Math.round(coordinates[tour[i]][0] * normfac);
                    y1 = (int) Math.round(coordinates[tour[i]][1] * normfac);
                    x2 = (int) Math.round(coordinates[tour[i + 1]][0] * normfac);
                    y2 = (int) Math.round(coordinates[tour[i + 1]][1] * normfac);
                    gc.drawLine(10 + x1, 10 + y1, 10 + x2, 10 + y2);
                }
                x1 = (int) Math.round(coordinates[tour[tour.length - 1]][0] * normfac);
                y1 = (int) Math.round(coordinates[tour[tour.length - 1]][1] * normfac);
                x2 = (int) Math.round(coordinates[0][0] * normfac);
                y2 = (int) Math.round(coordinates[0][1] * normfac);
                gc.drawLine(10 + x1, 10 + y1, 10 + x2, 10 + y2);
            }

            for (int i = 0; i < coordinates.length; i++) {
                int x = (int) Math.round(coordinates[i][0] * normfac);
                int y = (int) Math.round(coordinates[i][1] * normfac);
                gc.setColor(nodecolor);
                gc.fillOval(10 + x - noderadius / 2, 10 + y - noderadius / 2, noderadius, noderadius);
                gc.setColor(Color.black);
                gc.drawOval(10 + x - noderadius / 2, 10 + y - noderadius / 2, noderadius, noderadius);
            }
        }
    }


}
