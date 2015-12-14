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
