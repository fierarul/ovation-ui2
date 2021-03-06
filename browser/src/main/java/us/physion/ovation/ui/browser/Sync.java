/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.browser;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.slf4j.LoggerFactory;
import us.physion.ovation.exceptions.OvationException;
import us.physion.ovation.ui.interfaces.ConnectionProvider;
import us.physion.ovation.ui.interfaces.EventQueueUtilities;

@ActionID(
        category = "Edit",
        id = "us.physion.ovation.ui.browser.Sync")
@ActionRegistration(
        iconBase = "us/physion/ovation/ui/browser/sync.png",
        displayName = "#CTL_Sync")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 1300),
    @ActionReference(path = "Toolbars/Data", position = 100),
    @ActionReference(path = "Shortcuts", name = "DC-S")
})
@Messages("CTL_Sync=Sync")
public final class Sync implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        EventQueueUtilities.runOffEDT(new Runnable() {

            @Override
            public void run() {
                LoggerFactory.getLogger(Sync.class).debug("Syncing data from cloud...");
                final ProgressHandle ph = ProgressHandleFactory.createHandle("Syncing data from cloud...");
                ph.start();
                ListenableFuture<Boolean> l = Lookup.getDefault().lookup(ConnectionProvider.class).getDefaultContext().getCoordinator().sync();
                Futures.addCallback(l, new FutureCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        ph.finish();
                        if ((Boolean) result) {
                            BrowserUtilities.resetView();
                        } else {
                            throw new OvationException("Something went wrong during sync. See log for details");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ph.finish();
                        throw new OvationException(t);
                    }
                });
            }
        });

    }
}
