package io.github.kabanfriends.craftgr.forge.config;

import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.config.GRConfigCategory;
import io.github.kabanfriends.craftgr.config.GRConfigOptions;
import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import io.github.kabanfriends.craftgr.forge.config.value.ForgeConfigBuildable;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GRConfigForge extends GRConfig {

    @Override
    public Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create();

        Component title = Component.translatable("text.craftgr.config.title");
        builder.setTitle(title);
        builder.setParentScreen(parent);
        ConfigCategory root = builder.getOrCreateCategory(title);

        for (GRConfigCategory grc : GRConfigOptions.categories) {
            SubCategoryBuilder category = builder.entryBuilder().startSubCategory(grc.getTitle());
            category.setExpanded(grc.getExpanded());

            for (GRConfigOption entry : grc.getOptions()) {
                category.add(((ForgeConfigBuildable) entry).getEntry(builder.entryBuilder()));
            }

            root.addEntry(category.build());
        }

        builder.setSavingRunnable(this::save);

        return builder.build();
    }
}
