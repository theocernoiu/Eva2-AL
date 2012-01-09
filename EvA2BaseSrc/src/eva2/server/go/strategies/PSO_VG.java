package eva2.server.go.strategies;

import eva2.gui.GenericObjectEditor;
import eva2.server.go.problems.AbstractOptimizationProblem;
import eva2.server.go.problems.InterfaceOptimizationProblem;
import eva2.tools.SelectedTag;

/**
 * Created by IntelliJ IDEA.
 * User: andrei.lihu
 * Date: Feb 9, 2011
 * Time: 9:49:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PSO_VG extends ParticleSwarmOptimization {
    private static final long serialVersionUID = -149996122795669591L;

    public PSO_VG() {
        super();
        algType = new SelectedTag("Inertness");
        algType.setSelectedTag(0);
        m_Phi2 = 1.49445;
        setPhi1(0);
    }

    public PSO_VG(PSO_VG a) {
        super(a);
    }

    public Object clone() {
        return new PSO_VG(this);
    }

    public String globalInfo() {
        return "Pedersen's PSO-VG: An Inertial 'Social Only' Particle Swarm Optimization.";
    }

    public String getName() {
        return "PSO-VG_" + getTopology() + "_" + getTopologyRange() + "_" + getPhi2();
    }

    public String getStringRepresentation() {
        StringBuilder strB = new StringBuilder(200);
        strB.append("Social Only Particle Swarm Optimization:\nOptimization Problem: ");
        strB.append(this.m_Problem.getStringRepresentationForProblem(this));
        strB.append("\n");
        strB.append(this.m_Population.getStringRepresentation());
        return strB.toString();
    }

    public void SetProblem(InterfaceOptimizationProblem problem) {
        super.SetProblem(problem);
        if (problem instanceof AbstractOptimizationProblem) {
            ((AbstractOptimizationProblem) problem).informAboutOptimizer(this);
        }
    }

    public void setPhi1(double l) {
        m_Phi1 = 0;
    }

    public void setGOEShowProperties(Class<?> cls) {
        super.setGOEShowProperties(cls);
        GenericObjectEditor.setShowProperty(cls, "phi1", false);
        GenericObjectEditor.setShowProperty(cls, "algoType", false);
    }
}
