package eva2.server.go.gp;

import eva2.server.go.individuals.codings.gp.AbstractGPNode;
import eva2.server.go.problems.InterfaceProgramProblem;
import eva2.server.go.problems.PArtificialAnt;


/**
 * The traditional artificial ant if node taking two nodes as
 * argument. The first is executed if there is food ahead else
 * the second node is executed.
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 16.06.2003
 * Time: 13:41:32
 * To change this template use Options | File Templates.
 */
public class GPNodeFlowAASensor extends AbstractGPNode implements java.io.Serializable {

    public GPNodeFlowAASensor() {

    }

    public GPNodeFlowAASensor(GPNodeFlowAASensor node) {
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
        if (obj instanceof GPNodeFlowAASensor) {
            GPNodeFlowAASensor node = (GPNodeFlowAASensor) obj;
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
        return "If_Food_Ahead";
    }

    /**
     * This method allows you to clone the Nodes
     *
     * @return the clone
     */
    public Object clone() {
        return (Object) new GPNodeFlowAASensor(this);
    }

    /**
     * This method will return the current arity
     *
     * @return Arity.
     */
    public int getArity() {
        return 2;
    }

    /**
     * This method will evaluate a given node
     *
     * @param environment
     */
    public Object evaluate(InterfaceProgramProblem environment) {
        if (environment instanceof PArtificialAnt) {
            if (((Boolean) environment.getSensorValue("If_Food_Ahead")).booleanValue()) {
                return this.m_Nodes[0].evaluate(environment);
            } else {
                return this.m_Nodes[1].evaluate(environment);
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
