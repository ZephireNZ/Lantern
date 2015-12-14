package org.spongepowered.lantern.world;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;

public class LanternWorldCreationSettings implements WorldCreationSettings {


    private String name;
    private boolean enabled;
    private boolean loadOnStartup;
    private boolean keepSpawnLoaded;
    private long seed;
    private GameMode gamemode;
    private GeneratorType generator;
    private Collection<WorldGeneratorModifier> modifiers;
    private boolean mapFeatures;
    private boolean hardcore;
    private boolean commandsAllowed = true; // Clientside only
    private boolean bonusChest = false; // TODO
    private DimensionType dimension;
    private DataContainer generatorSettings;

    public LanternWorldCreationSettings(WorldProperties props) {
        this(props.getWorldName(), props.isEnabled(), props.loadOnStartup(), props.doesKeepSpawnLoaded(),
                props.getSeed(), props.getGameMode(), props.getGeneratorType(), props.getGeneratorModifiers(),
                props.usesMapFeatures(), props.isHardcore(), props.getDimensionType(), props.getGeneratorSettings());
    }

    // One hell of a ctor...
    public LanternWorldCreationSettings(String name, boolean enabled, boolean loadOnStartup, boolean keepSpawnLoaded,
                                        long seed, GameMode gamemode, GeneratorType generator, Collection<WorldGeneratorModifier> modifiers,
                                        boolean mapFeatures, boolean hardcore, DimensionType dimension, DataContainer generatorSettings) {
        this.name = name;
        this.enabled = enabled;
        this.loadOnStartup = loadOnStartup;
        this.keepSpawnLoaded = keepSpawnLoaded;
        this.seed = seed;
        this.gamemode = gamemode;
        this.generator = generator;
        this.modifiers = modifiers;
        this.mapFeatures = mapFeatures;
        this.hardcore = hardcore;
        this.dimension = dimension;
        this.generatorSettings = generatorSettings;
    }

    @Override
    public String getWorldName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean loadOnStartup() {
        return this.loadOnStartup;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.keepSpawnLoaded;
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public GameMode getGameMode() {
        return this.gamemode;
    }

    @Override
    public GeneratorType getGeneratorType() {
        return this.generator;
    }

    @Override
    public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        return this.modifiers;
    }

    @Override
    public boolean usesMapFeatures() {
        return this.mapFeatures;
    }

    @Override
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Override
    public boolean commandsAllowed() {
        return this.commandsAllowed;
    }

    @Override
    public boolean bonusChestEnabled() {
        return this.bonusChest;
    }

    @Override
    public DimensionType getDimensionType() {
        return this.dimension;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return this.generatorSettings;
    }
}
