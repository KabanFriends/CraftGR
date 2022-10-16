package io.github.kabanfriends.craftgr.config.compat.impl;

import io.github.kabanfriends.craftgr.config.compat.ClothCompat;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class Cloth7Compat extends ClothCompat {

    @SuppressWarnings("unchecked")
    public void setDefaultValue(FieldBuilder builder, Object value) {
        if (builder instanceof TextFieldBuilder field) {
            field.setDefaultValue((String)value);
            return;
        }
        if (builder instanceof IntSliderBuilder field) {
            field.setDefaultValue((int)value);
            return;
        }
        if (builder instanceof IntFieldBuilder field) {
            field.setDefaultValue((int)value);
            return;
        }
        if (builder instanceof FloatFieldBuilder field) {
            field.setDefaultValue((float)value);
            return;
        }
        if (builder instanceof SelectorBuilder field) {
            field.setDefaultValue(value);
            return;
        }
        if (builder instanceof ColorFieldBuilder field) {
            field.setDefaultValue((int)value);
            return;
        }
        if (builder instanceof BooleanToggleBuilder field) {
            field.setDefaultValue((boolean)value);
            return;
        }
    }

    public void setTooltip(FieldBuilder builder, Component tooltip) {
        if (builder instanceof TextFieldBuilder field) {
            field.setTooltip(tooltip);
            return;
        }
        if (builder instanceof IntSliderBuilder field) {
            field.setTooltip(tooltip);
            return;
        }
        if (builder instanceof IntFieldBuilder field) {
            field.setTooltip(tooltip);
            return;
        }
        if (builder instanceof FloatFieldBuilder field) {
            field.setTooltip(tooltip);
            return;
        }
        if (builder instanceof SelectorBuilder field) {
            field.setTooltip(tooltip);
            return;
        }
        if (builder instanceof ColorFieldBuilder field) {
            field.setTooltip(tooltip);
            return;
        }
        if (builder instanceof BooleanToggleBuilder field) {
            field.setTooltip(tooltip);
        }
    }

    @SuppressWarnings("unchecked")
    public void setSaveConsumer(FieldBuilder builder, Consumer consumer) {
        if (builder instanceof TextFieldBuilder field) {
            field.setSaveConsumer(consumer);
            return;
        }
        if (builder instanceof IntSliderBuilder field) {
            field.setSaveConsumer(consumer);
            return;
        }
        if (builder instanceof IntFieldBuilder field) {
            field.setSaveConsumer(consumer);
            return;
        }
        if (builder instanceof FloatFieldBuilder field) {
            field.setSaveConsumer(consumer);
            return;
        }
        if (builder instanceof SelectorBuilder field) {
            field.setSaveConsumer(consumer);
            return;
        }
        if (builder instanceof ColorFieldBuilder field) {
            field.setSaveConsumer(consumer);
            return;
        }
        if (builder instanceof BooleanToggleBuilder field) {
            field.setSaveConsumer(consumer);
        }
    }
}
