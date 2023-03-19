package io.github.kabanfriends.craftgr.forge.config.value;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.BooleanConfigOption;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class BooleanConfigOptionForge extends BooleanConfigOption implements ForgeConfigBuildable {

    public BooleanConfigOptionForge(String key, boolean value) {
        super(key, value);
    }

    @Override
    public AbstractConfigListEntry getEntry(ConfigEntryBuilder builder) {
        return builder.startBooleanToggle(Component.translatable("text.craftgr.config.option." + getKey()), getValue())
                .setTooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .setDefaultValue(getDefaultValue())
                .setSaveConsumer(value -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
