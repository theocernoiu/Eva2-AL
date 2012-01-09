package eva2.server.go.problems.portfolio.objective;

import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionObjective;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 07.04.2005
 * Time: 17:41:01
 * To change this template use File | Settings | File Templates.
 */
public class PropertyPortfolioSelectionObjectives implements java.io.Serializable {

    public InterfacePortfolioSelectionObjective[] m_AvailableTargets;
    public InterfacePortfolioSelectionObjective[] m_SelectedTargets;

    public PropertyPortfolioSelectionObjectives(InterfacePortfolioSelectionObjective[] d) {
        this.m_AvailableTargets = d;
        this.m_SelectedTargets = null;
    }

    public PropertyPortfolioSelectionObjectives(PropertyPortfolioSelectionObjectives d) {
        this.m_AvailableTargets = new InterfacePortfolioSelectionObjective[d.m_AvailableTargets.length];
        for (int i = 0; i < this.m_AvailableTargets.length; i++) {
            this.m_AvailableTargets[i] = (InterfacePortfolioSelectionObjective) d.m_AvailableTargets[i].clone();
        }
        this.m_SelectedTargets = new InterfacePortfolioSelectionObjective[d.m_SelectedTargets.length];
        for (int i = 0; i < this.m_SelectedTargets.length; i++) {
            this.m_SelectedTargets[i] = (InterfacePortfolioSelectionObjective) d.m_SelectedTargets[i].clone();
        }
    }

    public Object clone() {
        return (Object) new PropertyPortfolioSelectionObjectives(this);
    }

    /**
     * This method will allow you to set the value of the InterfaceOptimizationTarget array
     *
     * @param d The InterfaceOptimizationTarget[]
     */
    public void setSelectedTargets(InterfacePortfolioSelectionObjective[] d) {
        this.m_SelectedTargets = d;
    }

    /**
     * This method will return the InterfaceOptimizationTarget array
     *
     * @return The InterfaceOptimizationTarget[].
     */
    public InterfacePortfolioSelectionObjective[] getSelectedTargets() {
        return this.m_SelectedTargets;
    }

    /**
     * This method will return the InterfaceOptimizationTarget array
     *
     * @return The InterfaceOptimizationTarget[].
     */
    public InterfacePortfolioSelectionObjective[] getAvailableTargets() {
        return this.m_AvailableTargets;
    }

    /**
     * This method allows you to remove a Target from the list
     *
     * @param index The index of the target to be removed.
     */
    public void removeTarget(int index) {
        if ((index < 0) || (index >= this.m_SelectedTargets.length)) return;

        InterfacePortfolioSelectionObjective[] newList = new InterfacePortfolioSelectionObjective[this.m_SelectedTargets.length - 1];
        int j = 0;
        for (int i = 0; i < this.m_SelectedTargets.length; i++) {
            if (index != i) {
                newList[j] = this.m_SelectedTargets[i];
                j++;
            }
        }
        this.m_SelectedTargets = newList;
    }

    /**
     * This method allows you to add a new target to the list
     *
     * @param optTarget
     */
    public void addTarget(InterfacePortfolioSelectionObjective optTarget) {
        InterfacePortfolioSelectionObjective[] newList = new InterfacePortfolioSelectionObjective[this.m_SelectedTargets.length + 1];
        for (int i = 0; i < this.m_SelectedTargets.length; i++) {
            newList[i] = this.m_SelectedTargets[i];
        }
        newList[this.m_SelectedTargets.length] = optTarget;
        this.m_SelectedTargets = newList;
    }
}