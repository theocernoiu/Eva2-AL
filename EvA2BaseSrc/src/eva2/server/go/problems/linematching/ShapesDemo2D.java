package eva2.server.go.problems.linematching;

/*
 * 1.2 version.
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * This is like the FontDemo applet in volume 1, except that it
 * uses the Java 2D APIs to define and render the graphics and text.
 */

public class ShapesDemo2D extends JApplet {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    final static int maxCharHeight = 15;
    final static int minFontSize = 6;

    final static Color bg = Color.white;
    final static Color fg = Color.black;

    Color[] allColors = new Color[32768];
    //((float)0.9, (float)0.2, (float)0.9);
    final static Color white = Color.white;

    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);

    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);
    Dimension totalSize;
    FontMetrics fontMetrics;

    private void initPic() {
        for (float r = 0; r < 32; r++) {
            for (float g = 0; g < 32; g++) {
                for (float b = 0; b < 32; b++) {
                    float red = r / (float) 32.0;
                    float green = g / (float) 32.0;
                    float blue = b / (float) 32.0;

                    allColors[(int) r * 32 * 32 + (int) g * 32 + (int) b] = new Color(red, green, blue);
                }
            }

        }
    }

    public void init() {
        //Initialize drawing colors
        setBackground(bg);
        setForeground(fg);
        initPic();
    }


    public void paint(Graphics g) {
        Dimension d = getSize();
        int gridWidth = d.width / 6;
        int gridHeight = d.height / 2;


        // draw Line2D.Double
        for (int xCord = 0; xCord < SingletonPicture.PIC_WIDTH; xCord++) {
            for (int yCord = 0; yCord < SingletonPicture.PIC_HEIGHT; yCord++) {
                //g2.setPaint(allColors[SingletonPicture.instance().getPixel(xCord, yCord)[0]/2*32*16+SingletonPicture.instance().getPixel(xCord, yCord)[1]*16+SingletonPicture.instance().getPixel(xCord, yCord)[2]/2]);
                //g2.draw(new Line2D.Double(xCord,yCord, xCord, yCord));
                g.setColor(allColors[SingletonPicture.instance().getPixel(xCord, yCord)[0] * 32 * 32 + SingletonPicture.instance().getPixel(xCord, yCord)[1] * 32 + SingletonPicture.instance().getPixel(xCord, yCord)[2]]);
                g.drawLine(xCord, yCord, xCord, yCord);
            }

        }

    }


}
