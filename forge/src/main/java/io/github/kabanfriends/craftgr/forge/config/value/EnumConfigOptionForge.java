package io.github.kabanfriends.craftgr.forge.config.value;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.value.impl.EnumConfigOption;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class EnumConfigOptionForge extends EnumConfigOption implements ForgeConfigBuildable {

    private final ConfigEnumHolder[] holders;
    private final Map<String, ConfigEnumHolder> holderByName;

    private ConfigEnumHolder defaultHolder;

    public EnumConfigOptionForge(String key, Enum value) {
        super(key, value);

        Object[] enums = baseClass.getEnumConstants();
        holders = new ConfigEnumHolder[enums.length];
        holderByName = new HashMap<>();

        for (int i = 0; i < enums.length; i++) {
            Enum e = (Enum) enums[i];
            ConfigEnumHolder holder = new ConfigEnumHolder(Component.translatable("text.craftgr.config.option." + getKey() + "." + e.name()), e);
            holders[i] = holder;
            holderByName.put(e.name(), holder);

            if (e.name().equals(getDefaultValue().name())) {
                defaultHolder = holder;
            }
        }
    }

    @Override
    public AbstractConfigListEntry getEntry(ConfigEntryBuilder builder) {
        return builder.startSelector(Component.translatable("text.craftgr.config.option." + getKey()), holders, holderByName.get(getValue().name()))
                .setNameProvider(ConfigEnumHolder::getTitle)
                .setTooltip(Component.translatable("text.craftgr.config.option." + getKey() + ".tooltip"))
                .setDefaultValue(defaultHolder)
                .setSaveConsumer(value -> CraftGR.getConfig().setValue(this, value))
                .build();
    }
}
