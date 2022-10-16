package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.compat.ClothCompat;
import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.ColorFieldBuilder;
import net.minecraft.network.chat.Component;

public class ColorConfigValue extends GRConfigValue<Integer> {

    public ColorConfigValue(String key, Integer value) {
        super(key, value);
    }

    public Integer deserialize(JsonPrimitive jsonValue) {
        if (jsonValue.isString()) {
            return Integer.parseInt(jsonValue.getAsString(), 16);
        }
        return getDefaultValue();
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(Integer.toHexString(getValue()));
    }

    public ColorFieldBuilder getBuilder(ConfigEntryBuilder builder) {
        ColorFieldBuilder field = builder.startColorField(Component.translatable("text.craftgr.config.option." + getKey()), getValue());
        ClothCompat.getCompat().setDefaultValue(field, getDefaultValue());
        return field;
    }
}
