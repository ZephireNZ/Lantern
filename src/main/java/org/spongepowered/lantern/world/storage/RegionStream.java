package org.spongepowered.lantern.world.storage;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.storage.ChunkDataStream;
import org.spongepowered.lantern.util.nbt.NbtDataInputStream;

import java.io.IOException;

import javax.annotation.Nullable;

public class RegionStream implements ChunkDataStream {

    private final RegionFile region;
    private RegionFile.RegionIterator iterator;

    public RegionStream(RegionFile region) {
        this.region = region;
        this.iterator = region.iterator();
    }

    @Nullable
    @Override
    public DataContainer next() {
        if(!hasNext()) return null;

        try (NbtDataInputStream is = new NbtDataInputStream(iterator.next())) {
            return is.read();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public int available() {
        return iterator.remaining();
    }

    @Override
    public void reset() {
        iterator = region.iterator();
    }
}
