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
package org.spongepowered.lantern.io.nbt;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Vector;

/**
 * Utility methods for transforming various objects to and from NBT.
 */
public final class NbtSerialization {

    //TODO: Remove?

    private NbtSerialization() {
    }

    /**
     * Read an item stack in from an NBT tag. Returns null if no item exists.
     * @param tag The tag to read from.
     * @return The resulting ItemStack, or null.
     */
    public static ItemStack readItem(DataView tag) {
//        final Material material;
//        if (tag.isString("id")) {
//            material = ItemIds.getItem(tag.getString("id"));
//        } else if (tag.isShort("id")) {
//            material = Material.getMaterial(tag.getShort("id"));
//        } else {
//            return null;
//        }
//        final short damage = tag.isShort("Damage") ? tag.getShort("Damage") : 0;
//        final byte count = tag.isByte("Count") ? tag.getByte("Count") : 0;
//
//        if (material == null || material == Material.AIR || count == 0) {
//            return null;
//        }
//        ItemStack stack = new ItemStack(material, count, damage);
//        if (tag.isCompound("tag")) {
//            stack.setItemMeta(GlowItemFactory.instance().readNbt(material, tag.getCompound("tag")));
//        }
//        return stack;
        return null; //TODO: Implement
    }

    /**
     * Write an item stack to an NBT tag. Null stacks produce an empty tag,
     * and if slot is negative it is omitted from the result.
     * @param stack The stack to write, or null.
     * @param slot The slot, or negative to omit.
     * @return The resulting tag.
     */
    public static DataView writeItem(ItemStack stack, int slot) {
//        CompoundTag tag = new CompoundTag();
//        if (stack == null || stack.getType() == Material.AIR) {
//            return tag;
//        }
//        tag.putString("id", ItemIds.getName(stack.getType()));
//        tag.putShort("Damage", stack.getDurability());
//        tag.putByte("Count", stack.getAmount());
//        if (slot >= 0) {
//            tag.putByte("Slot", slot);
//        }
//        CompoundTag meta = GlowItemFactory.instance().writeNbt(stack.getItemMeta());
//        if (meta != null) {
//            tag.putCompound("tag", meta);
//        }
//        return tag;
        return null; //TODO: Implement
    }

    /**
     * Read a full inventory (players, chests, etc.) from a compound list.
     * @param tagList The list of CompoundTags to read from.
     * @param start The slot number to consider the inventory's start.
     * @param size The desired size of the inventory.
     * @return An array with the contents of the inventory.
     */
    public static ItemStack[] readInventory(List<DataView> tagList, int start, int size) {
//        ItemStack[] items = new ItemStack[size];
//        for (CompoundTag tag : tagList) {
//            byte slot = tag.isByte("Slot") ? tag.getByte("Slot") : 0;
//            if (slot >= start && slot < start + size) {
//                items[slot - start] = readItem(tag);
//            }
//        }
//        return items;
        return null; //TODO: Implement
    }

    /**
     * Write a full inventory (players, chests, etc.) to a compound list.
     * @param items An array with the contents of the inventory.
     * @param start The slot number to consider the inventory's start.
     * @return The list of CompoundTags.
     */
    public static List<DataView> writeInventory(ItemStack[] items, int start) {
//        List<CompoundTag> out = new ArrayList<>();
//        for (int i = 0; i < items.length; i++) {
//            ItemStack stack = items[i];
//            if (stack != null) {
//                out.add(writeItem(stack, start + i));
//            }
//        }
//        return out;
        return null; //TODO: Implement
    }

    /**
     * Attempt to resolve a world based on the contents of a compound tag.
     * @param compound The tag to read the world from.
     * @return The world, or null if none could be found.
     */
    public static World readWorld(DataView compound) {
        return null; //TODO: Implement
//        World world = null;
//        if (compound.isLong("WorldUUIDLeast") && compound.isLong("WorldUUIDMost")) {
//            long uuidLeast = compound.getLong("WorldUUIDLeast");
//            long uuidMost = compound.getLong("WorldUUIDMost");
//            world = server.getWorld(new UUID(uuidMost, uuidLeast));
//        }
//        if (world == null && compound.isString("World")) {
//            world = server.getWorld(compound.getString("World"));
//        }
//        if (world == null && compound.isInt("Dimension")) {
//            int dim = compound.getInt("Dimension");
//            for (World sWorld : server.getWorlds()) {
//                if (sWorld.getEnvironment().getId() == dim) {
//                    world = sWorld;
//                    break;
//                }
//            }
//        }
//        return world;
    }

    /**
     * Save world identifiers (UUID and dimension) to a compound tag for
     * later lookup.
     * @param world The world to identify.
     * @param compound The tag to write to.
     */
    public static void writeWorld(World world, DataView compound) {
//        UUID worldUUID = world.getUID();
//        // world UUID used by Bukkit and code above
//        compound.putLong("WorldUUIDMost", worldUUID.getMostSignificantBits());
//        compound.putLong("WorldUUIDLeast", worldUUID.getLeastSignificantBits());
//        // leave a Dimension value for possible Vanilla use
//        compound.putInt("Dimension", world.getEnvironment().getId());
        //TODO: Implement
    }

    /**
     * Read a Location from the "Pos" and "Rotation" children of a tag. If
     * "Pos" is absent or invalid, null is returned. If "Rotation" is absent
     * or invalid, it is skipped and a location without rotation is returned.
     * @param world The world of the location (see readWorld).
     * @param tag The tag to read from.
     * @return The location, or null.
     */
    public static Location<World> listTagsToLocation(World world, DataView tag) {
//        // check for position list
//        if (tag.isList("Pos", TagType.DOUBLE)) {
//            List<Double> pos = tag.getList("Pos", TagType.DOUBLE);
//            if (pos.size() == 3) {
//                Location location = new Location(world, pos.get(0), pos.get(1), pos.get(2));
//
//                // check for rotation
//                if (tag.isList("Rotation", TagType.FLOAT)) {
//                    List<Float> rot = tag.getList("Rotation", TagType.FLOAT);
//                    if (rot.size() == 2) {
//                        location.setYaw(rot.get(0));
//                        location.setPitch(rot.get(1));
//                    }
//                }
//
//                return location;
//            }
//        }
//
//        return null;
        return null; //TODO: Implement
    }

    /**
     * Write a Location to the "Pos" and "Rotation" children of a tag. Does
     * not save world information, use writeWorld instead.
     * @param loc The location to write.
     * @param tag The tag to write to.
     */
    public static void locationToListTags(Location<World> loc, DataView tag) {
//        tag.putList("Pos", TagType.DOUBLE, Arrays.asList(loc.getX(), loc.getY(), loc.getZ()));
//        tag.putList("Rotation", TagType.FLOAT, Arrays.asList(loc.getYaw(), loc.getPitch()));
        //TODO: Implement
    }

    /**
     * Create a Vector from a list of doubles. If the list is invalid, a
     * zero vector is returned.
     * @param list The list to read from.
     * @return The Vector.
     */
    public static Vector listToVector(List<Double> list) {
//        if (list.size() == 3) {
//            return new Vector(list.get(0), list.get(1), list.get(2));
//        }
//        return new Vector(0, 0, 0);
        return null; //TODO: Implement
    }

    /**
     * Create a list of doubles from a Vector.
     * @param vec The vector to write.
     * @return The list.
     */
    public static List<Double> vectorToList(Vector vec) {
//        return Arrays.asList(vec.getX(), vec.getY(), vec.getZ());
        return null; //TODO: Implement
    }

}
