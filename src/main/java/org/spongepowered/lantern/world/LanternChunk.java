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
package org.spongepowered.lantern.world;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.persistence.InvalidDataException;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.block.tileentity.LanternTileEntity;
import org.spongepowered.lantern.entity.LanternEntity;
import org.spongepowered.lantern.io.entity.EntityStorage;
import org.spongepowered.lantern.util.NibbleArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.spongepowered.api.data.DataQuery.of;
import static org.spongepowered.lantern.data.util.DataQueries.ENTITY_ID;
import static org.spongepowered.lantern.util.DataUtils.getByteArray;
import static org.spongepowered.lantern.util.DataUtils.getIntArray;

public class LanternChunk implements Chunk {

    /**
     * The dimensions of a chunk (width: x, height: z, depth: y).
     */
    public static final int WIDTH = 16, HEIGHT = 16, DEPTH = 256;

    /**
     * The Y depth of a single chunk section.
     */
    private static final int SEC_DEPTH = 16;

    // Chunk Data Queries
    public static final DataQuery LEVEL = of("Level");
    public static final DataQuery SECTIONS = of("Sections");
    public static final DataQuery BLOCKS = of("Blocks");
    public static final DataQuery ADD = of("Add");
    public static final DataQuery BLOCK_DATA = of("Data");
    public static final DataQuery BLOCK_LIGHT = of("BlockLight");
    public static final DataQuery SKY_LIGHT = of("SkyLight");
    public static final DataQuery TERRAIN_POPULATED = of("TerrainPopulated");
    public static final DataQuery BIOMES = of("Biomes");
    public static final DataQuery HEIGHT_MAP = of("HeightMap");
    public static final DataQuery ENTITIES = of("Entities");
    public static final DataQuery TILE_ENTITIES = of("TileEntities");

    public Collection<LanternTileEntity> getRawTileEntities() {
        return tileEntities.values();
    }

    public static final class ChunkSection {
        private static final int ARRAY_SIZE = WIDTH * HEIGHT * SEC_DEPTH;

        // these probably should be made non-public
        public final char[] types;
        public final NibbleArray skyLight;
        public final NibbleArray blockLight;
        public int count; // amount of non-air blocks

        /**
         * Create a new, empty ChunkSection.
         */
        public ChunkSection() {
            types = new char[ARRAY_SIZE];
            skyLight = new NibbleArray(ARRAY_SIZE);
            blockLight = new NibbleArray(ARRAY_SIZE);
            skyLight.fill((byte) 0xf);
        }

        /**
         * Create a ChunkSection with the specified chunk data. This
         * ChunkSection assumes ownership of the arrays passed in, and they
         * should not be further modified.
         */
        public ChunkSection(char[] types, NibbleArray skyLight, NibbleArray blockLight) {
            if (types.length != ARRAY_SIZE || skyLight.size() != ARRAY_SIZE || blockLight.size() != ARRAY_SIZE) {
                throw new IllegalArgumentException("An array length was not " + ARRAY_SIZE + ": " + types.length + " " + skyLight.size() + " " + blockLight.size());
            }
            this.types = types;
            this.skyLight = skyLight;
            this.blockLight = blockLight;
            recount();
        }

        /**
         * Calculate the index into internal arrays for the given coordinates.
         */
        public int index(int x, int y, int z) {
            if (x < 0 || z < 0 || x >= WIDTH || z >= HEIGHT) {
                throw new IndexOutOfBoundsException("Coords (x=" + x + ",z=" + z + ") out of section bounds");
            }
            return ((y & 0xf) << 8) | (z << 4) | x;
        }

        /**
         * Recount the amount of non-air blocks in the chunk section.
         */
        public void recount() {
            count = 0;
            for (char type : types) {
                if (type != 0) {
                    count++;
                }
            }
        }

        /**
         * Take a snapshot of this section which will not reflect future changes.
         */
        public ChunkSection snapshot() {
            return new ChunkSection(types.clone(), skyLight.snapshot(), blockLight.snapshot());
        }
    }

    /**
     * The world of this chunk.
     */
    private final LanternWorld world;

    /**
     * The coordinates of this chunk.
     */
    private final Vector3i position;

    /**
     * The array of chunk sections this chunk contains, or null if it is unloaded.
     */
    private ChunkSection[] sections;

    /**
     * The array of biomes this chunk contains, or null if it is unloaded.
     */
    private byte[] biomes;

    /**
     * The height map values values of each column, or null if it is unloaded.
     * The height for a column is one plus the y-index of the highest non-air
     * block in the column.
     */
    private byte[] heightMap;

    /**
     * The tile entities that reside in this chunk.
     */
    private final HashMap<Integer, LanternTileEntity> tileEntities = new HashMap<>();

