package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.config.entry.ConfigField;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ColorConfigField extends ConfigField<Color> {

    public ColorConfigField(String key, Color value) {
        super(key, value);
    }

    @Override
    public Color deserialize(JsonPrimitive jsonValue) {
        if (jsonValue.isString()) {
            return new Color(Integer.parseInt(jsonValue.getAsString(), 16));
        }
        return getDefaultValue();
    }

    @Override
    public JsonPrimitive serialize() {
        int color = getValue().getRGB() & 0xFFFFFF;
        return new JsonPrimitive(Integer.toHexString(color));
    }

    @Override
    public OptionProvider<Color> getOptionProvider() {
        return new OptionProvider<Color>() {
            @Override
            public Option<Color> getOption(ModConfig config) {
                return Option.<Color>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".description")))
                        .controller(ColorControllerBuilder::create)
                        .binding(getDefaultValue(), () -> getValue(), (value) -> apply(config, value))
                        .build();
            }
        };
    }
}
