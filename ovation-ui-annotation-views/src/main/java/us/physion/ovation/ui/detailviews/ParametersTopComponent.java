/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.detailviews;

import java.util.*;
import javax.swing.table.DefaultTableModel;
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
import ovation.*;
import us.physion.ovation.ui.*;
import us.physion.ovation.ui.interfaces.EventQueueUtilities;
import us.physion.ovation.ui.interfaces.IEntityWrapper;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//us.physion.ovation.detailviews//Parameters//EN",
autostore = false)
@TopComponent.Description(preferredID = "ParametersTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "leftSlidingSide", openAtStartup = true)
@ActionID(category = "Window", id = "us.physion.ovation.detailviews.ParametersTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ParametersAction",
preferredID = "ParametersTopComponent")
@Messages({
    "CTL_ParametersAction=Parameters",
    "CTL_ParametersTopComponent=Parameters Window",
    "HINT_ParametersTopComponent=This is a Parameters window"
})
public final class ParametersTopComponent extends TopComponent {

    Lookup.Result global;
    private Collection<? extends IEntityWrapper> entities;
    private LookupListener listener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {

            //TODO: we should have some other Interface for things that can setEntities the tags view
            //then we could get rid of the Library dependancy on the Explorer API
            if (TopComponent.getRegistry().getActivated() instanceof ExplorerManager.Provider)
            {
                update();
            }
        }
    };
    
    public void update()
    {
        EventQueueUtilities.runOffEDT(new Runnable() {

            public void run() {
                setEntities(global.allInstances());
            }
        });
    }
    
    public List<TableTreeKey> setEntities(final Collection<? extends IEntityWrapper> entities)
    {
        this.entities = entities;

        Map<String, Map<String, Object>> tables = new HashMap<String, Map<String, Object>>();
        for (IEntityWrapper ew : entities)
        {
            IEntityBase eb = ew.getEntity();
            if (eb instanceof Epoch)
            {
                addParams(tables, "Protocol Parameters", ((Epoch)eb).getProtocolParameters());
            }
            if (eb instanceof Stimulus)
            {
               addParams(tables, "Stimulus Parameters", ((Stimulus)eb).getStimulusParameters());
            }
            if (eb instanceof IIOBase)
            {
                addParams(tables, "Device Parameters", ((IIOBase)eb).getDeviceParameters());
            }
            if (eb instanceof AnalysisRecord)
            {
                addParams(tables, "Analysis Parameters", ((AnalysisRecord)eb).getAnalysisParameters());
            }
        }
        ArrayList<TableTreeKey> tableKeys = new ArrayList<TableTreeKey>();
        for (String key: tables.keySet())
        {
            tableKeys.add(new ParameterSet(key, tables.get(key)));
        }
        ((ScrollableTableTree)jScrollPane2).setKeys(tableKeys);
        return tableKeys;
    }
    
    public Collection<? extends IEntityWrapper> getEntities()
    {
        return entities;
    }
    
    public ScrollableTableTree getTableTree()
    {
        return ((ScrollableTableTree)jScrollPane2);
    }
    
    protected void setTableTree(ScrollableTableTree t)
    {
        jScrollPane2 = t;
    }
    
    public ParametersTopComponent() {
        initComponents();
        setName(Bundle.CTL_ParametersTopComponent());
        setToolTipText(Bundle.HINT_ParametersTopComponent());
        
        global = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class);
        global.addLookupListener(listener);
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new ScrollableTableTree();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
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

    private void addParams(Map<String, Map<String, Object>> tables, String paramName, Map<String, Object> parametersToAdd) {
        Map<String, Object> params = tables.get(paramName);
        if (params == null) {
            params = parametersToAdd;
        } else {
            //create a new map here, because the Ovation jar returns unmodifiable maps
            params = new HashMap<String, Object>();
            params.putAll(tables.get(paramName));
            for (String paramKey : parametersToAdd.keySet() ) {
                if (params.containsKey(paramKey))
                {
                    MultiUserParameter p = new MultiUserParameter(params.get(paramKey));
                    p.add(parametersToAdd.get(paramKey));
                    params.put(paramKey, p);
                }else{
                    params.put(paramKey, parametersToAdd.get(paramKey));
                }
            }
        }
        tables.put(paramName, params);
    }
}
