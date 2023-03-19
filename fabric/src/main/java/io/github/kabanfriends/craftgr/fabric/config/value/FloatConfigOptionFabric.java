package io.github.kabanfriends.craftgr.fabric.config.value;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.string.number.FloatFieldController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.FloatConfigOption;
import net.minecraft.network.chat.Component;

public class FloatConfigOptionFabric extends FloatConfigOption implements FabricConfigBuildable {

    public FloatConfigOptionFabric(String key, float value) {
        super(key, value);
    }

    @Override
    public Option getOption() {
        return Option.createBuilder(Float.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller(FloatFieldController::new)
                .binding(getDefaultValue(), this::getValue, (value) -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
