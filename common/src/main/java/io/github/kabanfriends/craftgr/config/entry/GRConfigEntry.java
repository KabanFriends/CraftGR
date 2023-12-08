package io.github.kabanfriends.craftgr.config.entry;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;

public abstract class GRConfigEntry<T> {

    private final String key;

    private final T defaultValue;
    private T value;

    public GRConfigEntry(String key, T value) {
        this.key = key;
        this.defaultValue = value;
        this.value = value;
    }

    public abstract Option<T> getOption();

    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        this.value = (T) value;
    }

    public T getValue() {
        return value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }

    public T deserialize(JsonPrimitive jsonValue) {
        return null;
    }

    public JsonPrimitive serialize() {
        return null;
    }
}
