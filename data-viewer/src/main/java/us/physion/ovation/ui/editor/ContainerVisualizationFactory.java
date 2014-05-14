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

import org.openide.nodes.Node;
import us.physion.ovation.domain.OvationEntity;
import us.physion.ovation.ui.interfaces.IEntityNode;

public interface ContainerVisualizationFactory {

    public ContainerVisualization createVisualization(IEntityNode e);

    public int getPreferenceForContainer(OvationEntity e);
}