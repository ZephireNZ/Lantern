package org.spongepowered.lantern.util.nbt;

/**
 * The {@code TAG_Byte} tag.
 */
public final class ByteTag extends Tag<Byte> {

    /**
     * The value.
     */
    private final byte value;

    /**
     * Creates the tag.
     * @param value The value.
     */
    public ByteTag(byte value) {
        super(TagTypes.BYTE);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

}

