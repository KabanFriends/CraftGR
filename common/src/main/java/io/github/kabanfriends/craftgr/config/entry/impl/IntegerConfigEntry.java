package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
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

    public Option getOption() {
        Option.Builder<Integer> builder = Option.createBuilder(Integer.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value));

        if (hasRange) {
            return builder.controller((option) -> new IntegerSliderController(option, minValue, maxValue, 1)).build();
        }

        return builder.controller(IntegerFieldController::new).build();
    }

    public IntegerConfigEntry setRange(int min, int max) {
        hasRange = true;
        minValue = min;
        maxValue = max;

        return this;
    }
}
