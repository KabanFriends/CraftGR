package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import net.minecraft.network.chat.Component;

public class EnumConfigEntry<T extends Enum<T>> extends GRConfigEntry<T> {

    protected final Class<T> enumClass;
    protected final T[] enumValues;

    @SuppressWarnings("unchecked")
    public EnumConfigEntry(String key, T value) {
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

    public T deserialize(JsonPrimitive jsonValue) {
        return Enum.valueOf(enumClass, jsonValue.getAsString());
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue().name());
    }

    public Option<T> getOption() {
        return Option.<T>createBuilder()
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .description(OptionDescription.of(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip")))
                .controller((option) -> EnumControllerBuilder.create(option)
                        .enumClass(enumClass)
                        .formatValue((value) -> Component.translatable("text.craftgr.config.option." + getKey() + "." + value.name()))
                )
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value))
                .build();
    }
}
