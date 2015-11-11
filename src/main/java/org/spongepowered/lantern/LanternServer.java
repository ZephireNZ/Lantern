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
package org.spongepowered.lantern;

import com.google.common.util.concurrent.ListenableFuture;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.service.world.ChunkLoadService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.lantern.launch.console.ConsoleManager;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class LanternServer implements Server {

    /**
     * The console manager of this server.
     */
    private final ConsoleManager consoleManager = new ConsoleManager();

    public LanternServer() {
        //TODO: Get Ops, whitelist, bans

        start();
        bind();
    }

    public void start() {
        consoleManager.startConsole();
        consoleManager.startFile("latest.log"); //TODO: proper logging

        //TODO: Load Ops, whitelist, bans

        //TODO: Load worlds

        //TODO: Start schedulers
    }

    public void bind() {
        //TODO: Implement
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return null; //TODO: Implement
    }

    @Override
    public int getMaxPlayers() {
        return 0; //TODO: Implement
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        return null; //TODO: Implement
    }

    @Override
    public Collection<World> getWorlds() {
        return null; //TODO: Implement
    }

    @Override
    public Collection<WorldProperties> getUnloadedWorlds() {
        return null; //TODO: Implement
    }

    @Override
    public Collection<WorldProperties> getAllWorldProperties() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<World> getWorld(UUID uniqueId) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<World> getWorld(String worldName) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<WorldProperties> getDefaultWorld() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<World> loadWorld(String worldName) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<World> loadWorld(UUID uniqueId) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<World> loadWorld(WorldProperties properties) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(String worldName) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        return null; //TODO: Implement
    }

    @Override
    public boolean unloadWorld(World world) {
        return false; //TODO: Implement
    }

    @Override
    public Optional<WorldProperties> createWorld(WorldCreationSettings settings) {
        return null; //TODO: Implement
    }

    @Override
    public ListenableFuture<Optional<WorldProperties>> copyWorld(WorldProperties worldProperties, String copyName) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        return null; //TODO: Implement
    }

    @Override
    public ListenableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        return null; //TODO: Implement
    }

    @Override
    public boolean saveWorldProperties(WorldProperties properties) {
        return false; //TODO: Implement
    }

    @Override
    public ChunkLayout getChunkLayout() {
        return null; //TODO: Implement
    }

    @Override
    public int getRunningTimeTicks() {
        return 0; //TODO: Implement
    }

    @Override
    public MessageSink getBroadcastSink() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        return null; //TODO: Implement
    }

    @Override
    public boolean hasWhitelist() {
        return false; //TODO: Implement
    }

    @Override
    public void setHasWhitelist(boolean enabled) {
        //TODO: Implement
    }

    @Override
    public boolean getOnlineMode() {
        return false; //TODO: Implement
    }

    @Override
    public Text getMotd() {
        return null; //TODO: Implement
    }

    @Override
    public void shutdown() {
        //TODO: Implement
    }

    @Override
    public void shutdown(Text kickMessage) {
        //TODO: Implement
    }

    @Override
    public ConsoleSource getConsole() {
        return null; //TODO: Implement
    }

    @Override
    public ChunkLoadService getChunkLoadService() {
        return null; //TODO: Implement
    }

    @Override
    public double getTicksPerSecond() {
        return 0; //TODO: Implement
    }

    @Override
    public Optional<ResourcePack> getDefaultResourcePack() {
        return null; //TODO: Implement
    }
}
