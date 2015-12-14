package org.spongepowered.lantern.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.persistence.InvalidDataException;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.PlayerSimulator;
import org.spongepowered.api.world.TeleporterAgent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.lantern.world.storage.LanternChunkLayout;
import org.spongepowered.lantern.world.storage.LanternWorldStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class LanternWorld implements World {

    private final WorldProperties properties;
    private final LanternWorldStorage storage;
    private final ChunkManager chunkManager;

    public LanternWorld(LanternWorldStorage storage, WorldProperties properties) {
        this.storage = checkNotNull(storage);
        this.properties = checkNotNull(properties);
        this.chunkManager = new ChunkManager(this);
    }

    /**
     * Updates all the entities within this world.
     */
    public void pulse() {
        //TODO: Implement
    }

    @Override
    public Difficulty getDifficulty() {
        return properties.getDifficulty();
    }

    @Override
    public String getName() {
        return properties.getWorldName();
    }

    @Override
    public Optional<Chunk> getChunk(Vector3i position) {
        return getChunk(position.getX(), position.getY(), position.getY());
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z) {
        if (!LanternChunkLayout.instance.isValidChunk(x, y, z)) {
            return Optional.empty();
        }

        if(chunkManager.isChunkLoaded(x, z)) {
            return Optional.of(chunkManager.getChunk(x, z));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Chunk> loadChunk(Vector3i position, boolean shouldGenerate) {
        return loadChunk(position.getX(), position.getY(), position.getZ(), shouldGenerate);
    }

    @Override
    public Optional<Chunk> loadChunk(int x, int y, int z, boolean shouldGenerate) {
        if (!LanternChunkLayout.instance.isValidChunk(x, y, z)) {
            return Optional.empty();
        }
        if(chunkManager.isChunkLoaded(x, z)) return Optional.of(chunkManager.getChunk(x, z));

        chunkManager.loadChunk(x, z, shouldGenerate);
        if(chunkManager.isChunkLoaded(x, z)) return Optional.of(chunkManager.getChunk(x, z));

        return Optional.empty();
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        return false; //TODO: Implement
    }

    @Override
    public Iterable<Chunk> getLoadedChunks() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Entity> getEntity(UUID uuid) {
        return null; //TODO: Implement
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<String> getGameRule(String gameRule) {
        return null; //TODO: Implement
    }

    @Override
    public Map<String, String> getGameRules() {
        return null; //TODO: Implement
    }

    @Override
    public Dimension getDimension() {
        return null; //TODO: Implement
    }

    @Override
    public WorldGenerator getWorldGenerator() {
        return null; //TODO: Implement
    }

    @Override
    public void setWorldGenerator(WorldGenerator generator) {
        //TODO: Implement
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return false; //TODO: Implement
    }

    @Override
    public void setKeepSpawnLoaded(boolean keepLoaded) {
        //TODO: Implement
    }

    @Override
    public LanternWorldStorage getWorldStorage() {
        return this.storage;
    }

    @Override
    public Scoreboard getScoreboard() {
        return null; //TODO: Implement
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        //TODO: Implement
    }

    @Override
    public WorldCreationSettings getCreationSettings() {
        return new LanternWorldCreationSettings(properties);
    }

    @Override
    public WorldProperties getProperties() {
        return this.properties;
    }

    @Override
    public Location<World> getSpawnLocation() {
        return null; //TODO: Implement
    }

    @Override
    public void triggerExplosion(Explosion explosion) {
        //TODO: Implement
    }

    @Override
    public TeleporterAgent getTeleporterAgent() {
        return null; //TODO: Implement
    }

    @Override
    public PlayerSimulator getPlayerSimulator() {
        return null; //TODO: Implement
    }

    @Override
    public Context getContext() {
        return null; //TODO: Implement
    }

    @Override
    public void setBlock(Vector3i position, BlockState block, boolean notifyNeighbors) {
        //TODO: Implement
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block, boolean notifyNeighbors) {
        //TODO: Implement
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type, boolean notifyNeighbors) {
        //TODO: Implement
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type, boolean notifyNeighbors) {
        //TODO: Implement
    }

    @Override
    public BlockSnapshot createSnapshot(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public boolean restoreSnapshot(BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        return false; //TODO: Implement
    }

    @Override
    public boolean restoreSnapshot(Vector3i position, BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        return false; //TODO: Implement
    }

    @Override
    public boolean restoreSnapshot(int x, int y, int z, BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        return false; //TODO: Implement
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(Vector3i position, int priority, int ticks) {
        return null; //TODO: Implement
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        return null; //TODO: Implement
    }

    @Override
    public void removeScheduledUpdate(Vector3i position, ScheduledBlockUpdate update) {
        //TODO: Implement
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        //TODO: Implement
    }

    @Override
    public boolean isLoaded() {
        return false; //TODO: Implement
    }

    @Override
    public Extent getExtentView(Vector3i newMin, Vector3i newMax) {
        return null; //TODO: Implement
    }

    @Override
    public Extent getExtentView(DiscreteTransform3 transform) {
        return null; //TODO: Implement
    }

    @Override
    public Extent getRelativeExtentView() {
        return null; //TODO: Implement
    }

    @Override
    public Vector2i getBiomeMin() {
        return null; //TODO: Implement
    }

    @Override
    public Vector2i getBiomeMax() {
        return null; //TODO: Implement
    }

    @Override
    public Vector2i getBiomeSize() {
        return null; //TODO: Implement
    }

    @Override
    public boolean containsBiome(Vector2i position) {
        return false; //TODO: Implement
    }

    @Override
    public boolean containsBiome(int x, int z) {
        return false; //TODO: Implement
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return null; //TODO: Implement
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        return null; //TODO: Implement
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return null; //TODO: Implement
    }

    @Override
    public MutableBiomeArea getBiomeCopy() {
        return null; //TODO: Implement
    }

    @Override
    public MutableBiomeArea getBiomeCopy(StorageType type) {
        return null; //TODO: Implement
    }

    @Override
    public ImmutableBiomeArea getImmutableBiomeCopy() {
        return null; //TODO: Implement
    }

    @Override
    public Vector3i getBlockMin() {
        return null; //TODO: Implement
    }

    @Override
    public Vector3i getBlockMax() {
        return null; //TODO: Implement
    }

    @Override
    public Vector3i getBlockSize() {
        return null; //TODO: Implement
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return false; //TODO: Implement
    }

    @Override
    public BlockState getBlock(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public BlockType getBlockType(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return null; //TODO: Implement
    }

    @Override
    public MutableBlockVolume getBlockCopy() {
        return null; //TODO: Implement
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        return null; //TODO: Implement
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return null; //TODO: Implement
    }

    @Override
    public Collection<Entity> getEntities() {
        return null; //TODO: Implement
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3d position) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer, Vector3d position) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Entity> restoreSnapshot(EntitySnapshot snapshot, Vector3d position) {
        return null; //TODO: Implement
    }

    @Override
    public boolean spawnEntity(Entity entity, Cause cause) {
        return false; //TODO: Implement
    }

    @Override
    public UUID getUniqueId() {
        return null; //TODO: Implement
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        return null; //TODO: Implement
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Direction direction, Class<T> propertyClass) {
        return null; //TODO: Implement
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        return null; //TODO: Implement
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        return null; //TODO: Implement
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        return null; //TODO: Implement
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        return null; //TODO: Implement
    }

    @Override
    public boolean supports(int x, int y, int z, Key<?> key) {
        return false; //TODO: Implement
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return false; //TODO: Implement
    }

    @Override
    public Set<Key<?>> getKeys(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public Set<ImmutableValue<?>> getValues(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from, MergeFunction function) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        return null; //TODO: Implement
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataView container) {
        return false; //TODO: Implement
    }

    @Override
    public void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        //TODO: Implement
    }

    @Override
    public void setBiome(Vector2i position, BiomeType biome) {
        //TODO: Implement
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        //TODO: Implement
    }

    @Override
    public MutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        return null; //TODO: Implement
    }

    @Override
    public MutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return null; //TODO: Implement
    }

    @Override
    public MutableBiomeArea getRelativeBiomeView() {
        return null; //TODO: Implement
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        return null; //TODO: Implement
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<TileEntity> getTileEntity(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        return null; //TODO: Implement
    }

    @Override
    public void setBlock(Vector3i position, BlockState block) {
        //TODO: Implement
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        //TODO: Implement
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type) {
        //TODO: Implement
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type) {
        //TODO: Implement
    }

    @Override
    public MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        return null; //TODO: Implement
    }

    @Override
    public MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return null; //TODO: Implement
    }

    @Override
    public MutableBlockVolume getRelativeBlockView() {
        return null; //TODO: Implement
    }

    @Override
    public boolean containsBlock(Vector3i position) {
        return false; //TODO: Implement
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        //TODO: Implement
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        //TODO: Implement
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume) {
        //TODO: Implement
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch) {
        //TODO: Implement
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume) {
        //TODO: Implement
    }

    @Override
    public void sendTitle(Title title) {
        //TODO: Implement
    }

    @Override
    public Weather getWeather() {
        return null; //TODO: Implement
    }

    @Override
    public long getRemainingDuration() {
        return 0; //TODO: Implement
    }

    @Override
    public long getRunningDuration() {
        return 0; //TODO: Implement
    }

    @Override
    public void forecast(Weather weather) {
        //TODO: Implement
    }

    @Override
    public void forecast(Weather weather, long duration) {
        //TODO: Implement
    }

    public void setTime(long time) {
        // TODO: Implement
    }

    public void setFullTime(long time) {
        // TODO: Implement
    }

    public void setSpawnLocation(int x, int y, int z) {
        // TODO: Implement
    }

    public void setGameRule(String rule, String value) {
        // TODO: Implement
    }

    @Override
    public Collection<Direction> getFacesWithProperty(int x, int y, int z, Class<? extends Property<?, ?>> propertyClass) {
        return null; //TODO: Implement
    }

    @Override
    public void sendMessage(ChatType type, Text message) {
        //TODO: Implement
    }

    @Override
    public void sendMessages(ChatType type, Text... messages) {
        //TODO: Implement
    }

    @Override
    public void sendMessages(ChatType type, Iterable<Text> messages) {
        //TODO: Implement
    }
}
