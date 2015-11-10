package org.spongepowered.lantern.entity;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.lantern.data.LanternDataHolder;

public abstract class LanternEntity extends LanternDataHolder implements Entity {

    public boolean shouldSave() {
        return false; //TODO Implement
    }


    //TODO

}
