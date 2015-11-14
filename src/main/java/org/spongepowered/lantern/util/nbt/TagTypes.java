package org.spongepowered.lantern.util.nbt;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class TagTypes {

    public static final TagType<Void, EndTag> END = new TagType<>((byte) 0, "End", null, EndTag.class);
    public static final TagType<Byte, ByteTag> BYTE = new TagType<>((byte) 1, "Byte", Byte.class, ByteTag.class);
    public static final TagType<Short, ShortTag> SHORT = new TagType<>((byte) 2, "Short", Short.class, ShortTag.class);
    public static final TagType<Integer, IntTag> INT = new TagType<>((byte) 3, "Int", Integer.class, IntTag.class);
    public static final TagType<Long, LongTag> LONG = new TagType<>((byte) 4, "Long", Long.class, LongTag.class);
    public static final TagType<Float, FloatTag> FLOAT = new TagType<>((byte) 5, "Float", Float.class, FloatTag.class);
    public static final TagType<Double, DoubleTag> DOUBLE = new TagType<>((byte) 6, "Double", Double.class, DoubleTag.class);
    public static final TagType<byte[], ByteArrayTag> BYTE_ARRAY = new TagType<>((byte) 7, "Byte_Array", byte[].class, ByteArrayTag.class);
    public static final TagType<String, StringTag> STRING = new TagType<>((byte) 9, "String", String.class, StringTag.class);
    public static final TagType<List, ListTag> LIST = new TagType<>((byte) 9, "List", List.class, ListTag.class);
    public static final TagType<Map, CompoundTag> COMPOUND = new TagType<>((byte) 10, "Compound", Map.class, CompoundTag.class);
    public static final TagType<int[], IntArrayTag> INT_ARRAY = new TagType<>((byte) 11, "Int_Array", int[].class, IntArrayTag.class);

    private static final ImmutableList<TagType> typeMapping = ImmutableList.<TagType>builder()
            .add(END)
            .add(BYTE)
            .add(SHORT)
            .add(INT)
            .add(LONG)
            .add(FLOAT)
            .add(DOUBLE)
            .add(BYTE_ARRAY)
            .add(STRING)
            .add(LIST)
            .add(COMPOUND)
            .add(INT_ARRAY)
            .build();

    public static @Nullable TagType byId(int id) {
        if (id < 0 || id >= typeMapping.size()) return null;
        return typeMapping.get(id);
    }

    static TagType byIdOrError(int id) throws IOException {
        TagType ret = byId(id);
        if (ret == null) {
            throw new IOException("Invalid Tag Type");
        } else {
            return ret;
        }
    }
}
