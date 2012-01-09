package eva2.server.go.problems;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import eva2.gui.JEFrame;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualIntegerData;
import eva2.server.go.individuals.InterfaceDataTypeInteger;
import eva2.server.go.individuals.InterfaceDataTypePermutation;
import eva2.server.go.individuals.OBGAIndividualPermutationData;
import eva2.server.go.populations.Population;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.tools.BasicResourceLoader;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 28.01.2005
 * Time: 13:49:29
 * To change this template use File | Settings | File Templates.
 */
public class TSXQueensProblem extends AbstractOptimizationProblem implements java.io.Serializable {

    protected AbstractEAIndividual m_OverallBest = null;
    protected AbstractEAIndividual m_IntArrayIndividual = null;
    protected int m_ProblemDimension = 8;
    protected boolean m_UseIntNotPermutation = false;

    // This is graphics stuff
    transient private ImageIcon m_BlackQueen, m_WhiteQueen;
    transient private Dimension m_MinSize;
    transient private boolean m_Show = false;
    transient private JEFrame mainFrame;
    transient private JPanel mainPanel;
    transient private JPanel buttonPanel;
    transient private JPanel enviromentJPanel;
    transient private JButton[][] enviromentGraph;

    public TSXQueensProblem() {
        this.m_Template = new OBGAIndividualPermutationData();
        this.m_IntArrayIndividual = new GAIndividualIntegerData();
        this.initProblem();
    }

    public TSXQueensProblem(TSXQueensProblem b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        //TSXQueensProblem
        if (b.m_IntArrayIndividual != null)
            this.m_IntArrayIndividual = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_IntArrayIndividual).clone();
        if (b.m_OverallBest != null)
            this.m_OverallBest = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_OverallBest).clone();
        this.m_ProblemDimension = b.m_ProblemDimension;
        this.m_UseIntNotPermutation = b.m_UseIntNotPermutation;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new TSXQueensProblem(this);
    }

    /**
     * This method init the enviroment panel if necessary.
     */
    private void initEnvironmentPanel() {
        this.m_MinSize = new Dimension(45, 45);
        if (this.mainFrame == null) {
            mainFrame = new JEFrame("X-Queens Problem");
            mainFrame.setSize(100, 100);
            this.mainPanel = new JPanel();
            this.buttonPanel = new JPanel();
            this.enviromentJPanel = new JPanel();
            this.mainPanel.setLayout(new BorderLayout());
            this.buttonPanel.setLayout(new BorderLayout());
            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    m_OverallBest = null;
                }
            });
//            JButton printButton = new JButton("Print");
//            printButton.addActionListener(this.printListener);
            this.buttonPanel.add(resetButton, BorderLayout.NORTH);
//            this.buttonPanel.add(printButton, BorderLayout.SOUTH);
            this.enviromentJPanel.setLayout(new GridLayout(this.m_ProblemDimension, this.m_ProblemDimension));
            this.enviromentGraph = new JButton[this.m_ProblemDimension][this.m_ProblemDimension];
            for (int i = 0; i < this.m_ProblemDimension; i++) {
                for (int j = 0; j < this.m_ProblemDimension; j++) {
                    this.enviromentGraph[i][j] = new JButton();
                    this.enviromentGraph[i][j].setPreferredSize(this.m_MinSize);
                    this.enviromentGraph[i][j].setMinimumSize(this.m_MinSize);
                    if (this.isBlackTile(i, j)) this.enviromentGraph[i][j].setBackground(Color.BLACK);
                    else this.enviromentGraph[i][j].setBackground(Color.WHITE);
                    this.enviromentJPanel.add(this.enviromentGraph[i][j]);
                }
            }
            this.mainPanel.add(this.enviromentJPanel, BorderLayout.CENTER);
            this.mainPanel.add(this.buttonPanel, BorderLayout.SOUTH);
            mainFrame.getContentPane().add(this.mainPanel);
            mainFrame.pack();
            mainFrame.setVisible(true);
        }
    }

    /**
     * This method simply determines wether or not a tile is black
     *
     * @param i x axis
     * @param j y axis
     * @return True it the tile is black
     */
    private boolean isBlackTile(int i, int j) {
        if ((i % 2 == 1) && (j % 2 == 1)) return true;
        if ((i % 2 == 0) && (j % 2 == 0)) return true;
        return false;
    }

