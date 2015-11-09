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
package org.spongepowered.lantern;

import org.spongepowered.api.MinecraftVersion;

public class LanternMinecraftVersion implements MinecraftVersion {

    private final String name;
    private final int protocol;

    public LanternMinecraftVersion(String name, int protocol) {
        this.name = name;
        this.protocol = protocol;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        if (this.equals(o)) {
            return 0;
        } else if (o.isLegacy()) {
            return 1;
        } else {
            return this.getProtocol() - ((LanternMinecraftVersion) o).getProtocol();
        }
    }
}
