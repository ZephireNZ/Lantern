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
package org.spongepowered.lantern.service.persistance;


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.spongepowered.api.data.DataQuery.of;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.translator.DataTranslator;
import org.spongepowered.lantern.util.nbt.ByteArrayTag;
import org.spongepowered.lantern.util.nbt.ByteTag;
import org.spongepowered.lantern.util.nbt.CompoundTag;
import org.spongepowered.lantern.util.nbt.DoubleTag;
import org.spongepowered.lantern.util.nbt.FloatTag;
import org.spongepowered.lantern.util.nbt.IntArrayTag;
import org.spongepowered.lantern.util.nbt.IntTag;
import org.spongepowered.lantern.util.nbt.ListTag;
import org.spongepowered.lantern.util.nbt.LongTag;
import org.spongepowered.lantern.util.nbt.ShortTag;
import org.spongepowered.lantern.util.nbt.StringTag;
import org.spongepowered.lantern.util.nbt.Tag;
import org.spongepowered.lantern.util.nbt.TagType;
import org.spongepowered.lantern.util.nbt.TagTypes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class NbtTranslator implements DataTranslator<CompoundTag> {

    private static final NbtTranslator instance = new NbtTranslator();

    public static NbtTranslator getInstance() {
        return instance;
    }

    private NbtTranslator() { } // #NOPE

    private static CompoundTag containerToCompound(final DataView container) {
        checkNotNull(container);
        CompoundTag compound = new CompoundTag();
        containerToCompound(container, compound);
        return compound;
    }

    private static void containerToCompound(final DataView container, final CompoundTag compound) {
        // We don't need to get deep values since all nested DataViews will be found
        // from the instance of checks.
        checkNotNull(container);
        checkNotNull(compound);
        for (Map.Entry<DataQuery, Object> entry : container.getValues(false).entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey().asString('.');
            if (value instanceof DataView) {
                CompoundTag inner = new CompoundTag();
                containerToCompound(container.getView(entry.getKey()).get(), inner);
                compound.putCompound(key, inner);
            } else {
                compound.put(key, getBaseFromObject(value));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Tag getBaseFromObject(Object value) {
        checkNotNull(value);
        if (value instanceof Byte) {
            return new ByteTag((Byte) value);
        } else if (value instanceof Short) {
            return new ShortTag((Short) value);
        } else if (value instanceof Integer) {
            return new IntTag((Integer) value);
        } else if (value instanceof Long) {
            return new LongTag((Long) value);
        } else if (value instanceof Float) {
            return new FloatTag((Float) value);
        } else if (value instanceof Double) {
            return new DoubleTag((Double) value);
        } else if (value instanceof String) {
            return new StringTag((String) value);
        } else if (value.getClass().isArray()) {
            if (value instanceof byte[]) {
                byte[] array = ArrayUtils.clone((byte[]) value);
                return new ByteArrayTag(array);
            } else if (value instanceof Byte[]) {
                byte[] array = new byte[((Byte[]) value).length];
                int counter = 0;
                for (Byte data : (Byte[]) value) {
                    array[counter++] = data;
                }
                return new ByteArrayTag(array);
            } else if (value instanceof int[]) {
                int[] array = ArrayUtils.clone((int[]) value);
                return new IntArrayTag(array);
            } else if (value instanceof Integer[]) {
                int[] array = new int[((Integer[]) value).length];
                int counter = 0;
                for (Integer data : (Integer[]) value) {
                    array[counter++] = data;
                }
                return new IntArrayTag(array);
            }
        } else if (value instanceof List) {
            ListTag list;
            if(((List) value).isEmpty()) {
                list = new ListTag(TagTypes.END, Collections.emptyList());
            } else {
                TagType type = getBaseFromObject(((List) value).get(0)).getType();
                list = new ListTag(type, (List) value);
            }
            return list;
        } else if (value instanceof Map) {
            CompoundTag compound = new CompoundTag();
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
                if (entry.getKey() instanceof DataQuery) {
                    compound.put(((DataQuery) entry.getKey()).asString('.'), getBaseFromObject(entry.getValue()));
                } else {
                    compound.put(entry.getKey().toString(), getBaseFromObject(entry.getValue()));
                }
            }
            return compound;
        } else if (value instanceof DataSerializable) {
            return containerToCompound(((DataSerializable) value).toContainer());
        } else if (value instanceof DataView) {
            return containerToCompound((DataView) value);
        }
        throw new IllegalArgumentException("Unable to translate object to NBTBase!");
    }

    @SuppressWarnings("unchecked")
    private static DataContainer getViewFromCompound(CompoundTag compound) {
        checkNotNull(compound);
        DataContainer container = new MemoryDataContainer();
        for (String key : compound.getValue().keySet()) {
            Tag base = compound.getTag(key, Tag.class);
            setInternal(base, base.getType(), container, key); // gotta love recursion
        }
        return container;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setInternal(Tag base, TagType type, DataView view, String key) {
        checkNotNull(base);
        checkNotNull(type);
        checkNotNull(view);
        checkNotNull(key);
        checkArgument(!key.isEmpty());
        if(type == TagTypes.END) {
            return;
        }
        if(type == TagTypes.LIST) {
            ListTag<Tag> list = (ListTag<Tag>) base;
            TagType listType = list.getChildType();
            List<Object> objectList = list.getValue().stream()
                    .map(tag -> fromTagBase(tag, listType))
                    .collect(Collectors.toList());
            view.set(of('.', key), objectList);
            return;
        }
        if(type == TagTypes.COMPOUND) {
            DataView internalView = view.createView(of('.', key));
            CompoundTag compound = (CompoundTag) base;
            for (String internalKey : compound.getValue().keySet()) {
                Tag internalBase = compound.getTag(internalKey, Tag.class);
                // Basically.... more recursion.
                // Reasoning: This avoids creating a new DataContainer which would
                // then be copied in to the owning DataView anyways. We can internally
                // set the actual data directly to the child view instead.
                setInternal(internalBase, internalBase.getType(), internalView, internalKey);
            }
            return;
        }

        view.set(of('.', key), base.getValue());
    }

    @SuppressWarnings("unchecked")
    private static Object fromTagBase(Tag base, TagType type) {
        if(type == TagTypes.END) {
            return null;
        }
        if(type == TagTypes.LIST) {
            ListTag<Tag> list = (ListTag<Tag>) base;
            TagType listType = list.getChildType();
            return list.getValue().stream()
                    .map(tag -> fromTagBase(tag, listType))
                    .collect(Collectors.toList());
        }
        if(type == TagTypes.COMPOUND) {
            return getViewFromCompound((CompoundTag) base);
        }

        return base.getValue();
    }

    @Override
    public CompoundTag translateData(DataView container) {
        return NbtTranslator.containerToCompound(container);
    }

    @Override
    public void translateContainerToData(CompoundTag node, DataView container) {
        NbtTranslator.containerToCompound(container, node);
    }

    @Override
    public DataContainer translateFrom(CompoundTag node) {
        return NbtTranslator.getViewFromCompound(node);
    }
}
