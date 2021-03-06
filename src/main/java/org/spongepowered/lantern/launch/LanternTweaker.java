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
package org.spongepowered.lantern.launch;

import static com.google.common.io.Resources.getResource;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.lantern.SpongeImpl;

import java.io.File;
import java.net.URL;
import java.util.List;

public class LanternTweaker implements ITweaker {

    private static final Logger logger = LogManager.getLogger(SpongeImpl.ECOSYSTEM_NAME);

    private String[] args = ArrayUtils.EMPTY_STRING_ARRAY;

    public static Logger getLogger() {
        return logger;
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        if (args != null && !args.isEmpty()) {
            this.args = args.toArray(new String[args.size()]);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader loader) {
        loader.addClassLoaderExclusion("io.netty.");
        loader.addClassLoaderExclusion("jline.");
        loader.addClassLoaderExclusion("org.fusesource.");

        // Don't allow libraries to be transformed
        loader.addTransformerExclusion("joptsimple.");

        // Server libraries
        loader.addTransformerExclusion("com.google.gson.");
        loader.addTransformerExclusion("org.apache.commons.codec.");
        loader.addTransformerExclusion("org.apache.commons.io.");
        loader.addTransformerExclusion("org.apache.commons.lang3.");
        loader.addTransformerExclusion("com.flowpowered.networking.");

        // SpongeAPI
        loader.addTransformerExclusion("com.flowpowered.noise.");
        loader.addTransformerExclusion("com.flowpowered.math.");
        loader.addTransformerExclusion("org.slf4j.");

        // Guice
        loader.addTransformerExclusion("com.google.inject.");
        loader.addTransformerExclusion("org.aopalliance.");

        // Configurate
        loader.addTransformerExclusion("ninja.leaping.configurate.");
        loader.addTransformerExclusion("com.typesafe.config.");
        loader.addTransformerExclusion("org.yaml.snakeyaml.");

        // Sponge Launch
        loader.addTransformerExclusion("org.spongepowered.tools.");
        loader.addClassLoaderExclusion("org.spongepowered.lantern.launch.");

        logger.debug("Applying access transformer...");
        Launch.blackboard.put("lantern.at", new URL[]{ getResource("lantern_at.cfg") });
        loader.registerTransformer("org.spongepowered.lantern.launch.AccessTransformer");

        logger.info("Initialization finished. Starting Minecraft server...");
    }

    @Override
    public String getLaunchTarget() {
        return "org.spongepowered.lantern.Lantern";
    }

    @Override
    public String[] getLaunchArguments() {
        return this.args;
    }
}
