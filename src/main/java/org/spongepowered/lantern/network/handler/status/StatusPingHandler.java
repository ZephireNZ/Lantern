package org.spongepowered.lantern.network.handler.status;

import com.flowpowered.networking.MessageHandler;
import org.spongepowered.lantern.network.LanternSession;
import org.spongepowered.lantern.network.message.status.StatusPingMessage;

public final class StatusPingHandler implements MessageHandler<LanternSession, StatusPingMessage> {

    @Override
    public void handle(LanternSession session, StatusPingMessage message) {
        session.send(message);
    }
}
