package org.spongepowered.lantern.world;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class LanternTeleportHelper implements TeleportHelper {

    @Override
    public Optional<Location<World>> getSafeLocation(Location<World> location) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Location<World>> getSafeLocation(Location<World> location, int height, int width) {
        return null; //TODO: Implement
    }
}
