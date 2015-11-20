package org.spongepowered.lantern.world.difficulty;

import org.spongepowered.api.world.difficulty.Difficulty;

public class LanternDifficulty implements Difficulty {

    private final int id;
    private final String name;

    public LanternDifficulty(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getNumericId() {
        return this.id;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
