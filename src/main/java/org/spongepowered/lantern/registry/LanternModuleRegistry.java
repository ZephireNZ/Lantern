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
