package org.spongepowered.lantern.util.nbt;

/**
 * The {@code TAG_Int} tag.
 */
final class IntTag extends Tag<Integer> {

    /**
     * The value.
     */
    private final int value;

    /**
     * Creates the tag.
     * @param value The value.
     */
    public IntTag(int value) {
        super(TagType.INT);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

}

