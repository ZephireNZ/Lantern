package org.spongepowered.lantern.util.nbt;

/**
 * The {@code TAG_String} tag.
 */
public final class StringTag extends Tag<String> {

    /**
     * The value.
     */
    private final String value;

    /**
     * Creates the tag.
     * @param value The value.
     */
    public StringTag(String value) {
        super(TagTypes.STRING);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}

