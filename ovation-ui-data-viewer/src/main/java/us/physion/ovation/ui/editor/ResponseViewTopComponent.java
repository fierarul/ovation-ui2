/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.editor;

import com.google.common.collect.Sets;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.display.SingleImagePanel;
import com.pixelmed.display.SourceImage;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.explorer.view.BeanTreeView;
import org.slf4j.LoggerFactory;
import us.physion.ovation.domain.AnalysisRecord;
import us.physion.ovation.domain.Epoch;
import us.physion.ovation.domain.Measurement;
import us.physion.ovation.domain.mixin.DataElement;
import us.physion.ovation.domain.mixin.DataElementContainer;
import us.physion.ovation.ui.interfaces.ClickableCellEditor;
import us.physion.ovation.ui.interfaces.EventQueueUtilities;
import us.physion.ovation.ui.interfaces.IEntityWrapper;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//us.physion.ovation.editor//ResponseView//EN",
autostore = false)
@TopComponent.Description(preferredID = "ResponseViewTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "us.physion.ovation.editor.ResponseViewTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ResponseViewAction",
preferredID = "ResponseViewTopComponent")
@Messages({
    "CTL_ResponseViewAction=Selection View",
    "CTL_ResponseViewTopComponent=Selection Viewer",
    "HINT_ResponseViewTopComponent=Displays the current selected entity, if possible"
})
public final class ResponseViewTopComponent extends TopComponent {

