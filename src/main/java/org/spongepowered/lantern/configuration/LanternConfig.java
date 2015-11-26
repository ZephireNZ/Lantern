package org.spongepowered.lantern.configuration;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.util.Functional;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.util.IpSet;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import javax.annotation.Nullable;

public class LanternConfig<T extends LanternConfig.ConfigBase> {
    public enum Type {
        GLOBAL(GlobalConfig.class),
        DIMENSION(DimensionConfig.class),
        WORLD(WorldConfig.class);

        private final Class<? extends ConfigBase> type;

        Type(Class<? extends ConfigBase> type) {
            this.type = type;
        }
    }

    public static final String CONFIG_ENABLED = "config-enabled";

    // DEBUG
    public static final String DEBUG_THREAD_CONTENTION_MONITORING = "thread-contention-monitoring";
    public static final String DEBUG_DUMP_CHUNKS_ON_DEADLOCK = "dump-chunks-on-deadlock";
    public static final String DEBUG_DUMP_HEAP_ON_DEADLOCK = "dump-heap-on-deadlock";
    public static final String DEBUG_DUMP_THREADS_ON_WARN = "dump-threads-on-warn";

    // ENTITY
    public static final String ENTITY_MAX_BOUNDING_BOX_SIZE = "max-bounding-box-size";
    public static final String ENTITY_MAX_SPEED = "max-speed";
    public static final String ENTITY_COLLISION_WARN_SIZE = "collision-warn-size";
    public static final String ENTITY_COUNT_WARN_SIZE = "count-warn-size";
    public static final String ENTITY_ITEM_DESPAWN_RATE = "item-despawn-rate";
    public static final String ENTITY_ACTIVATION_RANGE_CREATURE = "creature-activation-range";
    public static final String ENTITY_ACTIVATION_RANGE_MONSTER = "monster-activation-range";
    public static final String ENTITY_ACTIVATION_RANGE_AQUATIC = "aquatic-activation-range";
    public static final String ENTITY_ACTIVATION_RANGE_AMBIENT = "ambient-activation-range";
    public static final String ENTITY_ACTIVATION_RANGE_MISC = "misc-activation-range";
    public static final String ENTITY_HUMAN_PLAYER_LIST_REMOVE_DELAY = "human-player-list-remove-delay";
    public static final String ENTITY_PAINTING_RESPAWN_DELAY = "entity-painting-respawn-delay";

    // BUNGEECORD
    public static final String BUNGEECORD_IP_FORWARDING = "ip-forwarding";

    // GENERAL
    public static final String GENERAL_DISABLE_WARNINGS = "disable-warnings";
    public static final String GENERAL_CHUNK_LOAD_OVERRIDE = "chunk-load-override";

    // LOGGING
    public static final String LOGGING_BLOCK_BREAK = "block-break";
    public static final String LOGGING_BLOCK_MODIFY = "block-modify";
    public static final String LOGGING_BLOCK_PLACE = "block-place";
    public static final String LOGGING_BLOCK_POPULATE = "block-populate";
    public static final String LOGGING_BLOCK_TRACKING = "block-tracking";
    public static final String LOGGING_CHUNK_LOAD = "chunk-load";
    public static final String LOGGING_CHUNK_UNLOAD = "chunk-unload";
    public static final String LOGGING_ENTITY_DEATH = "entity-death";
    public static final String LOGGING_ENTITY_DESPAWN = "entity-despawn";
    public static final String LOGGING_ENTITY_COLLISION_CHECKS = "entity-collision-checks";
    public static final String LOGGING_ENTITY_SPAWN = "entity-spawn";
    public static final String LOGGING_ENTITY_SPEED_REMOVAL = "entity-speed-removal";
    public static final String LOGGING_STACKTRACES = "log-stacktraces";

    // BLOCK TRACKING BLACKLIST
    public static final String BLOCK_TRACKING = "block-tracking";
    public static final String BLOCK_TRACKING_BLACKLIST = "block-blacklist";

    // MODULES
    public static final String MODULE_ENTITY_ACTIVATION_RANGE = "entity-activation-range";
    public static final String MODULE_BUNGEECORD = "bungeecord";

