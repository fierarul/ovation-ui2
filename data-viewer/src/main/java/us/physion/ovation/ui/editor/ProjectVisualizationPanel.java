/*
 * Copyright (C) 2014 Physion LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package us.physion.ovation.ui.editor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.joda.time.DateTime;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.NbBundle.Messages;
import us.physion.ovation.domain.AnalysisRecord;
import us.physion.ovation.domain.Experiment;
import us.physion.ovation.domain.Measurement;
import us.physion.ovation.domain.Project;
import us.physion.ovation.domain.mixin.DataElement;
import us.physion.ovation.exceptions.OvationException;
import us.physion.ovation.ui.browser.BrowserUtilities;
import static us.physion.ovation.ui.editor.AnalysisRecordVisualizationPanel.getDataElementsFromEntity;
import static us.physion.ovation.ui.editor.DatePickers.zonedDate;
import us.physion.ovation.ui.interfaces.EventQueueUtilities;
import us.physion.ovation.ui.interfaces.IEntityNode;
import us.physion.ovation.ui.interfaces.IEntityWrapper;
import us.physion.ovation.ui.reveal.api.RevealNode;

/**
 * Data viewer visualization for Project entities
 *
 * @author barry
 */
@Messages({
    "Default_Experiment_Purpose=New Experiment",
    "Project_New_Analysis_Record_Name=New Analysis",
    "Project_Drop_Files_To_Add_Experiment_Data=Drop files to add Exerpiment data",
    "Project_Drop_Files_To_Add_Analysis=Drop files to add analyses"
})
public class ProjectVisualizationPanel extends AbstractContainerVisualizationPanel {

    /**
     * Creates new form ProjectVisualizationPanel
     */
    public ProjectVisualizationPanel(IEntityNode n) {
        super(n);

        initComponents();
        initUI();

        //node.refresh();
    }

