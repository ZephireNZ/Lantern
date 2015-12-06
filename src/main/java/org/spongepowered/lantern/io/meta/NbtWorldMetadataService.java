package org.spongepowered.lantern.io.meta;

import static org.spongepowered.api.data.DataQuery.of;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.io.WorldMetadataService;
import org.spongepowered.lantern.util.nbt.NbtDataInputStream;
import org.spongepowered.lantern.util.nbt.NbtDataOutputStream;
import org.spongepowered.lantern.world.LanternWorld;
import org.spongepowered.lantern.world.LanternWorldProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NbtWorldMetadataService implements WorldMetadataService {

    public static final DataQuery LEVEL_DATA = of("Data");
    public static final DataQuery SEED = of("RandomSeed");
    public static final DataQuery CLEAR_WEATHER_TIME = of("clearWeatherTime");
    public static final DataQuery THUNDERING = of("thundering");
    public static final DataQuery THUNDER_TIME = of("thunderTime");
    public static final DataQuery RAINING = of("raining");
    public static final DataQuery RAIN_TIME = of("rainTime");
    public static final DataQuery TIME = of("Time");
    public static final DataQuery WORLD_TIME = of("DayTime");
    public static final DataQuery SPAWN_X = of("SpawnX");
    public static final DataQuery SPAWN_Y = of("SpawnY");
    public static final DataQuery SPAWN_Z = of("SpawnZ");
    public static final DataQuery GAME_RULES = of("GameRules");
    public static final DataQuery PLAYER = of("Player");
    public static final DataQuery LEVEL_NAME = of("LevelName");
    public static final DataQuery VERSION = of("version");
    public static final DataQuery LAST_PLAYED = of("LastPlayed");
    public static final DataQuery DIMENSION_ID = of("dimensionId");
    public static final DataQuery DIMENSION_TYPE = of("dimensionType");
    public static final DataQuery WORLD_ENABLED = of("enabled");
    public static final DataQuery KEEP_SPAWN_LOADED = of("keepSpawnLoaded");
    public static final DataQuery LOAD_ON_STARTUP = of("loadOnStartup");
    public static final DataQuery GENERATOR_MODIFIERS = of("generatorModifiers");
    public static final DataQuery GENERATOR_NAME = of("generatorName");
    public static final DataQuery GENERATOR_OPTIONS = of("generatorOptions");
    public static final DataQuery GAME_TYPE = of("GameType");
    public static final DataQuery MAP_FEATURES = of("MapFeatures");
    public static final DataQuery HARDCORE = of("hardcore");
    public static final DataQuery ALLOW_COMMANDS = DataQuery.of("allowCommands");
    public static final DataQuery INITIALIZED = of("initialized");
    public static final DataQuery BORDER_X = of("BorderCenterX");
    public static final DataQuery BORDER_Z = of("BorderCenterZ");
    public static final DataQuery BORDER_SIZE = of("BorderSize");
    public static final DataQuery BORDER_TIME_REMAINING = of("BorderSizeLerpTime");
    public static final DataQuery BORDER_SAFE_ZONE = of("BorderSafeZone");
    public static final DataQuery BORDER_DAMAGE_PER_BLOCK = of("BorderDamagePerBlock");
    public static final DataQuery BORDER_TARGET_SIZE = of("BorderSizeLerpTarget");
    public static final DataQuery BORDER_WARNING_DISTANCE = of("BorderWarningBlocks");
    public static final DataQuery BORDER_WARNING_TIME = of("BorderWarningTime");
    public static final DataQuery DIFFICULTY = of("Difficulty");

    private final LanternWorld world;
    private final Path dir;

    public NbtWorldMetadataService(LanternWorld world, Path dir) {
        this.world = world;
        this.dir = dir;

        try {
            Files.createDirectories(dir);
        } catch (IOException e){
            SpongeImpl.getLogger().warn("Failed to create directory: " + dir);
        }
    }

    @Override
    public LanternWorldProperties readWorldData() throws IOException {
        LanternWorldProperties properties = new LanternWorldProperties();

        Path levelFile = dir.resolve("level.dat");
        if (Files.isRegularFile(levelFile)) {
            try (NbtDataInputStream in = new NbtDataInputStream(Files.newInputStream(levelFile))) {
                DataView level = in.read();
                properties.loadVanillaData(level);
            } catch (IOException e) {
                handleWorldException("level.dat", e);
            }
        }

        Path spongeFile = dir.resolve("level_sponge.dat");
        if (Files.isRegularFile(spongeFile)) {
            try (NbtDataInputStream in = new NbtDataInputStream(Files.newInputStream(spongeFile))) {
                DataView sponge = in.read();
                properties.loadSpongeData(sponge);
            } catch (IOException e) {
                handleWorldException("level_sponge.dat", e);
            }
        }

        return properties;
    }

    private void handleWorldException(String file, IOException e) {
        SpongeImpl.getGame().getServer().unloadWorld(world);
        SpongeImpl.getLogger().error("Unable to access " + file + " for world " + world.getName(), e);
    }

    @Override
    public void writeWorldData() throws IOException {
        LanternWorldProperties properties = world.getProperties();

        try (NbtDataOutputStream nbtOut = new NbtDataOutputStream(Files.newOutputStream(dir.resolve("level.dat")))) {
            nbtOut.write(properties.getVanillaRoot());
        } catch (IOException e) {
            handleWorldException("level.dat", e);
        }

        try (NbtDataOutputStream nbtOut = new NbtDataOutputStream(Files.newOutputStream(dir.resolve("level_sponge.dat")))) {
            nbtOut.write(properties.getSpongeRoot());
        } catch (IOException e) {
            handleWorldException("level_sponge.dat", e);
        }
    }
}
