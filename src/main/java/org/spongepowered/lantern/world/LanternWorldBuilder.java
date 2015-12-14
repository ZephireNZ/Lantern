package org.spongepowered.lantern.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.TeleporterAgent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.world.gen.WorldGeneratorRegistry;

import java.util.Optional;
import java.util.Random;

public class LanternWorldBuilder implements WorldBuilder {

    private String name;
    private long seed;
    private GameMode gameMode;
    private GeneratorType generatorType;
    private DimensionType dimensionType;
    private boolean mapFeaturesEnabled;
    private boolean hardcore;
    private boolean worldEnabled;
    private boolean loadOnStartup;
    private boolean keepSpawnLoaded;
    private DataContainer generatorSettings;
    private ImmutableList<WorldGeneratorModifier> generatorModifiers;

    public LanternWorldBuilder() {
        reset();
    }

    public LanternWorldBuilder(WorldCreationSettings settings) {
        fill(settings);
    }

    public LanternWorldBuilder(WorldProperties properties) {
        fill(properties);
    }

    @Override
    public LanternWorldBuilder fill(WorldCreationSettings settings) {
        checkNotNull(settings);
        this.name = settings.getWorldName();
        this.seed = settings.getSeed();
        this.gameMode = settings.getGameMode();
        this.generatorType = settings.getGeneratorType();
        this.dimensionType = settings.getDimensionType();
        this.mapFeaturesEnabled = settings.usesMapFeatures();
        this.hardcore = settings.isHardcore();
        this.worldEnabled = settings.isEnabled();
        this.loadOnStartup = settings.loadOnStartup();
        this.keepSpawnLoaded = settings.doesKeepSpawnLoaded();
        return this;
    }

    @Override
    public LanternWorldBuilder fill(WorldProperties properties) {
        checkNotNull(properties);
        this.name = properties.getWorldName();
        this.seed = properties.getSeed();
        this.gameMode = properties.getGameMode();
        this.generatorType = properties.getGeneratorType();
        this.dimensionType = properties.getDimensionType();
        this.mapFeaturesEnabled = properties.usesMapFeatures();
        this.hardcore = properties.isHardcore();
        this.worldEnabled = properties.isEnabled();
        this.loadOnStartup = properties.loadOnStartup();
        this.keepSpawnLoaded = properties.doesKeepSpawnLoaded();
        return this;
    }

    @Override
    public WorldBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public WorldBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public WorldBuilder gameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    @Override
    public WorldBuilder generator(GeneratorType type) {
        this.generatorType = type;
        return this;
    }

    @Override
    public WorldBuilder dimensionType(DimensionType type) {
        this.dimensionType = type;
        return this;
    }

    @Override
    public WorldBuilder usesMapFeatures(boolean enabled) {
        this.mapFeaturesEnabled = enabled;
        return this;
    }

    @Override
    public WorldBuilder hardcore(boolean enabled) {
        this.hardcore = enabled;
        return this;
    }

    @Override
    public WorldBuilder enabled(boolean state) {
        this.worldEnabled = state;
        return this;
    }

    @Override
    public WorldBuilder loadsOnStartup(boolean state) {
        this.loadOnStartup = state;
        return this;
    }

    @Override
    public WorldBuilder keepsSpawnLoaded(boolean state) {
        this.keepSpawnLoaded = state;
        return this;
    }

    @Override
    public WorldBuilder generatorSettings(DataContainer settings) {
        this.generatorSettings = settings;
        return this;
    }

    @Override
    public WorldBuilder generatorModifiers(WorldGeneratorModifier... modifiers) {
        ImmutableList<WorldGeneratorModifier> defensiveCopy = ImmutableList.copyOf(modifiers);
        WorldGeneratorRegistry.getInstance().checkAllRegistered(defensiveCopy);
        this.generatorModifiers = defensiveCopy;
        return this;
    }

    @Override
    public WorldBuilder teleporterAgent(TeleporterAgent agent) {
        // TODO
        return null;
    }

    @Override
    public Optional<World> build() throws IllegalStateException {
        final WorldCreationSettings settings = buildSettings();
        SpongeImpl.getGame().getServer().createWorld(settings);
        return SpongeImpl.getGame().getServer().loadWorld(settings.getWorldName());
    }

    @Override
    public WorldCreationSettings buildSettings() throws IllegalStateException {
        return new LanternWorldCreationSettings(this.name, this.worldEnabled, this.loadOnStartup, this.keepSpawnLoaded, this.seed,
                this.gameMode, this.generatorType, this.generatorModifiers, this.mapFeaturesEnabled, this.hardcore, this.dimensionType, this.generatorSettings);
    }

    @Override
    public WorldBuilder reset() {
        this.name = "spongeworld";
        this.seed = new Random().nextLong();
        this.gameMode = GameModes.SURVIVAL;
        this.generatorType = GeneratorTypes.DEFAULT;
        this.dimensionType = DimensionTypes.OVERWORLD;
        this.mapFeaturesEnabled = true;
        this.hardcore = false;
        this.worldEnabled = true;
        this.loadOnStartup = true;
        this.keepSpawnLoaded = false;
        this.generatorSettings = new MemoryDataContainer();
        this.generatorModifiers = ImmutableList.of();
        return this;
    }
}