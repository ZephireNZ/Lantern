package org.spongepowered.lantern.world.storage;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.spongepowered.api.data.DataQuery.of;
import static org.spongepowered.lantern.data.util.DataQueries.*;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.entity.living.player.LanternGameMode;
import org.spongepowered.lantern.world.difficulty.LanternDifficulty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class LanternWorldProperties implements WorldProperties {

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
    // Sponge fields
    private UUID uuid;
    private boolean enabled;
    private DimensionType dimensionType;
    private boolean loadOnStartup;
    private boolean keepSpawnLoaded;
    private Collection<WorldGeneratorModifier> modifiers;

    // Vanilla fields
    private String name;
    private Vector3i spawnPos;
    private GeneratorType generator;
    private long seed;
    private long totalTime;
    private long worldTime;
    private int dimensionId;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private int clearTime;
    private GameMode gamemode;
    private boolean useMapFeatures;
    private boolean hardcore;
    private boolean commandsAllowed;
    private boolean initialized;
    private Difficulty difficulty;
    private Vector3d borderCenter;
    private double borderDiameter;
    private long borderTimeRemaining;
    private double borderTargetDiameter;
    private double borderDamageThreshold;
    private double borderDamage;
    private int borderWarningTime;
    private int borderWarningDistance;
    private Map<String, String> gamerules;
    private DataContainer generatorSettings;

    // Block Tracking
    private BiMap<Integer, UUID> playerUniqueIdMap = HashBiMap.create();
    private List<UUID> pendingUniqueIds = Lists.newArrayList();
    private int trackedUniqueIdCount = 0;
    private List<DataView> playerUniqueData;

    //Direct section links
    private DataView rootData;
    private DataView spongeRootData;
    private DataView spongeData;
    private DataView levelData;

    public LanternWorldProperties() {
        this.uuid = UUID.randomUUID();
        this.enabled = true;
        this.dimensionType = DimensionTypes.OVERWORLD;
        this.loadOnStartup = true;
        this.keepSpawnLoaded = true;
        this.modifiers = Lists.newArrayList();
        this.name = "world-" + uuid.toString();
        this.generator = GeneratorTypes.DEFAULT;
        this.seed = new Random().nextLong();
        this.totalTime = 0;
        this.worldTime = 0;
        this.dimensionId = 0; //TODO: Load from registry
        this.raining = false;
        this.rainTime = 0;
        this.thundering = false;
        this.thunderTime = 0;
        this.gamemode = GameModes.SURVIVAL;
        this.useMapFeatures = true;
        this.hardcore = false;
        this.commandsAllowed = true;
        this.initialized = false;
        this.difficulty = Difficulties.EASY;
        this.borderCenter = new Vector3d(0D, 0D, 0D);
        this.borderDiameter = 6.0E7D;
        this.borderTimeRemaining = 0L;
        this.borderTargetDiameter = 0.0D;
        this.borderDamageThreshold = 5.0D;
        this.borderDamage = 0.2D;
        this.borderWarningTime = 15;
        this.borderWarningDistance = 5;
        this.gamerules = new HashMap<>(); //TODO: Default gamerules
        this.generatorSettings = new MemoryDataContainer();
        this.spawnPos = Vector3i.ZERO;

        this.spongeRootData = new MemoryDataContainer();
        this.rootData = new MemoryDataContainer();
        this.spongeData = spongeRootData.createView(SPONGE_DATA);
        this.levelData = rootData.createView(LEVEL_DATA);
        this.playerUniqueData = Lists.newArrayList();

    }

    public LanternWorldProperties(WorldCreationSettings settings) {
        this();
        this.name = settings.getWorldName();
        this.enabled = settings.isEnabled();
        this.loadOnStartup = settings.loadOnStartup();
        this.keepSpawnLoaded = settings.doesKeepSpawnLoaded();
        this.seed = settings.getSeed();
        this.gamemode = settings.getGameMode();
        this.generator = settings.getGeneratorType();
        this.modifiers = settings.getGeneratorModifiers();
        this.useMapFeatures = settings.usesMapFeatures();
        this.hardcore = settings.isHardcore();
        this.commandsAllowed = settings.commandsAllowed();
        this.dimensionType = settings.getDimensionType();
        this.dimensionId = 0; //TODO: Load from registry
        this.generatorSettings = settings.getGeneratorSettings().copy();
    }

    private void updateSpongeData() {
        this.spongeData.set(LEVEL_NAME, this.name);
        this.spongeData.set(DIMENSION_ID, this.dimensionId);
        this.spongeData.set(DIMENSION_TYPE, this.dimensionType.getDimensionClass().getName());
        this.spongeData.set(SPONGE_UUID_MOST, this.uuid.getMostSignificantBits());
        this.spongeData.set(SPONGE_UUID_LEAST, this.uuid.getLeastSignificantBits());
        this.spongeData.set(WORLD_ENABLED, this.enabled);
        this.spongeData.set(KEEP_SPAWN_LOADED, this.keepSpawnLoaded);
        this.spongeData.set(LOAD_ON_STARTUP, this.loadOnStartup);

        List<String> modifierIds = this.modifiers.stream().map(WorldGeneratorModifier::getId).collect(Collectors.toList());
        this.spongeData.set(GENERATOR_MODIFIERS, modifierIds);

        Iterator<UUID> iterator = this.pendingUniqueIds.iterator();
        while (iterator.hasNext()) {
            UUID uuidToAdd = iterator.next();
            DataContainer valueNbt = new MemoryDataContainer();
            valueNbt.set(SPONGE_UUID_MOST, uuidToAdd.getMostSignificantBits());
            valueNbt.set(SPONGE_UUID_LEAST, uuidToAdd.getLeastSignificantBits());
            this.playerUniqueData.add(valueNbt);
            iterator.remove();
        }
        this.spongeData.set(SPONGE_PLAYER_UUID_TABLE, playerUniqueData);
    }

    private void updateVanillaData() {
        this.levelData.set(SEED, this.seed);
        this.levelData.set(GENERATOR_NAME, this.generator.getName());
        //TODO: Mojangson
        this.levelData.set(GENERATOR_OPTIONS, this.generatorSettings);
        this.levelData.set(GAME_TYPE, ((LanternGameMode) this.gamemode).getNumericId());
        this.levelData.set(MAP_FEATURES, this.useMapFeatures);
        this.levelData.set(SPAWN_X, this.spawnPos.getX());
        this.levelData.set(SPAWN_Y, this.spawnPos.getY());
        this.levelData.set(SPAWN_Z, this.spawnPos.getZ());
        this.levelData.set(TIME, this.totalTime);
        this.levelData.set(WORLD_TIME, this.worldTime);
        this.levelData.set(LAST_PLAYED, System.currentTimeMillis());
        this.levelData.set(LEVEL_NAME, this.name);
        this.levelData.set(VERSION, 19133);
        this.levelData.set(CLEAR_WEATHER_TIME, this.clearTime);
        this.levelData.set(RAINING, this.raining);
        this.levelData.set(RAIN_TIME, this.rainTime);
        this.levelData.set(THUNDERING, this.thundering);
        this.levelData.set(HARDCORE, this.hardcore);
        this.levelData.set(ALLOW_COMMANDS, this.commandsAllowed);
        this.levelData.set(INITIALIZED, this.initialized);
        this.levelData.set(BORDER_X, this.borderCenter.getX());
        this.levelData.set(BORDER_Z, this.borderCenter.getZ());
        this.levelData.set(BORDER_SIZE, this.borderDiameter);
        this.levelData.set(BORDER_TIME_REMAINING, this.borderTimeRemaining);
        this.levelData.set(BORDER_SAFE_ZONE, this.borderDamageThreshold);
        this.levelData.set(BORDER_DAMAGE_PER_BLOCK, this.borderDamage);
        this.levelData.set(BORDER_TARGET_SIZE, this.borderTargetDiameter);
        this.levelData.set(BORDER_WARNING_DISTANCE, (double) this.borderWarningDistance);
        this.levelData.set(BORDER_WARNING_TIME, (double) this.borderWarningTime);
        this.levelData.set(DIFFICULTY, (byte) ((LanternDifficulty) difficulty).getNumericId());
        this.levelData.set(GAME_RULES, this.gamerules);
        this.levelData.set(THUNDER_TIME, this.thunderTime);
    }

    public void loadSpongeData(DataView data) {
        this.spongeRootData = checkNotNull(data);
        this.spongeData = data.getView(SPONGE_DATA).orElse(data.createView(SPONGE_DATA));
        DataView sponge = this.spongeData;

        this.name = sponge.getString(LEVEL_NAME).orElse(this.name);
        this.dimensionId = sponge.getInt(DIMENSION_ID).orElse(this.dimensionId);
//        if(sponge.contains(DIMENSION_TYPE)) this.dimensionType = sponge.getString(DIMENSION_TYPE).get(); TODO: Pending registry
        if(sponge.contains(SPONGE_UUID_LEAST, SPONGE_UUID_MOST)) {
            this.uuid = new UUID(sponge.getLong(SPONGE_UUID_MOST).get(), sponge.getLong(SPONGE_UUID_LEAST).get());
        }
        this.enabled = sponge.getBoolean(WORLD_ENABLED).orElse(this.enabled);
        this.keepSpawnLoaded = sponge.getBoolean(KEEP_SPAWN_LOADED).orElse(this.keepSpawnLoaded);
        this.loadOnStartup = sponge.getBoolean(LOAD_ON_STARTUP).orElse(this.loadOnStartup);
        if(sponge.contains(GENERATOR_MODIFIERS)) {
//            this.modifiers = sponge.getString(GENERATOR_MODIFIERS).get(); TODO: Pending registry
        }
        this.trackedUniqueIdCount = 0;
        this.playerUniqueIdMap.clear();
        if(sponge.contains(SPONGE_PLAYER_UUID_TABLE)) {
            for(DataView child : sponge.getViewList(SPONGE_PLAYER_UUID_TABLE).get()) {
                UUID uuid = new UUID(child.getLong(SPONGE_UUID_MOST).get(), child.getLong(SPONGE_UUID_LEAST).get());
                this.playerUniqueIdMap.put(this.trackedUniqueIdCount, uuid);
                this.trackedUniqueIdCount++;
            }
        }

    }

    public void loadVanillaData(DataView data) {
        this.rootData = data;
        this.levelData = data.getView(LEVEL_DATA).orElse(data.createView(LEVEL_DATA));

        DataView level = this.levelData;

        GameRegistry reg = SpongeImpl.getRegistry();
        this.name = level.getString(LEVEL_NAME).orElse(this.name);
        this.generator = reg.getType(GeneratorType.class, level.getString(GENERATOR_NAME).orElse("DEFAULT")).orElse(GeneratorTypes.DEFAULT);
        this.seed = level.getLong(SEED).orElse(this.seed);
        this.totalTime = level.getLong(TIME).orElse(this.totalTime);
        this.worldTime = level.getLong(WORLD_TIME).orElse(this.worldTime);
        this.dimensionId = level.getInt(DIMENSION_ID).orElse(this.dimensionId);
        this.raining = level.getBoolean(RAINING).orElse(this.raining);
        this.rainTime = level.getInt(RAIN_TIME).orElse(this.rainTime);
        this.thundering = level.getBoolean(THUNDERING).orElse(this.thundering);
        this.thunderTime = level.getInt(THUNDER_TIME).orElse(this.thunderTime);
        this.clearTime = level.getInt(CLEAR_WEATHER_TIME).orElse(this.clearTime);
//        this.gamemode = null; TODO: Pending registry
        this.useMapFeatures = level.getBoolean(MAP_FEATURES).orElse(this.useMapFeatures);
        this.hardcore = level.getBoolean(HARDCORE).orElse(this.hardcore);
        this.commandsAllowed = level.getBoolean(ALLOW_COMMANDS).orElse(this.commandsAllowed);
        this.initialized = level.getBoolean(INITIALIZED).orElse(this.initialized);
//        this.difficulty = null; TODO: Pending registry
        if(level.contains(BORDER_X, BORDER_Z)) {
            this.borderCenter = new Vector3d(level.getDouble(BORDER_X).get(), 0, level.getDouble(BORDER_Z).get());
        }
        this.borderDiameter = level.getDouble(BORDER_SIZE).orElse(this.borderDiameter);
        this.borderTimeRemaining = level.getLong(BORDER_TIME_REMAINING).orElse(this.borderTimeRemaining);
        this.borderTargetDiameter = level.getDouble(BORDER_TARGET_SIZE).orElse(this.borderTargetDiameter);
        this.borderDamageThreshold = level.getDouble(BORDER_SAFE_ZONE).orElse(this.borderDamageThreshold);
        this.borderDamage = level.getDouble(BORDER_DAMAGE_PER_BLOCK).orElse(this.borderDamage);
        this.borderWarningTime = level.getInt(BORDER_WARNING_TIME).orElse(this.borderWarningTime);
        this.borderWarningDistance = level.getInt(BORDER_WARNING_DISTANCE).orElse(this.borderWarningDistance);
//        this.gamerules = new HashMap<>(); //TODO: Load rules
//        this.generatorSettings = null; TODO: Load Mojangson
    }

    public int getIndexForUniqueId(UUID uuid) {
        if (this.playerUniqueIdMap.inverse().get(uuid) == null) {
            this.playerUniqueIdMap.put(this.trackedUniqueIdCount, uuid);
            this.pendingUniqueIds.add(uuid);
            return this.trackedUniqueIdCount++;
        } else {
            return this.playerUniqueIdMap.inverse().get(uuid);
        }
    }

    public Optional<UUID> getUniqueIdForIndex(int index) {
        return Optional.ofNullable(this.playerUniqueIdMap.get(index));
    }

    public int getDimensionId() {
        return this.dimensionId;
    }

    public void setDimensionId(int id) {
        this.dimensionId = id;
    }

    public void setUUID(UUID uuid) {
        this.uuid = checkNotNull(uuid);
    }

    public void setDimensionType(DimensionType type) {
        this.dimensionType = checkNotNull(type);
    }

    public void setTotalTime(long time) {
        this.totalTime = time;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setWorldName(String name) {
        this.name = checkNotNull(name);
    }

    public DataView getSpongeRoot() {
        updateSpongeData();
        return this.spongeRootData;
    }

    public DataView getVanillaRoot() {
        updateVanillaData();
        return this.rootData;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    @Override
    public boolean loadOnStartup() {
        return this.loadOnStartup;
    }

    @Override
    public void setLoadOnStartup(boolean state) {
        this.loadOnStartup = state;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.keepSpawnLoaded;
    }

    @Override
    public void setKeepSpawnLoaded(boolean state) {
        this.keepSpawnLoaded = state;
    }

    @Override
    public String getWorldName() {
        return this.name;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Vector3i getSpawnPosition() {
        return this.spawnPos;
    }

    @Override
    public void setSpawnPosition(Vector3i position) {
        this.spawnPos = checkNotNull(position);
    }

    @Override
    public GeneratorType getGeneratorType() {
        return this.generator;
    }

    @Override
    public void setGeneratorType(GeneratorType type) {
        this.generator = checkNotNull(type);
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public long getTotalTime() {
        return this.totalTime;
    }

    @Override
    public long getWorldTime() {
        return this.worldTime;
    }

    @Override
    public void setWorldTime(long time) {
        this.worldTime = Math.max(time, 0L);
    }

    @Override
    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean state) {
        this.raining = state;
    }

    @Override
    public int getRainTime() {
        return this.rainTime;
    }

    @Override
    public void setRainTime(int time) {
        this.rainTime = time;
    }

    @Override
    public boolean isThundering() {
        return this.thundering;
    }

    @Override
    public void setThundering(boolean state) {
        this.thundering = state;
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime;
    }

    @Override
    public void setThunderTime(int time) {
        this.thunderTime = time;
    }

    @Override
    public GameMode getGameMode() {
        return this.gamemode;
    }

    @Override
    public void setGameMode(GameMode gamemode) {
        this.gamemode = checkNotNull(gamemode);
    }

    @Override
    public boolean usesMapFeatures() {
        return this.useMapFeatures;
    }

    @Override
    public void setMapFeaturesEnabled(boolean state) {
         this.useMapFeatures = state;
    }

    @Override
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Override
    public void setHardcore(boolean state) {
        this.hardcore = state;
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }

    @Override
    public void setCommandsAllowed(boolean state) {
        this.commandsAllowed = state;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = checkNotNull(difficulty);
    }

    @Override
    public Vector3d getWorldBorderCenter() {
        return this.borderCenter;
    }

    @Override
    public void setWorldBorderCenter(double x, double z) {
        this.borderCenter = new Vector3d(x, 0, z);
    }

    @Override
    public double getWorldBorderDiameter() {
        return this.borderDiameter;
    }

    @Override
    public void setWorldBorderDiameter(double diameter) {
        this.borderDiameter = diameter;
    }

    @Override
    public long getWorldBorderTimeRemaining() {
        return this.borderTimeRemaining;
    }

    @Override
    public void setWorldBorderTimeRemaining(long time) {
        this.borderTimeRemaining = time;
    }

    @Override
    public double getWorldBorderTargetDiameter() {
        return this.borderTargetDiameter;
    }

    @Override
    public void setWorldBorderTargetDiameter(double diameter) {
        this.borderTargetDiameter = diameter;
    }

    @Override
    public double getWorldBorderDamageThreshold() {
        return this.borderDamageThreshold;
    }

    @Override
    public void setWorldBorderDamageThreshold(double distance) {
        this.borderDamageThreshold = distance;
    }

    @Override
    public double getWorldBorderDamageAmount() {
        return this.borderDamage;
    }

    @Override
    public void setWorldBorderDamageAmount(double damage) {
        this.borderDamage = damage;
    }

    @Override
    public int getWorldBorderWarningTime() {
        return this.borderWarningTime;
    }

    @Override
    public void setWorldBorderWarningTime(int time) {
        this.borderWarningTime = time;
    }

    @Override
    public int getWorldBorderWarningDistance() {
        return this.borderWarningDistance;
    }

    @Override
    public void setWorldBorderWarningDistance(int distance) {
        this.borderWarningDistance = distance;
    }

    @Override
    public Optional<String> getGameRule(String gameRule) {
       return Optional.ofNullable(this.gamerules.get(checkNotNull(gameRule)));
    }

    @Override
    public Map<String, String> getGameRules() {
        return ImmutableMap.copyOf(this.gamerules);
    }

    @Override
    public void setGameRule(String gameRule, String value) {
        this.gamerules.put(checkNotNull(gameRule), checkNotNull(value));
    }

    @Override
    public DataContainer getAdditionalProperties() {
        DataContainer copy = this.spongeRootData.copy(); // TODO: Deep copy?
        copy.remove(SPONGE_DATA);
        return copy;
    }

    @Override
    public Optional<DataView> getPropertySection(DataQuery path) {
        return this.spongeRootData.getView(path);
    }

    @Override
    public void setPropertySection(DataQuery path, DataView data) {
        this.spongeRootData.set(path, data);
    }

    @Override
    public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        return this.modifiers;
    }

    @Override
    public void setGeneratorModifiers(Collection<WorldGeneratorModifier> modifiers) {
        this.modifiers = ImmutableSet.copyOf(modifiers);
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return this.generatorSettings;
    }

    @Override
    public DataContainer toContainer() {
        updateSpongeData();
        updateVanillaData();
        return this.rootData.copy(); //TODO: Deep copy?
    }
}
