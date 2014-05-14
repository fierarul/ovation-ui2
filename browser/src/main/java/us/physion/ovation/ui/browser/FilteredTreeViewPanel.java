package us.physion.ovation.ui.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JToggleButton;
import org.openide.awt.Actions;
import org.openide.explorer.view.BeanTreeView;
import us.physion.ovation.ui.browser.TreeFilter.NavigatorType;

public final class FilteredTreeViewPanel extends javax.swing.JPanel {

    private final TreeFilter filter;
    public FilteredTreeViewPanel(TreeFilter info, final String actionId) {
        filter = info;
        initComponents();

        getTreeView().setRootVisible(false);

        bindToggleButtons(filter);

        newRootEntityButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Actions.forID("Edit", actionId).actionPerformed(e);
            }
        });
    }

    public JToggleButton getEpochGroupToggle() {
        return epochGroupToggle;
    }

    public JToggleButton getEpochToggle() {
        return epochToggle;
    }

    public JToggleButton getExperimentToggle() {
        return experimentToggle;
    }

    public BeanTreeView getTreeView() {
        return (BeanTreeView) treeView;
    }

    private void bindToggleButtons(final TreeFilter info) {
        if (info.getNavigatorType() == NavigatorType.PROJECT) {
            //only source gets this, disable otherwise
            experimentToggle.setEnabled(false);
        }


        experimentToggle.setSelected(info.isExperimentsVisible());
        experimentToggle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                info.setExperimentsVisible(experimentToggle.isSelected());
            }
        });

        epochGroupToggle.setSelected(info.isEpochGroupsVisible());
        epochGroupToggle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                info.setEpochGroupsVisible(epochGroupToggle.isSelected());
            }
        });

        epochToggle.setSelected(info.isEpochsVisible());
        epochToggle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                info.setEpochsVisible(epochToggle.isSelected());
            }
        });

        info.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                boolean newValue = (Boolean) evt.getNewValue();

                if (evt.getPropertyName().equals("experimentsVisible")) {
                    experimentToggle.setSelected(newValue);
                }

                if (evt.getPropertyName().equals("epochGroupsVisible")) {
                    epochGroupToggle.setSelected(newValue);
                }

                if(evt.getPropertyName().equals("epochsVisible")) {
                    epochToggle.setSelected(newValue);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
        treeView = new BeanTreeView();
        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        experimentToggle = new javax.swing.JToggleButton();
        epochGroupToggle = new javax.swing.JToggleButton();
        epochToggle = new javax.swing.JToggleButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        newRootEntityButton = new javax.swing.JButton();

        jSplitPane1.setBorder(null);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setLeftComponent(treeView);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        experimentToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/us/physion/ovation/ui/browser/experiment.png"))); // NOI18N
        experimentToggle.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(experimentToggle, org.openide.util.NbBundle.getMessage(FilteredTreeViewPanel.class, "FilteredTreeViewPanel.experimentToggle.text")); // NOI18N
        experimentToggle.setToolTipText(org.openide.util.NbBundle.getMessage(FilteredTreeViewPanel.class, "FilteredTreeViewPanel.experimentToggle.toolTipText")); // NOI18N
        experimentToggle.setFocusable(false);
        experimentToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        experimentToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(experimentToggle);

        epochGroupToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/us/physion/ovation/ui/browser/epochGroup.png"))); // NOI18N
        epochGroupToggle.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(epochGroupToggle, org.openide.util.NbBundle.getMessage(FilteredTreeViewPanel.class, "FilteredTreeViewPanel.epochGroupToggle.text")); // NOI18N
        epochGroupToggle.setToolTipText(org.openide.util.NbBundle.getMessage(FilteredTreeViewPanel.class, "FilteredTreeViewPanel.epochGroupToggle.toolTipText")); // NOI18N
        epochGroupToggle.setFocusable(false);
        epochGroupToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        epochGroupToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(epochGroupToggle);

        epochToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/us/physion/ovation/ui/browser/epoch.png"))); // NOI18N
        epochToggle.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(epochToggle, org.openide.util.NbBundle.getMessage(FilteredTreeViewPanel.class, "FilteredTreeViewPanel.epochToggle.text")); // NOI18N
        epochToggle.setToolTipText(org.openide.util.NbBundle.getMessage(FilteredTreeViewPanel.class, "FilteredTreeViewPanel.epochToggle.toolTipText")); // NOI18N
        epochToggle.setFocusable(false);
        epochToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        epochToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(epochToggle);
        jToolBar1.add(filler1);

        org.openide.awt.Mnemonics.setLocalizedText(newRootEntityButton, org.openide.util.NbBundle.getMessage(FilteredTreeViewPanel.class, "FilteredTreeViewPanel.newRootEntityButton.text")); // NOI18N
        newRootEntityButton.setToolTipText(org.openide.util.NbBundle.getMessage(FilteredTreeViewPanel.class, "FilteredTreeViewPanel.newRootEntityButton.toolTipText")); // NOI18N
        newRootEntityButton.setFocusable(false);
        newRootEntityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newRootEntityButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(newRootEntityButton);

        jSplitPane1.setRightComponent(jToolBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .add(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .add(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton epochGroupToggle;
    private javax.swing.JToggleButton epochToggle;
    private javax.swing.JToggleButton experimentToggle;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton newRootEntityButton;
    private javax.swing.JScrollPane treeView;
    // End of variables declaration//GEN-END:variables
}
