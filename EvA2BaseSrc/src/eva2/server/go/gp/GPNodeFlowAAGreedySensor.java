package eva2.server.go.gp;

import eva2.server.go.individuals.codings.gp.AbstractGPNode;
import eva2.server.go.problems.InterfaceProgramProblem;
import eva2.server.go.problems.PArtificialAnt;

/**
 * This is a greedy if node for the artifical ant problem, as
 * implemented by one of the students, which uses the sensor as
 * input move ahead if true and uses the one and only node as
 * alternative action. And it works fine.
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 27.06.2003
 * Time: 14:35:54
 * To change this template use Options | File Templates.
 */
public class GPNodeFlowAAGreedySensor extends AbstractGPNode implements java.io.Serializable {

    public GPNodeFlowAAGreedySensor() {

    }

    public GPNodeFlowAAGreedySensor(GPNodeFlowAAGreedySensor node) {
        this.m_Depth = node.m_Depth;
        this.m_Parent = node.m_Parent;
        this.m_Nodes = new AbstractGPNode[node.m_Nodes.length];
        for (int i = 0; i < node.m_Nodes.length; i++) this.m_Nodes[i] = (AbstractGPNode) node.m_Nodes[i].clone();
    }

    /**
     * This method allows you to determine wehter or not two subtrees
     * are actually the same.
     *
     * @param obj The other subtree.
     * @return boolean if equal true else false.
     */
    public boolean equals(Object obj) {
        if (obj instanceof GPNodeFlowAAGreedySensor) {
            GPNodeFlowAAGreedySensor node = (GPNodeFlowAAGreedySensor) obj;
            if (this.m_Nodes.length != node.m_Nodes.length) return false;
            for (int i = 0; i < this.m_Nodes.length; i++) {
                if (!this.m_Nodes[i].equals(node.m_Nodes[i])) return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method will be used to identify the node in the GPAreaEditor
     *
     * @return The name.
     */
    public String getName() {
        return "If_Food_Ahead=>MoveAhead";
    }

    /**
     * This method allows you to clone the Nodes
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new GPNodeFlowAAGreedySensor(this);
    }

    /**
     * This method will return the current arity
     *
     * @return Arity.
     */
    public int getArity() {
        return 1;
    }

    /**
     * This method will evaluate a given node
     *
     * @param environment
     */
    public Object evaluate(InterfaceProgramProblem environment) {
        if (environment instanceof PArtificialAnt) {
            if (((Boolean) environment.getSensorValue("If_Food_Ahead")).booleanValue()) {
                environment.setActuatorValue("Move_Ahead", null);
                return null;
            } else {
                return this.m_Nodes[0].evaluate(environment);
            }
        } else {
            Object[] result = new Object[this.m_Nodes.length];
            for (int i = 0; i < this.m_Nodes.length; i++) {
                result[i] = this.m_Nodes[i].evaluate(environment);
            }
            return result;
        }
    }

    @Override
    public String getOpIdentifier() {
        return "IfFoodAhead";
    }

//    /** This method returns a string representation
//     * @return string
//     */
//    public String getStringRepresentation() {
//        String result = "IfFoodAhead( ";
//        for (int i = 0; i < this.m_Nodes.length; i++) result += this.m_Nodes[i].getStringRepresentation() +" ";
//        result += ")";
//        return result;
//    }
}
