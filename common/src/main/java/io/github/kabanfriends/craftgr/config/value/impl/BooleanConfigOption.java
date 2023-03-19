package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;

public abstract class BooleanConfigOption extends GRConfigOption<Boolean> {

    public BooleanConfigOption(String key, boolean value) {
        super(key, value);
    }

    public Boolean deserialize(JsonPrimitive jsonValue) {
        return jsonValue.getAsBoolean();
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }
}
