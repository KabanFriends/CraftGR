package io.github.kabanfriends.craftgr.config;

import io.github.kabanfriends.craftgr.config.entry.ConfigField;
import net.minecraft.network.chat.Component;

public class ConfigGroup {

    private Component title;
    private boolean expanded;
    private ConfigField[] fields;

    public ConfigGroup(Component title, boolean expanded, ConfigField... fields) {
        this.title = title;
        this.expanded = expanded;
        this.fields = fields;
    }

    public ConfigField[] getFields() {
        return fields;
    }

    public boolean getExpanded() {
        return expanded;
    }

    public Component getTitle() {
        return title;
    }
}
