package org.spongepowered.lantern.plugin;

import com.google.inject.Singleton;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.SpongeVersion;

import java.util.Optional;

@Singleton
public class MinecraftPluginContainer extends AbstractPluginContainer {

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
        return SpongeVersion.MINECRAFT_VERSION.getName();
    }

    @Override
    public Optional<Object> getInstance() {
        return Optional.empty();
    }
}
