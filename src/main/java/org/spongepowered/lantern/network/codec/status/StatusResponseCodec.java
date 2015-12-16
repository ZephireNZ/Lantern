package org.spongepowered.lantern.network.codec.status;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.spongepowered.lantern.network.message.status.StatusResponseMessage;

import java.io.IOException;

public final class StatusResponseCodec implements Codec<StatusResponseMessage> {
    @Override
    public StatusResponseMessage decode(ByteBuf buf) throws IOException {
        String json = ByteBufUtils.readUTF8(buf);
        return new StatusResponseMessage(json);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, StatusResponseMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getJson());
        return buf;
    }
}
