package org.spongepowered.lantern.plugin;

import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.guice.LanternPluginGuiceModule;

import java.util.Optional;

public class LanternPluginContainer extends AbstractPluginContainer {

    private final String id;
    private final String name;
    private final String version;
    private final Optional<Object> instance;

    public LanternPluginContainer(Class<?> pluginClass) {
        Plugin info = pluginClass.getAnnotation(Plugin.class);
        this.id = info.id();
        this.name = info.name();
        this.version = info.version();

        this.instance = Optional.of(SpongeImpl.getInjector().createChildInjector(new LanternPluginGuiceModule(this, pluginClass)).getInstance(pluginClass));
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
    public Optional<Object> getInstance() {
        return this.instance;
    }
}
