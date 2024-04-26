package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class FloatConfigEntry extends GRConfigEntry<Float> {

    private Function<Float, Component> formatter;

    public FloatConfigEntry(String key, float value) {
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
            public Option<Float> getOption() {
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
                        .binding(getDefaultValue(), FloatConfigEntry.this::getValue, (value) -> GRConfig.setValue(FloatConfigEntry.this, value))
                        .build();
            }
        };
    }

    public FloatConfigEntry setFormatter(Function<Float, Component> formatter) {
        this.formatter = formatter;
        return this;
    }
}
