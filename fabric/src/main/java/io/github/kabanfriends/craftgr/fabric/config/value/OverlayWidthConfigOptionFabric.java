package io.github.kabanfriends.craftgr.fabric.config.value;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.OverlayWidthConfigOption;
import net.minecraft.network.chat.Component;

public class OverlayWidthConfigOptionFabric extends OverlayWidthConfigOption implements FabricConfigBuildable {

    public OverlayWidthConfigOptionFabric(String key, int value) {
        super(key, value);
    }

    @Override
    public Option getOption() {
        return Option.createBuilder(Integer.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .controller((option) -> new IntegerSliderController(option, MIN_VALUE, MAX_VALUE, 1, (value) -> Component.literal((WIDTH_OFFSET + value * 2) + "px")))
                .binding(getDefaultValue(), this::getValue, (value) -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
