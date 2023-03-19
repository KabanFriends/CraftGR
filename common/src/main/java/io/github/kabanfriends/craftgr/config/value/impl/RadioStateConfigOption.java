package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;

public abstract class RadioStateConfigOption extends GRConfigOption {

    public RadioStateConfigOption(String key) {
        super(key, null);
    }

    @Override
    public Object deserialize(JsonPrimitive jsonValue) {
        return null;
    }

    @Override
    public JsonPrimitive serialize() {
        return null;
    }
}
