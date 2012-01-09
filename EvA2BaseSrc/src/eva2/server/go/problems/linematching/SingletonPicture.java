/*
 * Created on Oct 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package eva2.server.go.problems.linematching;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * @author sehnke
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class SingletonPicture {

    private static SingletonPicture instance;

    private SingletonPicture() {
        loadPicture();
    }

    public static SingletonPicture instance() {
        if (instance == null) {
            instance = new SingletonPicture();
        }

        return instance;
    }

    public byte[] getPixel(int x, int y) {
        return picture[x][y];
    }

    public void setPixel(int x, int y, byte[] c) {
        for (int i = 0; i < c.length; i++) {
            if ((x >= 0) && (x < PIC_WIDTH) && (y >= 0) && (y < PIC_HEIGHT)) {
                picture[x][y][i] = c[i];
            }
        }

    }

    public void drawPicture() {
        JFrame f = new JFrame("ShapesDemo2D");
        //f.addWindowListener(new WindowAdapter() {
        //    public void windowClosing(WindowEvent e) {System.exit(0);}
        //});
        JApplet applet = new ShapesDemo2D();
        f.getContentPane().add("Center", applet);
        applet.init();
        applet.show();

        f.pack();
        f.setSize(new Dimension(PIC_WIDTH, PIC_HEIGHT));
        f.setVisible(true);

    }

    private void loadPicture() {
        // TODO load real Picture
        java.util.Random rnd = new java.util.Random();
        picture = new byte[PIC_WIDTH][PIC_HEIGHT][3];
        for (int x = 0; x < PIC_WIDTH; x++) {
            for (int y = 0; y < PIC_HEIGHT; y++) {
                for (int c = 0; c < 3; c++) {

                    int zuf = rnd.nextInt();
                    byte zufall = (byte) zuf;
                    zufall /= 8;
                    zufall += 16;
                    zuf = (int) zufall;

                    picture[x][y][c] = (byte) zufall;
                    if ((x > PIC_WIDTH / 2) && (x < (PIC_WIDTH * 3) / 4)
                            && (y > PIC_HEIGHT / 2) && (y < (PIC_HEIGHT * 3) / 4)
                            && (c == 2)) {
                        picture[x][y][c] = 31;
                    }
                }
            }
        }
    }

    private byte[][][] picture;
    private Graphics g;
    public static final int PIC_WIDTH = 768;
    public static final int PIC_HEIGHT = 576;

    /**
     *
     */
    public void update() {
        // TODO Auto-generated method stub

    }

}
