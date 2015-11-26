package org.spongepowered.lantern.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class RegistryHelper {

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping, Collection<String> ignoredFields) {
        boolean mappingSuccess = true;
        for (Field f : apiClass.getDeclaredFields()) {
            if (ignoredFields.contains(f.getName())) {
                continue;
            }
            try {
                if (!mapping.containsKey(f.getName().toLowerCase())) {
                    continue;
                }
                f.set(null, mapping.get(f.getName().toLowerCase()));
            } catch (Exception e) {
                e.printStackTrace();
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    public static boolean mapFields(Class<?> apiClass, Function<String, ?> mapFunction) {
        boolean mappingSuccess = true;
        for (Field f : apiClass.getDeclaredFields()) {
            try {
                f.set(null, mapFunction.apply(f.getName()));
            } catch (Exception e) {
                e.printStackTrace();
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping) {
        return mapFields(apiClass, mapping, Collections.<String>emptyList());
    }

    public static boolean setFactory(Class<?> apiClass, Object factory) {
        try {
            apiClass.getDeclaredField("factory").set(null, factory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    public static void setFinalStatic(Class clazz, String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiers = field.getClass().getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
