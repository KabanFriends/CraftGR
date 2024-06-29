package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MessageUtil {

    public static void sendConnectingMessage() {
        if (CraftGR.getInstance().getMinecraft().player == null) return;

        MutableComponent icon = Component.literal("→ ");
        icon.withStyle(ChatFormatting.GOLD);
        MutableComponent message = Component.translatable("text.craftgr.message.connecting");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.getInstance().getMinecraft().player.displayClientMessage(icon.append(message), true);
    }

    public static void sendReconnectingMessage() {
        if (CraftGR.getInstance().getMinecraft().player == null) return;

        MutableComponent icon = Component.empty().append(CraftGR.RECONNECT_ICON).append(" ");
        icon.withStyle(ChatFormatting.GOLD);
        MutableComponent message = Component.translatable("text.craftgr.message.reconnecting");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.getInstance().getMinecraft().player.displayClientMessage(icon.append(message), true);
    }

    public static void sendConnectionErrorMessage() {
        if (CraftGR.getInstance().getMinecraft().player == null) return;

        MutableComponent icon = Component.literal("❌ ");
        icon.withStyle(ChatFormatting.DARK_RED);
        MutableComponent message = Component.translatable("text.craftgr.message.connectionError");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.getInstance().getMinecraft().player.displayClientMessage(icon.append(message), true);
    }

    public static void sendAudioStartedMessage() {
        if (CraftGR.getInstance().getMinecraft().player == null) return;

        MutableComponent icon = Component.literal("▶ ");
        icon.withStyle(ChatFormatting.GREEN);
        MutableComponent message = Component.translatable("text.craftgr.message.started");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.getInstance().getMinecraft().player.displayClientMessage(icon.append(message), true);
    }

    public static void sendAudioStoppedMessage() {
        if (CraftGR.getInstance().getMinecraft().player == null) return;

        MutableComponent icon = Component.literal("■ ");
        icon.withStyle(ChatFormatting.RED);
        MutableComponent message = Component.translatable("text.craftgr.message.stopped");
        message.withStyle(ChatFormatting.WHITE);

        CraftGR.getInstance().getMinecraft().player.displayClientMessage(icon.append(message), true);
    }
}
