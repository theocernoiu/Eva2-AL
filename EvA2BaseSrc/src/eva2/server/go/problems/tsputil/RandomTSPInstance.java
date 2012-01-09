package eva2.server.go.problems.tsputil;


import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import eva2.gui.PropertyFilePath;
import eva2.tools.math.RNG;


/**
 * <p>Title: EvA2</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p/>
 * <p>Company: </p>
 * This class implements an instance of the TSP-Problem of an customizable size an random city-distances.
 *
 * @author planatsc
 * @version 1.0
 */
public class RandomTSPInstance implements InterfaceTSPInstance, java.io.Serializable {

    String name;
    String metric;
    String comment;
    int size;
    double[][] distancematrix;
    boolean builddistancematrix;
    double[][] coordinates;


    public RandomTSPInstance() {
        name = "RandomInstance " + RNG.getRandomSeed();
        metric = "EUC_2D";
        comment = "Just a random TSP instance.";
        this.setSize(10);
    }


    public double[][] getDistanceMatrix() {
        if (this.builddistancematrix) return this.distancematrix;
        double[][] tempdistancematrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tempdistancematrix[i][j] = distanceeuclidean(i, j);
            }
        }
        return tempdistancematrix;
    }

    public String getMetric() {
        return this.metric;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public double getDistance(int node1, int node2) {
        if (this.builddistancematrix) {
            return distancematrix[node1][node2];
        } else {
            return distanceeuclidean(node1, node2);
        }
    }

    private double distanceeuclidean(int node1, int node2) {
        return Math.sqrt(Math.pow((coordinates[node1][0] - coordinates[node2][0]), 2) +
                Math.pow((coordinates[node1][1] - coordinates[node2][1]), 2));
    }

    public double[][] getCoordinates() {
        return this.coordinates;
    }


    public static void main(String args[]) {
        RandomTSPInstance test = new RandomTSPInstance();
    }

    public double getX(int node) {
        return this.coordinates[node][0];
    }

    public double getY(int node) {
        return this.coordinates[node][1];
    }

    public String getComment() {
        return comment;
    }

    public void setSize(int size) {
        this.size = size;
        coordinates = new double[size][2];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i][0] = RNG.randomDouble(0, 10);
            coordinates[i][1] = RNG.randomDouble(0, 10);
        }
        builddistancematrix = true;
        if (builddistancematrix) {
            //System.out.println("Building distance matrix");
            distancematrix = new double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    distancematrix[i][j] = distanceeuclidean(i, j);
                }
            }
        }

    }

}
