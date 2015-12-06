package org.spongepowered.lantern.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.lantern.config.LanternConfigManager;

import java.io.File;
import java.nio.file.Path;

public class LanternPluginGuiceModule extends AbstractModule {

    private final PluginContainer container;
    private final Class<?> pluginClass;

    public LanternPluginGuiceModule(PluginContainer container, Class<?> pluginClass) {
        this.container = container;
        this.pluginClass = pluginClass;
    }

    @Override
    protected void configure() {
        ConfigDir privateConfigDir = new ConfigDirAnnotation(false);
        DefaultConfig sharedConfigFile = new ConfigFileAnnotation(true);
        DefaultConfig privateConfigFile = new ConfigFileAnnotation(false);

        bind(this.pluginClass).in(Scopes.SINGLETON);
        bind(PluginContainer.class).toInstance(this.container);
        bind(Logger.class).toInstance(this.container.getLogger());

        // Plugin-private config directory (shared dir is in the global guice module)
        bind(Path.class).annotatedWith(privateConfigDir).toProvider(PrivateConfigDirProvider.class);
        bind(File.class).annotatedWith(privateConfigDir).toProvider(FilePrivateConfigDirProvider.class);
        bind(Path.class).annotatedWith(sharedConfigFile).toProvider(SharedConfigFileProvider.class); // Shared-directory config file
        bind(File.class).annotatedWith(sharedConfigFile).toProvider(FileSharedConfigFileProvider.class);
        bind(Path.class).annotatedWith(privateConfigFile).toProvider(PrivateConfigFileProvider.class); // Plugin-private directory config file
        bind(File.class).annotatedWith(privateConfigFile).toProvider(FilePrivateConfigFileProvider.class);

        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(sharedConfigFile)
                .toProvider(SharedHoconConfigProvider.class); // Loader for shared-directory config file
        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(privateConfigFile)
                .toProvider(PrivateHoconConfigProvider.class); // Loader for plugin-private directory config file
    }

    private static class PrivateConfigDirProvider implements Provider<Path> {

        private final PluginContainer container;

        @Inject
        private PrivateConfigDirProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public Path get() {
            return LanternConfigManager.getPrivateRoot(this.container).getDirectory();
        }
    }

    private static class PrivateConfigFileProvider implements Provider<Path> {

        private final PluginContainer container;

        @Inject
        private PrivateConfigFileProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public Path get() {
            return LanternConfigManager.getPrivateRoot(this.container).getConfigPath();
        }

    }

    private static class SharedConfigFileProvider implements Provider<Path> {

        private final PluginContainer container;

        @Inject
        private SharedConfigFileProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public Path get() {
            return LanternConfigManager.getSharedRoot(this.container).getConfigPath();
        }

    }

    private static class SharedHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final PluginContainer container;

        @Inject
        private SharedHoconConfigProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return LanternConfigManager.getSharedRoot(this.container).getConfig();
        }

    }

    private static class PrivateHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final PluginContainer container;

        @Inject
        private PrivateHoconConfigProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return LanternConfigManager.getPrivateRoot(this.container).getConfig();
        }

    }

    // TODO: Support this without extra classes (basically it would be nice if Guice allowed something like an "alias" for File so we would only
    // need to add the conversion step Path -> File (Path.toFile()) once.

    private static class FilePrivateConfigDirProvider implements Provider<File> {

        private final Path configDir;

        @Inject
        private FilePrivateConfigDirProvider(@ConfigDir(sharedRoot = false) Path configDir) {
            this.configDir = configDir;
        }

        @Override
        public File get() {
            return configDir.toFile();
        }
    }

    private static class FilePrivateConfigFileProvider implements Provider<File> {

        private final Path configPath;

        @Inject
        private FilePrivateConfigFileProvider(@DefaultConfig(sharedRoot = false) Path configPath) {
            this.configPath = configPath;
        }

        @Override
        public File get() {
            return configPath.toFile();
        }

    }

    private static class FileSharedConfigFileProvider implements Provider<File> {

        private final Path configPath;

        @Inject
        private FileSharedConfigFileProvider(@DefaultConfig(sharedRoot = true) Path configPath) {
            this.configPath = configPath;
        }

        @Override
        public File get() {
            return configPath.toFile();
        }

    }
}
