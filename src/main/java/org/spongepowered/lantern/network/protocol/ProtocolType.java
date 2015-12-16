package org.spongepowered.lantern.network.protocol;

/**
 * Enumeration of the different Minecraft protocol states.
 */
public enum ProtocolType {
    HANDSHAKE(new HandshakeProtocol()),
    STATUS(new StatusProtocol()),
    LOGIN(new LoginProtocol()),
    PLAY(new PlayProtocol());

    private final Protocol protocol;

    ProtocolType(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Get a GlowProtocol corresponding to this protocol type.
     * @return A matching GlowProtocol.
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Get the ProtocolType corresponding to the given id if possible.
     * @param id The protocol type id.
     * @return The matching ProtocolType, or null.
     */
    public static ProtocolType getById(int id) {
        final ProtocolType[] values = values();
        if (id < 0 || id >= values.length) {
            return null;
        }
        return values[id];
    }
}
