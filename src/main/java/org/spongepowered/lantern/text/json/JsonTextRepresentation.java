package org.spongepowered.lantern.text.json;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentation;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;

import java.util.Locale;

public class JsonTextRepresentation implements TextRepresentation {
    public static final JsonTextRepresentation INSTANCE = new JsonTextRepresentation();

    private JsonTextRepresentation() {}

    @Override
    public String to(Text text) {
        return ""; //TODO: Implement
    }

    @Override
    public String to(Text text, Locale locale) {
        return ""; //TODO: Implement
    }

    @Override
    public Text from(String input) throws TextMessageException {
        return Texts.of(); //TODO: Implement
    }

    @Override
    public Text fromUnchecked(String input) {
        return Texts.of(); //TODO: Implement
    }
}
