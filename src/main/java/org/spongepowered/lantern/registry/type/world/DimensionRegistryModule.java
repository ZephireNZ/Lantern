package org.spongepowered.lantern.registry.type.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.lantern.config.LanternConfig;
import org.spongepowered.lantern.registry.ExtraClassCatalogRegistryModule;
import org.spongepowered.lantern.registry.util.RegisterCatalog;
import org.spongepowered.lantern.world.dimension.EndDimension;
import org.spongepowered.lantern.world.dimension.LanternDimensionType;
import org.spongepowered.lantern.world.dimension.NetherDimension;
import org.spongepowered.lantern.world.dimension.OverworldDimension;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DimensionRegistryModule implements ExtraClassCatalogRegistryModule<DimensionType, Dimension> {

    @RegisterCatalog(DimensionTypes.class)
    private final Map<String, DimensionType> dimensionTypeMappings = Maps.newHashMap();
    private final Map<Class<? extends Dimension>, LanternConfig<LanternConfig.DimensionConfig>> dimensionConfigs = Maps.newHashMap();
    public final Map<Class<? extends Dimension>, DimensionType> dimensionClassMappings = Maps.newHashMap();
    public final Map<UUID, String> worldFolderUniqueIdMappings = Maps.newHashMap();

    public static DimensionRegistryModule getInstance() {
        return Holder.instance;
    }

    @Override
    public boolean hasRegistrationFor(Class<? extends Dimension> mappedClass) {
        return this.dimensionClassMappings.containsKey(mappedClass);
    }

    @Override
    public DimensionType getForClass(Class<? extends Dimension> clazz) {
        return this.dimensionClassMappings.get(checkNotNull(clazz));
    }

    @Override
    public void registerAdditionalCatalog(DimensionType extraCatalog) {
        this.dimensionTypeMappings.put(extraCatalog.getId().toLowerCase(), extraCatalog);
        this.dimensionClassMappings.put(extraCatalog.getDimensionClass(), extraCatalog);
    }

    @Override
    public Optional<DimensionType> getById(String id) {
        return Optional.ofNullable(this.dimensionTypeMappings.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<DimensionType> getAll() {
        return Collections.unmodifiableCollection(this.dimensionTypeMappings.values());
    }

    @Override
    public void registerDefaults() {
        registerAdditionalCatalog(new LanternDimensionType("overworld", "Overworld", true, OverworldDimension.class));
        registerAdditionalCatalog(new LanternDimensionType("nether", "Nether", false, NetherDimension.class));
        registerAdditionalCatalog(new LanternDimensionType("end", "The End", false, EndDimension.class));
    }

    public void registerWorldUniqueId(UUID uuid, String folderName) {
        this.worldFolderUniqueIdMappings.put(uuid, folderName);
    }

    public boolean isConfigRegistered(Class<? extends Dimension> clazz) {
        return this.dimensionConfigs.containsKey(clazz);
    }

    public void registerConfig(Class<? extends Dimension> dimension, LanternConfig<LanternConfig.DimensionConfig> config) {
        this.dimensionConfigs.put(dimension, config);
    }

    public LanternConfig<LanternConfig.DimensionConfig> getConfig(Class<? extends Dimension> aClass) {
        return this.dimensionConfigs.get(aClass);
    }

    private DimensionRegistryModule() { }

    private static final class Holder {

        private static final DimensionRegistryModule instance = new DimensionRegistryModule();
    }
}