    Lookup.Result global;
    List<ResponsePanel> responsePanels = new ArrayList<ResponsePanel>();
    ChartTableModel chartModel = new ChartTableModel(responsePanels);
    Lookup l;
    Future updateEntitySelection;
    ResponseCellRenderer cellRenderer = new ResponseCellRenderer();
    private LookupListener listener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {

            //TODO: we should have some other Interface for things that can update the tags view
            //then we could get rid of the Library dependancy on the Explorer API
            if (TopComponent.getRegistry().getActivated() instanceof ExplorerManager.Provider) {
                resetTableEditor();
                updateEntitySelection();
            }
        }
    };

    public ResponseViewTopComponent() {
        initTopComponent();
    }

    private void initTopComponent() {
        initComponents();
        
        setName("Data Viewer");//Bundle.CTL_ResponseViewTopComponent());
        setToolTipText("Displays the selected DataElements");//Bundle.HINT_ResponseViewTopComponent());
        global = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class);
        global.addLookupListener(listener);
        jTable1.setDefaultRenderer(ResponsePanel.class, cellRenderer);
        jTable1.setDefaultEditor(ResponsePanel.class, new ClickableCellEditor(cellRenderer));
        cellRenderer.setTable(jTable1);
        jTable1.setVisible(true);
        jTable1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        responseListPane.setVisible(true);
    }
    //delete - creating a new table takes too long
    private JTable initTable()
    {
        JTable table = new ResponseTable();
        table.setDefaultEditor(ResponsePanel.class, new ClickableCellEditor(cellRenderer));
        cellRenderer.setTable(table);
        table.setVisible(true);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        table.setDefaultEditor(ResponsePanel.class, new ClickableCellEditor(cellRenderer));
        return table;
    }
    
    private void resetTableEditor()
    {
        jTable1.setDefaultEditor(ResponsePanel.class, new ClickableCellEditor(cellRenderer));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        responseListPane = new BeanTreeView();
        jTable1 = new ResponseTable();

        jTable1.setModel(chartModel);
        responseListPane.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(responseListPane, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(responseListPane, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable jTable1;
    private javax.swing.JScrollPane responseListPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        //responseListPane.setVisible(false);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    protected void updateEntitySelection() {
        if (jTable1.isEditing())
        {
            jTable1.setCellEditor(null);
        }
        final Collection<? extends IEntityWrapper> entities = global.allInstances();
        Runnable r = new Runnable() {

            @Override
            public void run() {
                updateEntitySelection(entities);
            }
        };

        if (updateEntitySelection != null && !updateEntitySelection.isDone())
        {
            updateEntitySelection.cancel(true);
            LoggerFactory.getLogger(ResponseViewTopComponent.class).debug("Cancelled other thread");
        }
        updateEntitySelection = EventQueueUtilities.runOffEDT(r);
    }

    protected List<Visualization> updateEntitySelection(Collection<? extends IEntityWrapper> entities) {
        
        LinkedList<DataElement> responseList = new LinkedList<DataElement>();

        Iterator i = entities.iterator();
        while(i.hasNext())
        {
            IEntityWrapper ew = (IEntityWrapper)i.next();
            if (DataElementContainer.class.isAssignableFrom(ew.getType())) {
                DataElementContainer container = (DataElementContainer) (ew.getEntity());//getEntity gets the context for the given thread
                responseList.addAll(Sets.newHashSet(container.getDataElements().values()));
                
                if (container instanceof Epoch)
                {
                    for (AnalysisRecord a : ((Epoch)container).getAnalysisRecords()) {
                        responseList.addAll(Sets.newHashSet(a.getOutputs().values()));
                    }
                }

            } else if (DataElement.class.isAssignableFrom(ew.getType())) {
                responseList.add((DataElement)ew.getEntity());
            }
        }

        List<Visualization> responseGroups = new LinkedList<Visualization>();

        for (DataElement rw : responseList) {
            boolean added = false;
            for (Visualization group : responseGroups) {
                if (group.shouldAdd(rw)) {
                    group.add(rw);
                    added = true;
                    break;
                }
            }
            if (!added) {
                responseGroups.add(ResponseWrapperFactory.create(rw).createVisualization(rw));
            }
        }
        
        EventQueueUtilities.runOnEDT(updateChartRunnable(responseGroups));
        return responseGroups;
    }
    
    //for debugging
    protected static void error(String s)
    {
        JDialog d = new JDialog(new JFrame(), true);
        d.setPreferredSize(new Dimension(500, 500));
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setLocationRelativeTo(null);
        JLabel l = new JLabel();
        l.setText(s);
        d.add(l);
        d.setVisible(true);
    }

    private Runnable updateChartRunnable(final List<Visualization> responseGroups) {
        final int height = this.getHeight();
        return new Runnable() {

            @Override
            public void run() {
                int initialSize = responsePanels.size();
                while (!responsePanels.isEmpty()) {
                    responsePanels.remove(0);
                }
                
                if (responseGroups.size() != 0) 
                {
                    /*int nonStrictHeight = height/responseGroups.size();
                    jTable1.setRowHeight(nonStrictHeight);
                    for (Visualization c : responseGroups) {
                        Component p = c.generatePanel();
                        responsePanels.add(new ResponsePanel(p));
                    }*/
                    
                    //This is for setting each row in the table to a more appropriate height
                    int[] rowHeights = new int[responseGroups.size()];//highest allowable height for each row
                    ArrayList<Integer> strictHeights = new ArrayList<Integer>();
                    int totalStrictHeight = 0;
                    int flexiblePanels = 0;
                    int minHeight = 150;//min height of a chart
                    
                    for (Visualization c : responseGroups) {
                        Component p = c.generatePanel();

                        int row = responsePanels.size();
                        if (p instanceof StrictSizePanel)
                        {
                            int strictHeight = ((StrictSizePanel)p).getStrictSize().height;
                            strictHeights.add(strictHeight);
                            rowHeights[row] = strictHeight;
                            totalStrictHeight += strictHeight;
                        }  else{
                            rowHeights[row] = Integer.MAX_VALUE;
                            flexiblePanels++;
                        }  
                        responsePanels.add(new ResponsePanel(p));
                    }
                    int flexiblePanelHeight = minHeight;
                    if (flexiblePanels != 0)
                    {
                        flexiblePanelHeight = Math.max(minHeight, (height - totalStrictHeight)/flexiblePanels);
                    }
                    for (int i=0; i<rowHeights.length; ++i)
                    {
                        if (rowHeights[i] == Integer.MAX_VALUE)
                            rowHeights[i] = flexiblePanelHeight;
                    }
                    //I have to do this, so theres a smooth transition between 
                    if (jTable1.getRowHeight() != rowHeights[0])
                        jTable1.setRowHeight(rowHeights[0]);
                    cellRenderer.setHeights(rowHeights);
                    ((ResponseTable)jTable1).setHeights(rowHeights);
                    int tableHeight = totalStrictHeight + flexiblePanels*flexiblePanelHeight;
                    jTable1.setSize(jTable1.getWidth(), tableHeight);
                    if (jTable1.isEditing())
                        chartModel.fireTableStructureChanged();
                    else
                        chartModel.fireTableDataChanged();
                }
                else if (responseGroups.size() < initialSize) {
                    //jTable1.setSize(0, height);
                    chartModel.fireTableRowsDeleted(responseGroups.size(), initialSize - 1);
                }
            }
        };
    }

    //for testing
    protected ChartTableModel getChartTableModel() {
        return chartModel;
    }
}
