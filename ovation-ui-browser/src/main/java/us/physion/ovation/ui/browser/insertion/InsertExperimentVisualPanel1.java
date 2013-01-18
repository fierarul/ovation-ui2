/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.browser.insertion;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openide.util.ChangeSupport;

/**
 *
 * @author huecotanks
 */
public class InsertExperimentVisualPanel1 extends javax.swing.JPanel {

    ChangeSupport cs;
    
    String purpose;
    DateTime start;
    DateTime end;
    
    DateTimePicker startPicker;
    DateTimePicker endPicker;
    /**
     * Creates new form InsertExperimentVisualPanel1
     */
    public InsertExperimentVisualPanel1(ChangeSupport cs) {
        initComponents();
        purpose = "";
        
        this.cs = cs;
        startPicker = DatePickerUtilities.createDateTimePicker();
        startPicker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if ("date".equals(propertyChangeEvent.getPropertyName())) {
                    startDateTimeChanged();
                }
            }
        });
        endPicker = DatePickerUtilities.createDateTimePicker();
        endPicker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if ("date".equals(propertyChangeEvent.getPropertyName())) {
                    endDateTimeChanged();
                }
            }
        });
        
        jComboBox1.setSelectedItem(DatePickerUtilities.getID(startPicker));
        jComboBox2.setSelectedItem(DatePickerUtilities.getID(endPicker));
        startPane.setViewportView(startPicker);
        endPane.setViewportView(endPicker);
        start = null;
        end = null;
    }

    
    protected void startDateTimeChanged() {
        setStart(new DateTime(startPicker.getDate(),  DateTimeZone.forID(((String)jComboBox1.getSelectedItem()))));
    }
    protected void endDateTimeChanged() {
        setEnd(new DateTime(endPicker.getDate(),  DateTimeZone.forID(((String)jComboBox2.getSelectedItem()))));
    }
    @Override
    public String getName() {
        return "Insert Experiment";
    }

    String getPurpose() {
        return purpose;
    }

    DateTime getStart() {
        return start;
    }

    DateTime getEnd() {
        return end;
    }
    
    protected void setPurpose(String p)
    {
        boolean fireChange = true;
        if (purpose.isEmpty() == p.isEmpty())
            fireChange = false;
        purpose = p;
        
        if (fireChange)
            cs.fireChange();
    }
    protected void setStart(DateTime t)
    {
        start = t;
        cs.fireChange();
    }
    protected void setEnd(DateTime t)
    {
        end = t;
        cs.fireChange();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        startPane = new javax.swing.JScrollPane();
        endPane = new javax.swing.JScrollPane();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(InsertExperimentVisualPanel1.class, "InsertExperimentVisualPanel1.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(InsertExperimentVisualPanel1.class, "InsertExperimentVisualPanel1.jLabel2.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(InsertExperimentVisualPanel1.class, "InsertExperimentVisualPanel1.jLabel3.text")); // NOI18N

        jTextField1.setText(org.openide.util.NbBundle.getMessage(InsertExperimentVisualPanel1.class, "InsertExperimentVisualPanel1.jTextField1.text")); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        startPane.setBorder(null);
        startPane.setPreferredSize(new java.awt.Dimension(200, 30));

        endPane.setBorder(null);
        endPane.setPreferredSize(new java.awt.Dimension(200, 30));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(DatePickerUtilities.getTimeZoneIDs()));
        jComboBox1.setPreferredSize(new java.awt.Dimension(180, 30));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(DatePickerUtilities.getTimeZoneIDs()));
        jComboBox2.setPreferredSize(new java.awt.Dimension(180, 30));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jLabel3))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextField1)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(startPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(endPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jComboBox2, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(startPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(endPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(193, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        setPurpose(jTextField1.getText());
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        setEnd(new DateTime(endPicker.getDate(),  DateTimeZone.forID(((String)jComboBox2.getSelectedItem()))));
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        setStart(new DateTime(startPicker.getDate(),  DateTimeZone.forID(((String)jComboBox1.getSelectedItem()))));
    }//GEN-LAST:event_jComboBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane endPane;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JScrollPane startPane;
    // End of variables declaration//GEN-END:variables
}
