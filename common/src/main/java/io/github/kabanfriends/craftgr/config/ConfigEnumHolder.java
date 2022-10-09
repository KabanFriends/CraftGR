package io.github.kabanfriends.craftgr.config;

import net.minecraft.network.chat.Component;

public class ConfigEnumHolder {

    private final Component title;
    private final Enum value;

    public ConfigEnumHolder(Component title, Enum value) {
        this.title = title;
        this.value = value;
    }

    public Component getTitle() {
        return title;
    }

    public Enum getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.name();
    }
}
