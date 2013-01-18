/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui;

import ovation.Ovation;
import us.physion.ovation.ui.interfaces.EventQueueUtilities;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 *
 * @author huecotanks
 */
public class EditableTable extends javax.swing.JPanel implements TablePanel,ResizableTable {

    private JTable table;
    private ScrollableTableTree treeUtils;
    /**
     * Creates new form EditableTable
     */
    public EditableTable(JTable table, ScrollableTableTree t) {
        initComponents();
        jScrollPane1.getViewport().add(table, null);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        this.table = table;
        this.treeUtils = t;
        //this.setBorder(BorderFactory.createEtchedBorder());
    }
    
    public void resize()
    {
        JScrollPane sp = getScrollPane();
        if (sp != null) {
            sp.setSize(sp.getPreferredSize());
        }
        setSize(getPreferredSize());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        addButton.setText("+");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("-");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1)
            .add(layout.createSequentialGroup()
                .add(addButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 360, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(deleteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        //add a blank row to the table, make sure tableChanged deals with it appropriately
        int last = ((DefaultTableModel)table.getModel()).getRowCount() -1;
        
        String lastKey;
        if (last == -1)
            lastKey = "not null";
        else{
            lastKey = (String)table.getValueAt(last, 0);
        }
        if ( (lastKey != null && !lastKey.isEmpty()))
        {
            addBlankRow();
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
       
        deleteRows(table.getSelectedRows());
    }//GEN-LAST:event_deleteButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JTable getTable() {
        return table;
    }
    
    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public Dimension getPreferredSize(){  
        int height = 0;
        //this is voodoo magic, DO NOT CHANGE
        if (table.getHeight() ==0)
         {
             //this gets the height from the EditableTable default, so if this gets out of whack, modify the default size in the UI builder
            height = (int)super.getPreferredSize().getHeight();
         }
        else
        {
            height = (table.getRowCount())*table.getRowHeight() + 24 + addButton.getHeight();
        }
        int width = treeUtils == null ? getWidth() : treeUtils.getCellWidth();
        Dimension actual = new Dimension(width, height);
         return actual;  
       
    }
    
    protected JScrollPane getScrollPane()
    {
       return jScrollPane1;
    }

    protected void addBlankRow() {
        EventQueueUtilities.runOffEDT(new Runnable() {
            @Override
            public void run() {
                DefaultTableModel m = ((DefaultTableModel) table.getModel());
                int row = m.getRowCount();
                Object[] blankRow = new String[m.getColumnCount()];
                for (int i=0; i< m.getColumnCount(); i++)
                    blankRow[i] = "";
                m.addRow(blankRow);
                
                //manually set size of the containing scrollpane, since the table has resized
                JScrollPane sp = ((JScrollPane) table.getParent().getParent());
                sp.setSize(sp.getPreferredSize());
                EditableTable.this.setSize(EditableTable.this.getPreferredSize());
                table.getSelectionModel().setSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
                
                
                boolean noListener = true;
                for (TableModelListener l : m.getListeners(TableModelListener.class)) {
                    if (l instanceof EditableTableModelListener) {
                        noListener = false;

                        TableModelEvent t = new TableModelEvent(m, row, row, 1, TableModelEvent.INSERT);
                        l.tableChanged(t);
                        break;
                    }
                }
                if (noListener) {
                    Ovation.getLogger().debug("No listener available for the TableModel");
                }
            }
        });
    }
    
    /*protected void editRow(final int row, final String key, final Object value)
    {
        final DefaultTableModel m = ((DefaultTableModel) table.getModel());
        EventQueueUtilities.runOffEDT(new Runnable() {

            @Override
            public void run() {
                
                m.setValueAt(key, row, 0);
                m.setValueAt(value, row, 1);

                boolean noListener = true;
                for (TableModelListener l : m.getListeners(TableModelListener.class)) {
                    if (l instanceof EditableTableModelListener) {
                        noListener = false;

                        TableModelEvent t = new TableModelEvent(m, row, row, 1, TableModelEvent.UPDATE);
                        l.tableChanged(t);
                        break;
                    }
                }
                if (noListener) {
                    Ovation.getLogger().debug("No listener available for the TableModel");
                }
            }
        });
    }*/

    protected void deleteRows(int[] rows) {
        //There is a bug in getListeners - it doesnt find the EditableTableModelListener if you pass is EditableTableModelListener.class
        TableModelListener[] listeners = ((DefaultTableModel) table.getModel()).getListeners(TableModelListener.class);
        for (TableModelListener l : listeners) {
            if (l instanceof EditableTableModelListener) {
                ((EditableTableModelListener) l).deleteRows((DefaultTableModel) table.getModel(), rows);
                break;
            }
        }
    }
}
