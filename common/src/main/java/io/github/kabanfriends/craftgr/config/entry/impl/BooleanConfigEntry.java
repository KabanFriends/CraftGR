package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.minecraft.network.chat.Component;

public class BooleanConfigEntry extends GRConfigEntry<Boolean> {

    public BooleanConfigEntry(String key, boolean value) {
        super(key, value);
    }

    public Boolean deserialize(JsonPrimitive jsonValue) {
        return jsonValue.getAsBoolean();
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    public BooleanToggleBuilder getBuilder(ConfigEntryBuilder builder) {
        return builder.startBooleanToggle(Component.translatable("text.craftgr.config.option." + getKey()), getValue());
    }
}
