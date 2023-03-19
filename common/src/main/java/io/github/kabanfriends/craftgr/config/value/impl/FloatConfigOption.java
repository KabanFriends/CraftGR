package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;

public abstract class FloatConfigOption extends GRConfigOption<Float> {

    public FloatConfigOption(String key, float value) {
        super(key, value);
    }

    public Float deserialize(JsonPrimitive jsonValue) {
        float value = jsonValue.getAsFloat();
        if (value < 0f) {
            value = 0f;
        }
        return value;
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }
}
