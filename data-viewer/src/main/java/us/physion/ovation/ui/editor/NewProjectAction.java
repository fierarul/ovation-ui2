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

import java.awt.event.ActionEvent;
import org.joda.time.DateTime;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import us.physion.ovation.DataContext;
import us.physion.ovation.domain.Project;
import us.physion.ovation.ui.browser.BrowserUtilities;
import us.physion.ovation.ui.interfaces.ConnectionProvider;

@ActionID(
        category = "Edit",
        id = "us.physion.ovation.ui.browser.insertion.NewProjectAction"
)
@ActionRegistration(
        displayName = "#CTL_NewProjectAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File/New", position = 1300),
    @ActionReference(path = "Shortcuts", name = "SD-P")
})
@Messages({"CTL_NewProjectAction=Project...",
    "CTL_Default_Project_Name=New Project"
})
public final class NewProjectAction extends AbstractNewEntityAction<Project> {

    @Override
    public void actionPerformed(final ActionEvent e) {
        DataContext ctx = Lookup.getDefault().lookup(ConnectionProvider.class).getDefaultContext();

        final Project p = ctx.insertProject(Bundle.CTL_Default_Project_Name(),
                "",
                new DateTime());

        BrowserUtilities.reloadView(BrowserUtilities.PROJECT_BROWSER_ID);
        selectNode(p, BrowserUtilities.PROJECT_BROWSER_ID);
    }
}
