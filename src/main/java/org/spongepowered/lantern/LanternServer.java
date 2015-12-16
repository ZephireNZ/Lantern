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

import com.google.common.util.concurrent.ListenableFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.lantern.config.LanternConfig;
import org.spongepowered.lantern.launch.console.ConsoleManager;
import org.spongepowered.lantern.network.LanternNetworkServer;
import org.spongepowered.lantern.network.LanternSession;
import org.spongepowered.lantern.network.SessionRegistry;
import org.spongepowered.lantern.registry.type.world.WorldPropertyRegistryModule;
import org.spongepowered.lantern.scheduler.LanternScheduler;
import org.spongepowered.lantern.world.LanternWorld;
import org.spongepowered.lantern.world.LanternWorldBuilder;
import org.spongepowered.lantern.world.storage.LanternChunkLayout;
import org.spongepowered.lantern.world.storage.LanternWorldProperties;
import org.spongepowered.lantern.world.storage.LanternWorldStorage;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class LanternServer implements Server {

    public static final String NETHER_NAME = "DIM-1";
    public static final String THE_END_NAME = "DIM1";

    /**
     * A list of all active {@link LanternSession}s.
     */
    private final SessionRegistry sessionRegistry = new SessionRegistry();

    /**
     * The console manager of this server.
     */
    private final ConsoleManager consoleManager = new ConsoleManager();
    /**
     * The network server that manages the connection.
     */
    private final LanternNetworkServer networkServer = new LanternNetworkServer(this);

    public LanternServer() throws BindException {
        //TODO: Commandline Args?
        //TODO: Get Ops, whitelist, bans

        start();
        bind();
        //TODO: Query and rcon
    }

    public void start() {
        consoleManager.startConsole();
        consoleManager.startFile(SpongeImpl.getGlobalConfig().getConfig().getLogFile());

        //TODO: Fire AboutToStart

        //TODO: Fire AboutToStart

        //TODO: Load Ops, whitelist, bans

        //TODO: Load worlds
        loadAllWorlds();

        SpongeImpl.getGame().getScheduler().start();
    }

    public void loadAllWorlds() {
        Path worlds = SpongeImpl.getWorldDirectory();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(worlds)) {
            stream.forEach(world -> {
                Path spongeFile = world.resolve("level_sponge.dat");
                if(!Files.exists(spongeFile)) return;

                loadWorld(world.getFileName().toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        LanternConfig.GlobalConfig config = SpongeImpl.getGlobalConfig().getConfig();
        LanternConfig.ConfigBase worldConfig = SpongeImpl.getActiveConfig(DimensionTypes.OVERWORLD.getId(), config.getLevelName()).getConfig();
        LanternConfig.ConfigBase netherConfig = SpongeImpl.getActiveConfig(DimensionTypes.NETHER.getId(), NETHER_NAME).getConfig();
        LanternConfig.ConfigBase endConfig = SpongeImpl.getActiveConfig(DimensionTypes.THE_END.getId(), THE_END_NAME).getConfig();

        // Load the default worlds if they aren't loaded already
        createWorld(new LanternWorldBuilder()
                .name(config.getLevelName())
                .enabled(true)
                .loadsOnStartup(true)
                .keepsSpawnLoaded(true)
                .seed(config.getLevelSeed())
                .gameMode(config.getGamemode())
                .generator(worldConfig.getWorld().getGenerator())
                .dimensionType(DimensionTypes.OVERWORLD)
                .hardcore(worldConfig.getWorld().isHardcore())
//                .generatorSettings(worldConfig.getWorld().getGeneratorSettings()) //TODO: Generator Settings
                .buildSettings()
        , SpongeImpl.getWorldDirectory());
        loadWorld(config.getLevelName(), SpongeImpl.getWorldDirectory());

        //TODO: Check whether to load
        createWorld(new LanternWorldBuilder()
                .name(NETHER_NAME)
                .enabled(config.isAllowNether())
                .loadsOnStartup(true)
                .keepsSpawnLoaded(false)
                .seed(config.getLevelSeed())
                .gameMode(config.getGamemode())
                .generator(GeneratorTypes.NETHER)
                .dimensionType(DimensionTypes.NETHER)
                .hardcore(netherConfig.getWorld().isHardcore())
//                .generatorSettings(netherConfig.getWorld().getGeneratorSettings()) //TODO: Generator Settings
                .buildSettings()
        );
        loadWorld(NETHER_NAME);

        //TODO: Check whether to load
        createWorld(new LanternWorldBuilder()
                .name(THE_END_NAME)
                .enabled(config.isAllowEnd())
                .loadsOnStartup(true)
                .keepsSpawnLoaded(false)
                .seed(config.getLevelSeed())
                .gameMode(config.getGamemode())
                .generator(GeneratorTypes.THE_END)
                .dimensionType(DimensionTypes.THE_END)
                .hardcore(endConfig.getWorld().isHardcore())
//                .generatorSettings(endConfig.getWorld().getGeneratorSettings()) //TODO: Generator Settings
                .buildSettings()
        );
        loadWorld(THE_END_NAME);
    }

    public void bind() throws BindException {
        SocketAddress address = getBindAddress();

        SpongeImpl.getLogger().info("Binding to address: " + address + "...");
        ChannelFuture future = networkServer.bind(address);
        Channel channel = future.awaitUninterruptibly().channel();
        if (!channel.isActive()) {
            Throwable cause = future.cause();
            if (cause instanceof BindException) {
                throw (BindException) cause;
            }
            throw new RuntimeException("Failed to bind to address", cause);
        }

        SpongeImpl.getLogger().info("Successfully bound to: " + channel.localAddress());
    }

    private SocketAddress getBindAddress() {
        String ip = SpongeImpl.getGlobalConfig().getConfig().getServerIp();
        int port = SpongeImpl.getGlobalConfig().getConfig().getServerPort();

        if(ip.length() == 0) {
            return new InetSocketAddress(port);
        }

        return new InetSocketAddress(ip, port);
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return Collections.emptySet(); //TODO: Implement
    }

    @Override
    public int getMaxPlayers() {
        return 0; //TODO: Implement
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return Optional.empty(); //TODO: Implement
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        return Optional.empty(); //TODO: Implement
    }

    @Override
    public Collection<World> getWorlds() {
        return Collections.unmodifiableCollection(LanternScheduler.getInstance().getWorldScheduler().getWorlds());
    }

    @Override
    public Collection<WorldProperties> getUnloadedWorlds() {
        return Collections.emptySet(); //TODO: Implement
    }

    @Override
    public Collection<WorldProperties> getAllWorldProperties() {
        return Collections.emptySet(); //TODO: Implement
    }

    @Override
    public Optional<World> getWorld(UUID uniqueId) {
        for(World world : getWorlds()) {
            if(world.getUniqueId().equals(uniqueId)) return Optional.of(world);
        }
        return Optional.empty();
    }

    @Override
    public Optional<World> getWorld(String worldName) {
        for(World world : getWorlds()) {
            if(world.getName().equals(worldName)) return Optional.of(world);
        }
        return Optional.empty();
    }

    @Override
    public Optional<WorldProperties> getDefaultWorld() {
        return Optional.empty(); //TODO: Implement
    }

    @Override
    public Optional<World> loadWorld(UUID uniqueId) {
        return Optional.empty(); //TODO: Implement
    }

    @Override
    public Optional<World> loadWorld(WorldProperties properties) {
        if(properties == null) return Optional.empty();
        return loadWorld(properties.getWorldName());
    }

    @Override
    public Optional<World> loadWorld(String worldName) {
        final Optional<World> existing = getWorld(worldName);
        if(existing.isPresent()) return existing;

        return loadWorld(worldName, SpongeImpl.getWorldDirectory().resolve(worldName));
    }

    public Optional<World> loadWorld(String worldName, Path worldFolder) {
        final Optional<World> existing = getWorld(worldName);
        if(existing.isPresent()) return existing;

        if(Files.isRegularFile(worldFolder)) {
            throw new IllegalArgumentException("File exists with the name '" + worldName + "' and isn't a folder");
        }

        LanternWorldStorage storage = new LanternWorldStorage(worldFolder);
        Optional<WorldProperties> optProps = WorldPropertyRegistryModule.getInstance().getWorldProperties(worldName);
        WorldProperties properties = optProps.orElse(storage.getWorldProperties());
        if(properties instanceof LanternWorldProperties && ((LanternWorldProperties) properties).getWorldConfig() == null) {
            ((LanternWorldProperties) properties).setWorldConfig(SpongeImpl.getWorldConfig(worldName, properties.getDimensionType()).getConfig());
        }

        if(!properties.isEnabled()) {
            SpongeImpl.getLogger().error("Unable to load world " + worldName + ". World is disabled!");
            return Optional.empty();
        }

        int dim = ((LanternWorldProperties) properties).getDimensionId();
        //TODO: Register dimension ID?
        if(!WorldPropertyRegistryModule.getInstance().isWorldRegistered(worldName)) {
            WorldPropertyRegistryModule.getInstance().registerWorldProperties(properties);
        }

        LanternWorld world = new LanternWorld(storage, properties);
        LanternScheduler.getInstance().getWorldScheduler().addWorld(world);
        //TODO: Init spawn?
        Lantern.post(SpongeEventFactory.createLoadWorldEvent(SpongeImpl.getGame(), Cause.of(this), world));

        return Optional.of(world);
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(String worldName) {
        Optional<WorldProperties> loaded = WorldPropertyRegistryModule.getInstance().getWorldProperties(worldName);
        if(!loaded.isPresent()) return loaded;

        Path worlds = SpongeImpl.getWorldDirectory();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(worlds)) {
            for(Path world : stream) {
                if(!world.getFileName().toString().equals(worldName)) continue;

                Path spongeFile = world.resolve("level_sponge.dat");
                if(!Files.exists(spongeFile)) continue;
                return Optional.of(new LanternWorldStorage(world).getWorldProperties());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        Optional<WorldProperties> loaded = WorldPropertyRegistryModule.getInstance().getWorldProperties(uniqueId);
        if(!loaded.isPresent()) return loaded;

        Path worlds = SpongeImpl.getWorldDirectory();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(worlds)) {
            for(Path world : stream) {
                Path spongeFile = world.resolve("level_sponge.dat");
                if(!Files.exists(spongeFile)) continue;
                WorldProperties properties = new LanternWorldStorage(world).getWorldProperties();
                if(properties.getUniqueId().equals(uniqueId)) return Optional.of(properties);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean unloadWorld(World world) {
        return false; //TODO: Implement
    }

    @Override
    public Optional<WorldProperties> createWorld(WorldCreationSettings settings) {
        String name = settings.getWorldName();
        Optional<World> existing = getWorld(name);
        if(existing.isPresent()) return Optional.of(existing.get().getProperties());

        return createWorld(settings, SpongeImpl.getWorldDirectory().resolve(name));
    }

    public Optional<WorldProperties> createWorld(WorldCreationSettings settings, Path worldDir) {
        String name = settings.getWorldName();
        Optional<World> existing = getWorld(name);
        if(existing.isPresent()) return Optional.of(existing.get().getProperties());

        LanternConfig.WorldConfig config = SpongeImpl.getWorldConfig(settings.getWorldName(), settings.getDimensionType()).getConfig();
        LanternWorldStorage storage = new LanternWorldStorage(worldDir);
        LanternWorldProperties properties = new LanternWorldProperties(settings);
        properties.setWorldConfig(config);
        // Ensure these are set on the config
        properties.setEnabled(settings.isEnabled());
        properties.setLoadOnStartup(settings.loadOnStartup());
        properties.setPVPEnabled(settings.isPVPEnabled());

        try {
            storage.writeWorldProperties(properties);
        } catch (IOException e) {
            SpongeImpl.getLogger().error("Unable to create world properties for " + settings.getWorldName(), e);
            return Optional.empty();
        }

        // TODO: ConstructWorldEvent?
        if(!WorldPropertyRegistryModule.getInstance().isWorldRegistered(properties.getUniqueId())) {
            WorldPropertyRegistryModule.getInstance().registerWorldProperties(properties);
            return Optional.of(properties);
        } else {
            return WorldPropertyRegistryModule.getInstance().getWorldProperties(properties.getUniqueId());
        }
    }

    @Override
    public ListenableFuture<Optional<WorldProperties>> copyWorld(WorldProperties worldProperties, String copyName) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        return null; //TODO: Implement
    }

    @Override
    public ListenableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        return null; //TODO: Implement
    }

    @Override
    public boolean saveWorldProperties(WorldProperties properties) {
        return false; //TODO: Implement
    }

    @Override
    public ChunkLayout getChunkLayout() {
        return LanternChunkLayout.instance;
    }

    @Override
    public int getRunningTimeTicks() {
        return 0; //TODO: Implement
    }

    @Override
    public MessageSink getBroadcastSink() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        return null; //TODO: Implement
    }

    @Override
    public boolean hasWhitelist() {
        return false; //TODO: Implement
    }

    @Override
    public void setHasWhitelist(boolean enabled) {
        //TODO: Implement
    }

    @Override
    public boolean getOnlineMode() {
        return false; //TODO: Implement
    }

    @Override
    public Text getMotd() {
        return Texts.of(); //TODO: Implement
    }

    @Override
    public void shutdown() {
        //TODO: Implement
    }

    @Override
    public void shutdown(Text kickMessage) {
        //TODO: Implement
    }

    @Override
    public ConsoleSource getConsole() {
        return consoleManager.getSender();
    }

    @Override
    public double getTicksPerSecond() {
        return 0; //TODO: Implement
    }

    @Override
    public Optional<ResourcePack> getDefaultResourcePack() {
        return Optional.empty(); //TODO: Implement
    }

    @Override
    public ChunkTicketManager getChunkTicketManager() {
        return null; //TODO: Implement
    }

    @Override
    public GameProfileManager getGameProfileManager() {
        return null; //TODO: Implement
    }

    public SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    public boolean getProxySupport() {
        return SpongeImpl.getGlobalConfig().getConfig().getBungeeCord().getIpForwarding();
    }

    public Optional<Favicon> getFavicon() {
        return Optional.empty(); //TODO: Implement
    }
}
