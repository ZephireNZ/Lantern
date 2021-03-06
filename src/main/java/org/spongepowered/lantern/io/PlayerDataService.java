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

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.lantern.entity.living.player.LanternPlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * Provider of I/O for player data.
 */
public interface PlayerDataService {

    /**
     * Begin reading player data for online or offline player loading. Some
     * attributes may be read before or without constructing a player entity,
     * see {@link PlayerReader} for more information. When finished with the
     * PlayerReader, {@link PlayerReader#close()} should be called.
     * @param uuid The UUID of the player to read data for.
     * @return The {@link PlayerReader} to use.
     */
    PlayerReader beginReadingData(UUID uuid);

    /**
     * Shorthand method to read data into an existing player entity.
     * @param player The target player.
     */
    void readData(LanternPlayer player);

    /**
     * Save all data for an online player.
     * @param player The source player.
     */
    void writeData(LanternPlayer player);

    /**
     * Get a collection of all known offline players. Currently online
     * players may or may not be included, but if they are, they will be
     * included in OfflinePlayer form.
     * @return All known offline players.
     */
    Collection<User> getOfflinePlayers();

    /**
     * Locally look up the UUID of an offline player based on their name. If
     * no local player with the name was found, an offline-mode UUID is
     * returned.
     * @param name The name to look up.
     * @return The UUID of the player.
     */
    UUID lookupUUID(String name);

    /**
     * A piecewise reader for initializing new players.
     * See {@link PlayerDataService#beginReadingData}.
     */
    interface PlayerReader extends AutoCloseable {

        /**
         * Check whether the player has played before. If the player has
         * not played before, most of the rest of the fields will have their
         * default values. If the player has played before, some fields may
         * still not have meaningful values, depending on the data.
         * @return True if the player has played before.
         */
        boolean hasPlayedBefore();

        /**
         * Get the last saved location of the player if available.
         * @return The location, or null.
         */
        Location<World> getLocation();

        /**
         * Get the player's bed spawn location if available.
         * @return The location, or null.
         */
        Location<World> getBedSpawnLocation();

        /**
         * Get the first-played time if available.
         * @return Time in milliseconds since epoch, or 0.
         */
        long getFirstPlayed();

        /**
         * Get the last-played time if available.
         * @return Time in milliseconds since epoch, or 0.
         */
        long getLastPlayed();

        /**
         * Get the player's last known username if available.
         * @return The name, or null.
         */
        String getLastKnownName();

        /**
         * Finish reading the rest of the player's entity data into the
         * specified player entity.
         * @param player The target player.
         */
        void readData(LanternPlayer player);

        /**
         * Close any resources involved in reading the player data.
         */
        @Override
        void close();
    }

}
