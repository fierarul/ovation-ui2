package us.physion.ovation.ui.editor;

import com.google.common.collect.Sets;
import javax.swing.JComponent;
import us.physion.ovation.domain.OvationEntity;
import us.physion.ovation.domain.mixin.DataElement;

/**
 *
 * @author huecotanks
 */
public class DefaultVisualizationFactory implements VisualizationFactory
{
    @Override
    public DataVisualization createVisualization(DataElement r) {
        return new DefaultVisualization(r);
    }

    @Override
    public int getPreferenceForDataContainer(DataElement r) {
        return 2;
    }

    class DefaultVisualization extends AbstractDataVisualization    {
        final DataElement data;
        DefaultVisualization(DataElement d)
        {
            data = d;
        }

        @Override
        public JComponent generatePanel() {
            return new DefaultDataPanel(data);
        }

        @Override
        public boolean shouldAdd(DataElement r) {
            return false;
        }

        @Override
        public void add(DataElement r) {
            throw new UnsupportedOperationException("Create a new Visualization, rather than adding to an existing one");
        }

        @Override
        public Iterable<? extends OvationEntity> getEntities() {
            return Sets.newHashSet(data);
        }

    }

}
