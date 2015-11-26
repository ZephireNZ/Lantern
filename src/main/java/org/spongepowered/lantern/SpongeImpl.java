package org.spongepowered.lantern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.lantern.configuration.LanternConfig;
import org.spongepowered.lantern.registry.LanternGameRegistry;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nullable;

@Singleton
public class SpongeImpl {

    public static final String ECOSYSTEM_NAME = "Lantern";
    public static final String IMPLEMENTATION_VERSION = Objects.firstNonNull(LanternGame.class.getPackage().getImplementationVersion(), "DEV");
    public static final String API_VERSION = Objects.firstNonNull(LanternGame.class.getPackage().getSpecificationVersion(), "DEV");
    public static final MinecraftVersion MINECRAFT_VERSION = new LanternMinecraftVersion("1.8", 47);

    private static final Path gameDir = Paths.get(".");
    private static final Path configDir = gameDir.resolve("config");
    private static final Path pluginsDir = gameDir.resolve("mods");
    private static final Path modConfigDir = configDir.resolve("sponge");

    @Nullable
    private static SpongeImpl instance;
    @Nullable
    private static LanternConfig<LanternConfig.GlobalConfig> globalConfig;
    private final Injector injector;
    private final LanternGame game;
    private final Logger logger;
    private PluginContainer plugin;
    private PluginContainer minecraftPlugin;

    @Inject
    public SpongeImpl(Injector injector, LanternGame game, Logger logger, @Named(ECOSYSTEM_NAME) PluginContainer plugin, @Named("Minecraft") PluginContainer minecraftPlugin) {

        checkState(instance == null, "Sponge was already initialized");
        instance = this;

        this.injector = checkNotNull(injector);
        this.game = checkNotNull(game);
        this.logger = checkNotNull(logger);
        this.plugin = checkNotNull(plugin);
        this.minecraftPlugin = checkNotNull(minecraftPlugin);
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

    public static SpongeImpl getSponge() {
        checkState(instance != null, "Sponge was not initialized");
        return instance;
    }

    public static Injector getInjector() {
        return getSponge().injector;
    }

    public static LanternGame getGame() {
        return getSponge().game;
    }

    public static LanternGameRegistry getRegistry() {
        return getGame().getRegistry();
    }

    public static Logger getLogger() {
        return getSponge().logger;
    }

    public static PluginContainer getPlugin() {
        return getSponge().plugin;
    }

    public static PluginContainer getMinecraftPlugin() {
        return getSponge().minecraftPlugin;
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
            globalConfig = new LanternConfig<>(LanternConfig.Type.GLOBAL, modConfigDir.resolve("global.conf"), "sponge");
        }

        return globalConfig;
    }
}
