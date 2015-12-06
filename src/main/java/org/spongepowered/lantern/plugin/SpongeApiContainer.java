package org.spongepowered.lantern.plugin;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.SpongeVersion;

import java.util.Optional;

@Singleton
public class SpongeApiContainer extends AbstractPluginContainer {

    protected SpongeApiContainer() {
    }

    @Override
    public String getId() {
        return SpongeImpl.API_ID;
    }

    @Override
    public String getName() {
        return SpongeImpl.API_NAME;
    }

    @Override
    public String getVersion() {
        return SpongeVersion.API_VERSION;
    }

    @Override
    public Logger getLogger() {
        return SpongeImpl.getSlf4jLogger();
    }

    @Override
    public Optional<Object> getInstance() {
        return Optional.of(SpongeImpl.getGame());
    }
}
