package eva2.server.go.problems.portfolio.objective;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import eva2.gui.GenericObjectEditor;
import eva2.gui.PropertyEditorProvider;
import eva2.server.go.problems.portfolio.InterfacePortfolioSelectionObjective;
import eva2.server.go.tools.AbstractObjectEditor;
import eva2.server.go.tools.GeneralGEOFaker;
import eva2.server.go.tools.GeneralGOEProperty;
import eva2.tools.BasicResourceLoader;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 07.04.2005
 * Time: 17:45:12
 * To change this template use File | Settings | File Templates.
 */
public class PropertyPortfolioSelectionObjectivesEditor extends JPanel implements PropertyEditor, java.beans.PropertyChangeListener {

    /**
     * Handles property change notification
     */
    private PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
    /**
     * The label for when we can't edit that type
     */
    private JLabel m_Label = new JLabel("Can't edit", SwingConstants.CENTER);
    /**
     * The FilePath that is to be edited
     */
    private PropertyPortfolioSelectionObjectives m_OptimizationTargets;

    /**
     * The gaphix stuff
     */
    private JComponent m_Editor;
    private JPanel m_TargetList;
    private JComponent[] m_Targets;
    private JCheckBox[] m_Selection;
    private JButton[] m_Delete;
    private JScrollPane m_ScrollTargets;
    private GeneralGOEProperty[] m_Editors;
    private GeneralGEOFaker m_Component;
    private PropertyChangeListener m_self;

    public PropertyPortfolioSelectionObjectivesEditor() {
        m_self = this;
    }

