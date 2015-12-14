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
package org.spongepowered.lantern.registry.type;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.lantern.registry.CatalogRegistryModule;
import org.spongepowered.lantern.registry.util.RegisterCatalog;
import org.spongepowered.lantern.world.difficulty.LanternDifficulty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DifficultyRegistryModule implements CatalogRegistryModule<Difficulty> {

    @RegisterCatalog(Difficulties.class)
    private final Map<String, Difficulty> difficultyMappings = new HashMap<>();

    @Override
    public Optional<Difficulty> getById(String id) {
        return Optional.ofNullable(this.difficultyMappings.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<Difficulty> getAll() {
        return ImmutableList.copyOf(this.difficultyMappings.values());
    }

    @Override
    public void registerDefaults() {
        this.difficultyMappings.put("peaceful", new LanternDifficulty(0, "peaceful"));
        this.difficultyMappings.put("easy", new LanternDifficulty(1, "easy"));
        this.difficultyMappings.put("normal", new LanternDifficulty(2, "normal"));
        this.difficultyMappings.put("hard", new LanternDifficulty(3, "hard"));
    }
}