//
//    /** This action listener, called by the "Run/Restart" button, will init the problem and start the computation.
//     */
//    ActionListener resetListener = new ActionListener() {
//        public void actionPerformed(ActionEvent event) {
//            m_OverallBest = null;
//            for (int i = 0; i < m_ProblemDimension; i++) {
//                for (int j = 0; j < m_ProblemDimension; j++) {
//                    enviromentGraph[i][j] = new JButton();
//                    enviromentGraph[i][j].setPreferredSize(m_MinSize);
//                    enviromentGraph[i][j].setMinimumSize(m_MinSize);
//                    if (isBlackTile(i,j)) enviromentGraph[i][j].setBackground(Color.BLACK);
//                    else enviromentGraph[i][j].setBackground(Color.WHITE);
//                    enviromentJPanel.add(enviromentGraph[i][j]);
//                }
//            }
//        }
//    };
//
//    /** This action listener, called by the "Run/Restart" button, will init the problem and start the computation.
//     */
//    ActionListener printListener = new ActionListener() {
//        public void actionPerformed(ActionEvent event) {
//        String outfile ="";
//        try {
//            Robot       robot = new Robot();
//            Rectangle   area = mainFrame.getBounds();
//            BufferedImage   bufferedImage   = robot.createScreenCapture(area);
//            JFileChooser    fc              = new JFileChooser();
//            if (fc.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
//                //System.out.println("Name " + outfile);
//                  try {
//                    FileOutputStream fos = new FileOutputStream(fc.getSelectedFile().getAbsolutePath()+".jpeg");
//                    BufferedOutputStream bos = new BufferedOutputStream(fos);
//                    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
//                    encoder.encode(bufferedImage);
//                    bos.close();
//                  } catch (Exception eee) {}
//            }
//
//          } catch (AWTException ee) {
//            ee.printStackTrace();
//          }
//        }
//    };

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        this.m_OverallBest = null;

        // load the queenpics
        this.m_BlackQueen = null;
        this.m_WhiteQueen = null;
        try {
            BasicResourceLoader loader = BasicResourceLoader.instance();
            byte[] bytes = loader.getBytesFromResourceLocation("resources/images/QueenW.gif");
            Image image = Toolkit.getDefaultToolkit().createImage(bytes);
            this.m_WhiteQueen = new ImageIcon(image);
            bytes = loader.getBytesFromResourceLocation("resources/images/QueenB.gif");
            image = Toolkit.getDefaultToolkit().createImage(bytes);
            this.m_BlackQueen = new ImageIcon(image);
        } catch (java.lang.NullPointerException e) {
            System.err.println("Could not load queen pics! Please move the resources folder to the working directory.");
        }
        if (this.m_Show) this.initEnvironmentPanel();
    }

    /**
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        this.m_OverallBest = null;
        if (this.m_UseIntNotPermutation) {
            int[][] range = new int[this.m_ProblemDimension * 2][2];
            for (int i = 0; i < range.length; i++) {
                range[i][0] = 0;
                range[i][1] = this.m_ProblemDimension - 1;
            }
            ((InterfaceDataTypeInteger) this.m_IntArrayIndividual).setIntegerDataLength(2 * this.m_ProblemDimension);
            ((InterfaceDataTypeInteger) this.m_IntArrayIndividual).SetIntRange(range);
            AbstractOptimizationProblem.defaultInitPopulation(population, m_IntArrayIndividual, this);
        } else {
            ((InterfaceDataTypePermutation) this.m_Template).setPermutationDataLength(new int[]{this.m_ProblemDimension});
            AbstractOptimizationProblem.defaultInitPopulation(population, m_Template, this);
        }
    }
//    /** This method evaluates a given population and set the fitness values
//     * accordingly
//     * @param population    The population that is to be evaluated.
//     */
//    public void evaluate(Population population) {
//        AbstractEAIndividual    tmpIndy;
//
//        for (int i = 0; i < population.size(); i++) {
//            tmpIndy = (AbstractEAIndividual) population.get(i);
//            tmpIndy.resetConstraintViolation();
//            this.evaluate(tmpIndy);
//            population.incrFunctionCalls();
//        }
//    }

    /**
     * This method evaluate a single individual and sets the fitness values
     *
     * @param individual The individual that is to be evalutated
     */
    public void evaluate(AbstractEAIndividual individual) {
        double[] fitness = new double[1];
        int[] perm = null, intArray = null;

        if (this.m_UseIntNotPermutation) {
            intArray = ((InterfaceDataTypeInteger) individual).getIntegerData();
            for (int i = 0; i < intArray.length; i += 2) {
                for (int j = i + 2; j < intArray.length; j += 2) {
                    // check horizontally
                    if (intArray[i] == intArray[j])
                        fitness[0] += 1;
                    // check vertically
                    if (intArray[i + 1] == intArray[j + 1])
                        fitness[0] += 1;
                    // check for same position
                    if ((intArray[i] == intArray[j]) && (intArray[i + 1] == intArray[j + 1]))
                        fitness[0] += this.m_ProblemDimension;
                    // diagonal
                    if (Math.abs((intArray[j] - intArray[i])) == Math.abs((intArray[j + 1] - intArray[i + 1])))
                        fitness[0] += 1;
                }
            }
        } else {
            perm = ((InterfaceDataTypePermutation) individual).getPermutationData()[0];

            // since orthognal hits are already excluded simply check for diagonals
            for (int i = 0; i < perm.length; i++) {
                for (int j = i + 1; j < perm.length; j++) {
                    if (perm[j] - (j - i) == perm[i]) fitness[0] += 1;
                    if (perm[j] + (j - i) == perm[i]) fitness[0] += 1;
                }
            }
        }

        individual.SetFitness(fitness);

        if ((this.m_OverallBest == null) || (this.m_OverallBest.getFitness(0) > individual.getFitness(0))) {
            this.m_OverallBest = (AbstractEAIndividual) individual.clone();
            if (this.m_Show) {
                for (int i = 0; i < this.m_ProblemDimension; i++) {
                    for (int j = 0; j < this.m_ProblemDimension; j++) {
                        if ((!this.m_UseIntNotPermutation) && ((this.m_WhiteQueen != null) && (perm[i] == j))) {
                            if (this.isBlackTile(i, j)) this.enviromentGraph[i][j].setIcon(this.m_BlackQueen);
                            else this.enviromentGraph[i][j].setIcon(this.m_WhiteQueen);
                        } else {
                            this.enviromentGraph[i][j].setIcon(null);
                        }
                    }
                }
                if (this.m_UseIntNotPermutation) {
                    for (int i = 0; i < intArray.length; i += 2) {
                        if (this.isBlackTile(intArray[i], intArray[i + 1]))
                            this.enviromentGraph[intArray[i]][intArray[i + 1]].setIcon(this.m_BlackQueen);
                        else this.enviromentGraph[intArray[i]][intArray[i + 1]].setIcon(this.m_WhiteQueen);
                    }
                }
                this.mainFrame.validate();
                this.mainFrame.repaint();
            }
        }
    }

    /**
     * This method returns a string describing the optimization problem.
     *
     * @param opt The Optimizer that is used or had been used.
     * @return The description.
     */
    public String getStringRepresentationForProblem(InterfaceOptimizer opt) {
        String result = "";

        result += this.m_ProblemDimension + "-Queensproblem:\n";
        result += "Find a configuration of " + this.m_ProblemDimension + " queens on a ";
        result += this.m_ProblemDimension + "x" + this.m_ProblemDimension + " dimensional chess board such that they can't hit each other.\n";
        result += "Solution representation:\n";
        //result += this.m_Template.getSolutionRepresentationFor();
        return result;
    }

