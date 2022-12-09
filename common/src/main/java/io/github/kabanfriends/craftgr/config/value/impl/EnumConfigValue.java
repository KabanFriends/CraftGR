package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.ConfigEnumHolder;
import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SelectorBuilder;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class EnumConfigValue extends GRConfigValue<Enum> {

    private final Class baseClass;
    private final ConfigEnumHolder[] holders;
    private final Map<String, ConfigEnumHolder> holderByName;

    private ConfigEnumHolder defaultHolder;

    public EnumConfigValue(String key, Enum value) {
        super(key, value);
        baseClass = getDefaultValue().getDeclaringClass();

        Object[] enums = baseClass.getEnumConstants();
        holders = new ConfigEnumHolder[enums.length];
        holderByName = new HashMap<>();

        for (int i = 0; i < enums.length; i++) {
            Enum e = (Enum) enums[i];
            ConfigEnumHolder holder = new ConfigEnumHolder(Component.translatable("text.craftgr.config.option." + getKey() + "." + e.name()), e);
            holders[i] = holder;
            holderByName.put(e.name(), holder);

            if (e.name().equals(getDefaultValue().name())) {
                defaultHolder = holder;
            }
        }
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
        SelectorBuilder field = builder.startSelector(Component.translatable("text.craftgr.config.option." + getKey()), holders, holderByName.get(getValue().name()))
                .setNameProvider(ConfigEnumHolder::getTitle);
        field.setDefaultValue(defaultHolder);
        return field;
    }
}
