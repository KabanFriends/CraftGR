package io.github.kabanfriends.craftgr.fabric.config.value;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.BooleanConfigOption;
import net.minecraft.network.chat.Component;

public class BooleanConfigOptionFabric extends BooleanConfigOption implements FabricConfigBuildable {

    public BooleanConfigOptionFabric(String key, boolean value) {
        super(key, value);
    }

    @Override
    public Option getOption() {
        return Option.createBuilder(Boolean.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller(TickBoxController::new)
                .binding(getDefaultValue(), this::getValue, (value) -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
