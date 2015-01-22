/*
 * Copyright (C) 2015 Physion LLC
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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.NbBundle.Messages;
import us.physion.ovation.domain.Folder;
import us.physion.ovation.domain.Resource;
import us.physion.ovation.ui.browser.BrowserUtilities;
import us.physion.ovation.ui.interfaces.EventQueueUtilities;
import us.physion.ovation.ui.interfaces.IEntityNode;
import us.physion.ovation.ui.reveal.api.RevealNode;

/**
 * Folder visualization
 *
 * @author barry
 */
@Messages({
    "Folder_Drop_Files_To_Add_Resources=Drop files",
    "Adding_resources=Adding files..."
})
public class FolderVisualizationPanel extends AbstractContainerVisualizationPanel {

    /**
     * Creates new form FolderVisualizationPanel
     */
    public FolderVisualizationPanel(IEntityNode n) {
        super(n);
        initComponents();
        initUI();
    }

    private void initUI() {
        addFolderButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addFolder(true);
            }
        });

        fileWell.setDelegate(new FileWell.AbstractDelegate(Bundle.Folder_Drop_Files_To_Add_Resources()) {

            @Override
            public void filesDropped(final File[] files) {
                final ProgressHandle ph = ProgressHandleFactory.createHandle(Bundle.Adding_resources());

                ListenableFuture<Iterable<Resource>> addResources = EventQueueUtilities.runOffEDT(new Callable<Iterable<Resource>>() {

                    @Override
                    public Iterable<Resource> call() {
                        final List<Resource> resources = EntityUtilities.insertResources(getFolder(), files);
                        EventQueueUtilities.runOnEDT(new Runnable() {
                            @Override
                            public void run() {
                                if (!resources.isEmpty()) {
                                    RevealNode.forEntity(BrowserUtilities.PROJECT_BROWSER_ID, resources.get(0));
                                }
                            }
                        });

                        return resources;
                    }
                }, ph);

                Futures.addCallback(addResources, new FutureCallback<Iterable<Resource>>() {

                    @Override
                    public void onSuccess(final Iterable<Resource> result) {
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Unable to display added file(s)", t);
                    }
                });
            }
        });
    }

    private Folder addFolder(boolean reveal) {
        final Folder folder = getFolder().addFolder(Bundle.Default_Folder_Label());
        if (reveal) {
            node.refresh();
            RevealNode.forEntity(BrowserUtilities.PROJECT_BROWSER_ID, folder);
        }

        return folder;
    }

    public Folder getFolder() {
        return getNode().getEntity(Folder.class);
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

        jLabel1 = new javax.swing.JLabel();
        folderLabel = new javax.swing.JTextField();
        addFolderButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        fileWell = new us.physion.ovation.ui.editor.FileWell();

        setBackground(java.awt.Color.white);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FolderVisualizationPanel.class, "FolderVisualizationPanel.jLabel1.text")); // NOI18N

        folderLabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${folder.label}"), folderLabel, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        org.openide.awt.Mnemonics.setLocalizedText(addFolderButton, org.openide.util.NbBundle.getMessage(FolderVisualizationPanel.class, "FolderVisualizationPanel.addFolderButton.text")); // NOI18N

        jPanel1.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.select"));
        jPanel1.setLayout(new java.awt.BorderLayout());

        fileWell.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jPanel1.add(fileWell, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(folderLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addFolderButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(folderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(addFolderButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFolderButton;
    private us.physion.ovation.ui.editor.FileWell fileWell;
    private javax.swing.JTextField folderLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
