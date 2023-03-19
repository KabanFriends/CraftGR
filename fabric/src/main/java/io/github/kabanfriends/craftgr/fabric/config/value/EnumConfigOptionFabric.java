package io.github.kabanfriends.craftgr.fabric.config.value;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.cycling.CyclingListController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.EnumConfigOption;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class EnumConfigOptionFabric extends EnumConfigOption implements FabricConfigBuildable {

    public EnumConfigOptionFabric(String key, Enum value) {
        super(key, value);
    }

    @Override
    public Option getOption() {
        return Option.createBuilder(Enum.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller((option) -> new CyclingListController(option, Arrays.asList(enumValues), (value) -> Component.translatable("text.craftgr.config.option." + getKey() + "." + ((Enum)value).name())))
                .binding(getDefaultValue(), this::getValue, (value) -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
