package io.github.kabanfriends.craftgr.forge.config.value;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.IntegerConfigOption;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class IntegerConfigOptionForge extends IntegerConfigOption implements ForgeConfigBuildable {

    public IntegerConfigOptionForge(String key, int value) {
        super(key, value);
    }

    @Override
    public AbstractConfigListEntry getEntry(ConfigEntryBuilder builder) {
        if (hasRange) {
            return builder.startIntSlider(Component.translatable("text.craftgr.config.option." + getKey()), getValue(), minValue, maxValue)
                    .setTextGetter(value -> Component.literal(value.toString()))
                    .setTooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                    .setDefaultValue(getDefaultValue())
                    .setSaveConsumer(value -> CraftGR.getConfig().setValue(this, value))
                    .build();
        }

        return builder.startIntField(Component.translatable("text.craftgr.config.option." + getKey()), getValue())
                .setTooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .setDefaultValue(getDefaultValue())
                .setSaveConsumer(value -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
