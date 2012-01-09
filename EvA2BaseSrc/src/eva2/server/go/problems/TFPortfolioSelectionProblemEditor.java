package eva2.server.go.problems;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import eva2.gui.GenericObjectEditor;
import eva2.gui.PropertyEditorProvider;
import eva2.gui.PropertyPanel;
import eva2.gui.PropertySheetPanel;
import eva2.server.go.tools.AbstractObjectEditor;
import eva2.server.go.tools.GeneralGOEProperty;
import eva2.server.go.tools.GeneralGenericObjectEditorPanel;


/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 21.12.2004
 * Time: 13:29:45
 * To change this template use File | Settings | File Templates.
 */
public class TFPortfolioSelectionProblemEditor extends AbstractObjectEditor {

//    JPanel      m_StrategiesPanel, m_FriggelDings;

    /**
     * ****************************** java.beans.PropertyChangeListener ************************
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        m_Support.addPropertyChangeListener(l);
        if (this.m_EditorComponent != null) this.m_EditorComponent.addPropertyChangeListener(l);
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
        this.updateCenterComponent(evt); // Let our panel update before guys downstream
        m_Support.firePropertyChange("", m_Backup, m_Object);
    }
    /********************************* PropertyEditor *************************/
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
        if (m_Object != null) {
            String rep = m_Object.getClass().getName();
            rep = "Portfolio Selection Problem";
            int dotPos = rep.lastIndexOf('.');
            if (dotPos != -1) rep = rep.substring(dotPos + 1);
            FontMetrics fm = gfx.getFontMetrics();
            int vpad = (box.height - fm.getHeight()) / 2;
            gfx.drawString(rep, 2, fm.getHeight() + vpad - 2);
        } else {
        }
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
        if (m_EditorComponent == null) {
            m_EditorComponent = new GeneralGenericObjectEditorPanel(this);
        }
        return m_EditorComponent;
    }

    public String getAsText() {
        return null;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        throw new IllegalArgumentException(text);
    }

    public String getJavaInitializationString() {
        return "new " + m_Object.getClass().getName() + "()";
    }

    public String[] getTags() {
        return null;
    }

    /********************************* AbstractObjectEditor *************************/
    /**
     * This method will make a back up of the current
     * object value
     */
    public void makeBackup() {
        //@todo
    }

    /**
     * This method will use the backup to undo the
     * last action.
     */
    public void undoBackup() {
        //@todo
    }

    /**
     * This method allows you to set the current value
     *
     * @param obj The new value
     */
    public void setValue(Object obj) {
        if (obj instanceof TFPortfolioSelectionProblem) {
            this.m_Object = (TFPortfolioSelectionProblem) obj;
        } else {
//            System.out.println("No Genetic Algorithm!");
            this.firePropertyChange("Object Type Changed", this.m_Object, obj);
        }
    }

    /**
     * This method returns the class type
     *
     * @return Class
     */
    public Class getClassType() {
        return this.m_Object.getClass();
    }

    /** This method gets the classes from properties
     * @return Vector
     */
