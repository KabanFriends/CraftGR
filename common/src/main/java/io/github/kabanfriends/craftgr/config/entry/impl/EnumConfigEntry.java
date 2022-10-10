package io.github.kabanfriends.craftgr.config.entry.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.ConfigEnumHolder;
import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SelectorBuilder;
import net.minecraft.network.chat.Component;

public class EnumConfigEntry extends GRConfigEntry<Enum> {

    private final Class baseClass;

    public EnumConfigEntry(String key, Enum value) {
        super(key, value);
        baseClass = getDefaultValue().getDeclaringClass();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Enum e) {
            super.setValue(e);
        } else if (value instanceof ConfigEnumHolder holder) {
            super.setValue(holder.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public Enum deserialize(JsonPrimitive jsonValue) {
        return Enum.valueOf(baseClass, jsonValue.getAsString());
    }

    public JsonPrimitive serialize() {
        return new JsonPrimitive(getValue().name());
    }

    public SelectorBuilder getBuilder(ConfigEntryBuilder builder) {
        Object[] enums = baseClass.getEnumConstants();
        ConfigEnumHolder[] entries = new ConfigEnumHolder[enums.length];

        ConfigEnumHolder defaultEnum = null;
        ConfigEnumHolder currentEnum = null;

        for (int i = 0; i < enums.length; i++) {
            Enum e = (Enum) enums[i];
            ConfigEnumHolder holder = new ConfigEnumHolder(Component.translatable("text.craftgr.config.option." + getKey() + "." + e.name()), e);
            entries[i] = holder;

            if (e.name().equals(getDefaultValue().name())) {
                defaultEnum = holder;
            }
            if (e.name().equals(getValue().name())) {
                currentEnum = holder;
            }
        }

        return builder.startSelector(Component.translatable("text.craftgr.config.option." + getKey()), entries, currentEnum)
                .setNameProvider(ConfigEnumHolder::getTitle);
    }
}
