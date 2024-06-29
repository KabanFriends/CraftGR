package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;

import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.config.entry.ConfigField;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;

public class BooleanConfigField extends ConfigField<Boolean> {

    public BooleanConfigField(String key, boolean value) {
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
            public Option<Boolean> getOption(ModConfig config) {
                return Option.<Boolean>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".description")))
                        .controller(TickBoxControllerBuilder::create)
                        .binding(getDefaultValue(), () -> getValue(), (value) -> apply(config, value))
                        .build();
            }
        };
    }
}
