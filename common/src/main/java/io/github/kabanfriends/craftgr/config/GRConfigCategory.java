package io.github.kabanfriends.craftgr.config;

import io.github.kabanfriends.craftgr.config.entry.GRConfigEntry;
import net.minecraft.network.chat.Component;

public class GRConfigCategory {

    private Component title;
    private boolean expanded;
    private GRConfigEntry[] entries;

    public GRConfigCategory(Component title, boolean expanded, GRConfigEntry... entries) {
        this.title = title;
        this.expanded = expanded;
        this.entries = entries;
    }

    public GRConfigEntry[] getEntries() {
        return entries;
    }

    public boolean getExpanded() {
        return expanded;
    }

    public Component getTitle() {
        return title;
    }
}
