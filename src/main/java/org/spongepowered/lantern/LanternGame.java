package org.spongepowered.lantern;

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

import java.io.File;

public class LanternGame implements Game {

    @Override
    public Platform getPlatform() {
        return null; //TODO: Implement
    }

    @Override
    public Server getServer() {
        return null; //TODO: Implement
    }

    @Override
    public PluginManager getPluginManager() {
        return null; //TODO: Implement
    }

    @Override
    public EventManager getEventManager() {
        return null; //TODO: Implement
    }

    @Override
    public GameRegistry getRegistry() {
        return null; //TODO: Implement
    }

    @Override
    public ServiceManager getServiceManager() {
        return null; //TODO: Implement
    }

    @Override
    public SchedulerService getScheduler() {
        return null; //TODO: Implement
    }

    @Override
    public CommandService getCommandDispatcher() {
        return null; //TODO: Implement
    }

    @Override
    public TeleportHelper getTeleportHelper() {
        return null; //TODO: Implement
    }

    @Override
    public File getSavesDirectory() {
        return null; //TODO: Implement
    }

    @Override
    public GameState getState() {
        return null; //TODO: Implement
    }

    @Override
    public ChannelRegistrar getChannelRegistrar() {
        return null; //TODO: Implement
    }
}
