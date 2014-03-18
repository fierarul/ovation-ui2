/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.browser.insertion;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import us.physion.ovation.ui.TableTreeKey;
import us.physion.ovation.ui.ScrollableTableTree;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.util.Cancellable;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import us.physion.ovation.DataContext;
import us.physion.ovation.DataStoreCoordinator;
import us.physion.ovation.domain.AnnotatableEntity;
import us.physion.ovation.domain.Project;
import us.physion.ovation.domain.Source;
import us.physion.ovation.domain.User;
import us.physion.ovation.domain.mixin.ProcedureElement;
import us.physion.ovation.ui.browser.BrowserUtilities;
import us.physion.ovation.ui.browser.EntityWrapper;
import us.physion.ovation.ui.browser.ResetQueryAction;
import us.physion.ovation.ui.*;
import us.physion.ovation.ui.browser.EntityComparator;
import us.physion.ovation.ui.browser.FilteredEntityChildren;
import us.physion.ovation.ui.interfaces.IEntityWrapper;
import us.physion.ovation.ui.interfaces.ConnectionProvider;
import us.physion.ovation.ui.interfaces.EventQueueUtilities;
import us.physion.ovation.ui.interfaces.ExpressionTreeProvider;

/**
 *
 * @author huecotanks
 */
public class SourceSelector extends javax.swing.JPanel implements Lookup.Provider, ExplorerManager.Provider{

    @Override
    public String getName() {
        return "Select a Source";
    }
    
    ChangeSupport cs;
    private void resetSources() {
        DataContext ctx = Lookup.getDefault().lookup(ConnectionProvider.class).getDefaultContext();
        List<EntityWrapper> topLevelSources = Lists.newArrayList(Iterables.transform(ctx.getTopLevelSources(), new Function<Source, EntityWrapper>() {

                @Override
                public EntityWrapper apply(Source input) {
                    return new EntityWrapper(input);
                }
            }));
        
        Collections.sort(topLevelSources, new EntityComparator());
        
        em.setRootContext(new AbstractNode(new FilteredEntityChildren(topLevelSources, Sets.<Class>newHashSet(Source.class))));
    }

    Lookup l;
    ExplorerManager em;
    BeanTreeView sourcesTree;
    
    /**
     * Creates new form SourceSelector
     */
    public SourceSelector(ChangeSupport changeSupport, IEntityWrapper source) {
        this(changeSupport, source, Lookup.getDefault().lookup(ConnectionProvider.class).getDefaultContext());
    }
    public SourceSelector(ChangeSupport changeSupport, IEntityWrapper source, DataContext ctx) {//get rid of this DataCotext and get the tests passing
        initComponents();
        this.cs = changeSupport;
        //TODO: find the relative paths
        resetButton.setIcon(new ImageIcon("../Browser/src/us/physion/ovation/ui/browser/reset-query24.png"));
        runQueryButton.setIcon(new ImageIcon("../QueryTools/src/us/physion/ovation/query/query24.png"));
           
        em = new ExplorerManager();
        l = ExplorerUtils.createLookup(em, getActionMap());
        Lookup.Result <EntityWrapper> pe = l.lookupResult(EntityWrapper.class);
        pe.addLookupListener(new LookupListener() {

            @Override
            public void resultChanged(LookupEvent le) {
                cs.fireChange();
            }
        });
        
        sourcesTree = new BeanTreeView();
        sourcesTree.setRootVisible(true);

        treeView.setViewportView(sourcesTree);
        resetSources();
        
    }
    
    public Source getSource()
    {
        EntityWrapper ew = l.lookup(EntityWrapper.class);
        if (ew == null)
            return null;
        
        if (Source.class.isAssignableFrom(ew.getType()))
            return (Source)ew.getEntity();
        return null;
    }

    @Override
    public Lookup getLookup() {
        return l;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        runQueryButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        propertiesPane = new us.physion.ovation.ui.ScrollableTableTree();
        treeView = new javax.swing.JScrollPane();

        runQueryButton.setText(org.openide.util.NbBundle.getMessage(SourceSelector.class, "SourceSelector.runQueryButton.text")); // NOI18N
        runQueryButton.setBorderPainted(false);
        runQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runQueryButtonActionPerformed(evt);
            }
        });

        resetButton.setText(org.openide.util.NbBundle.getMessage(SourceSelector.class, "SourceSelector.resetButton.text")); // NOI18N
        resetButton.setBorderPainted(false);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        jSplitPane1.setDividerLocation(235);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(500, 300));

        propertiesPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jSplitPane1.setRightComponent(propertiesPane);
        jSplitPane1.setLeftComponent(treeView);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(runQueryButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(resetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(resetButton)
                    .add(runQueryButton))
                .add(18, 18, 18)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        resetSources();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void runQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runQueryButtonActionPerformed
        /*final ExpressionTreeProvider etp = Lookup.getDefault().lookup(ExpressionTreeProvider.class);
        ExpressionTree et = etp.getExpressionTree();
        
        final ExpressionTree result = ExpressionBuilder.editExpression(et).expressionTree;
        //run query, and reset
        EventQueueUtilities.runOffEDT(new Runnable(){
            @Override
            public void run() {
                 Iterator itr = dsc.getContext().query(result);
                 DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sources");
                Map<String, DefaultMutableTreeNode> sources = new HashMap<String, DefaultMutableTreeNode>(); 
                
                while(itr.hasNext())
                 {
                     Object n = itr.next();
                     if (n instanceof Source) {
                         Source child = (Source) n;
                         Source parent;
                         DefaultMutableTreeNode parentNode = null;
                         DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new EntityWrapper(child));
                         if (sources.containsKey(child.getURIString())) {
                             continue;
                         }
                         
                         while ( (parent = child.getParent()) != null)
                         {
                             if (sources.containsKey(parent.getURIString())) 
                             {
                                 parentNode = sources.get(parent.getURIString());
                                 break;
                             }else{
                                 sources.put(child.getURIString(), childNode);
                                 child = child.getParent();
                                 DefaultMutableTreeNode cn = new DefaultMutableTreeNode(new EntityWrapper(child));
                                 cn.add(childNode);
                                 childNode = cn;
                             }
                         }
                         if (parentNode == null){
                             parentNode = root;
                         }
                         parentNode.add(childNode);
                     }
                 }
                root.add(new DefaultMutableTreeNode("<None>"));
                ((DefaultTreeModel)sourcesTree.getModel()).setRoot(root);
            }
        });*/
    }//GEN-LAST:event_runQueryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane propertiesPane;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runQueryButton;
    private javax.swing.JScrollPane treeView;
    // End of variables declaration//GEN-END:variables

}
