/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.browser.insertion;

import com.google.common.collect.Sets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.openide.nodes.AbstractNode;
import org.openide.util.ChangeSupport;
import us.physion.ovation.domain.AnalysisRecord;
import us.physion.ovation.domain.Epoch;
import us.physion.ovation.domain.EpochContainer;
import us.physion.ovation.domain.EpochGroup;
import us.physion.ovation.domain.Experiment;
import us.physion.ovation.domain.OvationEntity;
import us.physion.ovation.domain.Project;
import us.physion.ovation.domain.Source;
import us.physion.ovation.domain.mixin.DataElement;
import us.physion.ovation.domain.mixin.DataElementContainer;
import us.physion.ovation.domain.mixin.EpochGroupContainer;
import us.physion.ovation.ui.browser.EntityChildren;
import us.physion.ovation.ui.browser.EntityWrapper;
import us.physion.ovation.ui.browser.FilteredEntityChildren;
import us.physion.ovation.ui.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class NamedDataElementSelectionPanel extends NamedEntitySelectionPanel{

    public NamedDataElementSelectionPanel(ChangeSupport cs)
    {
        super(cs, "Select the inputs for this analysis record.");
    }
    @Override
    public void resetEntities() {
        List<EntityWrapper> wrappers = new LinkedList();
        for (Project p : context.getProjects())
        {
            wrappers.add(new EntityWrapper(p));
        }
        em.setRootContext(new AbstractNode(new EntityChildren(wrappers)));
    }
    
    @Override
    public void addSelectedEntity()
    {
        IEntityWrapper e = getSelectedEntity();
        if (e != null)
        {
            LinkedList<DataElement> dataElements = new LinkedList();
            getDataElementsFromEntity(e.getEntity(), dataElements);
            for (DataElement element : dataElements)
            {
                addEntity(element.getName(), element);
            }
        }
    }

    private void getDataElementsFromEntity(OvationEntity e, List<DataElement> list)
    {
        if (e instanceof Project)
        {
           for (Experiment child : ((Project)e).getExperiments()) {
               getDataElementsFromEntity(child, list);
           }
        }
        if (e instanceof EpochGroupContainer)
        {
            for (EpochGroup child : ((EpochGroupContainer)e).getEpochGroups()) {
               getDataElementsFromEntity(child, list);
           }
        }
        if (e instanceof EpochContainer)
        {
            for (Epoch child : ((EpochContainer)e).getEpochs()) {
               getDataElementsFromEntity(child, list);
           }
        }
        if (e instanceof DataElementContainer)
        {
            for (DataElement child : ((DataElementContainer)e).getDataElements().values()) {
               getDataElementsFromEntity(child, list);
           }
        }
        if( e instanceof DataElement)
         {
             list.add((DataElement)e);
         }
         
    }
    
    @Override
    String getTitle() {
        return "Select named inputs";
    }
    
}
