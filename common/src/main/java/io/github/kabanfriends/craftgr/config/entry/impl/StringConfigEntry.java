package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.string.StringController;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
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

    public Option getOption() {
        return Option.createBuilder(String.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller(StringController::new)
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value))
                .build();
    }
}
