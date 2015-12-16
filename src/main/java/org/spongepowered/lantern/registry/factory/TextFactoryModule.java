package org.spongepowered.lantern.registry.factory;

import org.spongepowered.api.text.TextFactory;
import org.spongepowered.api.text.Texts;
import org.spongepowered.lantern.registry.FactoryRegistry;
import org.spongepowered.lantern.text.LanternTextFactory;

public class TextFactoryModule implements FactoryRegistry<TextFactory, Texts> {

    @Override
    public Class<Texts> getFactoryOwner() {
        return Texts.class;
    }

    @Override
    public TextFactory provideFactory() {
        return new LanternTextFactory();
    }
}
