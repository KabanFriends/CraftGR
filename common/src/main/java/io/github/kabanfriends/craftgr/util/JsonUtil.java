package io.github.kabanfriends.craftgr.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonUtil {

    @SuppressWarnings("unchecked")
    public static <T> T getValueWithDefault(JsonObject json, String key, T defaultValue, Class<T> clazz) {
        JsonElement element = json.get(key);
        if (element != null && !element.isJsonNull() && element.isJsonPrimitive()) {
            JsonPrimitive value = element.getAsJsonPrimitive();
            if (value.isNumber()) {
                Number number = value.getAsNumber();
                if (clazz == byte.class) return (T) Byte.valueOf(number.byteValue());
                if (clazz == double.class) return (T) Double.valueOf(number.doubleValue());
                if (clazz == float.class) return (T) Float.valueOf(number.floatValue());
                if (clazz == long.class) return (T) Long.valueOf(number.longValue());
                if (clazz == int.class) return (T) Integer.valueOf(number.intValue());
                if (clazz == short.class) return (T) Short.valueOf(number.shortValue());
            } else if (value.isString()) {
                return (T) value.getAsString();
            }
            return defaultValue;
        }
        return defaultValue;
    }
}
