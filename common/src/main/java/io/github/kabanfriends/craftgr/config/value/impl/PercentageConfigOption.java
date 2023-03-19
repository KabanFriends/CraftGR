package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import net.minecraft.util.Mth;

public abstract class PercentageConfigOption extends GRConfigOption<Integer> {

    protected static final int MIN_VALUE = 0;
    protected static final int MAX_VALUE = 100;

    public PercentageConfigOption(String key, int value) {
        super(key, value);
    }

    public Integer deserialize(JsonPrimitive jsonValue) {
        return Mth.clamp(jsonValue.getAsInt(), MIN_VALUE, MAX_VALUE);
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }
}
