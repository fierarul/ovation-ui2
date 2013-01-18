/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.browser.insertion;

import org.openide.util.ChangeSupport;

/**
 *
 * @author huecotanks
 */
public class InsertSourceVisualPanel1 extends javax.swing.JPanel {

    private ChangeSupport cs;
    private String label;
    /**
     * Creates new form InsertSourceVisualPanel1
     */
    public InsertSourceVisualPanel1(ChangeSupport changeSupport) {
        initComponents();
        cs = changeSupport;
        label = "";
    }
    
    public String getLabel()
    {
        return label;
    }
    
    protected void setLabel(String l)
    {
        boolean fireChange = true;
        if (label.isEmpty() == l.isEmpty())
            fireChange = false;
        label = l;
        if (fireChange)
            cs.fireChange();
    }
    
    @Override
    public String getName()
    {
        return "Insert Source";
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
        labelTextField = new javax.swing.JTextField();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(InsertSourceVisualPanel1.class, "InsertSourceVisualPanel1.jLabel1.text")); // NOI18N

        labelTextField.setText(org.openide.util.NbBundle.getMessage(InsertSourceVisualPanel1.class, "InsertSourceVisualPanel1.labelTextField.text")); // NOI18N
        labelTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                labelTextFieldKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                labelTextFieldKeyReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(labelTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(labelTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 272, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void labelTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_labelTextFieldKeyTyped
    }//GEN-LAST:event_labelTextFieldKeyTyped

    private void labelTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_labelTextFieldKeyReleased
        setLabel(labelTextField.getText());
    }//GEN-LAST:event_labelTextFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField labelTextField;
    // End of variables declaration//GEN-END:variables
}
