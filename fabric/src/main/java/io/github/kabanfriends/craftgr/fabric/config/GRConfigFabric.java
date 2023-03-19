package io.github.kabanfriends.craftgr.fabric.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.GRConfigCategory;
import io.github.kabanfriends.craftgr.config.GRConfigOptions;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import io.github.kabanfriends.craftgr.fabric.config.value.FabricConfigBuildable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GRConfigFabric extends GRConfig {

    @Override
    public Screen getConfigScreen(Screen parent) {
        YetAnotherConfigLib.Builder config = YetAnotherConfigLib.createBuilder();
        config.title(Component.translatable("text.craftgr.config.title"));

        ConfigCategory.Builder category = ConfigCategory.createBuilder();
        category.name(Component.translatable("text.craftgr.config.title"));

        for (GRConfigCategory grc : GRConfigOptions.categories) {
            OptionGroup.Builder group = OptionGroup.createBuilder();
            group.name(grc.getTitle());
            group.collapsed(!grc.getExpanded());

            for (GRConfigOption option : grc.getOptions()) {
                group.option(((FabricConfigBuildable) option).getOption());
            }

            category.group(group.build());
        }

        config.category(category.build());
        config.save(this::save);

        return config.build().generateScreen(parent);
    }
}
