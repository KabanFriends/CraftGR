package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class MessageUtil {

    public static void sendMessage(String message) {
        if (CraftGR.MC.player == null) return;
        CraftGR.MC.player.displayClientMessage(new TextComponent(message), false);
    }

    public static void sendTranslatableMessage(String key) {
        if (CraftGR.MC.player == null) return;
        CraftGR.MC.player.displayClientMessage(new TranslatableComponent(key), false);
    }
}
