package org.spongepowered.lantern.launch;

import net.minecraft.launchwrapper.IClassTransformer;

public class AccessTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return new byte[0]; //TODO: Implement
    }
}
