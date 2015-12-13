package org.spongepowered.lantern.world.dimension;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;

public class LanternDimensionType implements DimensionType {

    private String id;
    private String name;
    private boolean keepLoaded;
    private final Class<? extends Dimension> dimensionClass;

    public LanternDimensionType(String id, String name, boolean keepLoaded, Class<? extends Dimension> dimensionClass) {
        this.id = checkNotNull(id);
        this.name = checkNotNull(name);
        this.keepLoaded = checkNotNull(keepLoaded);
        this.dimensionClass = checkNotNull(dimensionClass);
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return keepLoaded;
    }

    @Override
    public Class<? extends Dimension> getDimensionClass() {
        return dimensionClass;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
