/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.browser.insertion;

import java.util.Iterator;
import javax.swing.JPanel;
import org.openide.util.ChangeSupport;
import ovation.AnalysisRecord;

public final class InsertAnalysisRecordVisualPanel2 extends JPanel {

    ChangeSupport cs;
    Iterator<AnalysisRecord> inputs;
    /**
     * Creates new form InsertAnalysisRecordVisualPanel2
     */
    public InsertAnalysisRecordVisualPanel2(ChangeSupport cs) {
        initComponents();
        this.cs = cs;
    }

    @Override
    public String getName() {
        return "Step #2";
    }
    
    public Iterator<AnalysisRecord> getInputs()
    {
        return inputs;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(InsertAnalysisRecordVisualPanel2.class, "InsertAnalysisRecordVisualPanel2.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(350, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addContainerGap(267, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
