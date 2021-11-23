package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class MessageUtil {

    public static void sendMessage(String message) {
        if (CraftGR.MC.player == null) return;
        CraftGR.MC.player.sendMessage(new LiteralText(message), false);
    }

    public static void sendTranslatableMessage(String key) {
        if (CraftGR.MC.player == null) return;
        CraftGR.MC.player.sendMessage(new TranslatableText(key), false);
    }
}
