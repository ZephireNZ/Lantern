package org.spongepowered.lantern.world.storage;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.storage.ChunkDataStream;

import javax.annotation.Nullable;

public class LanternChunkDataStream implements ChunkDataStream {

    @Nullable
    @Override
    public DataContainer next() {
        return null; //TODO: Implement
    }

    @Override
    public boolean hasNext() {
        return false; //TODO: Implement
    }

    @Override
    public int available() {
        return 0; //TODO: Implement
    }

    @Override
    public void reset() {
        //TODO: Implement
    }
}
