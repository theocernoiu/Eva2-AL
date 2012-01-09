package eva2.server.go.problems;

import eva2.gui.JEFrame;
import eva2.server.go.gp.GPNodeFlowAAGreedySensor;
import eva2.server.go.gp.GPNodeFlowAASensor;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GPIndividualProgramData;
import eva2.server.go.individuals.InterfaceDataTypeProgram;
import eva2.server.go.individuals.codings.gp.GPArea;
import eva2.server.go.individuals.codings.gp.GPNodeFlowExec2;
import eva2.server.go.individuals.codings.gp.GPNodeFlowExec3;
import eva2.server.go.individuals.codings.gp.GPNodeOutput;
import eva2.server.go.individuals.codings.gp.InterfaceProgram;
import eva2.server.go.populations.Population;
import eva2.server.go.strategies.InterfaceOptimizer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 17.06.2003
 * Time: 18:33:05
 * To change this template use Options | File Templates.
 */
public class PArtificialAnt extends AbstractOptimizationProblem implements InterfaceProgramProblem, java.io.Serializable {

    private boolean[][] m_Enviroment;
    private int m_MaximumStepsAllowed = 400;
    private GPArea m_GPArea = new GPArea();
    protected AbstractEAIndividual m_OverallBest = null;

    // tmp Values for evaluating the individual
    transient private boolean[][] currentEnviroment;
    transient private int[] currentIndividualPosition;
    transient private int currentStep;
    transient private int y = 0, x = 0, currentFoodCount = 0;

    // This is graphics stuff
    transient private boolean m_Show = false, m_Print = false;
    transient private JEFrame mainFrame;
    transient private JPanel mainPanel;
    transient private JPanel buttonPanel;
    transient private JPanel enviromentJPanel;
    transient private JButton[][] enviromentGraph;
    transient private JTextArea bestText;
    transient private JScrollPane bestTextScroll;

    public PArtificialAnt() {
        this.m_Template = new GPIndividualProgramData();
        this.compileArea();
        this.initProblem();
    }

