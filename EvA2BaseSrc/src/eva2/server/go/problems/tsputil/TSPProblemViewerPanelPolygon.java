package eva2.server.go.problems.tsputil;

import java.awt.Color;
import java.awt.Graphics;


/**
 * <p>Title: EvA2</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p/>
 * <p>Company: </p>
 * This is a polygonal (solid curve) view of a TSP-Instance.
 *
 * @author planatsc
 * @version 1.0
 */
public class TSPProblemViewerPanelPolygon extends TSPProblemViewerPanel {

    int noderadius;
    Color nodecolor;
    Color linecolor;
    double[][] coordinates;
    int[] tour;

    public TSPProblemViewerPanelPolygon() {
        noderadius = 2;
        nodecolor = Color.lightGray;
        linecolor = Color.orange;
        this.setOpaque(true);
        this.setBackground(Color.lightGray);
    }

    public void normCoordinates(double[][] coord) {
        double maxval = Double.MIN_VALUE;
        for (int i = 0; i < coord.length; i++)
            for (int j = 0; j < coord[0].length; j++)
                if (maxval < coord[i][j]) maxval = coord[i][j];
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
            if (tour != null) {
                int[] xc = new int[tour.length + 1];
                int[] yc = new int[tour.length + 1];
                gc.setColor(linecolor);
                xc[0] = (int) Math.round(coordinates[0][0] * normfac);
                yc[0] = (int) Math.round(coordinates[0][1] * normfac);
                for (int i = 0; i < tour.length; i++) {
                    xc[i + 1] = 10 + (int) Math.round(coordinates[tour[i]][0] * normfac);
                    yc[i + 1] = 20 + (int) Math.round(coordinates[tour[i]][1] * normfac);
                }
                gc.fillPolygon(xc, yc, xc.length);
                gc.setColor(Color.BLACK);
                gc.drawPolygon(xc, yc, xc.length);
            }
        }
    }


}
