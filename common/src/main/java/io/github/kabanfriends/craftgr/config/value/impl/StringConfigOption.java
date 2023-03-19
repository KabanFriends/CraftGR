package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
public abstract class StringConfigOption extends GRConfigOption<String> {

    public StringConfigOption(String key, String value) {
        super(key, value);
    }

    public String deserialize(JsonPrimitive jsonValue) {
        return jsonValue.getAsString();
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }
}
