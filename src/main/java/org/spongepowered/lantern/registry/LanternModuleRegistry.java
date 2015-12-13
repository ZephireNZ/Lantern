package org.spongepowered.lantern.registry;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.registry.type.DifficultyRegistryModule;
import org.spongepowered.lantern.registry.type.GameModeRegistryModule;
import org.spongepowered.lantern.registry.type.GeneratorRegistryModule;
import org.spongepowered.lantern.registry.type.world.DimensionRegistryModule;
import org.spongepowered.lantern.world.LanternWorldBuilder;

public class LanternModuleRegistry {

    public static LanternModuleRegistry getInstance() {
        return Holder.INSTANCE;
    }

    public void registerDefaultModules() {
        LanternGameRegistry registry = SpongeImpl.getRegistry();
        registerFactories();
        registerDefaultSuppliers(registry);
        registerModule(registry);
    }

    private void registerFactories() {
        //TODO: Implement
    }

    private void registerDefaultSuppliers(LanternGameRegistry registry) {
        registry.registerBuilderSupplier(WorldBuilder.class, LanternWorldBuilder::new);
    }

    private void registerModule(LanternGameRegistry registry) {
        registry.registerModule(DimensionType.class, DimensionRegistryModule.getInstance());
        registry.registerModule(GeneratorType.class, new GeneratorRegistryModule());
        registry.registerModule(GameMode.class, new GameModeRegistryModule());
        registry.registerModule(Difficulty.class, new DifficultyRegistryModule());
    }

    private LanternModuleRegistry() { }

    private static final class Holder {

        private static final LanternModuleRegistry INSTANCE = new LanternModuleRegistry();
    }

}