    public PArtificialAnt(PArtificialAnt b) {
        //AbstractOptimizationProblem
        if (b.m_Template != null)
            this.m_Template = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_Template).clone();
        //F1Problem
        if (b.m_OverallBest != null)
            this.m_OverallBest = (AbstractEAIndividual) ((AbstractEAIndividual) b.m_OverallBest).clone();
        if (b.m_GPArea != null)
            this.m_GPArea = (GPArea) b.m_GPArea.clone();
        if (b.m_Enviroment != null) {
            this.m_Enviroment = new boolean[b.m_Enviroment.length][b.m_Enviroment[0].length];
            for (int i = 0; i < this.m_Enviroment.length; i++) {
                for (int j = 0; j < this.m_Enviroment[i].length; j++) {
                    this.m_Enviroment[i][j] = b.m_Enviroment[i][j];

                }
            }
        }
        this.m_MaximumStepsAllowed = b.m_MaximumStepsAllowed;
    }

    /**
     * This method returns a deep clone of the problem.
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new PArtificialAnt(this);
    }

    /**
     * This method inits the Problem to log multiruns
     */
    public void initProblem() {
        this.m_OverallBest = null;
        this.m_Enviroment = new boolean[32][32];
        for (int i = 0; i < this.m_Enviroment.length; i++) {
            for (int j = 0; j < this.m_Enviroment[i].length; j++) {
                this.m_Enviroment[i][j] = false;
            }
        }
        this.m_Enviroment[0][0] = true;
        this.m_Enviroment[0][1] = true;
        this.m_Enviroment[0][2] = true;
        this.m_Enviroment[0][3] = true;
        this.m_Enviroment[1][3] = true;
        this.m_Enviroment[2][3] = true;
        this.m_Enviroment[3][3] = true;
        this.m_Enviroment[4][3] = true;
        this.m_Enviroment[5][3] = true;
        this.m_Enviroment[5][4] = true;
        this.m_Enviroment[5][5] = true;
        this.m_Enviroment[5][6] = true;
        this.m_Enviroment[5][8] = true;
        this.m_Enviroment[5][9] = true;
        this.m_Enviroment[5][10] = true;
        this.m_Enviroment[5][11] = true;
        this.m_Enviroment[5][12] = true;
        this.m_Enviroment[6][12] = true;
        this.m_Enviroment[7][12] = true;
        this.m_Enviroment[8][12] = true;
        this.m_Enviroment[9][12] = true;
        this.m_Enviroment[10][12] = true;
        this.m_Enviroment[12][12] = true;
        this.m_Enviroment[13][12] = true;
        this.m_Enviroment[14][12] = true;
        this.m_Enviroment[15][12] = true;
        this.m_Enviroment[18][12] = true;
        this.m_Enviroment[19][12] = true;
        this.m_Enviroment[20][12] = true;
        this.m_Enviroment[21][12] = true;
        this.m_Enviroment[22][12] = true;
        this.m_Enviroment[23][12] = true;
        this.m_Enviroment[24][11] = true;
        this.m_Enviroment[24][10] = true;
        this.m_Enviroment[24][9] = true;
        this.m_Enviroment[24][8] = true;
        this.m_Enviroment[24][7] = true;
        this.m_Enviroment[24][4] = true;
        this.m_Enviroment[24][3] = true;
        this.m_Enviroment[25][1] = true;
        this.m_Enviroment[26][1] = true;
        this.m_Enviroment[27][1] = true;
        this.m_Enviroment[28][1] = true;
        this.m_Enviroment[30][2] = true;
        this.m_Enviroment[30][3] = true;
        this.m_Enviroment[30][4] = true;
        this.m_Enviroment[30][5] = true;
        this.m_Enviroment[29][7] = true;
        this.m_Enviroment[28][7] = true;
        this.m_Enviroment[27][8] = true;
        this.m_Enviroment[27][9] = true;
        this.m_Enviroment[27][10] = true;
        this.m_Enviroment[27][11] = true;
        this.m_Enviroment[27][12] = true;
        this.m_Enviroment[27][13] = true;
        this.m_Enviroment[27][14] = true;
        this.m_Enviroment[26][16] = true;
        this.m_Enviroment[25][16] = true;
        this.m_Enviroment[24][16] = true;
        this.m_Enviroment[21][16] = true;
        this.m_Enviroment[19][16] = true;
        this.m_Enviroment[18][16] = true;
        this.m_Enviroment[17][16] = true;
        this.m_Enviroment[16][17] = true;
        this.m_Enviroment[15][20] = true;
        this.m_Enviroment[14][20] = true;
        this.m_Enviroment[11][20] = true;
        this.m_Enviroment[10][20] = true;
        this.m_Enviroment[9][20] = true;
        this.m_Enviroment[8][20] = true;
        this.m_Enviroment[5][21] = true;
        this.m_Enviroment[5][22] = true;
        this.m_Enviroment[4][24] = true;
        this.m_Enviroment[3][24] = true;
        this.m_Enviroment[2][25] = true;
        this.m_Enviroment[2][26] = true;
        this.m_Enviroment[2][27] = true;
        this.m_Enviroment[3][29] = true;
        this.m_Enviroment[4][29] = true;
        this.m_Enviroment[6][29] = true;
        this.m_Enviroment[9][29] = true;
        this.m_Enviroment[12][29] = true;
        this.m_Enviroment[14][28] = true;
        this.m_Enviroment[14][27] = true;
        this.m_Enviroment[14][26] = true;
        this.m_Enviroment[15][23] = true;
        this.m_Enviroment[18][24] = true;
        this.m_Enviroment[19][27] = true;
        this.m_Enviroment[22][26] = true;
        this.m_Enviroment[23][23] = true;

        if (this.m_Show) this.initEnvironmentPanel();
    }

    /**
     * This method compiles the area
     */
    private void compileArea() {
        this.m_GPArea = new GPArea();
        this.m_GPArea.add2CompleteList(new GPNodeFlowAASensor());
        this.m_GPArea.add2CompleteList(new GPNodeFlowAAGreedySensor(), false);
        this.m_GPArea.add2CompleteList(new GPNodeFlowExec2());
        this.m_GPArea.add2CompleteList(new GPNodeFlowExec3());
        this.m_GPArea.add2CompleteList(new GPNodeOutput("Turn_Left"));
        this.m_GPArea.add2CompleteList(new GPNodeOutput("Turn_Right"));
        this.m_GPArea.add2CompleteList(new GPNodeOutput("Move_Ahead"));
        this.m_GPArea.compileReducedList();
    }

    /**
     * This method init the enviroment panel if necessary.
     */
    private void initEnvironmentPanel() {
        if (this.mainFrame == null) {
            mainFrame = new JEFrame("Artificial Ant");
            mainFrame.setSize(100, 100);
            this.mainPanel = new JPanel();
            this.buttonPanel = new JPanel();
            this.enviromentJPanel = new JPanel();
            this.bestText = new JTextArea();
            this.bestTextScroll = new JScrollPane(this.bestText);
            this.bestTextScroll.setMinimumSize(new Dimension(50, 50));
            this.bestTextScroll.setPreferredSize(new Dimension(50, 50));
            this.mainPanel.setLayout(new BorderLayout());
            this.buttonPanel.setLayout(new BorderLayout());
            JButton clearButton = new JButton("Clear Result");
            clearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    m_OverallBest = null;
                    bestText.setText("");
                    for (int i = 0; i < m_Enviroment.length; i++) {
                        for (int j = 0; j < m_Enviroment[i].length; j++) {
                            if (m_Enviroment[i][j]) enviromentGraph[i][j].setBackground(Color.BLACK);
                            else enviromentGraph[i][j].setBackground(Color.WHITE);
                        }
                    }
                }
            });
