package org.spongepowered.lantern.data.util;

import static org.spongepowered.api.data.DataQuery.of;

import org.spongepowered.api.data.DataQuery;

public class DataQueries {
    public static final DataQuery LEVEL = of("Level");
    public static final DataQuery SECTIONS = of("Sections");
    public static final DataQuery SECTION_Y = of("Y");
    public static final DataQuery BLOCKS = of("Blocks");
    public static final DataQuery ADD = of("Add");
    public static final DataQuery DATA = of("Data");
    public static final DataQuery BLOCK_LIGHT = of("BlockLight");
    public static final DataQuery SKY_LIGHT = of("SkyLight");
    public static final DataQuery TERRAIN_POPULATED = of("TerrainPopulated");
    public static final DataQuery BIOMES = of("Biomes");
    public static final DataQuery HEIGHT_MAP = of("HeightMap");
    public static final DataQuery ENTITIES = of("Entities");
    public static final DataQuery ENTITY_ID = of("id");
    public static final DataQuery TILE_ENTITIES = of("TileEntities");
}
