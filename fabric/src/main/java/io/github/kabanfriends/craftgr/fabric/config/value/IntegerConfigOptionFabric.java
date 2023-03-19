package io.github.kabanfriends.craftgr.fabric.config.value;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.IntegerConfigOption;
import net.minecraft.network.chat.Component;

public class IntegerConfigOptionFabric extends IntegerConfigOption implements FabricConfigBuildable {

    public IntegerConfigOptionFabric(String key, int value) {
        super(key, value);
    }

    @Override
    public Option getOption() {
        Option.Builder<Integer> builder = Option.createBuilder(Integer.class)
                .name(Component.translatable("text.craftgr.config.option." + getKey()))
                .tooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .binding(getDefaultValue(), this::getValue, (value) -> CraftGR.getConfig().setValue(this, value));

        if (hasRange) {
            return builder.controller((option) -> new IntegerSliderController(option, minValue, maxValue, 1)).build();
        }
        return builder.controller(IntegerFieldController::new).build();
    }
}
