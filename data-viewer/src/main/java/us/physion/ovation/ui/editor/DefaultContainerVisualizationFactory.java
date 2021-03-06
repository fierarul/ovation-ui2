/*
 * Copyright (C) 2014 Physion LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package us.physion.ovation.ui.editor;

import com.google.common.collect.Sets;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.openide.util.lookup.ServiceProvider;
import us.physion.ovation.domain.OvationEntity;
import us.physion.ovation.ui.interfaces.IEntityNode;

@ServiceProvider(service = ContainerVisualizationFactory.class)
public class DefaultContainerVisualizationFactory implements ContainerVisualizationFactory {

    @Override
    public ContainerVisualization createVisualization(IEntityNode e) {
        return new DefaultVisualization(e.getEntity());
    }

    @Override
    public int getPreferenceForContainer(OvationEntity e) {
        return 1;
    }

    class DefaultVisualization implements ContainerVisualization {

        OvationEntity entity;

        DefaultVisualization(OvationEntity e) {
            entity = e;
        }

        @Override
        public JComponent generatePanel() {
            if (entity == null) {
                JPanel result = new JPanel();
                result.setBackground(Color.white);

                return result;
            }
            return new DefaultContainerPanel(entity);
        }

        @Override
        public Iterable<? extends OvationEntity> getEntities() {
            return Sets.newHashSet(entity);
        }

    }

}
