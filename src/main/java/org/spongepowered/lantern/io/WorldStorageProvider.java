/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.lantern.io;

import org.spongepowered.lantern.world.LanternWorld;

import java.io.File;

/**
 * Interface for providers of world data storage, including chunks and various
 * metadata.
 */
public interface WorldStorageProvider {

    /**
     * Initialize the storage to correspond to the given world.
     * @param world The world to use.
     */
    void setWorld(LanternWorld world);

    /**
     * Get the folder holding the world data, if the filesystem is being used.
     * @return The world folder, or null.
     */
    File getFolder();

    /**
     * Get the {@link ChunkIoService} for this world, to be used for reading
     * and writing chunk data.
     * @return The {@link ChunkIoService}.
     */
    ChunkIoService getChunkIoService();

    /**
     * Get the {@link WorldMetadataService} for this world, to be used for
     * reading and writing world metadata (seed, time, so on).
     * @return The {@link WorldMetadataService}.
     */
    WorldMetadataService getMetadataService();

    /**
     * Get the {@link PlayerDataService} for this world, to be used for
     * reading and writing data for online and offline players.
     * @return The {@link PlayerDataService}.
     */
    PlayerDataService getPlayerDataService();

    StructureDataService getStructureDataService();

    ScoreboardIoService getScoreboardIoService();
}
