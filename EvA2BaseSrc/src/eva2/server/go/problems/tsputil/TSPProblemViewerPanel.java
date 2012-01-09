package eva2.server.go.problems.tsputil;

import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;


/**
 * <p>Title: EvA2</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p/>
 * <p>Company: </p>
 * Definition of an interface for a specific view of a TSP-Instance given the the coordinates of the cities and a given tour.
 *
 * @author planatsc
 * @version 1.0
 */
public abstract class TSPProblemViewerPanel extends JPanel {

    public TSPProblemViewerPanel() {
    }


    /**
     * drawTSP draws the cities + the tour.
     *
     * @param coordinates double[][]
     * @param tour        int[]
     */
    public abstract void drawTSP(double[][] coordinates, int[] tour);


    /**
     * drawCities draws just the cities.
     *
     * @param coordinates double[][]
     */
    public abstract void drawCities(double[][] coordinates);


    /**
     * drawTour updates the tour.
     *
     * @param tour int[]
     */
    public abstract void drawTour(int[] tour);

    private Image _offScreen;

    public void update(Graphics g) {
        Rectangle bounds = bounds();
        if (_offScreen == null ||
                _offScreen.getWidth(this) < bounds.width ||
                _offScreen.getHeight(this) < bounds.height) {
            _offScreen = createImage(bounds.width, bounds.height);
            if (_offScreen == null) return;
        }
        Graphics offGraphics = _offScreen.getGraphics();
        offGraphics.setFont(g.getFont());
        offGraphics.setColor(g.getColor());
        super.update(offGraphics);
        g.drawImage(_offScreen, 0, 0, this);
    }

}
