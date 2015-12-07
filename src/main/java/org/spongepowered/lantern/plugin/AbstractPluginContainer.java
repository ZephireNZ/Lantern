package org.spongepowered.lantern.plugin;

import com.google.common.base.Objects;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class AbstractPluginContainer implements PluginContainer {

    protected AbstractPluginContainer() {
    }

    @Override
    public String toString() {
        return Objects.toStringHelper("Plugin")
                .add("id", this.getId())
                .add("name", this.getName())
                .add("version", this.getVersion())
                .toString();
    }
}
