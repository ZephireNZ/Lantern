package org.spongepowered.lantern.io.anvil;

import static org.spongepowered.api.data.DataQuery.of;
import static org.spongepowered.lantern.data.util.DataQueries.*;
import static org.spongepowered.lantern.util.DataUtils.getByteArray;
import static org.spongepowered.lantern.util.DataUtils.getIntArray;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.lantern.Sponge;
import org.spongepowered.lantern.block.tileentity.LanternTileEntity;
import org.spongepowered.lantern.entity.LanternEntity;
import org.spongepowered.lantern.io.entity.EntityStorage;
import org.spongepowered.lantern.util.NibbleArray;
import org.spongepowered.lantern.util.nbt.NbtDataInputStream;
import org.spongepowered.lantern.util.nbt.NbtDataOutputStream;
import org.spongepowered.lantern.world.LanternChunk;
import org.spongepowered.lantern.world.LanternChunk.ChunkSection;
import org.spongepowered.lantern.io.ChunkIoService;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An implementation of the {@link ChunkIoService} which reads and writes Anvil maps,
 * an improvement on the McRegion file format.
 */
public final class AnvilChunkIoService implements ChunkIoService {

    /**
     * The size of a region - a 32x32 group of chunks.
     */
    private static final int REGION_SIZE = 32;

    /**
     * The region file cache.
     */
    private final RegionFileCache cache;

    // todo: consider the session.lock file

    public AnvilChunkIoService(File dir) {
        cache = new RegionFileCache(dir, ".mca");
    }

