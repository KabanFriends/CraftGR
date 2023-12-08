package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ColorConfigEntry extends GRConfigEntry<Color> {

    public ColorConfigEntry(String key, Color value) {
        super(key, value);
    }

    public Color deserialize(JsonPrimitive jsonValue) {
        if (jsonValue.isString()) {
            return new Color(Integer.parseInt(jsonValue.getAsString(), 16));
        }
        return getDefaultValue();
    }

    public JsonPrimitive serialize() {
        int color = getValue().getRGB() & 0xFFFFFF;
        return new JsonPrimitive(Integer.toHexString(color));
    }

    public Option<Color> getOption() {
        return Option.<Color>createBuilder()
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip")))
                .controller(ColorControllerBuilder::create)
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value))
                .build();
    }
}
