package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class IntegerConfigEntry extends GRConfigEntry<Integer> {

    private boolean hasRange;

    private int maxValue;
    private int minValue;

    public IntegerConfigEntry(String key, int value) {
        super(key, value);
    }

    @Override
    public Integer deserialize(JsonPrimitive jsonValue) {
        int value = jsonValue.getAsInt();
        if (hasRange) {
            return Mth.clamp(value, minValue, maxValue);
        }
        return value;
    }

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public OptionProvider<Integer> getOptionProvider() {
        return new OptionProvider<Integer>() {
            @Override
            public Option<Integer> getOption() {
                Option.Builder<Integer> builder = Option.<Integer>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip")))
                        .binding(getDefaultValue(), IntegerConfigEntry.this::getValue, (value) -> GRConfig.setValue(IntegerConfigEntry.this, value));

                if (hasRange) {
                    return builder.controller((option) -> IntegerSliderControllerBuilder.create(option)
                            .step(1)
                            .range(minValue, maxValue)
                    ).build();
                }

                return builder.controller(IntegerFieldControllerBuilder::create).build();
            }
        };
    }

    public IntegerConfigEntry setRange(int min, int max) {
        hasRange = true;
        minValue = min;
        maxValue = max;

        return this;
    }
}
