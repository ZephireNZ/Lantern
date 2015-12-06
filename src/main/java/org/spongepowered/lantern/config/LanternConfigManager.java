package org.spongepowered.lantern.config;

import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.config.ConfigRoot;
import org.spongepowered.api.plugin.PluginContainer;

public class LanternConfigManager implements ConfigManager {

    @Override
    public ConfigRoot getSharedConfig(Object instance) {
        return null; //TODO: Implement
    }

    @Override
    public ConfigRoot getPluginConfig(Object instance) {
        return null; //TODO: Implement
    }

    public static ConfigRoot getPrivateRoot(PluginContainer container) {
        return null; // TODO: Implement
    }

    public static ConfigRoot getSharedRoot(PluginContainer container) {
        return null; // TODO: Implement
    }
}
