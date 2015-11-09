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

import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Platform;

import java.util.Map;

public class LanternPlatform implements Platform {

    private final MinecraftVersion version;
    private final String apiVersion;
    private final String implVersion;
    private final Map<String, Object> platformMap;

    public LanternPlatform(MinecraftVersion version, String apiVersion, String implVersion) {
        this.version = version;
        this.apiVersion = apiVersion;
        this.implVersion = implVersion;

        platformMap = ImmutableMap.<String, Object>builder()
                .put("Name", this.getName())
                .put("Type", this.getType())
                .put("ExecutionType", this.getExecutionType())
                .put("ApiVersion", this.getApiVersion())
                .put("ImplementationVersion", this.getVersion())
                .put("MinecraftVersion", this.getMinecraftVersion())
                .build();
    }

    @Override
    public Type getType() {
        return Type.SERVER;
    }

    @Override
    public Type getExecutionType() {
        return Type.SERVER;
    }

    @Override
    public String getName() {
        return "Lantern";
    }

    @Override
    public String getVersion() {
        return this.implVersion;
    }

    @Override
    public String getApiVersion() {
        return this.apiVersion;
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return this.version;
    }

    @Override
    public Map<String, Object> asMap() {
        return platformMap;
    }
}
