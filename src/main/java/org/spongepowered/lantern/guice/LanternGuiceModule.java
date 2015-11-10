package org.spongepowered.lantern.guice;

import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.name.Names.named;

import com.google.inject.AbstractModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.lantern.Lantern;
import org.spongepowered.lantern.Sponge;
import org.spongepowered.lantern.LanternGame;
import org.spongepowered.lantern.registry.LanternGameRegistry;
import org.spongepowered.lantern.event.LanternEventManager;
import org.spongepowered.lantern.plugin.LanternPluginManager;
import org.spongepowered.lantern.plugin.MinecraftPluginContainer;
import org.spongepowered.lantern.world.LanternTeleportHelper;

import java.io.File;
import java.nio.file.Path;

public class LanternGuiceModule extends AbstractModule {

    private final Lantern instance;

    public LanternGuiceModule(Lantern instance) {
        this.instance = instance;
    }

    @Override
    protected void configure() {
        bind(Lantern.class).toInstance(this.instance);
        bind(Logger.class).toInstance(LogManager.getLogger(Sponge.ECOSYSTEM_NAME));

        bind(PluginContainer.class).annotatedWith(named(Sponge.ECOSYSTEM_NAME)).toInstance(this.instance);
        bind(PluginContainer.class).annotatedWith(named("Minecraft")).to(MinecraftPluginContainer.class).in(SINGLETON);

        bind(Game.class).to(LanternGame.class).in(SINGLETON);
        bind(PluginManager.class).to(LanternPluginManager.class).in(SINGLETON);
        bind(EventManager.class).to(LanternEventManager.class).in(SINGLETON);
        bind(GameRegistry.class).to(LanternGameRegistry.class).in(SINGLETON);
        bind(ServiceManager.class).to(SimpleServiceManager.class).in(SINGLETON);
        bind(TeleportHelper.class).to(LanternTeleportHelper.class).in(SINGLETON);

        ConfigDirAnnotation sharedRoot = new ConfigDirAnnotation(true);
        bind(Path.class).annotatedWith(sharedRoot).toInstance(Sponge.getConfigDirectory());
    }
}
