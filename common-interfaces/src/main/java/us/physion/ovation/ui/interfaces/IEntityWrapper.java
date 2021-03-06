package us.physion.ovation.ui.interfaces;

import java.awt.Color;
import us.physion.ovation.domain.OvationEntity;

/**
 *
 * @author huecotanks
 */
public interface IEntityWrapper extends PropertyChange {

    public static final String PROP_ENTITY_UPDATE = "entity_update";
    public static final String PROP_NAME = "name";

    String getDisplayName();

    Color getDisplayColor();

    OvationEntity getEntity();

    OvationEntity getEntity(boolean includingTrash);

    <T extends OvationEntity> T getEntity(Class<T> clazz);

    Class getType();

    String getURI();

    boolean isLeaf();

    public boolean canRename();

    public void setName(String s);

    public String getName();
}
