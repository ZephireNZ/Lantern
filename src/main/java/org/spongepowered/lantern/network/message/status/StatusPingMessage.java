package org.spongepowered.lantern.network.message.status;

import com.flowpowered.networking.AsyncableMessage;
import lombok.Data;

@Data
public final class StatusPingMessage implements AsyncableMessage {

    private final long time;

    @Override
    public boolean isAsync() {
        return true;
    }

}
