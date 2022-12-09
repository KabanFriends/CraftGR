package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.TextFieldBuilder;
import net.minecraft.network.chat.Component;

public class StringConfigValue extends GRConfigValue<String> {

    public StringConfigValue(String key, String value) {
        super(key, value);
    }

    public String deserialize(JsonPrimitive jsonValue) {
        return jsonValue.getAsString();
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    public TextFieldBuilder getBuilder(ConfigEntryBuilder builder) {
        TextFieldBuilder field = builder.startTextField(Component.translatable("text.craftgr.config.option." + getKey()), getValue());
        field.setDefaultValue(getDefaultValue());
        return field;
    }
}
