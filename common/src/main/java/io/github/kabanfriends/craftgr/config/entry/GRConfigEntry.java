package io.github.kabanfriends.craftgr.config.entry;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl.api.Option;

public abstract class GRConfigEntry<T> {

    private String key;

    private T defaultValue;
    private T value;

    public GRConfigEntry(String key, T value) {
        this.key = key;
        this.defaultValue = value;
        this.value = value;
    }

    public abstract T deserialize(JsonPrimitive jsonValue);

    public abstract JsonPrimitive serialize();

    public abstract Option getOption();

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
}
