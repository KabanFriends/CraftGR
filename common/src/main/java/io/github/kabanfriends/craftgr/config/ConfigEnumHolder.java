package io.github.kabanfriends.craftgr.config;

import net.minecraft.network.chat.Component;

public record ConfigEnumHolder(Component title, Enum value) {

    @Override
    public String toString() {
        return value.name();
    }
}
