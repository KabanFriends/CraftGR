package io.github.kabanfriends.craftgr.config.compat.impl;

import io.github.kabanfriends.craftgr.config.compat.ClothCompat;
import io.github.kabanfriends.craftgr.config.entry.builder.RadioStateBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Cloth8Compat extends ClothCompat {

    public void setDefaultValue(FieldBuilder builder, Object value) {
        ((AbstractFieldBuilder) builder).setDefaultValue(value);
    }

    public void setTooltip(FieldBuilder builder, Component tooltip) {
        if (builder instanceof RadioStateBuilder radioBuilder) {
            radioBuilder.setTooltip(tooltip);
            return;
        }

        ((AbstractFieldBuilder) builder).setTooltip(tooltip);
    }

    public void setSaveConsumer(FieldBuilder builder, Consumer consumer) {
        if (builder instanceof RadioStateBuilder) {
            return;
        }

        ((AbstractFieldBuilder) builder).setSaveConsumer(consumer);
    }
}
