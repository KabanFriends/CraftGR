package io.github.kabanfriends.craftgr.forge.config.value;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.PercentageConfigOption;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class PercentageConfigOptionForge extends PercentageConfigOption implements ForgeConfigBuildable {

    public PercentageConfigOptionForge(String key, int value) {
        super(key, value);
    }

    @Override
    public AbstractConfigListEntry getEntry(ConfigEntryBuilder builder) {
        return builder.startIntSlider(Component.translatable("text.craftgr.config.option." + getKey()), getValue(), MIN_VALUE, MAX_VALUE)
                .setTextGetter(value -> Component.literal(value + "%"))
                .setTooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .setDefaultValue(getDefaultValue())
                .setSaveConsumer(value -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
