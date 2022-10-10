package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.ColorFieldBuilder;
import net.minecraft.network.chat.Component;

public class ColorConfigEntry extends GRConfigEntry<Integer> {

    public ColorConfigEntry(String key, Integer value) {
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
        return builder.startColorField(Component.translatable("text.craftgr.config.option." + getKey()), getValue());
    }
}
