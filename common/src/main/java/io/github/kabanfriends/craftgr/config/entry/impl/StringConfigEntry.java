package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
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

    public Option<String> getOption() {
        return Option.<String>createBuilder()
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip")))
                .controller(StringControllerBuilder::create)
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value))
                .build();
    }
}
