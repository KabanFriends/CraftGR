package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.minecraft.network.chat.Component;

public class BooleanConfigValue extends GRConfigValue<Boolean> {

    public BooleanConfigValue(String key, boolean value) {
        super(key, value);
    }

    public Boolean deserialize(JsonPrimitive jsonValue) {
        return jsonValue.getAsBoolean();
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    public BooleanToggleBuilder getBuilder(ConfigEntryBuilder builder) {
        BooleanToggleBuilder field = builder.startBooleanToggle(Component.translatable("text.craftgr.config.option." + getKey()), getValue());
        field.setDefaultValue(getDefaultValue());
        return field;
    }
}
