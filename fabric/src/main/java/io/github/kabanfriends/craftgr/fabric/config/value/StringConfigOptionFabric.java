package io.github.kabanfriends.craftgr.fabric.config.value;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.string.StringController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.StringConfigOption;
import net.minecraft.network.chat.Component;

public class StringConfigOptionFabric extends StringConfigOption implements FabricConfigBuildable {

    public StringConfigOptionFabric(String key, String value) {
        super(key, value);
    }

    @Override
    public Option getOption() {
        return Option.createBuilder(String.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller(StringController::new)
                .binding(getDefaultValue(), this::getValue, (value) -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
