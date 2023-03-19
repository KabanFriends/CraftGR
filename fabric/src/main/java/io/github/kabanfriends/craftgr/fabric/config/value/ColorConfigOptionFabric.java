package io.github.kabanfriends.craftgr.fabric.config.value;

import com.google.gson.JsonPrimitive;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.ColorController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.ColorConfigOption;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ColorConfigOptionFabric extends ColorConfigOption implements FabricConfigBuildable {

    public ColorConfigOptionFabric(String key, Color value) {
        super(key, value);
    }


    public Color deserialize(JsonPrimitive jsonValue) {
        if (jsonValue.isString()) {
            return new Color(Integer.parseInt(jsonValue.getAsString(), 16));
        }
        return getDefaultValue();
    }

    public JsonPrimitive serialize() {
        int color = getValue().getRGB() & 0xFFFFFF;
        return new JsonPrimitive(Integer.toHexString(color));
    }

    @Override
    public Option getOption() {
        return Option.createBuilder(Color.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller((option) -> new ColorController(option, false))
                .binding(getDefaultValue(), this::getValue, (value) -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
