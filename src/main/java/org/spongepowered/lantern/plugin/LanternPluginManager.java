package org.spongepowered.lantern.plugin;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.lantern.Sponge;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
public class LanternPluginManager implements PluginManager {

    public static final String SCAN_CLASSPATH_PROPERTY = "sponge.plugins.scanClasspath";
    private static final boolean SCAN_CLASSPATH = Boolean.getBoolean(SCAN_CLASSPATH_PROPERTY);

    private final Map<String, PluginContainer> plugins = Maps.newHashMap();
    private final Map<Object, PluginContainer> pluginInstances = Maps.newIdentityHashMap();

    private final LanternClassLoader classLoader;

    @Inject
    public LanternPluginManager(@Named("Lantern") PluginContainer lanternPlugin, @Named("Minecraft") PluginContainer minecraftPlugin) {
        registerPlugin(lanternPlugin);
        registerPlugin(minecraftPlugin);

        classLoader = new LanternClassLoader((URLClassLoader) Sponge.class.getClassLoader());
    }

    private void registerPlugin(PluginContainer plugin) {
        this.plugins.put(plugin.getId(), plugin);
        this.pluginInstances.put(plugin.getInstance(), plugin);
    }

    public void loadPlugins() throws IOException {
        Set<String> plugins;

        if (SCAN_CLASSPATH) {
            Sponge.getLogger().info("Scanning classpath for plugins...");

            // Find plugins on the classpath
            plugins = PluginScanner.scanClassPath(classLoader);
            if (!plugins.isEmpty()) {
                loadPlugins("classpath", plugins);
            }
        }

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(Sponge.getPluginsDirectory(), PluginScanner.ARCHIVE)) {
            for(Path path : stream) {
                plugins = PluginScanner.scanZip(path);
                if(!plugins.isEmpty()) {
                    classLoader.addURL(path.toUri().toURL());

                    loadPlugins(path, plugins);
                }
            }
        }
    }

    private void loadPlugins(Object source, Iterable<String> plugins) {
        for (String plugin : plugins) {
            try {
                Class<?> pluginClass = Class.forName(plugin);
                LanternPluginContainer container = new LanternPluginContainer(pluginClass);
                registerPlugin(container);
                Sponge.getGame().getEventManager().registerListeners(container, container.getInstance());

                Sponge.getLogger().info("Loaded plugin: {} {} (from {})", container.getName(), container.getVersion(), source);
            } catch (Throwable e) {
                Sponge.getLogger().error("Failed to load plugin: {} (from {})", plugin, source, e);
            }
        }
    }

    @Override
    public Optional<PluginContainer> fromInstance(Object instance) {
        checkNotNull(instance);

        if(instance instanceof PluginContainer) {
            return Optional.of((PluginContainer) instance);
        }

        return Optional.ofNullable(this.pluginInstances.get(instance));
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        return Optional.ofNullable(plugins.get(id));
    }

    @Override
    public Logger getLogger(PluginContainer plugin) {
        return ((LanternPluginContainer) plugin).getLogger();
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return Collections.unmodifiableCollection(this.plugins.values());
    }

    @Override
    public boolean isLoaded(String id) {
        return this.plugins.containsKey(id);
    }
}
