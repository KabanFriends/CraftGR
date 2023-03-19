package io.github.kabanfriends.craftgr.config;

import io.github.kabanfriends.craftgr.config.value.GRConfigOption;
import net.minecraft.network.chat.Component;

public class GRConfigCategory {

    private Component title;
    private boolean expanded;
    private GRConfigOption[] options;

    public GRConfigCategory(Component title, boolean expanded, GRConfigOption... options) {
        this.title = title;
        this.expanded = expanded;
        this.options = options;
    }

    public GRConfigOption[] getOptions() {
        return options;
    }

    public boolean getExpanded() {
        return expanded;
    }

    public Component getTitle() {
        return title;
    }
}
