package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.FloatListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.FloatFieldBuilder;
import net.minecraft.network.chat.Component;

public class FloatConfigEntry extends GRConfigEntry<Float> {

    public FloatConfigEntry(String key, float value) {
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
        return builder.startFloatField(Component.translatable("text.craftgr.config.option." + getKey()), getValue())
                .setDefaultValue(getDefaultValue());
    }
}
