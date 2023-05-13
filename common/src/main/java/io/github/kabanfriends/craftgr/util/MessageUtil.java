package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MessageUtil {

    public static void sendMessage(String message) {
        if (CraftGR.MC.player == null) return;
        CraftGR.MC.player.displayClientMessage(Component.literal(message), false);
    }

    public static void sendTranslatableMessage(String key) {
        if (CraftGR.MC.player == null) return;
        CraftGR.MC.player.displayClientMessage(Component.translatable(key), false);
    }

    public static void sendConnectingMessage() {
        if (CraftGR.MC.player == null) return;

        MutableComponent icon = Component.literal("→ ");
        icon.withStyle(ChatFormatting.GOLD);
        MutableComponent message = Component.translatable("text.craftgr.message.connecting");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.MC.player.displayClientMessage(icon.append(message), true);
    }

    public static void sendReconnectingMessage() {
        if (CraftGR.MC.player == null) return;

        MutableComponent icon = CraftGR.RECONNECT_ICON.copy().append(" ");
        icon.withStyle(ChatFormatting.GOLD);
        MutableComponent message = Component.translatable("text.craftgr.message.reconnecting");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.MC.player.displayClientMessage(icon.append(message), true);
    }

    public static void sendConnectionErrorMessage() {
        MutableComponent icon = Component.literal("❌ ");
        icon.withStyle(ChatFormatting.DARK_RED);
        MutableComponent message = Component.translatable("text.craftgr.message.connectionError");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.MC.player.displayClientMessage(icon.append(message), true);
    }

    public static void sendAudioStartedMessage() {
        if (CraftGR.MC.player == null) return;

        MutableComponent icon = Component.literal("▶ ");
        icon.withStyle(ChatFormatting.GREEN);
        MutableComponent message = Component.translatable("text.craftgr.message.started");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.MC.player.displayClientMessage(icon.append(message), true);
    }

    public static void sendAudioStoppedMessage() {
        MutableComponent icon = Component.literal("■ ");
        icon.withStyle(ChatFormatting.RED);
        MutableComponent message = Component.translatable("text.craftgr.message.stopped");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.MC.player.displayClientMessage(icon.append(message), true);
    }
}
