package io.github.kabanfriends.craftgr.forge.config.value;

import io.github.kabanfriends.craftgr.config.value.impl.RadioStateConfigOption;
import io.github.kabanfriends.craftgr.forge.config.entry.builder.RadioStateBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class RadioStateConfigOptionForge extends RadioStateConfigOption implements ForgeConfigBuildable {

    public RadioStateConfigOptionForge(String key) {
        super(key);
    }

    @Override
    public AbstractConfigListEntry getEntry(ConfigEntryBuilder builder) {
        return new RadioStateBuilder(Component.translatable("text.craftgr.config.option." + getKey()))
                .setTooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .build();
    }
}
