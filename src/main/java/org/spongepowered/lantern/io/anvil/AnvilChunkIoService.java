package org.spongepowered.lantern.io.anvil;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.lantern.Sponge;
import org.spongepowered.lantern.block.tileentity.LanternTileEntity;
import org.spongepowered.lantern.entity.LanternEntity;
import org.spongepowered.lantern.io.entity.EntityStorage;
import org.spongepowered.lantern.util.NibbleArray;
import org.spongepowered.lantern.util.nbt.CompoundTag;
import org.spongepowered.lantern.util.nbt.NBTInputStream;
import org.spongepowered.lantern.util.nbt.NBTOutputStream;
import org.spongepowered.lantern.util.nbt.TagType;
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

        CompoundTag levelTag;
        try (NBTInputStream nbt = new NBTInputStream(in, false)) {
            CompoundTag root = nbt.readCompound();
            levelTag = root.getCompound("Level");
        }

        // read the vertical sections
        List<CompoundTag> sectionList = levelTag.getCompoundList("Sections");
        ChunkSection[] sections = new ChunkSection[16];
        for (CompoundTag sectionTag : sectionList) {
            int y = sectionTag.getByte("Y");
            byte[] rawTypes = sectionTag.getByteArray("Blocks");
            NibbleArray extTypes = sectionTag.containsKey("Add") ? new NibbleArray(sectionTag.getByteArray("Add")) : null;
            NibbleArray data = new NibbleArray(sectionTag.getByteArray("Data"));
            NibbleArray blockLight = new NibbleArray(sectionTag.getByteArray("BlockLight"));
            NibbleArray skyLight = new NibbleArray(sectionTag.getByteArray("SkyLight"));

            char[] types = new char[rawTypes.length];
            for (int i = 0; i < rawTypes.length; i++) {
                types[i] = (char) (((extTypes == null ? 0 : extTypes.get(i)) << 12) | ((rawTypes[i] & 0xff) << 4) | data.get(i));
            }
            sections[y] = new ChunkSection(types, skyLight, blockLight);
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(levelTag.getBool("TerrainPopulated"));

        // read biomes
        if (levelTag.isByteArray("Biomes")) {
            chunk.setBiomes(levelTag.getByteArray("Biomes"));
        }
        // read height map
        if (levelTag.isIntArray("HeightMap")) {
            chunk.setHeightMap(levelTag.getIntArray("HeightMap"));
        } else {
            chunk.automaticHeightMap();
        }

        // read entities
        if (levelTag.isList("Entities", TagType.COMPOUND)) {
            for (CompoundTag entityTag : levelTag.getCompoundList("Entities")) {
                try {
                    // note that creating the entity is sufficient to add it to the world
                    EntityStorage.loadEntity(chunk.getWorld(), entityTag);
                } catch (Exception e) {
                    String id = entityTag.isString("id") ? entityTag.getString("id") : "<missing>";
                    if (e.getMessage() != null && e.getMessage().startsWith("Unknown entity type to load:")) {
                        Sponge.getLogger().warn("Unknown entity in " + chunk + ": " + id);
                    } else {
                        Sponge.getLogger().warn("Error loading entity in " + chunk + ": " + id, e);
                    }
                }
            }
        }

        // read tile entities
        List<CompoundTag> storedTileEntities = levelTag.getCompoundList("TileEntities");
        for (CompoundTag tileEntityTag : storedTileEntities) {
            int tx = tileEntityTag.getInt("x");
            int ty = tileEntityTag.getInt("y");
            int tz = tileEntityTag.getInt("z");
            Optional<TileEntity> tileEntity = chunk.getTileEntity(tx & 0xf, ty, tz & 0xf);
            if (tileEntity.isPresent()) {
                try {
                    ((LanternTileEntity) tileEntity.get()).loadNbt(tileEntityTag);
                } catch (Exception ex) {
                    String id = tileEntityTag.isString("id") ? tileEntityTag.getString("id") : "<missing>";
                    Sponge.getLogger().error("Error loading tile entity at " + tileEntity.get().getLocation().getBlockPosition() + ": " + id, ex);
                }
            } else {
                String id = tileEntityTag.isString("id") ? tileEntityTag.getString("id") : "<missing>";
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

        CompoundTag levelTags = new CompoundTag();

        // core properties
        levelTags.putInt("xPos", x);
        levelTags.putInt("zPos", z);
        levelTags.putBool("TerrainPopulated", chunk.isPopulated());
        levelTags.putLong("LastUpdate", 0);

        // chunk sections
        List<CompoundTag> sectionTags = new ArrayList<>();
        LanternChunk snapshot = chunk.snapshot(true, true);
        ChunkSection[] sections = snapshot.getRawSections();
        for (byte i = 0; i < sections.length; ++i) {
            ChunkSection sec = sections[i];
            if (sec == null) continue;

            CompoundTag sectionTag = new CompoundTag();
            sectionTag.putByte("Y", i);

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
            sectionTag.putByteArray("Blocks", rawTypes);
            if (extTypes != null) {
                sectionTag.putByteArray("Add", extTypes.getRawData());
            }
            sectionTag.putByteArray("Data", data.getRawData());
            sectionTag.putByteArray("BlockLight", sec.blockLight.getRawData());
            sectionTag.putByteArray("SkyLight", sec.skyLight.getRawData());

            sectionTags.add(sectionTag);
        }
        levelTags.putCompoundList("Sections", sectionTags);

        // height map and biomes
        levelTags.putIntArray("HeightMap", snapshot.getRawHeightmap());
        levelTags.putByteArray("Biomes", snapshot.getRawBiomes());

        // entities
        List<CompoundTag> entities = new ArrayList<>();
        for (LanternEntity entity : chunk.getRawEntities()) {
            if (!entity.shouldSave()) {
                continue;
            }
            try {
                CompoundTag tag = new CompoundTag();
                EntityStorage.save(entity, tag);
                entities.add(tag);
            } catch (Exception e) {
                Sponge.getLogger().warn("Error saving " + entity + " in " + chunk, e);
            }
        }
        levelTags.putCompoundList("Entities", entities);

        // tile entities
        List<CompoundTag> tileEntities = new ArrayList<>();
        for (LanternTileEntity entity : chunk.getRawTileEntities()) {
            try {
                CompoundTag tag = new CompoundTag();
                entity.saveNbt(tag);
                tileEntities.add(tag);
            } catch (Exception ex) {
                Sponge.getLogger().error("Error saving tile entity at " + entity.getBlock(), ex);
            }
        }
        levelTags.putCompoundList("TileEntities", tileEntities);

        CompoundTag levelOut = new CompoundTag();
        levelOut.putCompound("Level", levelTags);

        try (NBTOutputStream nbt = new NBTOutputStream(region.getChunkDataOutputStream(regionX, regionZ), false)) {
            nbt.writeTag(levelOut);
        }
    }

    @Override
    public void unload() throws IOException {
        cache.clear();
    }

}
