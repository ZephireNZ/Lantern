package org.spongepowered.lantern.network.protocol;

import org.spongepowered.lantern.network.codec.status.StatusPingCodec;
import org.spongepowered.lantern.network.codec.status.StatusRequestCodec;
import org.spongepowered.lantern.network.codec.status.StatusResponseCodec;
import org.spongepowered.lantern.network.handler.status.StatusPingHandler;
import org.spongepowered.lantern.network.handler.status.StatusRequestHandler;
import org.spongepowered.lantern.network.message.status.StatusPingMessage;
import org.spongepowered.lantern.network.message.status.StatusRequestMessage;
import org.spongepowered.lantern.network.message.status.StatusResponseMessage;

public final class StatusProtocol extends Protocol {
    public StatusProtocol() {
        super("STATUS", 2);
        inbound(0x00, StatusRequestMessage.class, StatusRequestCodec.class, StatusRequestHandler.class);
        inbound(0x01, StatusPingMessage.class, StatusPingCodec.class, StatusPingHandler.class);

        outbound(0x00, StatusResponseMessage.class, StatusResponseCodec.class);
        outbound(0x01, StatusPingMessage.class, StatusPingCodec.class);
    }
}
