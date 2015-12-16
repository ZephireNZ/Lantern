package org.spongepowered.lantern.network.message.status;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class StatusResponseMessage implements Message {

    private final String json;

    public StatusResponseMessage(String json) {
        this.json = json;
    }

}
