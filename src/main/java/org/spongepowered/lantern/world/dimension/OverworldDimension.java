package org.spongepowered.lantern.world.dimension;

import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;

public class OverworldDimension implements Dimension {

    private boolean playerRespawns = true;
    private boolean evaporate = false;

    @Override
    public String getName() {
        return "Overworld";
    }

    @Override
    public boolean allowsPlayerRespawns() {
        return this.playerRespawns;
    }

    @Override
    public void setAllowsPlayerRespawns(boolean allow) {
        this.playerRespawns = allow;
    }

    @Override
    public int getMinimumSpawnHeight() {
        return 0; //TODO: Implement
    }

    @Override
    public boolean doesWaterEvaporate() {
        return this.evaporate;
    }

    @Override
    public void setWaterEvaporates(boolean evaporates) {
        this.evaporate = evaporates;
    }

    @Override
    public boolean hasSky() {
        return true;
    }

    @Override
    public DimensionType getType() {
        return DimensionTypes.OVERWORLD;
    }

    @Override
    public int getHeight() {
        return 0; //TODO: Implement
    }

    @Override
    public int getBuildHeight() {
        return 0; //TODO: Implement
    }

    @Override
    public GeneratorType getGeneratorType() {
        return GeneratorTypes.OVERWORLD;
    }

    @Override
    public Context getContext() {
        return null; //TODO: Implement
    }
}
