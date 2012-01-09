package eva2.server.go.problems.tsputil;


/**
 * <p>Title: EvA2</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p/>
 * <p>Company: </p>
 * Interface for a generic instance of the Travelling Salesman Problem.
 *
 * @author planatsc
 * @version 1.0
 */
public interface InterfaceTSPInstance {

    /**
     * This method returns the distance matrix of the cities.
     *
     * @return double[][]
     */
    public double[][] getDistanceMatrix();


    /**
     * getMetric returns the description or the name of the metric used in this instance to calculate the distances between the cities.
     *
     * @return String
     */
    public String getMetric();


    /**
     * getName returns the name of the TSP-instance.
     *
     * @return String
     */
    public String getName();


    /**
     * getComment returns a comment describing the specific TSP-instance.
     *
     * @return String
     */
    public String getComment();


    /**
     * getSize returns the the number of cities in the TSP-instance.
     *
     * @return int
     */
    public int getSize();


    /**
     * getDistance returns the distance beetween two cities.
     *
     * @param node1 Number of the first city.
     * @param node2 Number of the second city.
     * @return Distance between the the first and the second city.
     */
    public double getDistance(int node1, int node2);


    /**
     * getX returns X-Coordinate of a city (if available).
     *
     * @param node Number of the city.
     * @return X-coordinate.
     */
    public double getX(int node);


    /**
     * getY returns Y-Coordinate of a city (if available).
     *
     * @param node int Number of the city.
     * @return double Y-coordinate.
     */
    public double getY(int node);

    /**
     * getCoordinats returns XY-Coordinates of all cities (if available).
     *
     * @return double[][] XY-coordinates-array.
     */
    public double[][] getCoordinates();

}
