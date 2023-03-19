package io.github.kabanfriends.craftgr.config.value.impl;

import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import net.minecraft.network.chat.Component;

public abstract class EnumConfigOption extends GRConfigOption<Enum> {

    protected final Class baseClass;
    protected final Enum[] enumValues;

    public EnumConfigOption(String key, Enum value) {
        super(key, value);
        baseClass = getDefaultValue().getDeclaringClass();

        enumValues = (Enum[]) baseClass.getEnumConstants();
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

    public static class ConfigEnumHolder {

        private final Component title;
        private final Enum value;

        public ConfigEnumHolder(Component title, Enum value) {
            this.title = title;
            this.value = value;
        }

        public Component getTitle() {
            return title;
        }

        public Enum getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value.name();
        }
    }
}
