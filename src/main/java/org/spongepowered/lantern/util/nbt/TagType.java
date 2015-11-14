package org.spongepowered.lantern.util.nbt;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * The types of NBT tags that exist.
 */
public enum TagType {

    END,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BYTE_ARRAY,
    STRING,
    LIST,
    COMPOUND,
    INT_ARRAY;

    TagType() {
    }

    public byte getId() {
        return (byte) ordinal();
    }

    public static @Nullable TagType byId(int id) {
        if (id < 0 || id >= values().length) return null;
        return values()[id];
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
