package io.github.kabanfriends.craftgr.config.entry;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.GRConfig;

import java.util.function.Consumer;

public abstract class GRConfigEntry<T> {

    private final String key;
    private final T defaultValue;

    private T value;
    private Consumer<T> applyCallback;

    public GRConfigEntry(String key, T value) {
        this.key = key;
        this.defaultValue = value;
        this.value = value;
        this.applyCallback = null;
    }

    public abstract OptionProvider<T> getOptionProvider();

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

    public GRConfigEntry<T> onApply(Consumer<T> callback) {
        this.applyCallback = callback;
        return this;
    }

    protected void apply(T value) {
        GRConfig.setValue(this, value);
        if (applyCallback != null) {
            applyCallback.accept(value);
        }
    }
}