    /**
     * This method will init the CustomEditor Panel
     */
    private void initCustomEditor() {
        m_self = this;
        this.m_Editor = new JPanel();
        this.m_Editor.setPreferredSize(new Dimension(400, 200));
        this.m_Editor.setMinimumSize(new Dimension(400, 200));

        // init the editors
        this.initEditors();
        this.m_TargetList = new JPanel();
        this.updateTargetList();
        this.m_ScrollTargets = new JScrollPane(this.m_TargetList);

        this.m_Editor.setLayout(new BorderLayout());
        this.m_Editor.add(this.m_ScrollTargets, BorderLayout.CENTER);

        // the add button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        JButton addButton = new JButton("Add Opt. Target");
        addButton.addActionListener(addTarget);
        JButton addWeightAggButton = new JButton("Weight Aggregation");
        addWeightAggButton.addActionListener(this.addWeightAggregation);
        buttonPanel.add(addButton);
        buttonPanel.add(addWeightAggButton);
        this.m_Editor.add(buttonPanel, BorderLayout.SOUTH);

        // Some description would be nice
        JTextArea jt = new JTextArea();
        jt.setFont(new Font("SansSerif", Font.PLAIN, 12));
        jt.setEditable(false);
        jt.setLineWrap(true);
        jt.setWrapStyleWord(true);
        jt.setText("Choose and parameterize optimization objectives.");
        jt.setBackground(getBackground());
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Info"),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)
        ));
        jp.setLayout(new BorderLayout());
        jp.add(jt, BorderLayout.CENTER);
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        JButton help = new JButton("Help");
        help.setEnabled(false);
        p2.add(help, BorderLayout.NORTH);
        jp.add(p2, BorderLayout.EAST);
        GridBagConstraints gbConstraints = new GridBagConstraints();

        this.m_Editor.add(jp, BorderLayout.NORTH);

        this.updateEditor();
    }

    /**
     * This method will init the editors
     */
    private void initEditors() {
        InterfacePortfolioSelectionObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        this.m_Editors = new GeneralGOEProperty[list.length];
        for (int i = 0; i < list.length; i++) {
            this.m_Editors[i] = new GeneralGOEProperty();
            this.m_Editors[i].m_Name = list[i].getName();
            try {
                this.m_Editors[i].m_Value = list[i];
                this.m_Editors[i].m_Editor = PropertyEditorProvider.findEditor(this.m_Editors[i].m_Value.getClass());
                if (this.m_Editors[i].m_Editor == null)
                    this.m_Editors[i].m_Editor = PropertyEditorProvider.findEditor(InterfacePortfolioSelectionObjective.class);
                if (this.m_Editors[i].m_Editor instanceof GenericObjectEditor)
                    ((GenericObjectEditor) this.m_Editors[i].m_Editor).setClassType(InterfacePortfolioSelectionObjective.class);
                this.m_Editors[i].m_Editor.setValue(this.m_Editors[i].m_Value);
                this.m_Editors[i].m_Editor.addPropertyChangeListener(this);
                AbstractObjectEditor.findViewFor(this.m_Editors[i]);
                if (this.m_Editors[i].m_View != null) this.m_Editors[i].m_View.repaint();
            } catch (Exception e) {
                System.out.println("Darn can't read the value...");
            }
        }
    }

    /**
     * This method updates the server list
     */
    private void updateTargetList() {
        BasicResourceLoader loader = BasicResourceLoader.instance();
        byte[] bytes;
        InterfacePortfolioSelectionObjective[] list = this.m_OptimizationTargets.getSelectedTargets();

        this.m_TargetList.removeAll();
        this.m_TargetList.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        this.m_Targets = new JComponent[list.length];
        this.m_Selection = new JCheckBox[list.length];
        this.m_Delete = new JButton[list.length];
        String[] cups = new String[8];
        for (int i = 0; i < cups.length; i++) cups[i] = "" + (i + 1);
        // The head title
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 10;
        this.m_TargetList.add(new JLabel("Select"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.weightx = 10;
        this.m_TargetList.add(new JLabel("Target"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.REMAINDER;
        gbc.gridx = 2;
        gbc.weightx = 1;
        this.m_TargetList.add(new JLabel("Remove"), gbc);
        for (int i = 0; i < list.length; i++) {
            // the selector
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.weightx = 10;
            this.m_Selection[i] = new JCheckBox();
            this.m_Selection[i].setEnabled(true);
            this.m_TargetList.add(this.m_Selection[i], gbc);
            // the status indicator
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 1;
            gbc.weightx = 10;
            this.m_Targets[i] = this.m_Editors[i].m_View;
            this.m_TargetList.add(this.m_Targets[i], gbc);
            // The delete button
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.REMAINDER;
            gbc.gridx = 2;
            gbc.weightx = 1;
            bytes = loader.getBytesFromResourceLocation("resources/images/Sub24.gif");
            try {
                this.m_Delete[i] = new JButton("", new ImageIcon(Toolkit.getDefaultToolkit().createImage(bytes)));
            } catch (java.lang.NullPointerException e) {
                System.out.println("Could not find sub24 icon, please move resources folder to working directory!");
                this.m_Delete[i] = new JButton("Sub");
            }
            this.m_Delete[i].addActionListener(deleteTarget);
            this.m_TargetList.add(this.m_Delete[i], gbc);
        }
        this.m_TargetList.repaint();
        this.m_TargetList.validate();
        if (this.m_ScrollTargets != null) {
            this.m_ScrollTargets.validate();
            this.m_ScrollTargets.repaint();
        }
        if (this.m_Editor != null) {
            this.m_Editor.validate();
            this.m_Editor.repaint();
        }
    }

    /**
     * This action listener,...
     */
    ActionListener updateTargets = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateTargetList();
        }
    };

    /**
     * This action listener,...
     */
    ActionListener addTarget = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            m_OptimizationTargets.addTarget((InterfacePortfolioSelectionObjective) m_OptimizationTargets.getAvailableTargets()[0].clone());
            int l = m_OptimizationTargets.getSelectedTargets().length;
            GeneralGOEProperty[] newEdit = new GeneralGOEProperty[l];
            for (int i = 0; i < m_Editors.length; i++) {
                newEdit[i] = m_Editors[i];
            }
            InterfacePortfolioSelectionObjective[] list = m_OptimizationTargets.getSelectedTargets();
            l--;
            newEdit[l] = new GeneralGOEProperty();
            newEdit[l].m_Name = list[l].getName();
            try {
                newEdit[l].m_Value = list[l];
                newEdit[l].m_Editor = PropertyEditorProvider.findEditor(newEdit[l].m_Value.getClass());
                if (newEdit[l].m_Editor == null)
                    newEdit[l].m_Editor = PropertyEditorProvider.findEditor(InterfacePortfolioSelectionObjective.class);
                if (newEdit[l].m_Editor instanceof GenericObjectEditor)
                    ((GenericObjectEditor) newEdit[l].m_Editor).setClassType(InterfacePortfolioSelectionObjective.class);
                newEdit[l].m_Editor.setValue(newEdit[l].m_Value);
                newEdit[l].m_Editor.addPropertyChangeListener(m_self);
                AbstractObjectEditor.findViewFor(newEdit[l]);
                if (newEdit[l].m_View != null) newEdit[l].m_View.repaint();
            } catch (Exception e) {
                System.out.println("Darn can't read the value...");
            }
            m_Editors = newEdit;
            updateTargetList();
        }
    };
    ActionListener addWeightAggregation = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            // generate new weight aggregated objective from selected ones
            ObjectivePortfolioAggWeighted newObj = new ObjectivePortfolioAggWeighted();
            InterfacePortfolioSelectionObjective[] agg;
            ArrayList tmpList = new ArrayList();
            for (int i = 0; i < m_Selection.length; i++) {
                if (m_Selection[i].isSelected()) {
                    tmpList.add(m_OptimizationTargets.getSelectedTargets()[i]);
                }
            }
            if (tmpList.size() < 2) {
                JOptionPane.showMessageDialog(m_Editor, "At least two objectives need to be selected for weight aggregation!", "Adding weight aggregation objective", JOptionPane.ERROR_MESSAGE);
                return;
            }
            agg = new InterfacePortfolioSelectionObjective[tmpList.size()];
            for (int i = 0; i < tmpList.size(); i++) {
                agg[i] = (InterfacePortfolioSelectionObjective) tmpList.get(i);
            }
            newObj.getOptimizationTargets().setSelectedTargets(agg);
            // deleting selected objectives
            for (int i = m_Selection.length - 1; i >= 0; i--) {
                if (m_Selection[i].isSelected()) m_OptimizationTargets.removeTarget(i);
            }
            // adding the new weight aggregated objective
            m_OptimizationTargets.addTarget(newObj);
            initEditors();
            updateTargetList();
        }
    };

    /** This action listener...
     *
     */

    /**
     * This action listener,...
     */
    ActionListener deleteTarget = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            int l = m_OptimizationTargets.getSelectedTargets().length, j = 0;
            GeneralGOEProperty[] newEdit = new GeneralGOEProperty[l - 1];
            for (int i = 0; i < m_Delete.length; i++) {
                if (event.getSource().equals(m_Delete[i])) m_OptimizationTargets.removeTarget(i);
                else {
                    newEdit[j] = m_Editors[i];
                    j++;
                }
            }
            m_Editors = newEdit;
            updateTargetList();
        }
    };

    /**
     * The object may have changed update the editor.
     */
    private void updateEditor() {
        if (this.m_Editor != null) {
            this.m_TargetList.validate();
            this.m_TargetList.repaint();
            this.m_ScrollTargets.validate();
            this.m_ScrollTargets.repaint();
            this.m_Editor.validate();
            this.m_Editor.repaint();
        }
    }


    /**
     * This method will set the value of object that is to be edited.
     *
     * @param o an object that must be an array.
     */
    public void setValue(Object o) {
        if (o instanceof PropertyPortfolioSelectionObjectives) {
            this.m_OptimizationTargets = (PropertyPortfolioSelectionObjectives) o;
            this.updateEditor();
        }
    }

    /**
     * Returns the current object.
     *
     * @return the current object
     */
    public Object getValue() {
        return this.m_OptimizationTargets;
    }

    public String getJavaInitializationString() {
        return "TEST";
    }

    /**
     *
     */
    public String getAsText() {
        return null;
    }

    /**
     *
     */
    public void setAsText(String text) throws IllegalArgumentException {
        throw new IllegalArgumentException(text);
    }

    /**
     *
     */
    public String[] getTags() {
        return null;
    }

    /**
     * This is used to hook an action listener to the ok button
     *
     * @param a The action listener.
     */
    public void addOkListener(ActionListener a) {
        //m_OKButton.addActionListener(a);
    }

    /**
     * This is used to remove an action listener from the ok button
     *
     * @param a The action listener
     */
    public void removeOkListener(ActionListener a) {
        //m_OKButton.removeActionListener(a);
    }

    /**
     * Returns true since the Object can be shown
     *
     * @return true
     */
    public boolean isPaintable() {
        return true;
    }

    /**
     * Paints a representation of the current classifier.
     *
     * @param gfx the graphics context to use
     * @param box the area we are allowed to paint into
     */
    public void paintValue(Graphics gfx, Rectangle box) {
        FontMetrics fm = gfx.getFontMetrics();
        int vpad = (box.height - fm.getAscent()) / 2;
        String rep = "Portfolio Selection Objectives";
        gfx.drawString(rep, 2, fm.getHeight() + vpad - 3);
    }

    /**
     * Returns true because we do support a custom editor.
     *
     * @return true
     */
    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     * Returns the array editing component.
     *
     * @return a value of type 'java.awt.Component'
     */
    public Component getCustomEditor() {
        if (this.m_Component == null) {
            this.initCustomEditor();
            this.m_Component = new GeneralGEOFaker((PropertyEditor) this, (JPanel) this.m_Editor);
        }
        return this.m_Component;
    }

    /**
     * This method will udate the status of the object taking the values from all
     * supsequent editors and setting them to my object.
     */
    public void updateCenterComponent(PropertyChangeEvent evt) {
        //this.updateTargetList();
        this.updateEditor();
    }

    /**
     * ****************************** java.beans.PropertyChangeListener ************************
     */

    public void addPropertyChangeListener(PropertyChangeListener l) {
        m_Support.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        m_Support.removePropertyChangeListener(l);
    }

    /**
     * This will wait for the GenericObjectEditor to finish
     * editing an object.
     *
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Object newVal = evt.getNewValue();
        Object oldVal = evt.getOldValue();
        InterfacePortfolioSelectionObjective[] list = this.m_OptimizationTargets.getSelectedTargets();
        for (int i = 0; i < list.length; i++) {
            if (oldVal.equals(list[i])) {
                list[i] = (InterfacePortfolioSelectionObjective) newVal;
                this.m_Editors[i].m_Name = list[i].getName();
                try {
                    this.m_Editors[i].m_Value = list[i];
                    this.m_Editors[i].m_Editor = PropertyEditorProvider.findEditor(this.m_Editors[i].m_Value.getClass());
                    if (this.m_Editors[i].m_Editor == null)
                        this.m_Editors[i].m_Editor = PropertyEditorProvider.findEditor(InterfacePortfolioSelectionObjective.class);
                    if (this.m_Editors[i].m_Editor instanceof GenericObjectEditor)
                        ((GenericObjectEditor) this.m_Editors[i].m_Editor).setClassType(InterfacePortfolioSelectionObjective.class);
                    this.m_Editors[i].m_Editor.setValue(this.m_Editors[i].m_Value);
                    this.m_Editors[i].m_Editor.addPropertyChangeListener(this);
                    AbstractObjectEditor.findViewFor(this.m_Editors[i]);
                    if (this.m_Editors[i].m_View != null) this.m_Editors[i].m_View.repaint();
                } catch (Exception e) {
                    System.out.println("Darn can't read the value...");
                }
                this.m_Targets[i] = this.m_Editors[i].m_View;
            }
        }
        //this.m_OptimizationTargets.setSelectedTargets(list);
        this.updateCenterComponent(evt); // Let our panel update before guys downstream
        m_Support.firePropertyChange("", m_OptimizationTargets, m_OptimizationTargets);
    }
}