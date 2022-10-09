package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class PercentageConfigEntry extends GRConfigEntry<Integer> {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    public PercentageConfigEntry(String key, int value) {
        super(key, value);
    }

    public Integer deserialize(JsonPrimitive jsonValue) {
        return Mth.clamp(jsonValue.getAsInt(), MIN_VALUE, MAX_VALUE);
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    public AbstractFieldBuilder getBuilder(ConfigEntryBuilder builder) {
        return builder.startIntSlider(Component.translatable("text.craftgr.config.option." + getKey()), getValue(), MIN_VALUE, MAX_VALUE)
                .setDefaultValue(getDefaultValue())
                .setTextGetter(value -> Component.literal(value + "%"));
    }
}
