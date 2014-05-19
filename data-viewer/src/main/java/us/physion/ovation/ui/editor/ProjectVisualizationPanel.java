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
import java.net.URI;
import java.util.concurrent.Callable;
import javax.swing.SwingUtilities;
import org.joda.time.DateTime;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeView;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import us.physion.ovation.domain.AnalysisRecord;
import us.physion.ovation.domain.Epoch;
import us.physion.ovation.domain.Experiment;
import us.physion.ovation.domain.Project;
import us.physion.ovation.domain.mixin.DataElement;
import us.physion.ovation.exceptions.OvationException;
import us.physion.ovation.ui.browser.BrowserUtilities;
import static us.physion.ovation.ui.editor.DatePickers.zonedDate;
import us.physion.ovation.ui.interfaces.EventQueueUtilities;
import us.physion.ovation.ui.interfaces.IEntityNode;
import us.physion.ovation.ui.interfaces.TreeViewProvider;

/**
 * Data viewer visualization for Project entities
 *
 * @author barry
 */
@Messages({
    "Default_Experiment_Purpose=New Experiment",
    "Project_New_Analysis_Record_Name=New Analysis"
})
public class ProjectVisualizationPanel extends AbstractContainerVisualizationPanel {

    FileDrop analysisDropListener;
    FileDrop experimentDropListener;

