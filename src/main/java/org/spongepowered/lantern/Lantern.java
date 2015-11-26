package org.spongepowered.lantern;

import static org.spongepowered.lantern.SpongeImpl.ECOSYSTEM_NAME;
import static org.spongepowered.lantern.SpongeImpl.IMPLEMENTATION_VERSION;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.SpongeEventFactoryUtils;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStateEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.lantern.guice.LanternGuiceModule;
import org.spongepowered.lantern.plugin.LanternPluginManager;
import org.spongepowered.lantern.registry.RegistryHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Lantern implements PluginContainer {

    public static Lantern instance;
    private final LanternGame game;

    public static void main(String[] args) {
        instance = new Lantern();
        //TODO: Options
    }


    private Lantern() {
        Guice.createInjector(new LanternGuiceModule(this)).getInstance(SpongeImpl.class);

        this.game = SpongeImpl.getGame();
        try {
            RegistryHelper.setFinalStatic(Sponge.class, "game", this.game);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        preInit();

        init();

        SpongeImpl.getGame().setServer(new LanternServer());
    }

    public void preInit() {
        try {
            SpongeImpl.getLogger().info("Loading Sponge...");

            Path gameDir = SpongeImpl.getGameDirectory();
            Path pluginsDir = SpongeImpl.getPluginsDirectory();
            Files.createDirectories(pluginsDir);

            // TODO: Register services
            // TODO: Pre-registry init
            // TODO: Pre-init registry

            this.game.getEventManager().registerListeners(this, this);
            this.game.getEventManager().registerListeners(this, this.game.getRegistry());

            SpongeImpl.getLogger().info("Loading plugins...");
            ((LanternPluginManager) this.game.getPluginManager()).loadPlugins();
            postState(GameConstructionEvent.class, GameState.CONSTRUCTION);
            SpongeImpl.getLogger().info("Initializing plugins...");
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

        SpongeImpl.getLogger().info("Successfully loaded and initialized plugins.");

        postState(GameLoadCompleteEvent.class, GameState.LOAD_COMPLETE);
    }

    @Listener
    public void onServerAboutToStart(GameAboutToStartServerEvent event) {
        //TODO: Implement
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        //TODO: Implement
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        //TODO: Implement
    }

    public void postState(Class<? extends GameStateEvent> type, GameState state) {
        SpongeImpl.getGame().setState(state);
        postEvent(SpongeEventFactoryUtils.createState(type, this.game));
    }

    public static boolean postEvent(Event event) {
        return SpongeImpl.getGame().getEventManager().post(event);
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
