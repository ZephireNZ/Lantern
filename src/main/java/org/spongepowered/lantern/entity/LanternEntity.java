package org.spongepowered.lantern.entity;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.entity.BreathingData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.entity.IgniteableData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvulnerabilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.PassengerData;
import org.spongepowered.api.data.manipulator.mutable.entity.SizeData;
import org.spongepowered.api.data.manipulator.mutable.entity.VelocityData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.lantern.data.LanternDataHolder;

import java.util.EnumSet;
import java.util.UUID;

public abstract class LanternEntity extends LanternDataHolder implements Entity {

    static {
        defaultManipulators.add(IgniteableData.class);
        defaultManipulators.add(VelocityData.class);
        defaultManipulators.add(DisplayNameData.class);
        defaultManipulators.add(InvisibilityData.class);
        defaultManipulators.add(FallDistanceData.class);
        defaultManipulators.add(SizeData.class);
        defaultManipulators.add(InvulnerabilityData.class);
        defaultManipulators.add(BreathingData.class);
        defaultManipulators.add(PassengerData.class);
    }

    public LanternEntity(DataView container) {
        super(container);
        //TODO: Entity data
    }

    public boolean shouldSave() {
        return false; //TODO Implement
    }

    @Override
    public World getWorld() {
        return null; //TODO: Implement
    }

    @Override
    public EntitySnapshot createSnapshot() {
        return null; //TODO: Implement
    }

    @Override
    public Location<World> getLocation() {
        return null; //TODO: Implement
    }

    @Override
    public void setLocation(Location<World> location) {
        //TODO: Implement
    }

    @Override
    public boolean setLocationSafely(Location<World> location) {
        return false; //TODO: Implement
    }

    @Override
    public Vector3d getRotation() {
        return null; //TODO: Implement
    }

    @Override
    public void setRotation(Vector3d rotation) {
        //TODO: Implement
    }

    @Override
    public void setLocationAndRotation(Location<World> location, Vector3d rotation) {
        //TODO: Implement
    }

    @Override
    public boolean setLocationAndRotationSafely(Location<World> location, Vector3d rotation) {
        return false; //TODO: Implement
    }

    @Override
    public void setLocationAndRotation(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        //TODO: Implement
    }

    @Override
    public boolean setLocationAndRotationSafely(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        return false; //TODO: Implement
    }

    @Override
    public Vector3d getScale() {
        return Vector3d.ONE;
    }

    @Override
    public void setScale(Vector3d scale) {
        // Not used currently
    }

    @Override
    public Transform<World> getTransform() {
        return null; //TODO: Implement
    }

    @Override
    public void setTransform(Transform<World> transform) {
        //TODO: Implement
    }

    @Override
    public boolean transferToWorld(String worldName, Vector3d position) {
        return false; //TODO: Implement
    }

    @Override
    public boolean transferToWorld(UUID uuid, Vector3d position) {
        return false; //TODO: Implement
    }

    @Override
    public boolean isOnGround() {
        return false; //TODO: Implement
    }

    @Override
    public boolean isRemoved() {
        return false; //TODO: Implement
    }

    @Override
    public boolean isLoaded() {
        return false; //TODO: Implement
    }

    @Override
    public void remove() {
        //TODO: Implement
    }

    @Override
    public boolean damage(double damage, Cause cause) {
        return false; //TODO: Implement
    }

    @Override
    public UUID getUniqueId() {
        return null; //TODO: Implement
    }

    //TODO

}
