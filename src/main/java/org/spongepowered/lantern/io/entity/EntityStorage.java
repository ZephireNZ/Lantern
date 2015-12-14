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
package org.spongepowered.lantern.io.entity;

import static org.spongepowered.lantern.data.util.DataQueries.*;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.lantern.entity.LanternEntity;
import org.spongepowered.lantern.io.nbt.NbtSerialization;
import org.spongepowered.lantern.world.LanternWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * The class responsible for mapping entity types to their storage methods
 * and reading and writing entity data using those storage methods.
 */
public final class EntityStorage {

    private EntityStorage() {
    }

    /**
     * A table which maps entity ids to compound readers. This is generally used to map
     * stored entities to actual entities.
     */
    private static final Map<String, EntityStore<?>> idTable = new HashMap<>();

    /**
     * A table which maps entities to stores. This is generally used to map
     * entities being stored.
     */
    private static final Map<Class<? extends LanternEntity>, EntityStore<?>> classTable = new HashMap<>();

    /**
     * Populates the maps with stores.
     */
    static {
        //TODO: Stores
//        bind(new PlayerStore());
//
//        // LivingEntities - Passive Entities
//        bind(new BatStore());
//        bind(new AgeableStore<>(GlowChicken.class, "Chicken"));
//        bind(new HorseStore());
//        bind(new PigStore());
//        bind(new RabbitStore());
//        bind(new SheepStore());
//
//        bind(new ItemStore());
//        bind(new TNTPrimedStorage());
//        bind(new ItemFrameStore());
    }

    /**
     * Binds a store by adding entries for it to the tables.
     * @param store The store object.
     * @param <T> The type of entity.
     */
    private static <T extends LanternEntity> void bind(EntityStore<T> store) {
        idTable.put(store.getId(), store);
        classTable.put(store.getType(), store);
    }

    /**
     * Load a new entity in the given world from the given data tag.
     * @param world The target world.
     * @param compound The tag to load from.
     * @return The newly constructed entity.
     * @throws IllegalArgumentException if there is an error in the data.
     */
    public static LanternEntity loadEntity(LanternWorld world, DataView compound) {
        // look up the store by the tag's id
        if (!compound.contains(ENTITY_ID)) {
            throw new IllegalArgumentException("Entity has no type");
        }
        EntityStore<?> store = idTable.get(compound.getString(ENTITY_ID).get());
        if (store == null) {
            throw new IllegalArgumentException("Unknown entity type to load: \"" + compound.getString(ENTITY_ID) + "\"");
        }

        // verify that, if the tag contains a world, it's correct
        World checkWorld = NbtSerialization.readWorld(compound);
        if (checkWorld != null && checkWorld != world) {
            throw new IllegalArgumentException("Entity in wrong world: stored in " + world + " but data says " + checkWorld);
        }

        // find out the entity's location
        Location<World> location = NbtSerialization.listTagsToLocation(world, compound);
        if (location == null) {
            throw new IllegalArgumentException("Entity has no location");
        }

        // create the entity instance and read the rest of the data
        return createEntity(store, location, compound);
    }

    /**
     * Helper method to call EntityStore methods for type safety.
     */
    private static <T extends LanternEntity> T createEntity(EntityStore<T> store, Location<World> location, DataView compound) {
        T entity = store.createEntity(location, compound);
        store.load(entity, compound);
        return entity;
    }

    /**
     * Finds a store by entity class, throwing an exception if not found.
     */
    private static EntityStore<?> find(Class<? extends LanternEntity> clazz, String type) {
        EntityStore<?> store = classTable.get(clazz);
        if (store == null) {
            // todo: maybe try to look up a parent class's store if one isn't found
            throw new IllegalArgumentException("Unknown entity type to " + type + ": " + clazz);
        }
        return store;
    }

    /**
     * Unsafe-cast an unknown EntityStore to the base type.
     */
    @SuppressWarnings("unchecked")
    private static EntityStore<LanternEntity> getBaseStore(EntityStore<?> store) {
        return ((EntityStore<LanternEntity>) store);
    }

    /**
     * Save an entity's data to the given compound tag.
     * @param entity The entity to save.
     * @param compound The target tag.
     */
    public static void save(LanternEntity entity, DataView compound) {
        // look up the store for the entity
        EntityStore<?> store = find(entity.getClass(), "save");

        // EntityStore knows how to save world and location information
        getBaseStore(store).save(entity, compound);
    }

    /**
     * Load an entity's data from the given compound tag.
     * @param entity The target entity.
     * @param compound The tag to load from.
     */
    public static void load(LanternEntity entity, DataView compound) {
        // look up the store for the entity
        EntityStore<?> store = find(entity.getClass(), "load");

        // work out the entity's location, using its current location if unavailable
        World world = NbtSerialization.readWorld(compound);
        if (world == null) {
            world = entity.getWorld();
        }
        Location<World> location = NbtSerialization.listTagsToLocation(world, compound);
        if (location != null) {
            entity.setLocation(location);
        }

        // read the rest of the entity's information
        getBaseStore(store).load(entity, compound);
    }

}
