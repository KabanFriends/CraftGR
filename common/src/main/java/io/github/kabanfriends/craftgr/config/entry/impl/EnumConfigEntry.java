package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.cycling.CyclingListController;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class EnumConfigEntry extends GRConfigEntry<Enum> {

    protected final Class baseClass;
    protected final Enum[] enumValues;

    public EnumConfigEntry(String key, Enum value) {
        super(key, value);
        baseClass = getDefaultValue().getDeclaringClass();

        enumValues = (Enum[]) baseClass.getEnumConstants();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Enum e) {
            super.setValue(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Enum deserialize(JsonPrimitive jsonValue) {
        return Enum.valueOf(baseClass, jsonValue.getAsString());
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue().name());
    }

    public Option getOption() {
        return Option.createBuilder(Enum.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller((option) -> new CyclingListController(option, Arrays.asList(enumValues), (value) -> Component.translatable("text.craftgr.config.option." + getKey() + "." + ((Enum)value).name())))
                .binding(getDefaultValue(), this::getValue, (value) -> GRConfig.setValue(this, value))
                .build();
    }
}
