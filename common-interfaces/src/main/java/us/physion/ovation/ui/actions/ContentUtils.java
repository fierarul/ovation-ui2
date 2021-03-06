/*
 * Copyright (C) 2015 Physion LLC
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
package us.physion.ovation.ui.actions;

import us.physion.ovation.domain.Resource;
import us.physion.ovation.domain.Revision;
import us.physion.ovation.domain.mixin.Content;

/**
 *
 * @author barry
 */
public class ContentUtils {
    public static String contentLabel(Content r) {
        if (r instanceof Resource) {
            return ((Resource) r).getLabel();
        } else if (r instanceof Revision) {
            return ((Revision) r).getResource().getLabel();
        } else {
            return r.getFilename();
        }
    }
}