//            JButton printButton = new JButton("Print");
//            printButton.addActionListener(this.printListener);
            this.buttonPanel.add(clearButton, BorderLayout.NORTH);
            this.buttonPanel.add(bestTextScroll, BorderLayout.CENTER);
//            this.buttonPanel.add(printButton, BorderLayout.SOUTH);
            this.enviromentJPanel.setLayout(new GridLayout(this.m_Enviroment.length, this.m_Enviroment[0].length));
            this.enviromentGraph = new JButton[this.m_Enviroment.length][this.m_Enviroment[0].length];
            for (int i = 0; i < this.m_Enviroment.length; i++) {
                for (int j = 0; j < this.m_Enviroment[i].length; j++) {
                    this.enviromentGraph[i][j] = new JButton();
                    this.enviromentGraph[i][j].setPreferredSize(new Dimension(15, 15));
                    if (this.m_Enviroment[i][j]) this.enviromentGraph[i][j].setBackground(Color.BLACK);
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

//    /** This action listener, called by the "Run/Restart" button, will init the problem and start the computation.
//     */
//    ActionListener clearListener = new ActionListener() {
//        public void actionPerformed(ActionEvent event) {
//            m_OverallBest = null;
//            bestText.setText("");
//            for (int i = 0; i < m_Enviroment.length; i++) {
//                for (int j = 0; j < m_Enviroment[i].length; j++) {
//                    if (m_Enviroment[i][j]) enviromentGraph[i][j].setBackground(Color.BLACK);
//                    else enviromentGraph[i][j].setBackground(Color.WHITE);
//                }
//           }
//        }
//    };

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
     * This method inits a given population
     *
     * @param population The populations that is to be inited
     */
    public void initPopulation(Population population) {
        this.initProblem();

        GPArea tmpArea[] = new GPArea[1];
        tmpArea[0] = this.m_GPArea;
        ((InterfaceDataTypeProgram) this.m_Template).setProgramDataLength(1);
        ((InterfaceDataTypeProgram) this.m_Template).SetFunctionArea(tmpArea);

        AbstractOptimizationProblem.defaultInitPopulation(population, m_Template, this);
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
        InterfaceProgram program;
        double fitness = 0, tmpValue;
        InterfaceDataTypeProgram tmpIndy;
        int maxFitness = 0;

        tmpIndy = (InterfaceDataTypeProgram) individual;
        program = tmpIndy.getProgramData()[0];
        //System.out.println("Programm: " + program.getSolutionRepresentationFor());
        this.currentEnviroment = new boolean[this.m_Enviroment.length][this.m_Enviroment[0].length];
        for (int i = 0; i < this.m_Enviroment.length; i++) {
            for (int j = 0; j < this.m_Enviroment[i].length; j++) {
                this.currentEnviroment[i][j] = this.m_Enviroment[i][j];
                if (this.m_Enviroment[i][j]) maxFitness++;
            }
        }

        this.currentFoodCount = 1;
        this.currentStep = 0;
        this.currentIndividualPosition = new int[3];
        this.currentIndividualPosition[0] = 0;
        this.currentIndividualPosition[1] = 0;
        this.currentIndividualPosition[2] = 1;
        this.x = this.m_Enviroment.length;
        this.y = this.m_Enviroment[0].length;
        while (this.currentStep < this.m_MaximumStepsAllowed) {
            //A individual.evaluate() w�rde in diesem Zusammenhang besser klingen
            program.evaluate(this);
        }
        fitness = maxFitness - this.currentFoodCount;
        // set the fitness of the individual
        individual.SetFitness(0, fitness);
        //System.out.println("Fitness: " + fitness);
        if ((this.m_OverallBest == null) || (this.m_OverallBest.getFitness(0) > individual.getFitness(0))) {
            this.m_OverallBest = (AbstractEAIndividual) individual.clone();
            if (this.m_Show) {
                this.bestText.setText("Result: " + this.m_OverallBest.getStringRepresentation());
                this.m_Print = true;
                for (int i = 0; i < this.m_Enviroment.length; i++) {
                    for (int j = 0; j < this.m_Enviroment[i].length; j++) {
                        if (this.m_Enviroment[i][j]) this.enviromentGraph[i][j].setBackground(Color.BLACK);
                        else this.enviromentGraph[i][j].setBackground(Color.WHITE);
                    }
                }
                this.evaluate(this.m_OverallBest);
                this.m_Print = false;
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

        result += "Artifical Ant Problem:\n";
        result += "The Santa-Fee trail is to be solved within " + this.m_MaximumStepsAllowed + " steps.";
        return result;
    }

    /** These methods are for the InterfaceProgramProblem and allow the program to acess to an
     * artificial environment, e.g. the Artifical Ant World or the x values for Symoblic Regression.
     */
    /**
     * This method allows a GP program to sense the environment, e.g.
     * input values, current time etc
     *
     * @param sensor The identifier for the sensor.
     * @return Sensor value
     */
    public Object getSensorValue(String sensor) {
        int tmpX, tmpY;

        if (this.currentIndividualPosition == null) return new Boolean(false);
        if (sensor == "If_Food_Ahead") {
            //System.out.println("Sensing");
            tmpX = this.currentIndividualPosition[0];
            tmpY = this.currentIndividualPosition[1];
            if (this.currentIndividualPosition[2] % 4 == 0) {
                // look north
                tmpY--;
                if (tmpY < 0) tmpY = this.y - 1;
            }
            if (this.currentIndividualPosition[2] % 4 == 1) {
                // look east
                tmpX++;
                if (tmpX > this.x - 1) tmpX = 0;
            }
            if (this.currentIndividualPosition[2] % 4 == 2) {
                // look south
                tmpY++;
                if (tmpY > this.y - 1) tmpY = 0;
            }
            if (this.currentIndividualPosition[2] % 4 == 3) {
                tmpX--;
                // look west
                if (tmpX < 0) tmpX = this.x - 1;
            }
            return new Boolean(this.currentEnviroment[tmpX][tmpY]);
        }
        return new Boolean(false);
    }

    /**
     * This method allows a GP program to act in the environment
     *
     * @param actuator  The identifier for the actuator.
     * @param parameter The actuator parameter.
     */
    public void setActuatorValue(String actuator, Object parameter) {
        if (this.currentIndividualPosition == null) return;
        if (this.currentStep > this.m_MaximumStepsAllowed) return;
        this.currentStep++;
        if (actuator.compareTo("Turn_Right") == 0) {
            //System.out.println("Turning right");
            this.currentIndividualPosition[2]++;
        } else if (actuator.compareTo("Turn_Left") == 0) {
            //System.out.println("Turining left");
            this.currentIndividualPosition[2]--;
        } else if (actuator.compareTo("Move_Ahead") == 0) {
            //System.out.println("Moving");
            // preform the movement
            if (this.currentIndividualPosition[2] % 4 == 0) this.currentIndividualPosition[1]--;
            if (this.currentIndividualPosition[2] % 4 == 1) this.currentIndividualPosition[0]++;
            if (this.currentIndividualPosition[2] % 4 == 2) this.currentIndividualPosition[1]++;
            if (this.currentIndividualPosition[2] % 4 == 3) this.currentIndividualPosition[0]--;
            // map the torus
            if (this.currentIndividualPosition[0] == -1) this.currentIndividualPosition[0] = this.x - 1;
            if (this.currentIndividualPosition[0] == this.x) this.currentIndividualPosition[0] = 0;
            if (this.currentIndividualPosition[1] == -1) this.currentIndividualPosition[1] = this.y - 1;
            if (this.currentIndividualPosition[1] == this.y) this.currentIndividualPosition[1] = 0;
            // check for food
            //System.out.println("position x:" + this.currentIndividualPosition[0] + " y:" + this.currentIndividualPosition[1] + "("+this.x+","+this.y+")");
            if (this.currentEnviroment[this.currentIndividualPosition[0]][this.currentIndividualPosition[1]]) {
                this.currentFoodCount++;
                //System.out.println("Food found");
                // of course the ant consumes the food
                this.currentEnviroment[this.currentIndividualPosition[0]][this.currentIndividualPosition[1]] = false;
                if (this.m_Print)
                    this.enviromentGraph[this.currentIndividualPosition[0]][this.currentIndividualPosition[1]].setBackground(Color.red);
            } else {
                if ((this.m_Print) && (!this.currentEnviroment[this.currentIndividualPosition[0]][this.currentIndividualPosition[1]])) {
                    this.enviromentGraph[this.currentIndividualPosition[0]][this.currentIndividualPosition[1]].setBackground(new Color(200, ((int) 254 - ((250 * this.currentStep) / this.m_MaximumStepsAllowed)), 200));
                }
            }
        }
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
        return "Artificial Ant Problem";
    }

    /**
     * This method returns a global info string
     *
     * @return description
     */
    public String globalInfo() {
        return "The task of the ant is to find all pieces of food";
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
     * This method allows you to set how many steps a ant is allowed to perform.
     *
     * @param i Number of maximal steps.
     */
    public void setAntSteps(int i) {
        this.m_MaximumStepsAllowed = i;
    }

    public int getAntSteps() {
        return this.m_MaximumStepsAllowed;
    }

    public String antStepsTipText() {
        return "The number of steps an ant is allowed to perform (400).";
    }

    /**
     * This method allows you to toggle the use of the elements in the GPArea.
     *
     * @param i Number of maximal steps.
     */
    public void setArea(GPArea i) {
        this.m_GPArea = i;
        GPArea tmpArea[] = new GPArea[1];
        tmpArea[0] = this.m_GPArea;
        ((InterfaceDataTypeProgram) this.m_Template).SetFunctionArea(tmpArea);
    }

    public GPArea getArea() {
        return this.m_GPArea;
    }

    public String areaTipText() {
        return "Select elements from the available area.";
    }

    /**
     * This method allows you to choose the EA individual
     *
     * @param indy The EAIndividual type
     */
    public void setGPIndividual(InterfaceDataTypeProgram indy) {
        this.m_Template = (AbstractEAIndividual) indy;
    }

    public InterfaceDataTypeProgram getGPIndividual() {
        return (InterfaceDataTypeProgram) this.m_Template;
    }
}