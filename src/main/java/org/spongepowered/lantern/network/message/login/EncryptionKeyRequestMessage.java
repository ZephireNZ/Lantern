package org.spongepowered.lantern.network.message.login;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class EncryptionKeyRequestMessage implements Message {

    private final String sessionId;
    private final byte[] publicKey;
    private final byte[] verifyToken;

}
