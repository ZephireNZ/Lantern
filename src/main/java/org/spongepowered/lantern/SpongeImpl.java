package org.spongepowered.lantern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.lantern.config.LanternConfig;
import org.spongepowered.lantern.registry.LanternGameRegistry;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nullable;

@Singleton
public class SpongeImpl {

    public static final String ECOSYSTEM_NAME = "Lantern";
    public static final String ECOSYSTEM_ID = "lantern";
    public static final String API_NAME = "SpongeAPI";
    public static final String API_ID = "spongeapi";

    public static final String CONFIG_NAME = "sponge"; // Preserve compatibility with Sponge

    private static final Path gameDir = Paths.get(".");
    private static final Path configDir = gameDir.resolve("config");
    private static final Path pluginsDir = gameDir.resolve("mods");
    private static final Path modConfigDir = configDir.resolve(CONFIG_NAME); // We want to preserve configs

    @Nullable
    private static SpongeImpl instance;
    @Nullable
    private static LanternConfig<LanternConfig.GlobalConfig> globalConfig;
    private final Injector injector;
    private final LanternGame game;
    private final Logger logger;
    private PluginContainer plugin;
    private PluginContainer minecraftPlugin;
    private final org.slf4j.Logger slf4jLogger;

    @Inject
    public SpongeImpl(Injector injector, LanternGame game, Logger logger, @Named(ECOSYSTEM_NAME) PluginContainer plugin, @Named("Minecraft") PluginContainer minecraftPlugin) {

        checkState(instance == null, "Sponge was already initialized");
        instance = this;

        this.injector = checkNotNull(injector);
        this.game = checkNotNull(game);
        this.logger = checkNotNull(logger);
        this.slf4jLogger = LoggerFactory.getLogger(this.logger.getName());
        this.plugin = checkNotNull(plugin);
        this.minecraftPlugin = checkNotNull(minecraftPlugin);
    }

    public static org.slf4j.Logger getSlf4jLogger() {
        return getInstance().slf4jLogger;
    }

    // Private utilties

    private <T> boolean registerService(Class<T> serviceClass, T serviceImpl) {
        try {
            SpongeImpl.getGame().getServiceManager().setProvider(SpongeImpl.getPlugin(), serviceClass, serviceImpl);
            return true;
        } catch (ProviderExistsException e) {
            SpongeImpl.getLogger().warn("Non-Sponge {} already registered: {}", serviceClass.getSimpleName(), e.getLocalizedMessage());
            return false;
        }
    }

    // Getters

    public static SpongeImpl getInstance() {
        checkState(instance != null, "Sponge was not initialized");
        return instance;
    }

    public static Injector getInjector() {
        return getInstance().injector;
    }

    public static LanternGame getGame() {
        return getInstance().game;
    }

    public static LanternGameRegistry getRegistry() {
        return getGame().getRegistry();
    }

    public static Logger getLogger() {
        return getInstance().logger;
    }

    public static PluginContainer getPlugin() {
        return getInstance().plugin;
    }

    public static PluginContainer getMinecraftPlugin() {
        return getInstance().minecraftPlugin;
    }

    public static Path getGameDirectory() {
        return gameDir;
    }

    public static Path getConfigDirectory() {
        return configDir;
    }

    public static Path getPluginsDirectory() {
        return pluginsDir;
    }

    public static LanternConfig<LanternConfig.GlobalConfig> getGlobalConfig() {
        if (globalConfig == null) {
            globalConfig = new LanternConfig<>(LanternConfig.Type.GLOBAL, modConfigDir.resolve("global.conf"), CONFIG_NAME);
        }

        return globalConfig;
    }
}
