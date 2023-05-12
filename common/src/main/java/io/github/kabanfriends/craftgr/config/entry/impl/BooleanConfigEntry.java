package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl.api.Option;

import dev.isxander.yacl.gui.controllers.TickBoxController;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
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

    public Option getOption() {
        return Option.createBuilder(Boolean.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller(TickBoxController::new)
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value))
                .build();
    }
}