    /**
     * The entities that reside in this chunk.
     */
    private final Set<LanternEntity> entities = new HashSet<>(4);

    /**
     * Whether the chunk has been populated by special features.
     * Used in map generation.
     */
    private boolean populated = false;

    /**
     * Creates a new chunk with specified coordinates.
     */
    LanternChunk(LanternWorld world, Vector3i position) {
        this.world = world;
        this.position = position;
    }

    protected LanternChunk(LanternWorld world, Vector3i position, ChunkSection[] sections, byte[] height, byte[] biomes) {
        this.world = world;
        this.position = position;

        int numSections = sections != null ? sections.length : 0;
        this.sections = new ChunkSection[numSections];
        for (int i = 0; i < numSections; ++i) {
            if (sections[i] != null) {
                this.sections[i] = sections[i].snapshot();
            }
        }

        this.heightMap = height;
        this.biomes = biomes;
    }

    public void load(DataContainer root) {
        DataView levelTag = root.getView(LEVEL).get();

        // read the vertical sections
        List<DataView> sectionList = levelTag.getViewList(SECTIONS).get();
        ChunkSection[] sections = new ChunkSection[16];
        for (DataView sectionTag : sectionList) {
            int y = sectionTag.getInt(Queries.POSITION_Y).get();
            byte[] rawTypes = getByteArray(sectionTag, BLOCKS);
            NibbleArray extTypes = sectionTag.contains(ADD) ? new NibbleArray(getByteArray(sectionTag, ADD)) : null;
            NibbleArray blockData = new NibbleArray(getByteArray(sectionTag, BLOCK_DATA));
            NibbleArray blockLight = new NibbleArray(getByteArray(sectionTag, BLOCK_LIGHT));
            NibbleArray skyLight = new NibbleArray(getByteArray(sectionTag, SKY_LIGHT));

            char[] types = new char[rawTypes.length];
            for (int i = 0; i < rawTypes.length; i++) {
                types[i] = (char) (((extTypes == null ? 0 : extTypes.get(i)) << 12) | ((rawTypes[i] & 0xff) << 4) | blockData.get(i));
            }
            sections[y] = new ChunkSection(types, skyLight, blockLight);
        }

        // initialize the chunk
        initializeSections(sections);
        setPopulated(levelTag.getBoolean(TERRAIN_POPULATED).get());

        // read biomes
        if (levelTag.contains(BIOMES)) {
            setBiomes(getByteArray(levelTag, BIOMES));
        }
        // read height map
        if (levelTag.contains(HEIGHT_MAP)) {
            setHeightMap(getIntArray(levelTag, HEIGHT_MAP));
        } else {
            automaticHeightMap();
        }

        // read entities
        //TODO: Rewrite
        if (levelTag.contains(ENTITIES)) {
            for (DataView entityTag : levelTag.getViewList(ENTITIES).get()) {
                //TODO: Use serializable?
                try {
                    // note that creating the entity is sufficient to add it to the world
                    EntityStorage.loadEntity(getWorld(), entityTag);
                } catch (Exception e) {
                    String id = entityTag.getString(ENTITY_ID).orElse("<missing>");
                    if (e.getMessage() != null && e.getMessage().startsWith("Unknown entity type to load:")) {
                        SpongeImpl.getLogger().warn("Unknown entity in " + this + ": " + id);
                    } else {
                        SpongeImpl.getLogger().warn("Error loading entity in " + this + ": " + id, e);
                    }
                }
            }
        }

        // read tile entities
        List<DataView> storedTileEntities = levelTag.getViewList(TILE_ENTITIES).get();
        for (DataView tileEntityTag : storedTileEntities) {
            int tx = tileEntityTag.getInt(of("x")).get();
            int ty = tileEntityTag.getInt(of("y")).get();
            int tz = tileEntityTag.getInt(of("z")).get();

            //TODO: Rewrite?
            Optional<TileEntity> tileEntity = getTileEntity(tx & 0xf, ty, tz & 0xf);
            if (tileEntity.isPresent()) {
                try {
                    ((LanternTileEntity) tileEntity.get()).loadNbt(tileEntityTag);
                } catch (Exception ex) {
                    String id = tileEntityTag.getString(ENTITY_ID).orElse("<missing>");
                    SpongeImpl.getLogger().error("Error loading tile entity at " + tileEntity.get().getLocation().getBlockPosition() + ": " + id, ex);
                }
            } else {
                String id = tileEntityTag.getString(ENTITY_ID).orElse("<missing>");
                SpongeImpl.getLogger().warn("Unknown tile entity at " + getWorld().getName() + "," + tx + "," + ty + "," + tz + ": " + id);
            }
        }
    }

