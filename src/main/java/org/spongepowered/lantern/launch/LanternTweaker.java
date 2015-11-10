package org.spongepowered.lantern.launch;

import static com.google.common.io.Resources.getResource;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.lantern.Sponge;
import org.spongepowered.lantern.launch.console.ConsoleManager;

import java.io.File;
import java.net.URL;
import java.util.List;

public class LanternTweaker implements ITweaker {

    private static final Logger logger = LogManager.getLogger(Sponge.ECOSYSTEM_NAME);

    private String[] args = ArrayUtils.EMPTY_STRING_ARRAY;

    public static Logger getLogger() {
        return logger;
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        new ConsoleManager();

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
        Launch.blackboard.put("vanilla.at", new URL[]{ getResource("common_at.cfg"), getResource("vanilla_at.cfg") });
        loader.registerTransformer("org.spongepowered.lantern.launch.AccessTransformer");

        logger.info("Initialization finished. Starting Minecraft server...");
    }

    @Override
    public String getLaunchTarget() {
        return null; //TODO: Implement
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0]; //TODO: Implement
    }
}
