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

import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;

public class EndDimension implements Dimension {

    private boolean playerRespawns = false;
    private boolean evaporate = false;

    @Override
    public String getName() {
        return "The End";
    }

    @Override
    public boolean allowsPlayerRespawns() {
        return this.playerRespawns;
    }

    @Override
    public void setAllowsPlayerRespawns(boolean allow) {
        this.playerRespawns = allow;
    }

    @Override
    public int getMinimumSpawnHeight() {
        return 0; //TODO: Implement
    }

    @Override
    public boolean doesWaterEvaporate() {
        return this.evaporate;
    }

    @Override
    public void setWaterEvaporates(boolean evaporates) {
        this.evaporate = evaporates;
    }

    @Override
    public boolean hasSky() {
        return true;
    }

    @Override
    public DimensionType getType() {
        return DimensionTypes.END;
    }

    @Override
    public int getHeight() {
        return 0; //TODO: Implement
    }

    @Override
    public int getBuildHeight() {
        return 0; //TODO: Implement
    }

    @Override
    public GeneratorType getGeneratorType() {
        return GeneratorTypes.END;
    }

    @Override
    public Context getContext() {
        return null; //TODO: Implement
    }
}
