package org.spongepowered.lantern.world.storage;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.storage.ChunkDataStream;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class MultiRegionStream implements ChunkDataStream {

    private Collection<RegionStream> streams;
    @Nullable
    private RegionStream current;
    private Iterator<RegionStream> iterator;
    private Set<RegionStream> remaining;

    public MultiRegionStream(RegionFileCache cache) {
        try {
            Collection<RegionFile> regions = cache.getCreatedRegions();
            streams = regions.stream().map(RegionStream::new).collect(Collectors.toSet());
            iterator = streams.iterator();
            remaining = Sets.newHashSet(streams);
            if(iterator.hasNext()) {
                current = iterator.next();
            }
        } catch (IOException e) {
            streams = Collections.emptySet();
            current = null;
            iterator = Iterators.emptyIterator();
        }
    }

    @Nullable
    @Override
    public DataContainer next() {
        traverseIterators();

        if(current == null) return null;
        if(current.hasNext()) return current.next();
        return null;
    }

    @Override
    public boolean hasNext() {
        traverseIterators();
        return current != null && current.hasNext();
    }

    @Override
    public int available() {
        int count = 0;
        for(RegionStream stream : streams) {
            count += stream.available();
        }
        return count;
    }

    @Override
    public void reset() {
        streams.forEach(RegionStream::reset);
        iterator = streams.iterator();
        remaining = Sets.newHashSet(streams);
        if(iterator.hasNext()) {
            current = iterator.next();
        }
    }

    /**
     * If a chunk iterator is empty, this will iterate regions looking for one
     * which contains a non-empty chunk iterator.
     */
    private void traverseIterators() {
        if(current == null) return;
        while(!current.hasNext() && iterator.hasNext()) {
            remaining.remove(current);
            current = iterator.next();
        }
    }
}
