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
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.service.scheduler.SchedulerService;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.lantern.service.scheduler.LanternScheduler;

import java.io.File;

public class LanternGame implements Game {

    private final Platform platform = new LanternPlatform(Lantern.MINECRAFT_VERSION, Lantern.API_VERSION, Lantern.IMPLEMENTATION_VERSION);
    private final PluginManager pluginManager;
    private final EventManager eventManager;
    private final GameRegistry gameRegistry;
    private final ServiceManager serviceManager;
    private final TeleportHelper teleportHelper;

    private GameState state = GameState.CONSTRUCTION;

    @Inject
    public LanternGame(PluginManager pluginManager, EventManager eventManager, GameRegistry gameRegistry, ServiceManager serviceManager, TeleportHelper teleportHelper) {
        this.pluginManager = checkNotNull(pluginManager);
        this.eventManager = checkNotNull(eventManager);
        this.gameRegistry = checkNotNull(gameRegistry);
        this.serviceManager = checkNotNull(serviceManager);
        this.teleportHelper = checkNotNull(teleportHelper);
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public Server getServer() {
        return null; //TODO: Implement
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
    public GameRegistry getRegistry() {
        return gameRegistry;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public SchedulerService getScheduler() {
        return LanternScheduler.getInstance();
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
    public File getSavesDirectory() {
        return null; //TODO: Implement
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
}
