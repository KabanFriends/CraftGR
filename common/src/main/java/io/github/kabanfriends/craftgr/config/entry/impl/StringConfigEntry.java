package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.TextFieldBuilder;
import net.minecraft.network.chat.Component;

public class StringConfigEntry extends GRConfigEntry<String> {

    public StringConfigEntry(String key, String value) {
        super(key, value);
    }

    public String deserialize(JsonPrimitive jsonValue) {
        return jsonValue.getAsString();
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    public TextFieldBuilder getBuilder(ConfigEntryBuilder builder) {
        return builder.startTextField(Component.translatable("text.craftgr.config.option." + getKey()), getValue());
    }
}
