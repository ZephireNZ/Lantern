package org.spongepowered.lantern.plugin;

import com.google.inject.Singleton;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.lantern.Lantern;

@Singleton
public class MinecraftPluginContainer implements PluginContainer {

    private static final String NAME = "Minecraft";

    protected MinecraftPluginContainer() {
    }

    @Override
    public String getId() {
        return NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return Lantern.MINECRAFT_VERSION.getName();
    }

    @Override
    public Object getInstance() {
        return null; // No minecraft instance to give...
    }
}
