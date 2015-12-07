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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Platform;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Map;

@Singleton
public class LanternPlatform implements Platform {

    private final MinecraftVersion version;
    private final Map<String, Object> platformMap;
    private final PluginContainer api;
    private final PluginContainer impl;

    @Inject
    public LanternPlatform(MinecraftVersion version, @Named(SpongeImpl.ECOSYSTEM_NAME) PluginContainer impl, @Named(SpongeImpl.API_NAME) PluginContainer api) {
        this.version = version;
        this.api = api;
        this.impl = impl;

        platformMap = ImmutableMap.<String, Object>builder()
                .put("Type", this.getType())
                .put("ApiName", this.api.getName())
                .put("ApiVersion", this.api.getVersion())
                .put("ImplementationName", this.impl.getName())
                .put("ImplementationVersion", this.api.getVersion())
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
    public PluginContainer getApi() {
        return this.api;
    }

    @Override
    public PluginContainer getImplementation() {
        return this.impl;
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return this.version;
    }

    @Override
    public Map<String, Object> asMap() {
        return platformMap;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("type", getType())
                .add("executionType", getExecutionType())
                .add("api", this.api)
                .add("impl", this.impl)
                .add("minecraftVersion", getMinecraftVersion())
                .toString();
    }
}
