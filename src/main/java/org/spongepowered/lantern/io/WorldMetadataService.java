package org.spongepowered.lantern.io;

import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;

/**
 * Provider of I/O for world metadata.
 */
public interface WorldMetadataService {

    /**
     * Reads the world's metadata from storage, including final values such as
     * seed and UUID that are only set on first load.
     * @return A {@link WorldProperties}
     * @throws IOException if an I/O error occurs.
     */
    WorldProperties readWorldData() throws IOException;

    /**
     * Write the world's metadata to storage.
     * @throws IOException if an I/O error occurs.
     */
    void writeWorldData() throws IOException;

}
