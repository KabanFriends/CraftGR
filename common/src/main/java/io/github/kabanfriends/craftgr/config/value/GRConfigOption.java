package io.github.kabanfriends.craftgr.config.value;

import com.google.gson.JsonPrimitive;

public abstract class GRConfigOption<T> {

    private String key;

    private T defaultValue;
    private T value;

    public GRConfigOption(String key, T value) {
        this.key = key;
        this.defaultValue = value;
        this.value = value;
    }

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

    public abstract T deserialize(JsonPrimitive jsonValue);

    public abstract JsonPrimitive serialize();
}
