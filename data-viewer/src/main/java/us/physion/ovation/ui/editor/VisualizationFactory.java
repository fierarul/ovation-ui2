/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.ui.editor;

import us.physion.ovation.domain.mixin.DataElement;


/**
 *
 * @author huecotanks
 */
public interface VisualizationFactory {
    public Visualization createVisualization(DataElement r);
    public int getPreferenceForDataContainer(DataElement r);
}