/**********************************************************************************************************************
 * These are for GUI
 */
    /**
     * This method allows the CommonJavaObjectEditorPanel to read the
     * name to the current object.
     *
     * @return The name.
     */
    public String getName() {
        return "Queens Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "Find a configuration of queens on the chess board such that they can't hit each other (orthogonal hits are already exclued by the coding style).";
    }

    /**
     * This method allows you to set the number of mulitruns that are to be performed,
     * necessary for stochastic optimizers to ensure reliable results.
     *
     * @param multiruns The number of multiruns that are to be performed
     */
    public void setProblemDimension(int multiruns) {
        this.m_ProblemDimension = multiruns;
    }

    public int getProblemDimension() {
        return this.m_ProblemDimension;
    }

    public String problemDimensionTipText() {
        return "Number of Queens and size of the chess board.";
    }

    /**
     * This method allows you to toggle path visualisation on and off.
     *
     * @param b True if the path is to be shown.
     */
    public void setShowPath(boolean b) {
        this.m_Show = b;
        if (this.m_Show) this.initEnvironmentPanel();
        else if (this.mainFrame != null) {
            this.mainFrame.dispose();
            this.mainFrame = null;
        }
    }

    public boolean getShowPath() {
        return this.m_Show;
    }

    public String showPathTipText() {
        return "Toggles the path visualisation.";
    }

    /**
     * This method allows you to toggle encoding toy.
     *
     * @param b True if the int[] is to be used.
     */
    public void setUseIntNotPermutation(boolean b) {
        this.m_UseIntNotPermutation = b;
    }

    public boolean getUseIntNotPermutation() {
        return this.m_UseIntNotPermutation;
    }

    public String useIntNotPermutationTipText() {
        return "Use the int[] instead of the permuation encoding.";
    }

    /**
     * This method allows you to choose the EA individual
     *
     * @param indy The EAIndividual type
     */
    public void setEAIndividual(InterfaceDataTypePermutation indy) {
        this.m_Template = (AbstractEAIndividual) indy;
    }

    public InterfaceDataTypePermutation getEAIndividual() {
        return (InterfaceDataTypePermutation) this.m_Template;
    }

    /**
     * This method allows you to choose the EA individual
     *
     * @param indy The EAIndividual type
     */
    public void setIntArrayIndividual(InterfaceDataTypeInteger indy) {
        this.m_IntArrayIndividual = (AbstractEAIndividual) indy;
    }

    public InterfaceDataTypeInteger getIntArrayIndividual() {
        return (InterfaceDataTypeInteger) this.m_IntArrayIndividual;
    }

    public String intArrayIndividualTipText() {
        return "The alternative int[] encoding.";
    }
}
