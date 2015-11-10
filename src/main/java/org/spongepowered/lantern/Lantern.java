package org.spongepowered.lantern;

import static org.spongepowered.lantern.Sponge.*;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import org.spongepowered.api.GameState;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.SpongeEventFactoryUtils;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStateEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.lantern.guice.LanternGuiceModule;
import org.spongepowered.lantern.plugin.LanternPluginManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Lantern implements PluginContainer {

    public static Lantern instance;
    private final LanternGame game;

    public static void main(String[] args) {
        instance = new Lantern();
    }


    private Lantern() {
        Guice.createInjector(new LanternGuiceModule(this)).getInstance(Sponge.class);

        this.game = Sponge.getGame();
    }

    public void preInit() {
        try {
            Sponge.getLogger().info("Loading Sponge...");

            Path gameDir = Sponge.getGameDirectory();
            Path pluginsDir = Sponge.getPluginsDirectory();
            Files.createDirectories(pluginsDir);

            // TODO: Register services
            // TODO: Pre-registry init
            // TODO: Pre-init registry

            this.game.getEventManager().registerListeners(this, this);
            this.game.getEventManager().registerListeners(this, this.game.getRegistry());

            Sponge.getLogger().info("Loading plugins...");
            ((LanternPluginManager) this.game.getPluginManager()).loadPlugins();
            postState(GameConstructionEvent.class, GameState.CONSTRUCTION);
            Sponge.getLogger().info("Initializing plugins...");
            postState(GamePreInitializationEvent.class, GameState.PRE_INITIALIZATION);

            //TODO: register permission calculator
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public void init() {
        //TODO: init registry
        postState(GameInitializationEvent.class, GameState.INITIALIZATION);

        //TODO: Register permissions

        // TODO: Post-init registry

        // TODO: Serialization complete

        postState(GamePostInitializationEvent.class, GameState.POST_INITIALIZATION);

        Sponge.getLogger().info("Successfully loaded and initialized plugins.");

        postState(GameLoadCompleteEvent.class, GameState.LOAD_COMPLETE);
    }

    public void postState(Class<? extends GameStateEvent> type, GameState state) {
        Sponge.getGame().setState(state);
        postEvent(SpongeEventFactoryUtils.createState(type, this.game));
    }

    public static boolean postEvent(Event event) {
        return getGame().getEventManager().post(event);
    }

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

}
