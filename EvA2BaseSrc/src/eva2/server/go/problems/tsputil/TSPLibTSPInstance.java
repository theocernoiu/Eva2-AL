package eva2.server.go.problems.tsputil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import eva2.gui.PropertyFilePath;
import eva2.tools.BasicResourceLoader;


/**
 * <p>Title: EvA2</p>
 * <p/>
 * <p>Description: </p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p/>
 * <p>Company: </p>
 * This class allows to read instances of the Travelling Sales Problem in the TSPLib-Format.
 *
 * @author planatsc
 * @version 1.0
 */
public class TSPLibTSPInstance implements InterfaceTSPInstance, java.io.Serializable {

    File m_file;
    PropertyFilePath m_filepath;
    String name;
    String metric;
    String comment;
    int size;
    transient double[][] distancematrix;
    boolean builddistancematrix;
    double[][] coordinates;


    public TSPLibTSPInstance() {
//	  m_filepath = new PropertyFilePath
//	  BasicResourceLoader.
        m_filepath = PropertyFilePath.getFilePathFromResource("resources/TSPLib/euc2d/eil101.tsp");
        if (m_filepath == null)
            System.err.println("Warning: empty filepath for TSPLibTSPInstance - missing resources?");
//	  m_filepath = new PropertyFilePath(System.getProperty("user.dir") + "/resources/TSPLib/euc2d/eil101.tsp");
        this.setFilepath(m_filepath);
        builddistancematrix = true;
    }


    public double[][] getDistanceMatrix() {
        if (this.builddistancematrix) return this.distancematrix;
        double[][] tempdistancematrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < (size - j); j++) {
                tempdistancematrix[i][j] = distanceeuclidean(i, j);
                tempdistancematrix[j][i] = distancematrix[i][j];
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
            if (distancematrix == null) {
                //System.out.println("Building distance matrix");
                distancematrix = new double[size][size];
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        distancematrix[i][j] = distanceeuclidean(i, j);
                    }
                }
            }
            return distancematrix[node1][node2];
        } else {
            return distanceeuclidean(node1, node2);
        }
    }


    /**
     * distanceeuclidean calculates the euclidean distance between two cities.
     *
     * @param node1 int first city
     * @param node2 second city
     * @return distance
     */
    private double distanceeuclidean(int node1, int node2) {
        return Math.sqrt(Math.pow(coordinates[node1][0] - coordinates[node2][0], 2) + Math.pow(coordinates[node1][1] - coordinates[node2][1], 2));
    }

    public File GetFile() {
        return m_file;
    }

    public double[][] getCoordinates() {
        return this.coordinates;
    }


    /**
     * setM_file reads a file in TSPLib-format, and configures the class.
     *
     * @param m_file File TSPLib-file
     */
    public boolean setFile(File m_file) {
        if (!m_file.exists()) {
            String path = m_file.getAbsolutePath();
            String jarTagStr = "jar!/"; // string marking the jar-part of a path
            if (path.contains(jarTagStr)) {
                // seems to be a path into a jar
                String relPath = m_file.getAbsolutePath().substring(path.indexOf(jarTagStr) + jarTagStr.length()); // add tag-length
                InputStream in = BasicResourceLoader.instance().getStreamFromResourceLocation(relPath);
                System.out.println("LOOKING AT " + relPath);
                if (readStream(new BufferedReader(new InputStreamReader(in)))) {
                    this.m_file = m_file;
                    return true;
                } else return false;
            }
            System.err.println("File does not exist, no jar stream found...");
            return false;
        } else {
            if (readFile(m_file)) {
                this.m_file = m_file;
                return true;
            } else return false;
        }
    }

    private boolean readFile(File f) {
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(f));
            return readStream(bufReader);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

//        BufferedReader reader = new BufferedReader(new FileReader(in));
    }

    private boolean readStream(BufferedReader reader) {
        try {
            boolean inNODE_COORD_SECTION = false;
            while (true) {
                String s = reader.readLine();
                if (s == null) break;
                //System.out.println(s);
                if (inNODE_COORD_SECTION) {
                    if (s.compareTo("EOF") == 0) break;
                    while (s.startsWith(" ")) s = s.substring(1);
                    String[] subs = s.split(" ");
                    int node = Integer.parseInt(subs[0]);
                    this.coordinates[node - 1][0] = Double.parseDouble(subs[1]);
                    this.coordinates[node - 1][1] = Double.parseDouble(subs[2]);
//          System.out.println(this.coordinates[node-1][0] + " " + this.coordinates[node-1][1]);
                } else {
                    int seppos = s.indexOf(":");
                    if (seppos != -1) {
                        String tagname = s.substring(0, seppos).trim();
                        String tagvalue = s.substring(seppos + 1).trim();
                        //System.out.println(tagname + "=>" + tagvalue);
                        if (tagname.compareTo("NAME") == 0) {
                            this.name = tagvalue;
                        } else if (tagname.compareTo("EDGE_WEIGHT_TYPE") == 0) {
                            this.metric = tagvalue;
                        } else if (tagname.compareTo("DIMENSION") == 0) {
                            this.size = Integer.parseInt(tagvalue);
                        } else if (tagname.compareTo("COMMENT") == 0) {
                            this.comment = tagvalue;
                        }
                    } else if (s.trim().compareTo("NODE_COORD_SECTION") == 0) {
                        inNODE_COORD_SECTION = true;
                        this.coordinates = new double[size][2];
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("e=" + e + " " + e.getMessage());
            return false;
        }
        //System.out.println("FileOK");
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
        return true;
    }

    public static void main(String args[]) {
        TSPLibTSPInstance test = new TSPLibTSPInstance();
    }

    public PropertyFilePath getFilepath() {
        return m_filepath;
    }

    public void setFilepath(PropertyFilePath m_filepath) {
        //System.out.println("!!!!!!!!!!!!!!setM_filepath!!!!!!!!!!!!");
//	  File f = new File(m_filepath.getCompleteFilePath());
        if (this.setFile(new File(m_filepath.getCompleteFilePath()))) {
            this.m_filepath = m_filepath;
        }
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

}