    // WORLD
    public static final String WORLD_INFINITE_WATER_SOURCE = "infinite-water-source";
    public static final String WORLD_FLOWING_LAVA_DECAY = "flowing-lava-decay";

    private static final String HEADER = "1.0\n"
            + "\n"
            + "# If you need help with the configuration or have any questions related to Sponge,\n"
            + "# join us at the IRC or drop by our forums and leave a post.\n"
            + "\n"
            + "# IRC: #sponge @ irc.esper.net ( http://webchat.esper.net/?channel=sponge )\n"
            + "# Forums: https://forums.spongepowered.org/\n";

    private Type type;
    private HoconConfigurationLoader loader;
    private CommentedConfigurationNode root = SimpleCommentedConfigurationNode.root(ConfigurationOptions.defaults()
            .setHeader(HEADER));
    private ObjectMapper<T>.BoundInstance configMapper;
    private T configBase;
    private String modId;
    private String configName;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public LanternConfig(Type type, Path path, String modId) {

        this.type = type;
        this.modId = modId;

        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                Files.createFile(path);
            }

            this.loader = HoconConfigurationLoader.builder().setPath(path).build();
            if (type == Type.GLOBAL) {
                this.configName = "GLOBAL";
            } else {
                this.configName = path.getParent().toString().toUpperCase();
            }

            this.configMapper = (ObjectMapper.BoundInstance) ObjectMapper.forClass(this.type.type).bindToNew();

