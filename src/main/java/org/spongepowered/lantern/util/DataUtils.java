package org.spongepowered.lantern.util;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

public class DataUtils {

    public static byte[] getByteArray(DataView view, DataQuery query) {
        Object opt = view.get(query).get();
        if(opt instanceof byte[]) {
            return (byte[]) opt;
        } else if(opt instanceof Byte[]) {
            return ArrayUtils.toPrimitive((Byte[]) opt);
        }

        throw new IllegalArgumentException("Object is not a byte array: " + opt);
    }

    public static int[] getIntArray(DataView view, DataQuery query) {
        Object opt = view.get(query).get();
        if(opt instanceof int[]) {
            return (int[]) opt;
        } else if(opt instanceof Integer[]) {
            return ArrayUtils.toPrimitive((Integer[]) opt);
        }

        throw new IllegalArgumentException("Object is not a int array: " + opt);
    }

}