    /**
     * Reads a chunk from its region file.
     * @param chunk The GlowChunk to read into.
     * @return Whether the
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public boolean read(LanternChunk chunk) throws IOException {
        Vector3i pos = chunk.getPosition();
        int x = pos.getX(), z = pos.getZ();
        RegionFile region = cache.getRegionFile(x, z);
        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);
        if (!region.hasChunk(regionX, regionZ)) {
            return false;
        }

        DataInputStream in = region.getChunkDataInputStream(regionX, regionZ);

        DataView levelTag;
        try (NbtDataInputStream nbt = new NbtDataInputStream(in, false)) {
            DataContainer root = nbt.read();
            levelTag = root.getView(LEVEL).get();
        }

        // read the vertical sections
        List<DataView> sectionList = levelTag.getViewList(SECTIONS).get();
        ChunkSection[] sections = new ChunkSection[16];
        for (DataView sectionTag : sectionList) {
            int y = sectionTag.getInt(SECTION_Y).get();
            byte[] rawTypes = getByteArray(sectionTag, BLOCKS);
            NibbleArray extTypes = sectionTag.contains(ADD) ? new NibbleArray(getByteArray(sectionTag, ADD)) : null;
            NibbleArray data = new NibbleArray(getByteArray(sectionTag, DATA));
            NibbleArray blockLight = new NibbleArray(getByteArray(sectionTag, BLOCK_LIGHT));
            NibbleArray skyLight = new NibbleArray(getByteArray(sectionTag, SKY_LIGHT));

            char[] types = new char[rawTypes.length];
            for (int i = 0; i < rawTypes.length; i++) {
                types[i] = (char) (((extTypes == null ? 0 : extTypes.get(i)) << 12) | ((rawTypes[i] & 0xff) << 4) | data.get(i));
            }
            sections[y] = new ChunkSection(types, skyLight, blockLight);
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(levelTag.getBoolean(TERRAIN_POPULATED).get());

        // read biomes
        if (levelTag.contains(BIOMES)) {
            chunk.setBiomes(getByteArray(levelTag, BIOMES));
        }
        // read height map
        if (levelTag.contains(HEIGHT_MAP)) {
            chunk.setHeightMap(getIntArray(levelTag, HEIGHT_MAP));
        } else {
            chunk.automaticHeightMap();
        }

        // read entities
        //TODO: Rewrite
        if (levelTag.contains(ENTITIES)) {
            for (DataView entityTag : levelTag.getViewList(ENTITIES).get()) {
                //TODO: Use serializable?
                try {
                    // note that creating the entity is sufficient to add it to the world
                    EntityStorage.loadEntity(chunk.getWorld(), entityTag);
                } catch (Exception e) {
                    String id = entityTag.getString(ENTITY_ID).orElse("<missing>");
                    if (e.getMessage() != null && e.getMessage().startsWith("Unknown entity type to load:")) {
                        Sponge.getLogger().warn("Unknown entity in " + chunk + ": " + id);
                    } else {
                        Sponge.getLogger().warn("Error loading entity in " + chunk + ": " + id, e);
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
            Optional<TileEntity> tileEntity = chunk.getTileEntity(tx & 0xf, ty, tz & 0xf);
            if (tileEntity.isPresent()) {
                try {
                    ((LanternTileEntity) tileEntity.get()).loadNbt(tileEntityTag);
                } catch (Exception ex) {
                    String id = tileEntityTag.getString(ENTITY_ID).orElse("<missing>");
                    Sponge.getLogger().error("Error loading tile entity at " + tileEntity.get().getLocation().getBlockPosition() + ": " + id, ex);
                }
            } else {
                String id = tileEntityTag.getString(ENTITY_ID).orElse("<missing>");
                Sponge.getLogger().warn("Unknown tile entity at " + chunk.getWorld().getName() + "," + tx + "," + ty + "," + tz + ": " + id);
            }
        }

        return true;
    }

    /**
     * Writes a chunk to its region file.
     * @param chunk The {@link LanternChunk} to write from.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(LanternChunk chunk) throws IOException {
        Vector3i pos = chunk.getPosition();
        int x = pos.getX(), z = pos.getZ();
        RegionFile region = cache.getRegionFile(x, z);
        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);

        DataContainer levelTags = new MemoryDataContainer();

        // core properties
        levelTags.set(of("xPos"), x);
        levelTags.set(of("zPos"), z);
        levelTags.set(TERRAIN_POPULATED, chunk.isPopulated());
        levelTags.set(of("LastUpdate"), 0);

        // chunk sections
        List<DataView> sectionTags = new ArrayList<>();
        LanternChunk snapshot = chunk.snapshot(true, true);
        ChunkSection[] sections = snapshot.getRawSections();
        for (byte i = 0; i < sections.length; ++i) {
            ChunkSection sec = sections[i];
            if (sec == null) continue;

            DataView sectionTag = new MemoryDataContainer();
            sectionTag.set(SECTION_Y, i);

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
            sectionTag.set(DATA, data.getRawData());
            sectionTag.set(BLOCK_LIGHT, sec.blockLight.getRawData());
            sectionTag.set(SKY_LIGHT, sec.skyLight.getRawData());

            sectionTags.add(sectionTag);
        }
        levelTags.set(SECTIONS, sectionTags);

        // height map and biomes
        levelTags.set(HEIGHT_MAP, snapshot.getRawHeightmap());
        levelTags.set(BIOMES, snapshot.getRawBiomes());

        // entities
        List<DataView> entities = new ArrayList<>();
        for (LanternEntity entity : chunk.getRawEntities()) {
            if (!entity.shouldSave()) {
                continue;
            }
            try {
                DataView tag = new MemoryDataContainer();
                EntityStorage.save(entity, tag);
                entities.add(tag);
            } catch (Exception e) {
                Sponge.getLogger().warn("Error saving " + entity + " in " + chunk, e);
            }
        }
        levelTags.set(ENTITIES, entities);

        // tile entities
        List<DataView> tileEntities = new ArrayList<>();
        for (LanternTileEntity entity : chunk.getRawTileEntities()) {
            try {
                DataView tag = new MemoryDataContainer();
                entity.saveNbt(tag);
                tileEntities.add(tag);
            } catch (Exception ex) {
                Sponge.getLogger().error("Error saving tile entity at " + entity.getBlock(), ex);
            }
        }
        levelTags.set(TILE_ENTITIES, tileEntities);

        DataView levelOut = new MemoryDataContainer();
        levelOut.set(LEVEL, levelTags);

        try (NbtDataOutputStream nbt = new NbtDataOutputStream(region.getChunkDataOutputStream(regionX, regionZ), false)) {
            nbt.write(levelOut);
        }
    }

    @Override
    public void unload() throws IOException {
        cache.clear();
    }

}
