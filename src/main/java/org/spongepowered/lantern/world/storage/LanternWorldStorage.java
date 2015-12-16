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
package org.spongepowered.lantern.world.storage;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.world.storage.ChunkDataStream;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.storage.WorldStorage;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.scheduler.LanternScheduler;
import org.spongepowered.lantern.util.nbt.NbtDataInputStream;
import org.spongepowered.lantern.util.nbt.NbtDataOutputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class LanternWorldStorage implements WorldStorage {

    private final RegionFileCache regions;
    private final ListeningExecutorService executor;
    private final Path worldDir;

    public LanternWorldStorage(Path world) {
        this.worldDir = world;
        this.regions = new RegionFileCache(world, ".mca");
        this.executor = MoreExecutors.listeningDecorator(LanternScheduler.getInstance().createAsyncExecutor(SpongeImpl.getInstance()));
    }

    @Override
    public ChunkDataStream getGeneratedChunks() {
        return new MultiRegionStream(regions);
    }

    @Override
    public ListenableFuture<Boolean> doesChunkExist(Vector3i chunkCoords) {
        return executor.submit(() -> {
            try {
                RegionFile region = regions.getRegionFile(chunkCoords.getX(), chunkCoords.getZ());
                return region.hasChunk(chunkCoords.getX(), chunkCoords.getZ());
            } catch (IOException e) {
                return false;
            }

        });
    }

    @Override
    public ListenableFuture<Optional<DataContainer>> getChunkData(Vector3i chunkCoords) {
        return this.executor.submit(() -> {
            try {
                RegionFile region = regions.getRegionFile(chunkCoords.getX(), chunkCoords.getZ());
                if(!region.hasChunk(chunkCoords.getX(), chunkCoords.getZ())) return Optional.empty();

                DataInputStream in = region.getChunkDataInputStream(chunkCoords.getX(), chunkCoords.getZ());
                if(in == null) return Optional.empty();

                try (NbtDataInputStream nbt = new NbtDataInputStream(in, false)) {
                    return Optional.of(nbt.read());
                }
            } catch (IOException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public WorldProperties getWorldProperties() {
        LanternWorldProperties properties = new LanternWorldProperties();

        Path levelFile = worldDir.resolve("level.dat");
        if (Files.isRegularFile(levelFile)) {
            try (NbtDataInputStream in = new NbtDataInputStream(Files.newInputStream(levelFile))) {
                DataView level = in.read();
                properties.loadVanillaData(level);
            } catch (IOException e) {
                handleWorldException("level.dat", e);
            }
        }

        Path spongeFile = worldDir.resolve("level_sponge.dat");
        if (Files.isRegularFile(spongeFile)) {
            try (NbtDataInputStream in = new NbtDataInputStream(Files.newInputStream(spongeFile))) {
                DataView sponge = in.read();
                properties.loadSpongeData(sponge);
            } catch (IOException e) {
                handleWorldException("level_sponge.dat", e);
            }
        }

        return properties;
    }

    private void handleWorldException(String file, IOException e) {
        SpongeImpl.getLogger().error("Unable to access " + file + " for world " + worldDir.getFileName(), e);
    }

    public void writeWorldProperties(LanternWorldProperties properties) throws IOException {
        try (NbtDataOutputStream nbtOut = new NbtDataOutputStream(Files.newOutputStream(worldDir.resolve("level.dat")))) {
            nbtOut.write(properties.getVanillaRoot());
        } catch (IOException e) {
            handleWorldException("level.dat", e);
        }

        try (NbtDataOutputStream nbtOut = new NbtDataOutputStream(Files.newOutputStream(worldDir.resolve("level_sponge.dat")))) {
            nbtOut.write(properties.getSpongeRoot());
        } catch (IOException e) {
            handleWorldException("level_sponge.dat", e);
        }
    }
}
