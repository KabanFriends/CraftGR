package io.github.kabanfriends.craftgr.forge.config.value;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.ColorConfigOption;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ColorConfigOptionForge extends ColorConfigOption implements ForgeConfigBuildable {

    public ColorConfigOptionForge(String key, Color value) {
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

    @Override
    public void setValue(Object value) {
        if (value instanceof Integer rgb) {
            super.setValue(new Color(rgb));
        } else if (value instanceof Color color) {
            super.setValue(color);
        }
    }

    public int getClothValue() {
        return getValue().getRGB() & 0xFFFFFF;
    }

    public int getClothDefaultValue() {
        return getDefaultValue().getRGB() & 0xFFFFFF;
    }

    @Override
    public AbstractConfigListEntry getEntry(ConfigEntryBuilder builder) {
        return builder.startColorField(Component.translatable("text.craftgr.config.option." + getKey()), getClothValue())
                .setTooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .setDefaultValue(getClothDefaultValue())
                .setSaveConsumer(value -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