    private void initUI() {

        setEntityBorder(this);

        startPicker.setDateTime(getProject().getStart());

        startZoneComboBox.setSelectedItem(getProject().getStart().getZone().getID());

        startPicker.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startDateTimeChanged();
            }
        });

        startZoneComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startDateTimeChanged();
            }
        });

        addExperimentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                addExperiment(true);

            }
        });

        experimentFileWell.setDelegate(new FileWell.AbstractDelegate(Bundle.Project_Drop_Files_To_Add_Experiment_Data()) {

            @Override
            public void filesDropped(final File[] files) {
                ListenableFuture<Experiment> addExp = EventQueueUtilities.runOffEDT(new Callable<Experiment>() {
                    @Override
                    public Experiment call() throws Exception {
                        return addExperiment(false);
                    }
                });
                Futures.addCallback(addExp, new FutureCallback<Experiment>() {

                    @Override
                    public void onSuccess(final Experiment result) {
                        final ProgressHandle ph = ProgressHandleFactory.createHandle(Bundle.Adding_measurements());

                        EventQueueUtilities.runOffEDT(new Runnable() {

                            @Override
                            public void run() {
                                final List<Measurement> m = EntityUtilities.insertMeasurements(result, files);
                                EventQueueUtilities.runOnEDT(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!m.isEmpty()) {
                                            RevealNode.forEntity(BrowserUtilities.PROJECT_BROWSER_ID, m.get(0));
                                        }
                                    }
                                });
                            }
                        }, ph);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Unable to add measurements", t);
                    }
                });
            }
        });

        analysisFileWell.setDelegate(new FileWell.AbstractDelegate(Bundle.Project_Drop_Files_To_Add_Analysis()) {

            @Override
            public void filesDropped(final File[] files) {

                Iterable<DataElement> inputElements = showInputsDialog();
                if (inputElements == null) {
                    inputElements = Lists.newArrayList();
                }

                final List<DataElement> inputs = Lists.newArrayList(inputElements);

                ListenableFuture<AnalysisRecord> addRecord = EventQueueUtilities.runOffEDT(new Callable<AnalysisRecord>() {

                    @Override
                    public AnalysisRecord call() throws Exception {
                        final AnalysisRecord record = addAnalysisRecord(files, inputs);
                        EventQueueUtilities.runOnEDT(new Runnable() {
                            @Override
                            public void run() {
                                RevealNode.forEntity(BrowserUtilities.PROJECT_BROWSER_ID, record);
                            }
                        });

                        return record;
                    }

                });

                Futures.addCallback(addRecord, new FutureCallback<AnalysisRecord>() {

                    @Override
                    public void onSuccess(final AnalysisRecord ar) {}

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Unable to display AnalysisRecord", t);
                    }
                });
            }
        });
    }

    private Iterable<DataElement> showInputsDialog() {
        SelectDataElementsDialog addDialog = new SelectDataElementsDialog((JFrame) SwingUtilities.getRoot(this),
                true,
                null);

        addDialog.setVisible(true);

        List<DataElement> result = Lists.newArrayList();
        if (addDialog.isSuccess()) {
            for (IEntityWrapper entityWrapper : addDialog.getSelectedEntities()) {
                for (DataElement entity : getDataElementsFromEntity(entityWrapper.getEntity())) {
                    result.add(entity);
                }
            }

            System.out.println(Sets.newHashSet(addDialog.getSelectedEntities()));
        } else {
            result = null;
        }

        addDialog.dispose();

        return result;
    }


    private Experiment addExperiment(boolean reveal) {
        final Experiment exp = getProject().insertExperiment(Bundle.Default_Experiment_Purpose(), new DateTime());

        if (reveal) {
            node.refresh();
            RevealNode.forEntity(BrowserUtilities.PROJECT_BROWSER_ID, exp);
        }

        return exp;
    }

    private AnalysisRecord addAnalysisRecord(File[] files, Iterable<DataElement> inputs) {
        getContext().beginTransaction();
        try {
            AnalysisRecord ar = getProject().addAnalysisRecord(Bundle.Project_New_Analysis_Record_Name(),
                    inputs,
                    null,
                    Maps.<String, Object>newHashMap());

            for (File f : files) {
                String name = f.getName();
                int i = 1;
                while (ar.getOutputs().keySet().contains(name)) {
                    name = name + "_" + i;
                    i++;
                }

                try {
                    ar.addOutput(
                            name,
                            f.toURI().toURL(),
                            ContentTypes.getContentType(f));
                } catch (MalformedURLException ex) {
                    logger.error("Unable to determine file URL", ex);
                    Toolkit.getDefaultToolkit().beep();
                } catch (IOException ex) {
                    logger.error("Unable to determine file content type", ex);
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            getContext().markModified(getProject());
            getContext().commitTransaction();

            return ar;
        } catch (Throwable t) {
            getContext().abortTransaction();
            throw new OvationException(t);
        }
    }

    protected void startDateTimeChanged() {
        getProject().setStart(zonedDate(startPicker, startZoneComboBox));
    }

    public Project
            getProject() {
        return getNode().getEntity(Project.class
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        projectTitleLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        purposeTextArea = new javax.swing.JTextArea();
        dateLabel = new javax.swing.JLabel();
        startPicker = new us.physion.ovation.ui.interfaces.DateTimePicker();
        projectNameField = new javax.swing.JTextField();
        startZoneComboBox = new javax.swing.JComboBox();
        addExperimentButton = new javax.swing.JButton();
        dropPanelContainer = new javax.swing.JPanel();
        experimentFileWell = new us.physion.ovation.ui.editor.FileWell();
        analysisFileWell = new us.physion.ovation.ui.editor.FileWell();

        setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));

        projectTitleLabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(projectTitleLabel, org.openide.util.NbBundle.getMessage(ProjectVisualizationPanel.class, "ProjectVisualizationPanel.projectTitleLabel.text")); // NOI18N

        jScrollPane1.setBackground(java.awt.Color.white);
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ProjectVisualizationPanel.class, "ProjectVisualizationPanel.jScrollPane1.border.title"))); // NOI18N

        purposeTextArea.setColumns(20);
        purposeTextArea.setLineWrap(true);
        purposeTextArea.setRows(5);
        purposeTextArea.setWrapStyleWord(true);
        purposeTextArea.setBorder(null);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${project.purpose}"), purposeTextArea, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(purposeTextArea);

        org.openide.awt.Mnemonics.setLocalizedText(dateLabel, org.openide.util.NbBundle.getMessage(ProjectVisualizationPanel.class, "ProjectVisualizationPanel.dateLabel.text")); // NOI18N

        projectNameField.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        projectNameField.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.background")));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${project.name}"), projectNameField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        startZoneComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${availableZoneIDs}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, startZoneComboBox);
        bindingGroup.addBinding(jComboBoxBinding);

        org.openide.awt.Mnemonics.setLocalizedText(addExperimentButton, org.openide.util.NbBundle.getMessage(ProjectVisualizationPanel.class, "ProjectVisualizationPanel.addExperimentButton.text")); // NOI18N

        dropPanelContainer.setBackground(java.awt.Color.white);
        dropPanelContainer.setLayout(new java.awt.GridLayout(1, 0));
        dropPanelContainer.add(experimentFileWell);
        dropPanelContainer.add(analysisFileWell);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(projectTitleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(projectNameField))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dateLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(startPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(startZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(addExperimentButton))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(dropPanelContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectTitleLabel)
                    .addComponent(projectNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startZoneComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(addExperimentButton)
                .addGap(18, 18, 18)
                .addComponent(dropPanelContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addExperimentButton;
    private us.physion.ovation.ui.editor.FileWell analysisFileWell;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JPanel dropPanelContainer;
    private us.physion.ovation.ui.editor.FileWell experimentFileWell;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField projectNameField;
    private javax.swing.JLabel projectTitleLabel;
    private javax.swing.JTextArea purposeTextArea;
    private us.physion.ovation.ui.interfaces.DateTimePicker startPicker;
    private javax.swing.JComboBox startZoneComboBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
