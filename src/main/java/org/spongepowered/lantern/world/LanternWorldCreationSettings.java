/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
    private boolean pvp;

    public LanternWorldCreationSettings(WorldProperties props) {
        this(props.getWorldName(), props.isEnabled(), props.loadOnStartup(), props.doesKeepSpawnLoaded(),
                props.getSeed(), props.getGameMode(), props.getGeneratorType(), props.getGeneratorModifiers(),
                props.usesMapFeatures(), props.isHardcore(), props.getDimensionType(), props.getGeneratorSettings(), props.isPVPEnabled());
    }

    // One hell of a ctor...
    public LanternWorldCreationSettings(String name, boolean enabled, boolean loadOnStartup, boolean keepSpawnLoaded,
                                        long seed, GameMode gamemode, GeneratorType generator, Collection<WorldGeneratorModifier> modifiers,
                                        boolean mapFeatures, boolean hardcore, DimensionType dimension, DataContainer generatorSettings, boolean pvp) {
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
        this.pvp = pvp;
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

    @Override
    public boolean isPVPEnabled() {
        return this.pvp;
    }
}
