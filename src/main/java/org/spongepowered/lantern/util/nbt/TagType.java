package org.spongepowered.lantern.util.nbt;

import java.lang.reflect.Constructor;

/**
 * The types of NBT tags that exist.
 */
public class TagType<V, T> {

    private final byte id;
    private final String name;
    private Class<V> valueClass;
    private final Class<T> tagClass;

    public TagType(byte id, String name, Class<V> valueClass, Class<T> tagClass) {
        this.id = id;
        this.name = name;
        this.valueClass = valueClass;
        this.tagClass = tagClass;
    }

    public byte getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public Class<T> getTagClass() {
        return tagClass;
    }

    public Constructor<T> getConstructor() throws NoSuchMethodException {
        return tagClass.getConstructor(valueClass);
    }
}