    public void save(DataView out) {
        int x = getPosition().getX();
        int z = getPosition().getZ();
        DataContainer levelTags = new MemoryDataContainer();

        // core properties
        levelTags.set(of("xPos"), x);
        levelTags.set(of("zPos"), z);
        levelTags.set(TERRAIN_POPULATED, isPopulated());
        levelTags.set(of("LastUpdate"), 0);

        // chunk sections
        List<DataView> sectionTags = new ArrayList<>();
        ChunkSection[] sections = getRawSections();
        for (byte i = 0; i < sections.length; ++i) {
            ChunkSection sec = sections[i];
            if (sec == null) continue;

            DataView sectionTag = new MemoryDataContainer();
            sectionTag.set(Queries.POSITION_Y, i);

            byte[] rawTypes = new byte[sec.types.length];
            NibbleArray extTypes = null;
            NibbleArray data = new NibbleArray(sec.types.length);
            for (int j = 0; j < sec.types.length; j++) {
                rawTypes[j] = (byte) ((sec.types[j] >> 4) & 0xFF);
                byte extType = (byte) (sec.types[j] >> 12);
                if (extType > 0) {
                    if (extTypes == null) {
                        extTypes = new NibbleArray(sec.types.length);
                    }
                    extTypes.set(j, extType);
                }
                data.set(j, (byte) (sec.types[j] & 0xF));
            }
            sectionTag.set(BLOCKS, rawTypes);
            if (extTypes != null) {
                sectionTag.set(ADD, extTypes.getRawData());
            }
            sectionTag.set(BLOCK_DATA, data.getRawData());
            sectionTag.set(BLOCK_LIGHT, sec.blockLight.getRawData());
            sectionTag.set(SKY_LIGHT, sec.skyLight.getRawData());

            sectionTags.add(sectionTag);
        }
        levelTags.set(SECTIONS, sectionTags);

        // height map and biomes
        levelTags.set(HEIGHT_MAP, getRawHeightmap());
        levelTags.set(BIOMES, getRawBiomes());

        // entities
        List<DataView> entities = new ArrayList<>();
        for (LanternEntity entity : getRawEntities()) {
            if (!entity.shouldSave()) {
                continue;
            }
            try {
                DataView tag = new MemoryDataContainer();
                EntityStorage.save(entity, tag);
                entities.add(tag);
            } catch (Exception e) {
                SpongeImpl.getLogger().warn("Error saving " + entity + " in " + this, e);
            }
        }
        levelTags.set(ENTITIES, entities);

        // tile entities
        List<DataView> tileEntities = new ArrayList<>();
        for (LanternTileEntity entity : getRawTileEntities()) {
            try {
                DataView tag = new MemoryDataContainer();
                entity.saveNbt(tag);
                tileEntities.add(tag);
            } catch (Exception ex) {
                SpongeImpl.getLogger().error("Error saving tile entity at " + entity.getBlock(), ex);
            }
        }
        levelTags.set(TILE_ENTITIES, tileEntities);

        DataView levelOut = new MemoryDataContainer();
        levelOut.set(LEVEL, levelTags);
    }

    /**
     * Initialize this chunk from the given sections.
     * @param initSections The ChunkSections to use.
     */
    public void initializeSections(ChunkSection[] initSections) {
        if (isLoaded()) {
            SpongeImpl.getLogger().error("Tried to initialize already loaded chunk " + position.toString(), new Throwable());
            return;
        }

        sections = new ChunkSection[DEPTH / SEC_DEPTH];
        System.arraycopy(initSections, 0, sections, 0, Math.min(sections.length, initSections.length));

        biomes = new byte[WIDTH * HEIGHT];
        heightMap = new byte[WIDTH * HEIGHT];

        // tile entity initialization
        for (int i = 0; i < sections.length; ++i) {
            if (sections[i] == null) continue;
            int by = 16 * i;
            for (int cx = 0; cx < WIDTH; ++cx) {
                for (int cz = 0; cz < HEIGHT; ++cz) {
                    for (int cy = by; cy < by + 16; ++cy) {
                        createTileEntity(cx, cy, cz, getBlockType(cx, cy, cz));
                    }
                }
            }
        }
    }

    private void createTileEntity(int cx, int cy, int cz, BlockType type) {
        //TODO: Implement
    }

    @Override
    public boolean isPopulated() {
        return populated;
    }

    /**
     * A single cubic section of a chunk, with all data.
     */
    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    /**
     * Set the entire biome array of this chunk.
     * @param newBiomes The biome array.
     */
    public void setBiomes(byte[] newBiomes) {
        if (biomes == null) {
            throw new IllegalStateException("Must initialize chunk first");
        }
        if (newBiomes.length != biomes.length) {
            throw new IllegalArgumentException("Biomes array not of length " + biomes.length);
        }
        System.arraycopy(newBiomes, 0, biomes, 0, biomes.length);
    }

