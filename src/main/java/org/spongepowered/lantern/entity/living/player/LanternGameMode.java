package org.spongepowered.lantern.entity.living.player;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.translation.Translation;

public class LanternGameMode implements GameMode {

    private int id;
    private String name;

    public LanternGameMode(int id, String name) {
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

    @Override
    public Translation getTranslation() {
        return null; //TODO: Implement
    }
}
