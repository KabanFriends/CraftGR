package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;

import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
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

    public Option<Boolean> getOption() {
        return Option.<Boolean>createBuilder()
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip")))
                .controller(TickBoxControllerBuilder::create)
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value))
                .build();
    }
}
