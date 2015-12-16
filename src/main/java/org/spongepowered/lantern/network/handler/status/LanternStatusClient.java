package org.spongepowered.lantern.network.handler.status;

import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.network.status.StatusClient;
import org.spongepowered.lantern.network.LanternSession;

import java.net.InetSocketAddress;
import java.util.Optional;

public class LanternStatusClient implements StatusClient {

    private final InetSocketAddress address;
    private final MinecraftVersion version;
    private final Optional<InetSocketAddress> host;

    public LanternStatusClient(InetSocketAddress address, MinecraftVersion version, Optional<InetSocketAddress> host) {
        this.address = address;
        this.version = version;
        this.host = host;
    }

    public LanternStatusClient(LanternSession session) {
        this(session.getAddress(), session.getVersion().get(), session.getVirtualHost());
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public MinecraftVersion getVersion() {
        return this.version;
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return this.host;
    }
}
