package org.spongepowered.lantern.util.nbt;

/**
 * The {@code TAG_Long} tag.
 */
public final class LongTag extends Tag<Long> {

    /**
     * The value.
     */
    private final long value;

    /**
     * Creates the tag.
     * @param value The value.
     */
    public LongTag(long value) {
        super(TagTypes.LONG);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

}

