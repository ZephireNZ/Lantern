package org.spongepowered.lantern.registry.type;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.lantern.registry.CatalogRegistryModule;
import org.spongepowered.lantern.registry.util.RegisterCatalog;
import org.spongepowered.lantern.world.gen.LanternGeneratorTypes;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class GeneratorRegistryModule implements CatalogRegistryModule<GeneratorType> {

    @RegisterCatalog(GeneratorTypes.class)
    private final Map<String, GeneratorType> generatorTypeMappings = Maps.newHashMap();

    @Override
    public Optional<GeneratorType> getById(String id) {
        return Optional.ofNullable(this.generatorTypeMappings.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<GeneratorType> getAll() {
        return ImmutableList.copyOf(this.generatorTypeMappings.values());
    }

    @Override
    public void registerDefaults() {
        this.generatorTypeMappings.put("default", LanternGeneratorTypes.DEFAULT);
        this.generatorTypeMappings.put("flat", LanternGeneratorTypes.FLAT);
        this.generatorTypeMappings.put("debug", LanternGeneratorTypes.DEBUG);
        this.generatorTypeMappings.put("nether", LanternGeneratorTypes.NETHER);
        this.generatorTypeMappings.put("end", LanternGeneratorTypes.END);
        this.generatorTypeMappings.put("overworld", LanternGeneratorTypes.OVERWORLD);
    }
}
