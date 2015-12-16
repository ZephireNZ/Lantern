package org.spongepowered.lantern.network.protocol;

import org.spongepowered.lantern.network.codec.handshake.HandshakeCodec;
import org.spongepowered.lantern.network.handler.handshake.HandshakeHandler;
import org.spongepowered.lantern.network.message.handshake.HandshakeMessage;

public final class HandshakeProtocol extends Protocol {
    public HandshakeProtocol() {
        super("HANDSHAKE", 0);
        inbound(0x00, HandshakeMessage.class, HandshakeCodec.class, HandshakeHandler.class);
    }
}
