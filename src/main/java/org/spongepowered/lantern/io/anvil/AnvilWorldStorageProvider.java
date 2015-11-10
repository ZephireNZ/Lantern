package org.spongepowered.lantern.io.anvil;

import org.spongepowered.lantern.io.ChunkIoService;
import org.spongepowered.lantern.io.PlayerDataService;
import org.spongepowered.lantern.io.ScoreboardIoService;
import org.spongepowered.lantern.io.StructureDataService;
import org.spongepowered.lantern.io.WorldMetadataService;
import org.spongepowered.lantern.io.WorldStorageProvider;
import org.spongepowered.lantern.world.LanternWorld;

import java.io.File;

/**
 * A {@link WorldStorageProvider} for the Anvil map format.
 */
public class AnvilWorldStorageProvider implements WorldStorageProvider {

    private final File dir;
    private LanternWorld world;
    private AnvilChunkIoService service;
//    private NbtWorldMetadataService meta;
//    private StructureDataService structures;
//    private PlayerDataService players;
//    private ScoreboardIoService scoreboard;

    public AnvilWorldStorageProvider(File dir) {
        this.dir = dir;
    }

    @Override
    public void setWorld(LanternWorld world) {
        if (this.world != null)
            throw new IllegalArgumentException("World is already set");
        this.world = world;
        service = new AnvilChunkIoService(dir);
//        meta = new NbtWorldMetadataService(world, dir);
//        structures = new NbtStructureDataService(world, new File(dir, "data"));
    }

    @Override
    public File getFolder() {
        return dir;
    }

    @Override
    public ChunkIoService getChunkIoService() {
        return service;
    }

    @Override
    public WorldMetadataService getMetadataService() {
        return null; // TODO: Implement
//        return meta;
    }

    @Override
    public StructureDataService getStructureDataService() {
        return null; // TODO: Implement
//        return structures;
    }

    @Override
    public PlayerDataService getPlayerDataService() {
        return null; // TODO: Implement
//        if (players == null) {
//            players = new NbtPlayerDataService(world.getServer(), new File(dir, "playerdata"));
//        }
//        return players;
    }

    @Override
    public ScoreboardIoService getScoreboardIoService() {
        return null; // TODO: Implement
//        if (scoreboard == null) {
//            this.scoreboard = new NbtScoreboardIoService(world.getServer(), new File(dir, "data"));
//        }
//        return scoreboard;
    }
}
