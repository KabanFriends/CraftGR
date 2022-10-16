package io.github.kabanfriends.craftgr.config;

import io.github.kabanfriends.craftgr.config.value.GRConfigValue;
import net.minecraft.network.chat.Component;

public class GRConfigCategory {

    private Component title;
    private boolean expanded;
    private GRConfigValue[] entries;

    public GRConfigCategory(Component title, boolean expanded, GRConfigValue... entries) {
        this.title = title;
        this.expanded = expanded;
        this.entries = entries;
    }

    public GRConfigValue[] getValues() {
        return entries;
    }

    public boolean getExpanded() {
        return expanded;
    }

    public Component getTitle() {
        return title;
    }
}
