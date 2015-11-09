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

import java.io.File;

import javax.annotation.Nullable;

@Singleton
public class Lantern implements PluginContainer {

    public static final String ECOSYSTEM_NAME = "Lantern";
    public static final String IMPLEMENTATION_VERSION = Objects.firstNonNull(LanternGame.class.getPackage().getImplementationVersion(), "DEV");
    public static final String API_VERSION = Objects.firstNonNull(LanternGame.class.getPackage().getSpecificationVersion(), "DEV");
    public static final MinecraftVersion MINECRAFT_VERSION = new LanternMinecraftVersion("1.8", 47);

    private static final File gameDir = new File(".");
    private static final File configDir = new File(gameDir, "config");
    private static final File pluginsDir = new File(gameDir, "mods");

    private static final File modConfigDir = new File(configDir, "sponge");
    @Nullable
    private static Lantern instance;
    @Nullable
    private static LanternConfig<LanternConfig.GlobalConfig> globalConfig;
    private final Injector injector;
    private final LanternGame game;
    private final Logger logger;
    private PluginContainer minecraftPlugin;

    @Inject
    public Lantern(Injector injector, LanternGame game, Logger logger, @Named("Minecraft") PluginContainer minecraftPlugin) {

        checkState(instance == null, "Sponge was already initialized");
        instance = this;

        this.injector = checkNotNull(injector);
        this.game = checkNotNull(game);
        this.logger = checkNotNull(logger);
        this.minecraftPlugin = checkNotNull(minecraftPlugin);
    }

    public void preInit() {
        // TODO: Register services
        // TODO: Pre-init registry
    }

    public void init() {
        //TODO: init registry
        //TODO: Register permissions
    }

    public void postInit() {
        //TODO: post-init registry
    }

    // Private utilties

    private <T> boolean registerService(Class<T> serviceClass, T serviceImpl) {
        try {
            Lantern.getGame().getServiceManager().setProvider(Lantern.getPlugin(), serviceClass, serviceImpl);
            return true;
        } catch (ProviderExistsException e) {
            Lantern.getLogger().warn("Non-Sponge {} already registered: {}", serviceClass.getSimpleName(), e.getLocalizedMessage());
            return false;
        }
    }

    // Getters

    @Override
    public String getId() {
        return ECOSYSTEM_NAME;
    }

    @Override
    public String getName() {
        return ECOSYSTEM_NAME;
    }

    @Override
    public String getVersion() {
        return IMPLEMENTATION_VERSION;
    }

    @Override
    public Object getInstance() {
        return this;
    }

    public static Lantern getLantern() {
        checkState(instance != null, "Sponge was not initialized");
        return instance;
    }

    public static Injector getInjector() {
        return getLantern().injector;
    }

    public static LanternGame getGame() {
        return getLantern().game;
    }

    public static LanternGameRegistry getRegistry() {
        return (LanternGameRegistry) getGame().getRegistry();
    }

    public static Logger getLogger() {
        return getLantern().logger;
    }

    public static PluginContainer getPlugin() {
        return getLantern();
    }

    public static PluginContainer getMinecraftPlugin() {
        return getLantern().minecraftPlugin;
    }

    public static File getGameDirectory() {
        return gameDir;
    }

    public static File getConfigDirectory() {
        return configDir;
    }

    public static File getPluginsDirectory() {
        return pluginsDir;
    }

    public static LanternConfig<LanternConfig.GlobalConfig> getGlobalConfig() {
        if (globalConfig == null) {
            globalConfig = new LanternConfig<>(LanternConfig.Type.GLOBAL, new File(modConfigDir, "global.conf"), "sponge");
        }

        return globalConfig;
    }
}
