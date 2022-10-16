package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.compat.ClothCompat;
import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.FloatFieldBuilder;
import net.minecraft.network.chat.Component;

public class FloatConfigValue extends GRConfigValue<Float> {

    public FloatConfigValue(String key, float value) {
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

    public FloatFieldBuilder getBuilder(ConfigEntryBuilder builder) {
        FloatFieldBuilder field =builder.startFloatField(Component.translatable("text.craftgr.config.option." + getKey()), getValue());
        ClothCompat.getCompat().setDefaultValue(field, getDefaultValue());
        return field;
    }
}
