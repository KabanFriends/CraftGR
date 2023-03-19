package io.github.kabanfriends.craftgr.forge.config.value;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

public interface ForgeConfigBuildable {

    AbstractConfigListEntry getEntry(ConfigEntryBuilder builder);
}
