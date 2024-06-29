package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormattableController;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.config.entry.ConfigField;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Function;

public class IntegerConfigField extends ConfigField<Integer> {

    private Function<Integer, Component> formatter;
    private boolean hasRange;
    private int maxValue;
    private int minValue;

    public IntegerConfigField(String key, int value) {
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
            public Option<Integer> getOption(ModConfig config) {
                Option.Builder<Integer> builder = Option.<Integer>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".description")))
                        .binding(getDefaultValue(), () -> getValue(), (value) -> apply(config, value));

                Function<Option<Integer>, ControllerBuilder<Integer>> function = (option) -> {
                    ValueFormattableController<Integer, ?> controllerBuilder = hasRange ?
                            IntegerSliderControllerBuilder.create(option)
                                    .step(1)
                                    .range(minValue, maxValue) :
                            IntegerFieldControllerBuilder.create(option);
                    if (formatter != null) {
                        controllerBuilder.formatValue(formatter::apply);
                    }
                    return controllerBuilder;
                };

                return builder.controller(function).build();
            }
        };
    }

    public IntegerConfigField setFormatter(Function<Integer, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    public IntegerConfigField setRange(int min, int max) {
        hasRange = true;
        minValue = min;
        maxValue = max;

        return this;
    }
}
