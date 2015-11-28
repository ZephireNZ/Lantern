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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Platform;
import org.spongepowered.api.data.ImmutableDataRegistry;
import org.spongepowered.api.data.manipulator.DataManipulatorRegistry;
import org.spongepowered.api.data.property.PropertyRegistry;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.service.persistence.SerializationManager;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.lantern.registry.LanternGameRegistry;
import org.spongepowered.lantern.service.scheduler.LanternScheduler;

import java.nio.file.Path;

public class LanternGame implements Game {

    private final Platform platform = new LanternPlatform(SpongeImpl.MINECRAFT_VERSION, SpongeImpl.API_VERSION, SpongeImpl.IMPLEMENTATION_VERSION);
    private final PluginManager pluginManager;
    private final EventManager eventManager;
    private final LanternGameRegistry gameRegistry;
    private final ServiceManager serviceManager;
    private final TeleportHelper teleportHelper;
    private final LanternScheduler scheduler;

    private GameState state = GameState.CONSTRUCTION;
    private LanternServer server;

    @Inject
    public LanternGame(PluginManager pluginManager, EventManager eventManager, LanternGameRegistry gameRegistry, ServiceManager serviceManager, TeleportHelper teleportHelper, LanternScheduler scheduler) {
        this.pluginManager = checkNotNull(pluginManager);
        this.eventManager = checkNotNull(eventManager);
        this.gameRegistry = checkNotNull(gameRegistry);
        this.serviceManager = checkNotNull(serviceManager);
        this.teleportHelper = checkNotNull(teleportHelper);
        this.scheduler = checkNotNull(scheduler);
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public LanternServer getServer() {
        return this.server;
    }

    public void setServer(LanternServer server) {
        Preconditions.checkState(this.server == null, "Server has already been initialised!");
        this.server = checkNotNull(server);
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public LanternGameRegistry getRegistry() {
        return gameRegistry;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public LanternScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public CommandService getCommandDispatcher() {
        return this.serviceManager.provideUnchecked(CommandService.class);
    }

    @Override
    public TeleportHelper getTeleportHelper() {
        return teleportHelper;
    }

    @Override
    public Path getSavesDirectory() {
        return SpongeImpl.getGameDirectory();
    }

    @Override
    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = checkNotNull(state);
    }

    @Override
    public ChannelRegistrar getChannelRegistrar() {
        return null; //TODO: Implement
    }

    @Override
    public GameDictionary getGameDictionary() {
        return null; //TODO: Implement
    }

    @Override
    public SerializationManager getSerializationService() {
        return null; //TODO: Implement
    }

    @Override
    public PropertyRegistry getPropertyRegistry() {
        return null; //TODO: Implement
    }

    @Override
    public DataManipulatorRegistry getManipulatorRegistry() {
        return null; //TODO: Implement
    }

    @Override
    public ImmutableDataRegistry getImmutableDataRegistry() {
        return null; //TODO: Implement
    }
}
