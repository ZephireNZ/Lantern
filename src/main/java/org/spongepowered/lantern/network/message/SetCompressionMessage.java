package org.spongepowered.lantern.network.message;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class SetCompressionMessage implements Message {

    private final int threshold;

}