    /**
     * Creates new form ProjectVisualizationPanel
     */
    public ProjectVisualizationPanel(IEntityNode n) {
        super(n);

        initComponents();
        initUI();

        node.refresh();
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
                addExperiment(e);

            }
        });

        experimentDropListener = new FileDrop(experimentDropPanelContainer, new FileDrop.Listener() {

            @Override
            public void filesDropped(final File[] files) {
                ListenableFuture<Experiment> addExp = addExperiment(new ActionEvent(this, 0, "experiment"));
                Futures.addCallback(addExp, new FutureCallback<Experiment>() {

                    @Override
                    public void onSuccess(final Experiment result) {
                        final ProgressHandle ph = ProgressHandleFactory.createHandle(Bundle.Adding_measurements());

                        TopComponent tc = WindowManager.getDefault().findTopComponent(OpenNodeInBrowserAction.PROJECT_BROWSER_ID);
                        if (!(tc instanceof ExplorerManager.Provider) || !(tc instanceof TreeViewProvider)) {
                            throw new IllegalStateException();
                        }

                        TreeView view = (TreeView) ((TreeViewProvider) tc).getTreeView();

                        view.expandNode((Node) node);

                        new OpenNodeInBrowserAction(OpenNodeInBrowserAction.PROJECT_BROWSER_ID,
                                Lists.newArrayList(result.getURI())).actionPerformed(new ActionEvent(this, 0, ""));

                        EventQueueUtilities.runOffEDT(new Runnable() {

                            @Override
                            public void run() {
                                insertMeasurements(result, files);
                                EventQueueUtilities.runOnEDT(new Runnable() {
                                    @Override
                                    public void run() {
                                        node.refresh();
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

        analysisDropListener = new FileDrop(analysisDropPanelContainer, new FileDrop.Listener() {

            @Override
            public void filesDropped(final File[] files) {

                final ProgressHandle ph = ProgressHandleFactory.createHandle(Bundle.AnalysisRecord_Adding_Outputs());

                TopComponent tc = WindowManager.getDefault().findTopComponent(OpenNodeInBrowserAction.PROJECT_BROWSER_ID);
                if (!(tc instanceof ExplorerManager.Provider) || !(tc instanceof TreeViewProvider)) {
                    throw new IllegalStateException();
                }

                final TreeView view = (TreeView) ((TreeViewProvider) tc).getTreeView();

                view.expandNode((Node) node);

                ListenableFuture<AnalysisRecord> addRecord = EventQueueUtilities.runOffEDT(new Callable<AnalysisRecord>() {

                    @Override
                    public AnalysisRecord call() {
                        return addAnalysisRecord(files);
                    }
                }, ph);

                Futures.addCallback(addRecord, new FutureCallback<AnalysisRecord>() {

                    @Override
                    public void onSuccess(final AnalysisRecord v) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                new OpenNodeInBrowserAction(OpenNodeInBrowserAction.PROJECT_BROWSER_ID,
                                        Lists.newArrayList(v.getURI())).actionPerformed(new ActionEvent(this, 0, ""));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable thrwbl) {
                        logger.error("Unable to add analysis record to project", thrwbl);
                    }
                });
            }
        });
    }

    private void insertMeasurements(Experiment exp, File[] files) {
        DateTime start = new DateTime();
        DateTime end = new DateTime();

        for (File f : files) {
            DateTime lastModified = new DateTime(f.lastModified());
            if (lastModified.isBefore(end)) {
                end = lastModified;
            }

            if (start.isBefore(lastModified)) {
                start = lastModified;
            }

        }

        Epoch e = exp.insertEpoch(start,
                end,
                null,
                Maps.<String, Object>newHashMap(),
                Maps.<String, Object>newHashMap());

        for (File f : files) {
            try {
                e.insertMeasurement(f.getName(),
                        Sets.<String>newHashSet(),
                        Sets.<String>newHashSet(),
                        f.toURI().toURL(),
                        ContentTypes.getContentType(f));
            } catch (MalformedURLException ex) {
                Toolkit.getDefaultToolkit().beep();
            } catch (IOException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    private ListenableFuture<Experiment> addExperiment(final ActionEvent e) {
        final Experiment exp = getProject().insertExperiment(Bundle.Default_Experiment_Purpose(), new DateTime());

        node.refresh();

        TopComponent projectBrowser = WindowManager.getDefault().findTopComponent(BrowserUtilities.PROJECT_BROWSER_ID);

        final TreeView tree = (TreeView) ((TreeViewProvider) projectBrowser).getTreeView();

        return EventQueueUtilities.runOffEDT(new Callable<Experiment>() {
            @Override
            public Experiment call() throws Exception {
                tree.expandNode((Node) node);
                new OpenNodeInBrowserAction(Lists.newArrayList(exp.getURI()),
                        null,
                        false,
                        Lists.<URI>newArrayList(),
                        OpenNodeInBrowserAction.PROJECT_BROWSER_ID).actionPerformed(e);

                return exp;
            }

        });
    }

    private AnalysisRecord addAnalysisRecord(File[] files) {
        getContext().beginTransaction();
        try {
            AnalysisRecord ar = getProject().addAnalysisRecord(Bundle.Project_New_Analysis_Record_Name(),
                    Maps.<String, DataElement>newHashMap(),
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

    public Project getProject() {
        return getNode().getEntity(Project.class);
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
        experimentDropPanelContainer = new javax.swing.JPanel();
        experimentDropPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        analysisDropPanelContainer = new javax.swing.JPanel();
        analysisDropPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));

        projectTitleLabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(projectTitleLabel, org.openide.util.NbBundle.getMessage(ProjectVisualizationPanel.class, "ProjectVisualizationPanel.projectTitleLabel.text")); // NOI18N

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
        dropPanelContainer.setLayout(new java.awt.GridLayout());

        experimentDropPanelContainer.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));
        experimentDropPanelContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        experimentDropPanelContainer.setMinimumSize(new java.awt.Dimension(150, 100));

        experimentDropPanel.setLayout(new java.awt.BorderLayout());

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel3.setForeground(java.awt.Color.darkGray);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ProjectVisualizationPanel.class, "ProjectVisualizationPanel.jLabel3.text")); // NOI18N
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        experimentDropPanel.add(jLabel3, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout experimentDropPanelContainerLayout = new javax.swing.GroupLayout(experimentDropPanelContainer);
        experimentDropPanelContainer.setLayout(experimentDropPanelContainerLayout);
        experimentDropPanelContainerLayout.setHorizontalGroup(
            experimentDropPanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentDropPanelContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(experimentDropPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addContainerGap())
        );
        experimentDropPanelContainerLayout.setVerticalGroup(
            experimentDropPanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentDropPanelContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(experimentDropPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                .addContainerGap())
        );

        dropPanelContainer.add(experimentDropPanelContainer);

        analysisDropPanelContainer.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));
        analysisDropPanelContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        analysisDropPanelContainer.setMinimumSize(new java.awt.Dimension(150, 100));

        analysisDropPanel.setLayout(new java.awt.BorderLayout());

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel4.setForeground(java.awt.Color.darkGray);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ProjectVisualizationPanel.class, "ProjectVisualizationPanel.jLabel4.text")); // NOI18N
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        analysisDropPanel.add(jLabel4, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout analysisDropPanelContainerLayout = new javax.swing.GroupLayout(analysisDropPanelContainer);
        analysisDropPanelContainer.setLayout(analysisDropPanelContainerLayout);
        analysisDropPanelContainerLayout.setHorizontalGroup(
            analysisDropPanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(analysisDropPanelContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(analysisDropPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addContainerGap())
        );
        analysisDropPanelContainerLayout.setVerticalGroup(
            analysisDropPanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(analysisDropPanelContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(analysisDropPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                .addContainerGap())
        );

        dropPanelContainer.add(analysisDropPanelContainer);

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
                    .addComponent(dropPanelContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
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
                .addContainerGap(99, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addExperimentButton;
    private javax.swing.JPanel analysisDropPanel;
    private javax.swing.JPanel analysisDropPanelContainer;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JPanel dropPanelContainer;
    private javax.swing.JPanel experimentDropPanel;
    private javax.swing.JPanel experimentDropPanelContainer;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField projectNameField;
    private javax.swing.JLabel projectTitleLabel;
    private javax.swing.JTextArea purposeTextArea;
    private us.physion.ovation.ui.interfaces.DateTimePicker startPicker;
    private javax.swing.JComboBox startZoneComboBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
