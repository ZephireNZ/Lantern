package org.spongepowered.lantern.config;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import org.spongepowered.api.config.ConfigRoot;
import org.spongepowered.lantern.SpongeImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LanternConfigRoot implements ConfigRoot {
    private final ObjectMapperFactory mapperFactory;
    private final String pluginName;
    private final Path baseDir;

    public LanternConfigRoot(ObjectMapperFactory mapperFactory, String pluginName, Path baseDir) {
        this.mapperFactory = mapperFactory;
        this.pluginName = pluginName;
        this.baseDir = baseDir;
    }

    @Override
    public Path getConfigPath() {
        Path configFile = this.baseDir.resolve(this.pluginName + ".conf");
        try {
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            SpongeImpl.getLogger().error("Failed to create plugin dir for {} at {}", this.pluginName, this.baseDir, e);
        }
        return configFile;
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> getConfig() {
        return HoconConfigurationLoader.builder()
                .setPath(getConfigPath())
                .setDefaultOptions(ConfigurationOptions.defaults().setObjectMapperFactory(mapperFactory))
                .build();
    }

    @Override
    public Path getDirectory() {
        return this.baseDir;
    }
}
