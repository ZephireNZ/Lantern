package org.spongepowered.lantern.block.tileentity;

import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataView;
import org.spongepowered.lantern.data.LanternDataHolder;

public abstract class LanternTileEntity extends LanternDataHolder implements TileEntity {

    public LanternTileEntity(DataView dataView) {
        super(dataView);
        //TODO: Implement
    }

    public void loadNbt(DataView tileEntityTag) {
        // TODO: Implement
    }

    public void saveNbt(DataView tag) {
        // TODO: Implement
    }

    //TODO

}
