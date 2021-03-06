package us.physion.ovation.ui.notes;

import java.awt.BorderLayout;
import java.awt.Color;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import us.physion.ovation.ui.interfaces.IEntityWrapper;

@ConvertAsProperties(
    dtd = "-//us.physion.ovation.ui.notes.api//ModernNotes//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "ModernNotesTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
//XXX: should be rightSlidingSide
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "us.physion.ovation.ui.notes.api.ModernNotesTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
    displayName = "#CTL_ModernNotesAction",
preferredID = "ModernNotesTopComponent")
@Messages({
    "CTL_ModernNotesAction=Notes",
    "CTL_ModernNotesTopComponent=Notes",
    "HINT_ModernNotesTopComponent=See notes",
    "CTL_AskBeforeDeleting=Ask before deletion",
    "CTL_Delete=Delete",
    "CTL_DeleteConfirm=Do you want to delete the note? A deleted note cannot be restored.",
    "CTL_Refresh=Refresh",
    "CTL_Save=Save",
    "CTL_UserGreeting=Welcome",
    "CTRL_DeleteConfirmTitle=Are you sure?",
    "CTRL_ShowGravatar=Show Gravatar icon",
    "HINT_Refresh=Refresh",
    "HINT_ShowGravatar=Show Gravatar icon"
})
public final class ModernNotesTopComponent extends TopComponent {

    private final Lookup.Result<IEntityWrapper> result;
    private final OvationNotes notes;

    public ModernNotesTopComponent() {
        setName(Bundle.CTL_ModernNotesTopComponent());
        setToolTipText(Bundle.HINT_ModernNotesTopComponent());

        setLayout(new BorderLayout());
        setBackground(Color.white);

        add(notes = new OvationNotes(), BorderLayout.CENTER);

        result = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class);
        result.addLookupListener(new LookupListener() {
            @Override
            public void resultChanged(LookupEvent le) {
                notes.refreshNotes(result.allInstances());
            }
        });
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
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
}
