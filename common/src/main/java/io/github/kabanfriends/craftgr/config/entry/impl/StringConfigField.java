package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.config.entry.ConfigField;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;

public class StringConfigField extends ConfigField<String> {

    public StringConfigField(String key, String value) {
        super(key, value);
    }

    @Override
    public String deserialize(JsonPrimitive jsonValue) {
        return jsonValue.getAsString();
    }

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public OptionProvider<String> getOptionProvider() {
        return new OptionProvider<String>() {
            @Override
            public Option<String> getOption(ModConfig config) {
                return Option.<String>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".description")))
                        .controller(StringControllerBuilder::create)
                        .binding(getDefaultValue(), () -> getValue(), (value) -> apply(config, value))
                        .build();
            }
        };
    }
}
