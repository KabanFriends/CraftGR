package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class PercentageConfigEntry extends GRConfigEntry<Integer> {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    public PercentageConfigEntry(String key, int value) {
        super(key, value);
    }

    @Override
    public Integer deserialize(JsonPrimitive jsonValue) {
        return Mth.clamp(jsonValue.getAsInt(), MIN_VALUE, MAX_VALUE);
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
                return Option.<Integer>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip")))
                        .controller((option) -> IntegerSliderControllerBuilder.create(option)
                                .step(1)
                                .range(MIN_VALUE, MAX_VALUE)
                                .formatValue((value) -> Component.literal(value + "%"))
                        )
                        .binding(getDefaultValue(), PercentageConfigEntry.this::getValue, (value) -> GRConfig.setValue(PercentageConfigEntry.this, value))
                        .build();
            }
        };
    }
}
