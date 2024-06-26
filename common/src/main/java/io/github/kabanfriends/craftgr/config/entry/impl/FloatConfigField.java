package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.config.entry.ConfigField;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class FloatConfigField extends ConfigField<Float> {

    private Function<Float, Component> formatter;

    public FloatConfigField(String key, float value) {
        super(key, value);
    }

    @Override
    public Float deserialize(JsonPrimitive jsonValue) {
        float value = jsonValue.getAsFloat();
        if (value < 0f) {
            value = 0f;
        }
        return value;
    }

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public OptionProvider<Float> getOptionProvider() {
        return new OptionProvider<Float>() {
            @Override
            public Option<Float> getOption(ModConfig config) {
                return Option.<Float>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".description")))
                        .controller((option) -> {
                            FloatFieldControllerBuilder controllerBuilder = FloatFieldControllerBuilder.create(option);
                            if (formatter != null) {
                                controllerBuilder.formatValue(formatter::apply);
                            }
                            return controllerBuilder;
                        })
                        .binding(getDefaultValue(), () -> getValue(), (value) -> apply(config, value))
                        .build();
            }
        };
    }

    public FloatConfigField setFormatter(Function<Float, Component> formatter) {
        this.formatter = formatter;
        return this;
    }
}
