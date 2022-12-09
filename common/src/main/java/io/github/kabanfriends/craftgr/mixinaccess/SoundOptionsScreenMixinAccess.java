package io.github.kabanfriends.craftgr.mixinaccess;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;

public interface SoundOptionsScreenMixinAccess {

    OptionsList getOptionsList();

    AbstractWidget getVolumeSlider();

    AbstractWidget getConfigButton();
}
