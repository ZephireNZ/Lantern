package org.spongepowered.lantern.util.nbt;

public class EndTag extends Tag<Void> {

    public EndTag() {
        super(TagTypes.END);
    }

    @Override
    public Void getValue() {
        return null;
    }
}
