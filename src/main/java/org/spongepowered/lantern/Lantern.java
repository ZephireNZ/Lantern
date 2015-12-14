/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.lantern;

import static org.spongepowered.lantern.SpongeImpl.ECOSYSTEM_ID;

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
import java.util.Optional;

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

            SpongeImpl.getRegistry().preRegistryInit();
            // TODO: Init services
            // TODO: Init commands
            SpongeImpl.getRegistry().preInit();

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
        SpongeImpl.getRegistry().init();
        postState(GameInitializationEvent.class, GameState.INITIALIZATION);

        //TODO: Register permissions

        SpongeImpl.getRegistry().postInit();

        // TODO: Data complete registration

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
        post(SpongeEventFactoryUtils.createState(type, this.game));
    }

    public static boolean post(Event event) {
        return SpongeImpl.getGame().getEventManager().post(event);
    }

    @Override
    public String getId() {
        return ECOSYSTEM_ID;
    }

    @Override
    public String getName() {
        return SpongeVersion.IMPLEMENTATION_NAME.orElse("Lantern");
    }

    @Override
    public String getVersion() {
        return SpongeVersion.IMPLEMENTATION_VERSION;
    }

    @Override
    public Optional<Object> getInstance() {
        return Optional.of(this);
    }

}
