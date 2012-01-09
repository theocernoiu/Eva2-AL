/*
 * Created on Nov 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package eva2.server.go.problems.linematching;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * @author sehnke
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class SingletonWhitePointList {

    private static SingletonWhitePointList instance;
    private static final int dimension = 11;
    private static double pRange[][] = new double[dimension][2];
    private static double param[] = new double[dimension];
    private static int fileLength;
    private static int whitePoints[][] = new int[30000][2];

    private SingletonWhitePointList() {

        this.initRange();
        this.initParams();

        FileInputStream file = null;
        try {
            file = new FileInputStream("points.txt");
        } catch (FileNotFoundException e) {
            System.out.println("File not found to load white points");
            e.printStackTrace();
        }

        fileLength = 0;

        try {
            fileLength = file.available();
        } catch (IOException e1) {
            System.out.println("Empty File");
            e1.printStackTrace();
        }

        if (fileLength >= 30000) fileLength = 30000;

        byte n8b0 = 0;
        byte n8b1 = 0;

        for (int i = 0; i < fileLength / 2; i++) {
            for (int j = 0; j < 2; j++) {

                try {
                    n8b0 = (byte) file.read();
                    n8b1 = (byte) file.read();
                } catch (IOException e2) {
                    System.out.println("unexpected end of File");
                    e2.printStackTrace();
                }
                whitePoints[i][j] = ((int) n8b0 - 33) * 64 + ((int) n8b1 - 33);


            }

        }

        loadPicture();
    }

    /**
     *
     */
    private void initParams() {

        FileInputStream file = null;
        try {
            file = new FileInputStream("params.txt");
        } catch (FileNotFoundException e) {
            System.out.println("File not found to load parameters");
            e.printStackTrace();
        }


        for (int i = 0; i < dimension; i++) {
            try {
                param[i] = file.read();
            } catch (IOException e2) {
                System.out.println("Not enought parameters in file");
                e2.printStackTrace();
            }


        }
        param[0] = -0.606743;
        param[1] = 0.00376636;
        param[2] = 0.0103715;
        param[3] = 50.0182;
        param[4] = 10.334;
        param[5] = 26.0201;
        param[6] = -5.00358;
        param[7] = -0.000158981;
        param[8] = 0.00000291834;
        param[9] = 0.103982;
        param[10] = 100;
    }

    /**
     *
     */
    private void initRange() {

        FileInputStream file = null;
        try {
            file = new FileInputStream("ranges.txt");
        } catch (FileNotFoundException e) {
            System.out.println("File not found to load parameter ranges");
            e.printStackTrace();
        }


        for (int i = 0; i < dimension; i++) {
            try {
                pRange[i][0] = file.read();
            } catch (IOException e2) {
                System.out.println("Not enought parameters in file");
                e2.printStackTrace();
            }


        }
        pRange[0][0] = -1.0; // const part
        pRange[0][0] = 1.0;
        pRange[1][0] = -0.02; // x-offset
        pRange[1][1] = 0.02;
        pRange[2][0] = -0.02; //y-offset
        pRange[2][1] = 0.02;
        pRange[3][0] = 30.0; // Param linear
        pRange[3][1] = 70.0;
        pRange[4][0] = 1.0; // Param parable
        pRange[4][1] = 25.0;
        pRange[5][0] = 20.0; // Param e-Funktion
        pRange[5][1] = 35.0;
        pRange[6][0] = -10.0; // e-funktion const teil
        pRange[6][1] = -2.0;
        pRange[7][0] = 0.0; // no use
        pRange[7][1] = 0.0;
        pRange[8][0] = 0.0; // no use
        pRange[8][1] = 0.0;
        pRange[9][0] = 0.05; // skalierung gesammt
        pRange[9][1] = 0.2;
        pRange[10][0] = 60.0; // steigung e-Funktion
        pRange[10][1] = 100.0;

    }

    public static double[] getParameterRange(int i) {
        if ((i < dimension) && (i >= 0)) {
            return pRange[i];
        } else {
            return pRange[0];
        }
    }

    public static double getParameter(int i) {
        if ((i < dimension) && (i >= 0)) {
            return param[i];
        } else {
            return -1;
        }
    }

    public static int getDimension() {
        return dimension;
    }

    public static SingletonWhitePointList instance() {
        if (instance == null) {
            instance = new SingletonWhitePointList();
            System.out.println(SingletonWhitePointList.getNumbOfWhitePoints());
        }

        return instance;
    }

    /**
     * @param d
     * @param e
     * @return
     */
    public static boolean onLine(double x, double y, int gen) {

        int robXPos = 1500;
        int penaltyLength = 1590;
        int fieldWidth = 8100;
        int fieldLength = 12100;
        int penaltyPoint = 4000;
        int radCircle = 1030;

//		int robXPos=1500;
//		int penaltyLength=1580;
//		int fieldWidth=7880;
//		int fieldLength=11850;
//		int penaltyPoint=3850;
//		int radCircle=980;

        int yR = 0;
        int xR = robXPos / 20;
        int xDif1 = robXPos / 20;
        int yDif1 = fieldWidth / 40;
        int xDif2 = -fieldLength / 40 + penaltyLength / 20 + robXPos / 20;
        int yDif2 = -fieldWidth / 40;
        int xDif3 = -fieldLength / 40 + robXPos / 20;
        int xDif4 = fieldLength / 40 + robXPos / 20;
        int xP = -penaltyPoint / 20 + robXPos / 20;
        int yP = 0;

        if ((x >= xDif2 - 2) && (x <= xDif2 + 2) && (y > yDif2 - 6) && (y < yDif1 + 6)) return true;
        if ((x >= xDif1 - 1) && (x <= xDif1 + 1) && (y > yDif2 - 6) && (y < yDif1 + 6)) return true;
        if (gen > 150) if ((x >= xDif3 - 6) && (x <= xDif3 + 6) && (y > yDif2 - 6) && (y < yDif1 + 6)) return true;
        if ((x >= xDif4 - 6) && (x <= xDif4 + 6) && (y > yDif2 - 6) && (y < yDif1 + 6)) return true;

        if ((y >= yDif2 - 6) && (y <= yDif2 + 6)) return true;
        if ((y >= yDif1 - 6) && (y <= yDif1 + 6)) return true;
        double r = Math.sqrt((double) ((x - xP) * (x - xP)) + (double) ((y - yP) * (y - yP)));
        if (r <= 3) {
            return true;
        }
        if ((Math.abs(x - xR) > radCircle / 20 + 4) || (Math.abs(y - yR) > radCircle / 20.0 + 4)) return false;
        r = Math.sqrt((double) ((x - xR) * (x - xR)) + (double) ((y - yR) * (y - yR)));
        if ((r <= radCircle / 20.0 + 2) && (r >= radCircle / 20.0 - 2)) {
            return true;
        } else {
            return false;
        }

    }

    public static int getNumbOfWhitePoints() {
        return fileLength;
    }

    public static int[] transform(double x, double y, double[] IV) {
        int result[] = new int[3];

        double dist = Math.sqrt(Math.pow((double) x / 1000.0 + IV[1], 2) + Math.pow((double) y / 1000.0 + IV[2], 2));

        // Kegel
        double fK = IV[3] * dist;

        //Parabell
        double fP = IV[4] * dist * dist;

//		Grad 3
        double fd = IV[7] * dist * dist * dist;

//		Grad 4
        double fv = IV[8] * dist * dist * dist * dist;

        //e-Funtion
        double fE = IV[5] * Math.pow(2.718281828, (double) (IV[10] * dist * dist + IV[6]));

        //Gesammtfunktion
        dist = (IV[0] + fK + fP + fE + fd + fv) * IV[9] * 50.0;

        if (dist > 0) {
            double pDist = Math.sqrt((((double) x + IV[1] * 1000.0) * ((double) x + IV[1] * 1000.0) + ((double) y + IV[2] * 1000.0) * ((double) y + IV[2] * 1000.0)));

            y = (int) (((y + IV[2] * 1000.0) / pDist) * dist);
            x = (int) (((x + IV[1] * 1000.0) / pDist) * dist);

            result[0] = (int) x;
            result[1] = (int) y;
            result[2] = (int) dist;
        } else {
            result[0] = 0;
            result[1] = 0;
            result[2] = 0;
        }
        return result;
    }

    /**
     * @param i
     * @return
     */
    public static double[] getPoint(int i) {
        double result[] = new double[2];
        result[0] = whitePoints[i][0];
        result[1] = whitePoints[i][1];

        return result;
    }

    private void loadPicture() {

        java.util.Random rnd = new java.util.Random();
        picture = new byte[PIC_WIDTH][PIC_HEIGHT][3];
        for (int x = 0; x < PIC_WIDTH; x++) {
            for (int y = 0; y < PIC_HEIGHT; y++) {
                for (int c = 0; c < 3; c++) {
                    picture[x][y][c] = 0;
                }
            }
        }
        for (int i = 0; i < fileLength / 2; i++) {
            int x = whitePoints[i][0];
            int y = whitePoints[i][1];
            if ((x > 0) && (x < PIC_WIDTH) && (y > 0) && (y < PIC_HEIGHT)) {
                for (int c = 0; c < 3; c++) {
                    picture[whitePoints[i][0]][whitePoints[i][1]][c] = 31;
                }
            }
        }
        for (int x = 0; x < PIC_WIDTH; x++) {
            for (int y = 0; y < PIC_HEIGHT; y++) {
                SingletonPicture.instance().setPixel(x, y, picture[x][y]);
            }
        }
    }

    public void showWorld(int gen) {

        byte[] tmpByte = new byte[3];
        for (int x = -PIC_WIDTH / 2; x < PIC_WIDTH / 2; x++) {
            for (int y = -PIC_HEIGHT / 2; y < PIC_HEIGHT / 2; y++) {
                for (int c = 0; c < 3; c++) {
                    tmpByte[c] = (byte) ((SingletonWhitePointList.onLine((double) x, (double) y, gen) ? 1 : 0) * 31);
                }
                SingletonPicture.instance().setPixel(x + PIC_WIDTH / 2, y + PIC_HEIGHT / 2, tmpByte);
            }
        }
    }

    private byte[][][] picture;
    public static final int PIC_WIDTH = 768;
    public static final int PIC_HEIGHT = 576;

    /**
     *
     */
    public void update() {
        SingletonPicture.instance().update();

    }

}
