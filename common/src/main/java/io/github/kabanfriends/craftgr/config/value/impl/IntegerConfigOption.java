package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import net.minecraft.util.Mth;

public abstract class IntegerConfigOption extends GRConfigOption<Integer> {

    protected boolean hasRange;

    protected int maxValue;
    protected int minValue;

    public IntegerConfigOption(String key, int value) {
        super(key, value);
    }

    public Integer deserialize(JsonPrimitive jsonValue) {
        int value = jsonValue.getAsInt();
        if (hasRange) {
            return Mth.clamp(value, minValue, maxValue);
        }
        return value;
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    public IntegerConfigOption setRange(int min, int max) {
        hasRange = true;
        minValue = min;
        maxValue = max;

        return this;
    }
}
