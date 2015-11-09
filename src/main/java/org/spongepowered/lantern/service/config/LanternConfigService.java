package org.spongepowered.lantern.service.config;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigRoot;
import org.spongepowered.api.service.config.ConfigService;

public class LanternConfigService implements ConfigService {

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
