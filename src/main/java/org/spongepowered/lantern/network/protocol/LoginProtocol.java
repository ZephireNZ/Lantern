package org.spongepowered.lantern.network.protocol;

import org.spongepowered.lantern.network.codec.SetCompressionCodec;
import org.spongepowered.lantern.network.message.SetCompressionMessage;

public final class LoginProtocol extends Protocol {
    public LoginProtocol() {
        super("LOGIN", 5);
        // TODO: Implement
//        inbound(0x00, LoginStartMessage.class, LoginStartCodec.class, LoginStartHandler.class);
//        inbound(0x01, EncryptionKeyResponseMessage.class, EncryptionKeyResponseCodec.class, EncryptionKeyResponseHandler.class);
//
//        outbound(0x00, KickMessage.class, KickCodec.class);
//        outbound(0x01, EncryptionKeyRequestMessage.class, EncryptionKeyRequestCodec.class);
//        outbound(0x02, LoginSuccessMessage.class, LoginSuccessCodec.class);
        outbound(0x03, SetCompressionMessage.class, SetCompressionCodec.class);
    }
}