//    public ArrayList<String> getClassesFromProperties() {
//        return GenericObjectEditor.getClassesFromProperties("eva2.server.go.OptimizationProblems.InterfaceOptimizationProblem");
//    }


    /**
     * This method return the global info on the current
     * object.
     *
     * @return The global info.
     */
    public String getGlobalInfo() {
        return ((TFPortfolioSelectionProblem) this.m_Object).globalInfo();
    }

    /**
     * This method returns the central editing panel for the current
     * object.
     *
     * @return The center component.
     */
    public JComponent getCenterComponent() {
        JPanel result = new JPanel();
        JPanel tmpPanel;
        JLabel tmpLabel;
        PropertyDescriptor m_Properties[];
        MethodDescriptor m_Methods[];
        GeneralGOEProperty editor;
        GridBagLayout gbLayout = new GridBagLayout();
        GridBagConstraints gbConst = new GridBagConstraints();
        try {
            BeanInfo bi = Introspector.getBeanInfo(this.m_Object.getClass());
            m_Properties = bi.getPropertyDescriptors();
            m_Methods = bi.getMethodDescriptors();
        } catch (IntrospectionException ex) {
            System.out.println("PropertySheetPanel: Couldn't introspect");
            return result;
        }
        // first sort the stuff
        JPanel centerPanel;
        centerPanel = new JPanel();
        centerPanel.setLayout(gbLayout);

        // The general setting panel
        JPanel generalSettings = new JPanel();
        generalSettings.setLayout(new GridLayout(1, 2));
        editor = this.getEditorFor("EAIndividual", m_Properties, m_Methods, this.m_Object);
        editor.m_Editor.removePropertyChangeListener(this);
        editor.m_Editor.addPropertyChangeListener(this);
        editor.m_View.repaint();
        this.m_Editors.put("EA Representation", editor);
        tmpLabel = new JLabel("EA Representation:");
        tmpLabel.setToolTipText(editor.m_TipText);
        generalSettings.add(tmpLabel);
        editor.m_View.setToolTipText(editor.m_TipText);
        editor.m_ViewWrapper = new JPanel();
        editor.m_ViewWrapper.setLayout(new BorderLayout());
        editor.m_ViewWrapper.add(editor.m_View, BorderLayout.CENTER);
        editor.m_ViewWrapper.setToolTipText(editor.m_TipText);
        generalSettings.add(editor.m_ViewWrapper);

        // The options panel
        JPanel generalOptions = new JPanel();
        generalOptions.setLayout(new GridLayout(2, 2));
        JRadioButton rbInit = new JRadioButton("Init Cardinality");
        rbInit.setSelected(((TFPortfolioSelectionProblem) m_Object).getUseCardInit());
        rbInit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected())
                    ((TFPortfolioSelectionProblem) m_Object).setUseCardInit(true);
                else ((TFPortfolioSelectionProblem) m_Object).setUseCardInit(false);
                PropertyChangeEvent et = new PropertyChangeEvent(this, "Init", this, null);
                updateCenterComponent(et);
                firePropertyChange("", m_Backup, m_Object);
            }
        });
        generalOptions.add(rbInit);
        JRadioButton rbBitMask = new JRadioButton("Use Bit Mask");
        rbBitMask.setSelected(((TFPortfolioSelectionProblem) m_Object).getUseBitMask());
        rbBitMask.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected())
                    ((TFPortfolioSelectionProblem) m_Object).setUseBitMask(true);
                else ((TFPortfolioSelectionProblem) m_Object).setUseBitMask(false);
                PropertyChangeEvent et = new PropertyChangeEvent(this, "Use Bit Mask", this, null);
                updateCenterComponent(et);
                firePropertyChange("", m_Backup, m_Object);
            }
        });
        generalOptions.add(rbBitMask);
        JRadioButton rbLamarck = new JRadioButton("Lamarckism");
        rbLamarck.setSelected(((TFPortfolioSelectionProblem) m_Object).getUseLamarckism());
        rbLamarck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected())
                    ((TFPortfolioSelectionProblem) m_Object).setUseLamarckism(true);
                else ((TFPortfolioSelectionProblem) m_Object).setUseLamarckism(false);
                PropertyChangeEvent et = new PropertyChangeEvent(this, "Lamarckism", this, null);
                updateCenterComponent(et);
                firePropertyChange("", m_Backup, m_Object);
            }
        });
        generalOptions.add(rbLamarck);
        JRadioButton rbLocalSearch = new JRadioButton("Use Local Search");
        rbLocalSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected())
                    ((TFPortfolioSelectionProblem) m_Object).setUseLocalSearch(true);
                else ((TFPortfolioSelectionProblem) m_Object).setUseLocalSearch(false);
                PropertyChangeEvent et = new PropertyChangeEvent(this, "LocalSearch", this, null);
                updateCenterComponent(et);
                firePropertyChange("", m_Backup, m_Object);
            }
        });
        rbLocalSearch.setEnabled(false);
        generalOptions.add(rbLocalSearch);

        // The problem panel
        JPanel problemPanel = new JPanel();
        problemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Problem Settings:"),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        problemPanel.setLayout(new GridLayout(5, 2));

        editor = this.getEditorFor("PortfolioProblem", m_Properties, m_Methods, this.m_Object);
        editor.m_Editor.removePropertyChangeListener(this);
        editor.m_Editor.addPropertyChangeListener(this);
        editor.m_View.repaint();
        this.m_Editors.put("Asset File", editor);
        tmpLabel = new JLabel("Choose Asset File:");
        tmpLabel.setToolTipText(editor.m_TipText);
        problemPanel.add(tmpLabel);
        editor.m_View.setToolTipText(editor.m_TipText);
        editor.m_ViewWrapper = new JPanel();
        editor.m_ViewWrapper.setLayout(new BorderLayout());
        editor.m_ViewWrapper.add(editor.m_View, BorderLayout.CENTER);
        editor.m_ViewWrapper.setToolTipText(editor.m_TipText);
        problemPanel.add(editor.m_ViewWrapper);

        editor = this.getEditorFor("OptimizationTargets", m_Properties, m_Methods, this.m_Object);
        editor.m_Editor.removePropertyChangeListener(this);
        editor.m_Editor.addPropertyChangeListener(this);
        editor.m_View.repaint();
        this.m_Editors.put("OptimizationTargets", editor);
        tmpLabel = new JLabel("Opt. Targets/Soft Const.:");
        tmpLabel.setToolTipText(editor.m_TipText);
        problemPanel.add(tmpLabel);
        editor.m_View.setToolTipText(editor.m_TipText);
        editor.m_ViewWrapper = new JPanel();
        editor.m_ViewWrapper.setLayout(new BorderLayout());
        editor.m_ViewWrapper.add(editor.m_View, BorderLayout.CENTER);
        editor.m_ViewWrapper.setToolTipText(editor.m_TipText);
        problemPanel.add(editor.m_ViewWrapper);

        editor = this.getEditorFor("Cardinality", m_Properties, m_Methods, this.m_Object);
        editor.m_Editor.removePropertyChangeListener(this);
        editor.m_Editor.addPropertyChangeListener(this);
        editor.m_View.repaint();
        this.m_Editors.put("Cardinality", editor);
        tmpLabel = new JLabel("Cardinality:");
        tmpLabel.setToolTipText(editor.m_TipText);
        problemPanel.add(tmpLabel);
        editor.m_View.setToolTipText(editor.m_TipText);
        editor.m_ViewWrapper = new JPanel();
        editor.m_ViewWrapper.setLayout(new BorderLayout());
        editor.m_ViewWrapper.add(editor.m_View, BorderLayout.CENTER);
        editor.m_ViewWrapper.setToolTipText(editor.m_TipText);
        problemPanel.add(editor.m_ViewWrapper);

        editor = this.getEditorFor("BuyInThreshold", m_Properties, m_Methods, this.m_Object);
        editor.m_Editor.removePropertyChangeListener(this);
        editor.m_Editor.addPropertyChangeListener(this);
        editor.m_View.repaint();
        this.m_Editors.put("Buy In Threshold", editor);
        tmpLabel = new JLabel("Buy-In Threshold:");
        tmpLabel.setToolTipText(editor.m_TipText);
        problemPanel.add(tmpLabel);
        editor.m_View.setToolTipText(editor.m_TipText);
        editor.m_ViewWrapper = new JPanel();
        editor.m_ViewWrapper.setLayout(new BorderLayout());
        editor.m_ViewWrapper.add(editor.m_View, BorderLayout.CENTER);
        editor.m_ViewWrapper.setToolTipText(editor.m_TipText);
        problemPanel.add(editor.m_ViewWrapper);

        editor = this.getEditorFor("RoundLots", m_Properties, m_Methods, this.m_Object);
        editor.m_Editor.removePropertyChangeListener(this);
        editor.m_Editor.addPropertyChangeListener(this);
        editor.m_View.repaint();
        this.m_Editors.put("Round Lots", editor);
        tmpLabel = new JLabel("Round-Lots:");
        tmpLabel.setToolTipText(editor.m_TipText);
        problemPanel.add(tmpLabel);
        editor.m_View.setToolTipText(editor.m_TipText);
        editor.m_ViewWrapper = new JPanel();
        editor.m_ViewWrapper.setLayout(new BorderLayout());
        editor.m_ViewWrapper.add(editor.m_View, BorderLayout.CENTER);
        editor.m_ViewWrapper.setToolTipText(editor.m_TipText);
        problemPanel.add(editor.m_ViewWrapper);

        // The metric panel
        JPanel metricPanel = new JPanel();
        metricPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Metric Settings:"),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        metricPanel.setLayout(new GridLayout(2, 2));
        editor = this.getEditorFor("Metric", m_Properties, m_Methods, this.m_Object);
        editor.m_Editor.removePropertyChangeListener(this);
        editor.m_Editor.addPropertyChangeListener(this);
        editor.m_View.repaint();
        this.m_Editors.put("Plot Metric", editor);
        tmpLabel = new JLabel("Choose Plot Metric:");
        tmpLabel.setToolTipText(editor.m_TipText);
        metricPanel.add(tmpLabel);
        editor.m_View.setToolTipText(editor.m_TipText);
        editor.m_ViewWrapper = new JPanel();
        editor.m_ViewWrapper.setLayout(new BorderLayout());
        editor.m_ViewWrapper.add(editor.m_View, BorderLayout.CENTER);
        editor.m_ViewWrapper.setToolTipText(editor.m_TipText);
        metricPanel.add(editor.m_ViewWrapper);
        if ((editor = this.getEditorFor("ShowPath", m_Properties, m_Methods, this.m_Object)) != null) {
            editor.m_Editor.removePropertyChangeListener(this);
            editor.m_Editor.addPropertyChangeListener(this);
            editor.m_View.repaint();
            this.m_Editors.put("Show Efficieny Front", editor);
            tmpLabel = new JLabel("Show Efficieny Front:");
            tmpLabel.setToolTipText(editor.m_TipText);
            metricPanel.add(tmpLabel);
            editor.m_View.setToolTipText(editor.m_TipText);
            editor.m_ViewWrapper = new JPanel();
            editor.m_ViewWrapper.setLayout(new BorderLayout());
            editor.m_ViewWrapper.add(editor.m_View, BorderLayout.CENTER);
            editor.m_ViewWrapper.setToolTipText(editor.m_TipText);
            metricPanel.add(editor.m_ViewWrapper);
        }
        // Finally the show panel
        centerPanel.setLayout(new GridBagLayout());
        gbConst = new GridBagConstraints();
        gbConst.anchor = GridBagConstraints.WEST;
        gbConst.fill = GridBagConstraints.BOTH;
        gbConst.gridy = 0;
        gbConst.gridx = 0;
        gbConst.weightx = 100;
        centerPanel.add(generalSettings, gbConst);
        gbConst = new GridBagConstraints();
        gbConst.anchor = GridBagConstraints.WEST;
        gbConst.fill = GridBagConstraints.BOTH;
        gbConst.gridy = 1;
        gbConst.gridx = 0;
        gbConst.weightx = 100;
        centerPanel.add(generalOptions, gbConst);
        gbConst = new GridBagConstraints();
        gbConst.anchor = GridBagConstraints.WEST;
        gbConst.fill = GridBagConstraints.BOTH;
        gbConst.gridy = 2;
        gbConst.gridx = 0;
        gbConst.weightx = 100;
        centerPanel.add(problemPanel, gbConst);
        gbConst = new GridBagConstraints();
        gbConst.anchor = GridBagConstraints.WEST;
        gbConst.fill = GridBagConstraints.BOTH;
        gbConst.gridy = 3;
        gbConst.gridx = 0;
        gbConst.weightx = 100;
        centerPanel.add(metricPanel, gbConst);
        result.setLayout(new BorderLayout());
        result.add(centerPanel, BorderLayout.CENTER);
        return result;
    }

    /**
     * This method will udate the status of the object taking the values from all
     * supsequent editors and setting them to my object.
     */
    public void updateCenterComponent(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof PropertyEditor) {
            PropertyEditor editor = (PropertyEditor) evt.getSource();
            GeneralGOEProperty prop;
            Enumeration myEnum = this.m_Editors.elements();
            while (myEnum.hasMoreElements()) {
                prop = (GeneralGOEProperty) myEnum.nextElement();
                if (prop.m_Editor == editor) {
                    Object value = editor.getValue();
                    Method setter = prop.m_setMethod;
                    Method getter = prop.m_getMethod;
                    prop.m_Value = value;
                    if (true) {
                        PropertyEditor tmpEdit = null;
                        Object newValue = evt.getNewValue();
                        if (newValue == null) newValue = editor.getValue();
                        tmpEdit = PropertyEditorProvider.findEditor(newValue.getClass());
                        if (tmpEdit == null) tmpEdit = PropertyEditorProvider.findEditor(prop.m_PropertyType);
                        if (tmpEdit.getClass() != prop.m_Editor.getClass()) {
                            value = newValue;
                            prop.m_Value = newValue;
                            prop.m_Editor = tmpEdit;
                            if (tmpEdit instanceof GenericObjectEditor)
                                ((GenericObjectEditor) tmpEdit).setClassType(prop.m_PropertyType);
                            prop.m_Editor.setValue(newValue);
                            JComponent newView = PropertySheetPanel.getView(tmpEdit);
                            if (newView == null) {
                                System.err.println("Warning: Property \"" + prop.m_Name + "\" has non-displayabale editor.  Skipping.");
                                continue;
                            }
                            prop.m_Editor.addPropertyChangeListener(this);
                            prop.m_View = newView;
                            if (prop.m_TipText != null) prop.m_View.setToolTipText(prop.m_TipText);
                            prop.m_ViewWrapper.removeAll();
                            prop.m_ViewWrapper.setLayout(new BorderLayout());
                            prop.m_ViewWrapper.add(prop.m_View, BorderLayout.CENTER);
                            prop.m_ViewWrapper.repaint();
                        }
                    }
//                    System.out.println("Value: "+value +" / m_Values[i]: " + m_Values[i]);
                    // Now try to update the target with the new value of the property
                    // and allow the target to do some changes to the value, therefore
                    // reread the new value from the target
                    try {
                        Object args[] = {value};
                        args[0] = value;
                        Object args2[] = {};
                        // setting the current value to the target object
                        setter.invoke(m_Object, args);
                        // i could also get the new value
                        //value = getter.invoke(m_Target, args2);
                        // Now i'm reading the set value from the target to my local values
                        prop.m_Value = getter.invoke(m_Object, args2);

                        if (value instanceof Integer) {
                            // This could check whether i have to set the value back to
                            // the editor, this would allow to check myu and lambda
                            // why shouldn't i do this for every property!?
//                            System.out.println("value: "+((Integer)value).intValue());
//                            System.out.println(" m_Values[i]: "+ ((Integer) m_Values[i]).intValue());
                            if (((Integer) value).intValue() != ((Integer) prop.m_Value).intValue()) {
                                editor.setValue(prop.m_Value);
                            }
                        }
                    } catch (InvocationTargetException ex) {
                        if (ex.getTargetException() instanceof PropertyVetoException) {
                            System.out.println("PropertySheetPanel.wasModified(): WARNING: Vetoed; reason is: " + ex.getTargetException().getMessage());
                        } else {
                            System.out.println("PropertySheetPanel.wasModified(): InvocationTargetException while updating " + prop.m_Name);
                            System.out.println("PropertySheetPanel.wasModified(): " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } catch (Exception ex) {
                        System.out.println("PropertySheetPanel.wasModified(): Unexpected exception while updating " + prop.m_Name);
                    }
                    //revalidate();
                    if (prop.m_View != null && prop.m_View instanceof PropertyPanel) {
                        //System.err.println("Trying to repaint the property canvas");
                        prop.m_View.repaint();
                        m_EditorComponent.revalidate();
                    }
                    break;
                } // end if (m_Editors[i] == editor) {
            } // end for (int i = 0 ; i < m_Editors.length; i++) {
        } // end if (evt.getSource() instanceof PropertyEditor) {

        // Now re-read all the properties and update the editors
        // for any other properties that have changed.
        GeneralGOEProperty prop;
        Enumeration myEnum = this.m_Editors.elements();
        while (myEnum.hasMoreElements()) {
            prop = (GeneralGOEProperty) myEnum.nextElement();
            Object o;
            Method getter = null;
            try {
                getter = prop.m_getMethod;
                Object args[] = {};
                o = getter.invoke(m_Object, args);
            } catch (Exception ex) {
                o = null;
            }
//            System.out.println(""+prop.m_Name+" value: "+ prop.m_Value + " ?=? " +o);
            if (o == prop.m_Value) {
                // The property is equal to its old value.
                continue;
            }
            if (o != null && o.equals(prop.m_Value)) {
                // The property is equal to its old value.
                continue;
            }
//            System.out.println("setting the new value");
            prop.m_Value = o;
            // Make sure we have an editor for this property...
            if (prop.m_Editor == null) {
                continue;
            }
            // The property has changed!  Update the editor.
            prop.m_Editor.removePropertyChangeListener(this);
            prop.m_Editor.setValue(o);
            prop.m_Editor.addPropertyChangeListener(this);
//            System.out.println(""+prop.m_Name+" value: "+ prop.m_Value + " ?=? " +o);
            if (prop.m_View != null) {
//	            System.out.println("Trying to repaint.");
                prop.m_View.repaint();
            }
        }

        // Make sure the target bean gets repainted.
        if (Beans.isInstanceOf(m_Object, Component.class)) {
            //System.out.println("Beans.getInstanceOf repaint ");
            ((Component) (Beans.getInstanceOf(m_Object, Component.class))).repaint();
        }
    }
}