    /**
     * Set the entire height map of this chunk.
     * @param newHeightMap The height map.
     */
    public void setHeightMap(int[] newHeightMap) {
        if (heightMap == null) {
            throw new IllegalStateException("Must initialize chunk first");
        }
        if (newHeightMap.length != heightMap.length) {
            throw new IllegalArgumentException("Height map not of length " + heightMap.length);
        }
        for (int i = 0; i < heightMap.length; ++i) {
            heightMap[i] = (byte) newHeightMap[i];
        }
    }

    /**
     * Automatically fill the height map after chunks have been initialized.
     */
    public void automaticHeightMap() {
        // determine max Y chunk section at a time
        int sy = sections.length - 1;
        for (; sy >= 0; --sy) {
            if (sections[sy] != null) {
                break;
            }
        }
        int y = (sy + 1) * 16;
        for (int x = 0; x < WIDTH; ++x) {
            for (int z = 0; z < HEIGHT; ++z) {
                heightMap[z * WIDTH + x] = (byte) lowerHeightMap(x, y, z);
            }
        }
    }

    /**
     * Scan downwards to determine the new height map value.
     */
    private int lowerHeightMap(int x, int y, int z) {
        for (--y; y >= 0; --y) {
            if (!getBlockType(x, y, z).equals(BlockTypes.AIR)) {
                break;
            }
        }
        return y + 1;
    }

    public LanternChunk snapshot(boolean includeMaxBlockY, boolean includeBiome) {
        return new LanternChunk(this.world, this.position, this.sections,
                includeMaxBlockY ? this.heightMap : null,
                includeBiome ? this.biomes : null);
    }

    public ChunkSection[] getRawSections() {
        return sections;
    }

    public int[] getRawHeightmap() {
        int[] result = new int[heightMap.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = heightMap[i];
        }
        return result;
    }

    public byte[] getRawBiomes() {
        return biomes;
    }

    public Collection<LanternEntity> getRawEntities() {
        return entities;
    }

    @Override
    public Vector3i getPosition() {
        return this.position;
    }

    @Override
    public LanternWorld getWorld() {
        return world;
    }

    @Override
    public boolean loadChunk(boolean generate) {
        return false; //TODO: Implement
    }

    @Override
    public boolean unloadChunk() {
        return false; //TODO: Implement
    }

    @Override
    public void setBlock(Vector3i position, BlockState block, boolean notifyNeighbors) {
        //TODO: Implement
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block, boolean notifyNeighbors) {
        setBlock(new Vector3i(x, y, z), block, notifyNeighbors);
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type, boolean notifyNeighbors) {
        //TODO: Implement
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type, boolean notifyNeighbors) {
        setBlockType(new Vector3i(x, y, z), type, notifyNeighbors);
    }

    @Override
    public BlockSnapshot createSnapshot(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        return createSnapshot(new Vector3i(x, y, z));
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
        return restoreSnapshot(new Vector3i(x, y, z), snapshot, force, notifyNeighbors);
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        return getScheduledUpdates(new Vector3i(x, y, z));
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(Vector3i position, int priority, int ticks) {
        return null; //TODO: Implement
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        return addScheduledUpdate(new Vector3i(x, y, z), priority, ticks);
    }

    @Override
    public void removeScheduledUpdate(Vector3i position, ScheduledBlockUpdate update) {
        //TODO: Implement
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        removeScheduledUpdate(new Vector3i(x, y, z), update);
    }

    @Override
    public boolean isLoaded() {
        return sections != null;
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
        return containsBiome(new Vector2i(x, z));
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return null; //TODO: Implement
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        return getBiome(new Vector2i(x, z));
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
    public boolean containsBlock(Vector3i position) {
        return false; //TODO: Implement
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return containsBlock(new Vector3i(x, y, z));
    }

    @Override
    public BlockState getBlock(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return getBlock(new Vector3i(x, y, z));
    }

    @Override
    public BlockType getBlockType(Vector3i position) {
        return null; //TODO: Implement
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return getBlockType(new Vector3i(x, y, z));
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
        return Collections.unmodifiableCollection(entities);
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
        setBiome(new Vector2i(x, z), biome);
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
        return getTileEntity(new Vector3i(x, y, z));
    }

    @Override
    public void setBlock(Vector3i position, BlockState block) {
        //TODO: Implement
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        setBlock(new Vector3i(x, y, z), block);
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type) {
        //TODO: Implement
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type) {
        setBlockType(new Vector3i(x, y, z), type);
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
    public Collection<Direction> getFacesWithProperty(int x, int y, int z, Class<? extends Property<?, ?>> propertyClass) {
        return null; //TODO: Implement
    }
}
