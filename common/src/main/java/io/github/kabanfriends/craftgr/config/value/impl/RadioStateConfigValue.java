package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.entry.builder.RadioStateBuilder;
import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class RadioStateConfigValue extends GRConfigValue {

    public RadioStateConfigValue(String key) {
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

    @Override
    public RadioStateBuilder getBuilder(ConfigEntryBuilder builder) {
        return new RadioStateBuilder(Component.translatable("text.craftgr.config.option." + getKey()));
    }
}
