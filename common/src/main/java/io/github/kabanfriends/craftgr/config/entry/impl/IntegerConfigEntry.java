package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class IntegerConfigEntry extends GRConfigEntry<Integer> {

    private boolean hasRange;

    private int maxValue;
    private int minValue;

    public IntegerConfigEntry(String key, int value) {
        super(key, value);
    }

    public Integer deserialize(JsonPrimitive jsonValue) {
        int value = jsonValue.getAsInt();
        if (hasRange) {
            return Mth.clamp(value, minValue, maxValue);
        }
        return value;
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    public FieldBuilder getBuilder(ConfigEntryBuilder builder) {
        if (hasRange) {
            return builder.startIntSlider(Component.translatable("text.craftgr.config.option." + getKey()), getValue(), minValue, maxValue)
                    .setTextGetter(value -> Component.literal(value.toString()));
        }

        return builder.startIntField(Component.translatable("text.craftgr.config.option." + getKey()), getValue());
    }

    public IntegerConfigEntry setRange(int min, int max) {
        hasRange = true;
        minValue = min;
        maxValue = max;

        return this;
    }
}
