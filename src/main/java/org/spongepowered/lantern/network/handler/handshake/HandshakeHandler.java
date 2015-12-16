package org.spongepowered.lantern.network.handler.handshake;

import com.flowpowered.networking.MessageHandler;
import org.spongepowered.lantern.LanternMinecraftVersion;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.SpongeVersion;
import org.spongepowered.lantern.network.LanternSession;
import org.spongepowered.lantern.network.ProxyData;
import org.spongepowered.lantern.network.message.handshake.HandshakeMessage;
import org.spongepowered.lantern.network.protocol.ProtocolType;

import java.net.InetSocketAddress;

public class HandshakeHandler implements MessageHandler<LanternSession, HandshakeMessage> {

    @Override
    public void handle(LanternSession session, HandshakeMessage message) {
        ProtocolType protocol = ProtocolType.getById(message.getState());
        if (protocol != ProtocolType.LOGIN && protocol != ProtocolType.STATUS) {
            session.disconnect("Invalid state");
            return;
        }

        session.setVirtualHost(InetSocketAddress.createUnresolved(message.getAddress(), message.getPort()));
        session.setVersion(LanternMinecraftVersion.of(message.getVersion()));

        // Proxies modify the hostname in the HandshakeMessage to contain
        // the client's UUID and (optionally) properties
        if (session.getServer().getProxySupport()) {
            try {
                session.setProxyData(new ProxyData(session, message.getAddress()));
            } catch (IllegalArgumentException ex) {
                session.disconnect("Invalid proxy data provided.");
                // protocol is still set here and below to prevent errors
                // trying to decode packets after this one under the wrong
                // protocol, even though client is kicked
                session.setProtocol(protocol);
                return;
            } catch (Exception ex) {
                SpongeImpl.getLogger().error("Error parsing proxy data for " + session, ex);
                session.disconnect("Failed to parse proxy data.");
                session.setProtocol(protocol);
                return;
            }
        }

        session.setProtocol(protocol);

        if (protocol == ProtocolType.LOGIN) {
            int version = SpongeVersion.MINECRAFT_VERSION.getProtocol();
            if (message.getVersion() < version) {
                session.disconnect("Outdated client! I'm running " + version);
            } else if (message.getVersion() > version) {
                session.disconnect("Outdated server! I'm running " + version);
            }
        }
    }
}
