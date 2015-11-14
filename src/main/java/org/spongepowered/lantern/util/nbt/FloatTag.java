package org.spongepowered.lantern.util.nbt;

/**
 * The {@code TAG_Float} tag.
 */
public final class FloatTag extends Tag<Float> {

    /**
     * The value.
     */
    private final float value;

    /**
     * Creates the tag.
     * @param value The value.
     */
    public FloatTag(float value) {
        super(TagTypes.FLOAT);
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }

}

