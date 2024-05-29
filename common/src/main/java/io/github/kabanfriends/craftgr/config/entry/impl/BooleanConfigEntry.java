package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;

import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;

public class BooleanConfigEntry extends GRConfigEntry<Boolean> {

    public BooleanConfigEntry(String key, boolean value) {
        super(key, value);
    }

    @Override
    public Boolean deserialize(JsonPrimitive jsonValue) {
        return jsonValue.getAsBoolean();
    }

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public OptionProvider<Boolean> getOptionProvider() {
        return new OptionProvider<Boolean>() {
            @Override
            public Option<Boolean> getOption() {
                return Option.<Boolean>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".description")))
                        .controller(TickBoxControllerBuilder::create)
                        .binding(getDefaultValue(), BooleanConfigEntry.this::getValue, BooleanConfigEntry.this::apply)
                        .build();
            }
        };
    }
}
