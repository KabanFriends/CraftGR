package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.config.entry.ConfigField;
import io.github.kabanfriends.craftgr.config.entry.OptionProvider;
import net.minecraft.network.chat.Component;

public class EnumConfigField<T extends Enum<T>> extends ConfigField<T> {

    protected final Class<T> enumClass;
    protected final T[] enumValues;

    public EnumConfigField(String key, T value) {
        super(key, value);
        enumClass = getDefaultValue().getDeclaringClass();
        enumValues = enumClass.getEnumConstants();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Enum<?> e) {
            super.setValue(e);
        }
    }

    @Override
    public T deserialize(JsonPrimitive jsonValue) {
        return Enum.valueOf(enumClass, jsonValue.getAsString());
    }

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue().name());
    }

    @Override
    public OptionProvider<T> getOptionProvider() {
        return new OptionProvider<T>() {
            @Override
            public Option<T> getOption(ModConfig config) {
                return Option.<T>createBuilder()
                        .name(Component.translatable("text.craftgr.config.option." + getKey()))
                        .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".description")))
                        .controller((option) -> EnumControllerBuilder.create(option)
                                .enumClass(enumClass)
                                .formatValue((value) -> Component.translatable("text.craftgr.config.option." + getKey() + "." + value.name()))
                        )
                        .binding(getDefaultValue(), () -> getValue(), (value) -> apply(config, value))
                        .build();
            }
        };
    }
}
