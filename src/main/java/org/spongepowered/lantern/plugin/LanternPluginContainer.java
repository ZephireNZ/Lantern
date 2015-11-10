package org.spongepowered.lantern.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.lantern.Sponge;
import org.spongepowered.lantern.guice.LanternPluginGuiceModule;

public class LanternPluginContainer implements PluginContainer {

    private final String id;
    private final String name;
    private final String version;
    private final Logger logger;
    private final Object instance;

    public LanternPluginContainer(Class<?> pluginClass) {
        Plugin info = pluginClass.getAnnotation(Plugin.class);
        this.id = info.id();
        this.name = info.name();
        this.version = info.version();
        this.logger = LoggerFactory.getLogger(this.id);

        this.instance = Sponge.getInjector().createChildInjector(new LanternPluginGuiceModule(this, pluginClass)).getInstance(pluginClass);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public Object getInstance() {
        return this.instance;
    }

    public Logger getLogger() {
        return this.logger;
    }
}
