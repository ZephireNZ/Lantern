package org.spongepowered.lantern.world;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.lantern.Lantern;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.world.storage.LanternWorldStorage;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChunkManager {

    /**
     * The world this ChunkManager is managing.
     */
    private final LanternWorld world;

    /**
     * The storage for this world.
     */
    private final LanternWorldStorage storage;

    /**
     * A map of chunks currently loaded in memory.
     */
    private final ConcurrentMap<Vector3i, LanternChunk> chunks = new ConcurrentHashMap<>();

    public ChunkManager(LanternWorld world) {
        this.world = world;
        this.storage = world.getWorldStorage();
        //TODO: Biome Grid
    }

    public LanternChunk getChunk(int x, int z) {
        Vector3i location = new Vector3i(x, 0, z);
        if(chunks.containsKey(location)) {
            return chunks.get(location);
        }

        LanternChunk chunk = new LanternChunk(world, location);
        LanternChunk prev = chunks.putIfAbsent(location, chunk);
        // If a chunk is loaded by the time we check, use that one instead
        return prev == null ? chunk : prev;
    }

    /**
     * Checks if the Chunk at the specified coordinates is loaded.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return true if the chunk is loaded, otherwise false.
     */
    public boolean isChunkLoaded(int x, int z) {
        Vector3i key = new Vector3i(x, 0, z);
        return chunks.containsKey(key) && chunks.get(key).isLoaded();
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        Vector3i location = new Vector3i(x, 0, z);
        LanternChunk chunk = getChunk(x, z);

        // try to load chunk
        try {
            Optional<DataContainer> data = storage.getChunkData(location).get();
            if(data.isPresent()) {
                chunk.load(data.get());
                Lantern.post(SpongeEventFactory.createLoadChunkEvent(Sponge.getGame(), Cause.of(SpongeImpl.getGame().getServer()), chunk));
                return true;
            }
        } catch (Exception e) {
            SpongeImpl.getLogger().error("Error while loading chunk (" + x + "," + z + ")", e);
            // an error in chunk reading may have left the chunk in an invalid state
            // (i.e. double initialization errors), so it's forcibly unloaded here
            chunk.unloadChunk();
        }

        // No generation needed
        if(!generate) return false;

        try {
            generateChunk(chunk, x, z);
        } catch (Throwable ex) {
             SpongeImpl.getLogger().error("Error while generating chunk (" + x + "," + z + ")", ex);
            return false;
        }

        Lantern.post(SpongeEventFactory.createLoadChunkEvent(Sponge.getGame(), Cause.of(SpongeImpl.getGame().getServer()), chunk));
        return true;
    }

    private void generateChunk(LanternChunk chunk, int x, int z) {
        //TODO: Implement
    }
}
