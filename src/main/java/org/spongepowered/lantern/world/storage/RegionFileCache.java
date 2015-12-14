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

/*
 ** 2011 January 5
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 **/

/*
 * 2011 February 16
 *
 * This source code is based on the work of Scaevolus (see notice above).
 * It has been slightly modified by Mojang AB to limit the maximum cache
 * size (relevant to extremely big worlds on Linux systems with limited
 * number of file handles). The region files are postfixed with ".mcr"
 * (Minecraft region file) instead of ".data" to differentiate from the
 * original McRegion files.
 *
 */

/*
 * Some changes have been made as part of the Glowstone project.
 */

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Maps;
import org.spongepowered.lantern.SpongeImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A simple cache and wrapper for efficiently accessing multiple RegionFiles
 * simultaneously.
 */
public class RegionFileCache {

    private static final int MAX_CACHE_SIZE = 256;

    private final Cache<Path, RegionFile> cache;

    private final String extension;
    private final Path regionDir;

    public RegionFileCache(Path basePath, String extension) {
        this.extension = extension;
        this.regionDir = basePath.resolve("region");

        try {
            Files.createDirectories(regionDir);
        } catch (IOException e){
            SpongeImpl.getLogger().warn("Failed to create directory: " + regionDir);
        }

        this.cache = CacheBuilder.<Path, RegionFile>newBuilder()
                .concurrencyLevel(4)
                .maximumSize(MAX_CACHE_SIZE)
                .softValues()
                .removalListener(new RemovalListener<Path, RegionFile>() {
                    @Override
                    public void onRemoval(RemovalNotification<Path, RegionFile> notification) {
                        if(notification.getValue() != null) {
                            try {
                                notification.getValue().close();
                            } catch (IOException ignored) {}
                        }
                    }
                })
                .build();
    }

    public RegionFile getRegionFile(int chunkX, int chunkZ) throws IOException {
        Path file = regionDir.resolve("r." + (chunkX >> 5) + "." + (chunkZ >> 5) + extension);

        try {
            return cache.get(file, () -> new RegionFile(file));
        } catch (ExecutionException e) {
            throw new IOException("Unable to load region file " + file.toString());
        }
    }

    public Collection<RegionFile> getCreatedRegions() throws IOException {
        Map<Path, RegionFile> regions = Maps.newHashMap(cache.asMap());
        Files.newDirectoryStream(regionDir, region -> !regions.containsKey(region)).forEach(regionPath -> {
            try {
                RegionFile region = new RegionFile(regionPath);
                regions.put(regionPath, region);
                cache.put(regionPath, region);
            } catch (IOException e) {
                SpongeImpl.getLogger().error("Unable to load region " + regionPath.toString());
            }
        });

        return regions.values();
    }

    public void clear() {
        cache.invalidateAll();
    }

}
