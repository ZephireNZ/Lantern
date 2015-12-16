package org.spongepowered.lantern.text;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextFactory;
import org.spongepowered.api.text.TextRepresentation;
import org.spongepowered.lantern.text.json.JsonTextRepresentation;

import java.util.Locale;

public class LanternTextFactory implements TextFactory {

    @Override
    public String toPlain(Text text) {
        return null; //TODO: Implement
    }

    @Override
    public String toPlain(Text text, Locale locale) {
        return null; //TODO: Implement
    }

    @Override
    public TextRepresentation json() {
        return JsonTextRepresentation.INSTANCE;
    }

    @Override
    public TextRepresentation xml() {
        return null; //TODO: Implement
    }

    @Override
    public char getLegacyChar() {
        return 0; //TODO: Implement
    }

    @Override
    public TextRepresentation legacy(char legacyChar) {
        return null; //TODO: Implement
    }

    @Override
    public String stripLegacyCodes(String text, char code) {
        return null; //TODO: Implement
    }

    @Override
    public String replaceLegacyCodes(String text, char from, char to) {
        return null; //TODO: Implement
    }
}
