/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.importer;

import us.physion.ovation.ui.browser.insertion.KeyValuePanel;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import us.physion.ovation.domain.EquipmentSetup;
import us.physion.ovation.ui.interfaces.BasicWizardPanel;

/**
 *
 * @author huecotanks
 */
class EquipmentSetupController extends BasicWizardPanel{

    KeyValuePanel c;
    public EquipmentSetupController() {
    }

    @Override
    public Component getComponent() {
        if (c == null)
            c = new KeyValuePanel(changeSupport, "Experiment: Equipment Setup", "This is the equipment setup for the current Experiment. Please make sure the device information below is valid for the entire experiment.");
        return c;
    }

    @Override
    public void readSettings(WizardDescriptor wd) {
        Map<String, Object> equipmentSetup = (Map<String, Object>) wd.getProperty("equipmentSetup");
        if (equipmentSetup == null) {
            equipmentSetup = new HashMap();
            Map<String, Map<String, Object>> devices = (Map<String, Map<String, Object>>) wd.getProperty("devices");
            if (devices != null) {
                for (String deviceName : devices.keySet()) {
                    Map<String, Object> device = devices.get(deviceName);
                    //name, manufacturer, properties
                    Map<String, Object> properties = (Map<String, Object>) device.get("properties");
                    String prefix = device.get("ID") + ".".replace("null", "").replace("\\.\\.", "\\.");
                    for (String key : properties.keySet()) {
                        equipmentSetup.put(prefix + key, properties.get(key));
                    }
                }
            }
        }
        
        KeyValuePanel panel = (KeyValuePanel)getComponent();
        panel.setParameters(equipmentSetup);
    }
    
    @Override
    public void storeSettings(WizardDescriptor wd) {
        KeyValuePanel panel = (KeyValuePanel)getComponent();
        wd.putProperty("equipmentSetup", panel.getParameters());
    }

    @Override
    public boolean isValid() {
        return true;
    }
    
}