            reload();
            save();
        } catch (Exception e) {
            SpongeImpl.getLogger().error("Failed to initialize configuration", e);
        }
    }

    public T getConfig() {
        return this.configBase;
    }

    public void save() {
        try {
            this.configMapper.serialize(this.root.getNode(this.modId));
            this.loader.save(this.root);
        } catch (IOException | ObjectMappingException e) {
            SpongeImpl.getLogger().error("Failed to save configuration", e);
        }
    }

    public void reload() {
        try {
            this.root = this.loader.load(ConfigurationOptions.defaults()
                    .setSerializers(
                            TypeSerializers.getDefaultSerializers().newChild().registerType(TypeToken.of(IpSet.class), new IpSet.IpSetSerializer()))
                    .setHeader(HEADER));
            this.configBase = this.configMapper.populate(this.root.getNode(this.modId));
        } catch (Exception e) {
            SpongeImpl.getLogger().error("Failed to load configuration", e);
        }
    }

    public CompletableFuture<CommentedConfigurationNode> updateSetting(String key, Object value) {
        return Functional.asyncFailableFuture(() -> {
            CommentedConfigurationNode upd = getSetting(key);
            upd.setValue(value);
            this.configBase = this.configMapper.populate(this.root.getNode(this.modId));
            this.loader.save(this.root);
            return upd;
        }, ForkJoinPool.commonPool());
    }

    public CommentedConfigurationNode getRootNode() {
        return this.root.getNode(this.modId);
    }

    public CommentedConfigurationNode getSetting(String key) {
        if (key.equalsIgnoreCase(LanternConfig.CONFIG_ENABLED)) {
            return getRootNode().getNode(key);
        } else if (!key.contains(".") || key.indexOf('.') == key.length() - 1) {
            return null;
        } else {
            String category = key.substring(0, key.indexOf('.'));
            String prop = key.substring(key.indexOf('.') + 1);
            return getRootNode().getNode(category).getNode(prop);
        }
    }

    public String getConfigName() {
        return this.configName;
    }

    public Type getType() {
        return this.type;
    }

    public static class GlobalConfig extends ConfigBase {

        @Setting(comment = "Configuration options related to the Sql service, including connection aliases etc")
        private SqlCategory sql = new SqlCategory();

        @Setting
        private CommandsCategory commands = new CommandsCategory();

        @Setting(value = "modules")
        private ModuleCategory modules = new ModuleCategory();

        @Setting("ip-sets")
        private Map<String, List<IpSet>> ipSets = new HashMap<>();

        @Setting(value = MODULE_BUNGEECORD)
        private BungeeCordCategory bungeeCord = new BungeeCordCategory();

        @Setting("max-height")
        private int maxHeight = 256;

        @Setting("max-tick-time")
        private int maxTickTime = 60000;

        @Setting
        private String motd = "A Minecraft Server";

        @Setting("online-mode")
        private boolean onlineMode = true;

        //TODO: Remove?
        @Setting("op-permission-level")
        private int opPermissionLevel = 4;

        @Setting("player-idle-timeout")
        private int playerIdleTimeout = 0;

        @Setting("network-compression-threshold")
        private int networkCompressionThreshold = 256;

        @Setting
        private QueryCategory query = new QueryCategory();

        @Setting
        private RconCategory rcon = new RconCategory();

        @Setting
        private ResourcepackCategory resourcepack = new ResourcepackCategory();

        @Setting("server-ip")
        private String serverIp = "";

        @Setting("server-port")
        private int serverPort = 25565;

        @Setting("white-list")
        private boolean whitelist = false;

        @Setting("allow-flight")
        private boolean allowFlight = false;

        @Setting("allow-nether")
        private boolean allowNether = true;

        @Setting("allow-end")
        private boolean allowEnd = true;

        @Setting("announce-player-achievements")
        private boolean announceAchievements = true;

        @Setting("force-gamemode")
        private boolean forceGamemode = false;

        @Setting
        private String gamemode = "SURVIVAL";

        @Setting("level-name")
        private String levelName = "world";

        @Setting("level-seed")
        private String levelSeed = "";

        @Setting("level-type")
        private String levelType = "DEFAULT";

        public BungeeCordCategory getBungeeCord() {
            return this.bungeeCord;
        }

        public SqlCategory getSql() {
            return this.sql;
        }

        public CommandsCategory getCommands() {
            return this.commands;
        }

        public ModuleCategory getModules() {
            return this.modules;
        }

        public Map<String, Predicate<InetAddress>> getIpSets() {
            return ImmutableMap.copyOf(Maps.transformValues(this.ipSets, new Function<List<IpSet>, Predicate<InetAddress>>() {
                @Nullable
                @Override
                public Predicate<InetAddress> apply(List<IpSet> input) {
                    return Predicates.and(input);
                }
            }));
        }

        public Predicate<InetAddress> getIpSet(String name) {
            return this.ipSets.containsKey(name) ? Predicates.and(this.ipSets.get(name)) : null;
        }

        public int getMaxHeight() {
            return maxHeight;
        }

        public int getMaxTickTime() {
            return maxTickTime;
        }

        public String getMotd() {
            return motd;
        }

        public boolean isOnlineMode() {
            return onlineMode;
        }

        public int getOpPermissionLevel() {
            return opPermissionLevel;
        }

        public int getPlayerIdleTimeout() {
            return playerIdleTimeout;
        }

        public int getNetworkCompressionThreshold() {
            return networkCompressionThreshold;
        }

        public QueryCategory getQuery() {
            return query;
        }

        public RconCategory getRcon() {
            return rcon;
        }

        public ResourcepackCategory getResourcepack() {
            return resourcepack;
        }

        public String getServerIp() {
            return serverIp;
        }

        public int getServerPort() {
            return serverPort;
        }

        public boolean isWhitelist() {
            return whitelist;
        }

        public boolean isAllowFlight() {
            return allowFlight;
        }

        public boolean isAllowNether() {
            return allowNether;
        }

        public boolean isAllowEnd() {
            return allowEnd;
        }

        public boolean isAnnounceAchievements() {
            return announceAchievements;
        }

        public boolean isForceGamemode() {
            return forceGamemode;
        }

        public String getGamemode() {
            return gamemode;
        }

        public String getLevelName() {
            return levelName;
        }

        public String getLevelSeed() {
            return levelSeed;
        }

        public String getLevelType() {
            return levelType;
        }
    }

    public static class DimensionConfig extends ConfigBase {

        @Setting(
                value = CONFIG_ENABLED,
                comment = "Enabling config will override Global.")
        protected boolean configEnabled = true;

        public DimensionConfig() {
            this.configEnabled = false;
        }

        public boolean isConfigEnabled() {
            return this.configEnabled;
        }

        public void setConfigEnabled(boolean configEnabled) {
            this.configEnabled = configEnabled;
        }
    }

    public static class WorldConfig extends ConfigBase {

        @Setting(
                value = CONFIG_ENABLED,
                comment = "Enabling config will override Dimension and Global.")
        protected boolean configEnabled = true;

        public WorldConfig() {
            this.configEnabled = false;
        }

        public boolean isConfigEnabled() {
            return this.configEnabled;
        }

        public void setConfigEnabled(boolean configEnabled) {
            this.configEnabled = configEnabled;
        }
    }

    public static class ConfigBase {

        @Setting(value = BLOCK_TRACKING)
        private BlockTrackingCategory blockTracking = new BlockTrackingCategory();
        @Setting
        private DebugCategory debug = new DebugCategory();
        @Setting
        private EntityCategory entity = new EntityCategory();
        @Setting(value = MODULE_ENTITY_ACTIVATION_RANGE)
        private EntityActivationRangeCategory entityActivationRange = new EntityActivationRangeCategory();
        @Setting
        private GeneralCategory general = new GeneralCategory();
        @Setting
        private LoggingCategory logging = new LoggingCategory();
        @Setting
        private WorldCategory world = new WorldCategory();
        @Setting
        private TimingsCategory timings = new TimingsCategory();

        public BlockTrackingCategory getBlockTracking() {
            return this.blockTracking;
        }

        public DebugCategory getDebug() {
            return this.debug;
        }

        public EntityCategory getEntity() {
            return this.entity;
        }

        public EntityActivationRangeCategory getEntityActivationRange() {
            return this.entityActivationRange;
        }

        public GeneralCategory getGeneral() {
            return this.general;
        }

        public LoggingCategory getLogging() {
            return this.logging;
        }

        public WorldCategory getWorld() {
            return this.world;
        }

        public TimingsCategory getTimings() {
            return this.timings;
        }
    }

    @ConfigSerializable
    public static class SqlCategory extends Category {
        @Setting(comment = "Aliases for SQL connections, in the format jdbc:protocol://[username[:password]@]host/database")
        private Map<String, String> aliases = new HashMap<>();

        public Map<String, String> getAliases() {
            return this.aliases;
        }
    }

    @ConfigSerializable
    public static class CommandsCategory extends Category {
        @Setting(comment = "A mapping from unqualified command alias to plugin id of the plugin that should handle a certain command")
        private Map<String, String> aliases = new HashMap<>();

        public Map<String, String> getAliases() {
            return this.aliases;
        }
    }

    @ConfigSerializable
    public static class DebugCategory extends Category {

        @Setting(value = DEBUG_THREAD_CONTENTION_MONITORING, comment = "Enable Java's thread contention monitoring for thread dumps")
        private boolean enableThreadContentionMonitoring = false;
        @Setting(value = DEBUG_DUMP_CHUNKS_ON_DEADLOCK, comment = "Dump chunks in the event of a deadlock")
        private boolean dumpChunksOnDeadlock = false;
        @Setting(value = DEBUG_DUMP_HEAP_ON_DEADLOCK, comment = "Dump the heap in the event of a deadlock")
        private boolean dumpHeapOnDeadlock = false;
        @Setting(value = DEBUG_DUMP_THREADS_ON_WARN, comment = "Dump the server thread on deadlock warning")
        private boolean dumpThreadsOnWarn = false;

        public boolean isEnableThreadContentionMonitoring() {
            return this.enableThreadContentionMonitoring;
        }

        public void setEnableThreadContentionMonitoring(boolean enableThreadContentionMonitoring) {
            this.enableThreadContentionMonitoring = enableThreadContentionMonitoring;
        }

        public boolean dumpChunksOnDeadlock() {
            return this.dumpChunksOnDeadlock;
        }

        public void setDumpChunksOnDeadlock(boolean dumpChunksOnDeadlock) {
            this.dumpChunksOnDeadlock = dumpChunksOnDeadlock;
        }

        public boolean dumpHeapOnDeadlock() {
            return this.dumpHeapOnDeadlock;
        }

        public void setDumpHeapOnDeadlock(boolean dumpHeapOnDeadlock) {
            this.dumpHeapOnDeadlock = dumpHeapOnDeadlock;
        }

        public boolean dumpThreadsOnWarn() {
            return this.dumpThreadsOnWarn;
        }

        public void setDumpThreadsOnWarn(boolean dumpThreadsOnWarn) {
            this.dumpThreadsOnWarn = dumpThreadsOnWarn;
        }
    }

    @ConfigSerializable
    public static class GeneralCategory extends Category {

        @Setting(value = GENERAL_DISABLE_WARNINGS, comment = "Disable warning messages to server admins")
        private boolean disableWarnings = false;
        @Setting(value = GENERAL_CHUNK_LOAD_OVERRIDE,
                comment = "Forces Chunk Loading on provide requests (speedup for mods that don't check if a chunk is loaded)")
        private boolean chunkLoadOverride = false;

        public boolean disableWarnings() {
            return this.disableWarnings;
        }

        public void setDisableWarnings(boolean disableWarnings) {
            this.disableWarnings = disableWarnings;
        }

        public boolean chunkLoadOverride() {
            return this.chunkLoadOverride;
        }

        public void setChunkLoadOverride(boolean chunkLoadOverride) {
            this.chunkLoadOverride = chunkLoadOverride;
        }
    }

    @ConfigSerializable
    public static class EntityCategory extends Category {

        @Setting(value = ENTITY_MAX_BOUNDING_BOX_SIZE, comment = "Max size of an entity's bounding box before removing it. Set to 0 to disable")
        private int maxBoundingBoxSize = 1000;
        @Setting(value = LanternConfig.ENTITY_MAX_SPEED, comment = "Square of the max speed of an entity before removing it. Set to 0 to disable")
        private int maxSpeed = 100;
        @Setting(value = ENTITY_COLLISION_WARN_SIZE,
                comment = "Number of colliding entities in one spot before logging a warning. Set to 0 to disable")
        private int maxCollisionSize = 200;
        @Setting(value = ENTITY_COUNT_WARN_SIZE,
                comment = "Number of entities in one dimension before logging a warning. Set to 0 to disable")
        private int maxCountWarnSize = 0;
        @Setting(value = ENTITY_ITEM_DESPAWN_RATE, comment = "Controls the time in ticks for when an item despawns.")
        private int itemDespawnRate = 6000;
        @Setting(value = ENTITY_HUMAN_PLAYER_LIST_REMOVE_DELAY,
                comment = "Number of ticks before the fake player entry of a human is removed from the tab list (range of 0 to 100 ticks).")
        private int humanPlayerListRemoveDelay = 10;
        @Setting(value = ENTITY_PAINTING_RESPAWN_DELAY,
                comment = "Number of ticks before a painting is respawned on clients when their art is changed")
        private int paintingRespawnDelaly = 2;

        @Setting("spawn-animals")
        private boolean spawnAnimals = true;

        @Setting("spawn-monsters")
        private boolean spawnMonsters = true;

        @Setting("spawn-npcs")
        private boolean spawnNpcs = true;

        public int getMaxBoundingBoxSize() {
            return this.maxBoundingBoxSize;
        }

        public void setMaxBoundingBoxSize(int maxBoundingBoxSize) {
            this.maxBoundingBoxSize = maxBoundingBoxSize;
        }

        public int getMaxSpeed() {
            return this.maxSpeed;
        }

        public void setMaxSpeed(int maxSpeed) {
            this.maxSpeed = maxSpeed;
        }

        public int getMaxCollisionSize() {
            return this.maxCollisionSize;
        }

        public void setMaxCollisionSize(int maxCollisionSize) {
            this.maxCollisionSize = maxCollisionSize;
        }

        public int getMaxCountWarnSize() {
            return this.maxCountWarnSize;
        }

        public void setMaxCountWarnSize(int maxCountWarnSize) {
            this.maxCountWarnSize = maxCountWarnSize;
        }

        public int getItemDespawnRate() {
            return this.itemDespawnRate;
        }

        public void setItemDespawnRate(int itemDespawnRate) {
            this.itemDespawnRate = itemDespawnRate;
        }

        public int getHumanPlayerListRemoveDelay() {
            return this.humanPlayerListRemoveDelay;
        }

        public void setHumanPlayerListRemoveDelay(int delay) {
            this.humanPlayerListRemoveDelay = Math.max(0, Math.min(delay, 100));
        }

        public int getPaintingRespawnDelaly() {
            return this.paintingRespawnDelaly;
        }

        public void setPaintingRespawnDelaly(int paintingRespawnDelaly) {
            this.paintingRespawnDelaly = Math.min(paintingRespawnDelaly, 1);
        }

        public boolean isSpawnAnimals() {
            return spawnAnimals;
        }

        public boolean isSpawnMonsters() {
            return spawnMonsters;
        }

        public boolean isSpawnNpcs() {
            return spawnNpcs;
        }
    }

    @ConfigSerializable
    public static class BungeeCordCategory extends Category {

        @Setting(value = BUNGEECORD_IP_FORWARDING,
                comment = "If enabled, allows BungeeCord to forward IP address, UUID, and Game Profile to this server")
        private boolean ipForwarding = false;

        public boolean getIpForwarding() {
            return this.ipForwarding;
        }
    }

    @ConfigSerializable
    public static class EntityActivationRangeCategory extends Category {

        @Setting(value = ENTITY_ACTIVATION_RANGE_CREATURE)
        private int creatureActivationRange = 32;
        @Setting(value = ENTITY_ACTIVATION_RANGE_MONSTER)
        private int monsterActivationRange = 32;
        @Setting(value = ENTITY_ACTIVATION_RANGE_AQUATIC)
        private int aquaticActivationRange = 32;
        @Setting(value = ENTITY_ACTIVATION_RANGE_AMBIENT)
        private int ambientActivationRange = 32;
        @Setting(value = ENTITY_ACTIVATION_RANGE_MISC)
        private int miscActivationRange = 16;

        public int getCreatureActivationRange() {
            return this.creatureActivationRange;
        }

        public void setCreatureActivationRange(int creatureActivationRange) {
            this.creatureActivationRange = creatureActivationRange;
        }

        public int getMonsterActivationRange() {
            return this.monsterActivationRange;
        }

        public void setMonsterActivationRange(int monsterActivationRange) {
            this.monsterActivationRange = monsterActivationRange;
        }

        public int getAquaticActivationRange() {
            return this.aquaticActivationRange;
        }

        public void setAquaticActivationRange(int aquaticActivationRange) {
            this.aquaticActivationRange = aquaticActivationRange;
        }

        public int getAmbientActivationRange() {
            return this.ambientActivationRange;
        }

        public void setAmbientActivationRange(int ambientActivationRange) {
            this.ambientActivationRange = ambientActivationRange;
        }

        public int getMiscActivationRange() {
            return this.miscActivationRange;
        }

        public void setMiscActivationRange(int miscActivationRange) {
            this.miscActivationRange = miscActivationRange;
        }
    }

    @ConfigSerializable
    public static class LoggingCategory extends Category {

        @Setting(value = LOGGING_BLOCK_BREAK, comment = "Log when blocks are broken")
        private boolean blockBreakLogging = false;
        @Setting(value = LOGGING_BLOCK_MODIFY, comment = "Log when blocks are modified")
        private boolean blockModifyLogging = false;
        @Setting(value = LOGGING_BLOCK_PLACE, comment = "Log when blocks are placed")
        private boolean blockPlaceLogging = false;
        @Setting(value = LOGGING_BLOCK_POPULATE, comment = "Log when blocks are populated in a chunk")
        private boolean blockPopulateLogging = false;
        @Setting(value = LOGGING_BLOCK_TRACKING, comment = "Log when blocks are placed by players and tracked")
        private boolean blockTrackLogging = false;
        @Setting(value = LOGGING_CHUNK_LOAD, comment = "Log when chunks are loaded")
        private boolean chunkLoadLogging = false;
        @Setting(value = LOGGING_CHUNK_UNLOAD, comment = "Log when chunks are unloaded")
        private boolean chunkUnloadLogging = false;
        @Setting(value = LOGGING_ENTITY_SPAWN, comment = "Log when living entities are spawned")
        private boolean entitySpawnLogging = false;
        @Setting(value = LOGGING_ENTITY_DESPAWN, comment = "Log when living entities are despawned")
        private boolean entityDespawnLogging = false;
        @Setting(value = LOGGING_ENTITY_DEATH, comment = "Log when living entities are destroyed")
        private boolean entityDeathLogging = false;
        @Setting(value = LOGGING_STACKTRACES, comment = "Add stack traces to dev logging")
        private boolean logWithStackTraces = false;
        @Setting(value = LOGGING_ENTITY_COLLISION_CHECKS, comment = "Whether to log entity collision/count checks")
        private boolean logEntityCollisionChecks = false;
        @Setting(value = LOGGING_ENTITY_SPEED_REMOVAL, comment = "Whether to log entity removals due to speed")
        private boolean logEntitySpeedRemoval = false;

        public boolean blockBreakLogging() {
            return this.blockBreakLogging;
        }

        public void setBlockBreakLogging(boolean flag) {
            this.blockBreakLogging = flag;
        }

        public boolean blockModifyLogging() {
            return this.blockModifyLogging;
        }

        public void setBlockModifyLogging(boolean flag) {
            this.blockModifyLogging = flag;
        }

        public boolean blockPlaceLogging() {
            return this.blockPlaceLogging;
        }

        public void setBlockPlaceLogging(boolean flag) {
            this.blockPlaceLogging = flag;
        }

        public boolean blockPopulateLogging() {
            return this.blockPopulateLogging;
        }

        public void setBlockPopulateLogging(boolean flag) {
            this.blockPopulateLogging = flag;
        }

        public boolean blockTrackLogging() {
            return this.blockTrackLogging;
        }

        public void setBlockTrackLogging(boolean flag) {
            this.blockTrackLogging = flag;
        }

        public boolean chunkLoadLogging() {
            return this.chunkLoadLogging;
        }

        public void setChunkLoadLogging(boolean flag) {
            this.chunkLoadLogging = flag;
        }

        public boolean chunkUnloadLogging() {
            return this.chunkUnloadLogging;
        }

        public void setChunkUnloadLogging(boolean flag) {
            this.chunkUnloadLogging = flag;
        }

        public boolean entitySpawnLogging() {
            return this.entitySpawnLogging;
        }

        public void setEntitySpawnLogging(boolean flag) {
            this.entitySpawnLogging = flag;
        }

        public boolean entityDespawnLogging() {
            return this.entityDespawnLogging;
        }

        public void setEntityDespawnLogging(boolean flag) {
            this.entityDespawnLogging = flag;
        }

        public boolean entityDeathLogging() {
            return this.entityDeathLogging;
        }

        public void setEntityDeathLogging(boolean flag) {
            this.entityDeathLogging = flag;
        }

        public boolean logWithStackTraces() {
            return this.logWithStackTraces;
        }

        public void setLogWithStackTraces(boolean flag) {
            this.logWithStackTraces = flag;
        }

        public boolean logEntityCollisionChecks() {
            return this.logEntityCollisionChecks;
        }

        public void setLogEntityCollisionChecks(boolean flag) {
            this.logEntityCollisionChecks = flag;
        }

        public boolean logEntitySpeedRemoval() {
            return this.logEntitySpeedRemoval;
        }

        public void setLogEntitySpeedRemoval(boolean flag) {
            this.logEntitySpeedRemoval = flag;
        }
    }

    @ConfigSerializable
    public static class ModuleCategory extends Category {

        @Setting(value = MODULE_BUNGEECORD)
        private boolean pluginBungeeCord = false;

        @Setting(value = MODULE_ENTITY_ACTIVATION_RANGE)
        private boolean pluginEntityActivation = true;

        @Setting("timings")
        private boolean pluginTimings = true;

        public boolean usePluginBungeeCord() {
            return this.pluginBungeeCord;
        }

        public void setPluginBungeeCord(boolean state) {
            this.pluginBungeeCord = state;
        }

        public boolean usePluginEntityActivation() {
            return this.pluginEntityActivation;
        }

        public void setPluginEntityActivation(boolean state) {
            this.pluginEntityActivation = state;
        }

        public boolean usePluginTimings() {
            return this.pluginTimings;
        }

        public void setPluginTimings(boolean state) {
            this.pluginTimings = state;
        }
    }

    @ConfigSerializable
    public static class BlockTrackingCategory extends Category {

        @Setting(value = BLOCK_TRACKING_BLACKLIST, comment = "Add block ids you wish to blacklist for player block placement tracking.")
        private List<String> blockBlacklist = new ArrayList<String>();

        public List<String> getBlockBlacklist() {
            return this.blockBlacklist;
        }
    }

    @ConfigSerializable
    public static class WorldCategory extends Category {

        @Setting(value = WORLD_INFINITE_WATER_SOURCE, comment = "Vanilla water source behavior - is infinite")
        private boolean infiniteWaterSource = false;

        @Setting(value = WORLD_FLOWING_LAVA_DECAY, comment = "Lava behaves like vanilla water when source block is removed")
        private boolean flowingLavaDecay = false;

        @Setting
        private String difficulty = "EASY";

        @Setting("enable-command-blocks")
        private boolean enableCommandBlocks = false;

        @Setting
        private boolean hardcore = false;

        @Setting("generate-structures")
        private boolean generateStructures = false;

        @Setting("generator-settings")
        private String generatorSettings = "";

        @Setting("max-world-size")
        private int maxWorldSize = 29999984;

        @Setting
        private boolean pvp = true;

        @Setting("spawn-protection")
        private int spawnProtection = 16;

        @Setting("view-distance")
        private int viewDistance = 10;

        public boolean hasInfiniteWaterSource() {
            return this.infiniteWaterSource;
        }

        public void setInfiniteWaterSource(boolean infiniteWaterSource) {
            this.infiniteWaterSource = infiniteWaterSource;
        }

        public boolean hasFlowingLavaDecay() {
            return this.flowingLavaDecay;
        }

        public void setFlowingLavaDecay(boolean flowingLavaDecay) {
            this.flowingLavaDecay = flowingLavaDecay;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public boolean isEnableCommandBlocks() {
            return enableCommandBlocks;
        }

        public boolean isHardcore() {
            return hardcore;
        }

        public boolean isGenerateStructures() {
            return generateStructures;
        }

        public String getGeneratorSettings() {
            return generatorSettings;
        }

        public int getMaxWorldSize() {
            return maxWorldSize;
        }

        public boolean isPvp() {
            return pvp;
        }

        public int getSpawnProtection() {
            return spawnProtection;
        }

        public int getViewDistance() {
            return viewDistance;
        }
    }

    @ConfigSerializable
    public static class TimingsCategory extends Category {

        @Setting
        private boolean verbose = false;

        @Setting
        private boolean enabled = true;

        @Setting("server-name-privacy")
        private boolean serverNamePrivacy = false;

        @Setting("hidden-config-entries")
        private List<String> hiddenConfigEntries = Lists.newArrayList("sponge.sql");

        @Setting("history-interval")
        private int historyInterval = 300;

        @Setting("history-length")
        private int historyLength = 3600;

        public boolean isVerbose() {
            return this.verbose;
        }

        public void setVerbose(boolean verbose) {
            this.verbose = verbose;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getHistoryInterval() {
            return this.historyInterval;
        }

        public void setHistoryInterval(int historyInterval) {
            this.historyInterval = historyInterval;
        }

        public int getHistoryLength() {
            return this.historyLength;
        }

        public void setHistoryLength(int historyLength) {
            this.historyLength = historyLength;
        }

    }

    @ConfigSerializable
    public static class QueryCategory extends Category {

        @Setting
        private boolean enabled = false;

        @Setting
        private int port = 25565;

        public boolean isEnabled() {
            return enabled;
        }

        public int getPort() {
            return port;
        }
    }

    @ConfigSerializable
    public static class RconCategory extends Category {

        @Setting
        private boolean enabled = false;

        @Setting
        private int port = 25575;

        @Setting
        private String password = "";

        public boolean isEnabled() {
            return enabled;
        }

        public int getPort() {
            return port;
        }

        public String getPassword() {
            return password;
        }
    }

    @ConfigSerializable
    public static class ResourcepackCategory extends Category {

        @Setting
        private String uri = "";

        @Setting
        private String hash = "";

        public String getUri() {
            return uri;
        }

        public String getHash() {
            return hash;
        }
    }

    @ConfigSerializable
    private static class Category {
    }
}
