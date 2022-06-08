package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.network.chat.Component;

public class MessageUtil {

    public static void sendMessage(String message) {
        if (CraftGR.MC.player == null) return;
        CraftGR.MC.player.displayClientMessage(Component.literal(message), false);
    }

    public static void sendTranslatableMessage(String key) {
        if (CraftGR.MC.player == null) return;
        CraftGR.MC.player.displayClientMessage(Component.translatable(key), false);
    }
}
