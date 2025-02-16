package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum ActionBarMessage {

    CONNECTING("→", ChatFormatting.GOLD, "text.craftgr.message.connecting"),
    RECONNECTING(CraftGR.RECONNECT_ICON.copy().withStyle(ChatFormatting.GOLD), "text.craftgr.message.reconnecting"),
    CONNECTION_ERROR("❌", ChatFormatting.DARK_RED, "text.craftgr.message.connectionError"),
    PLAYBACK_STARTED("▶", ChatFormatting.GREEN, "text.craftgr.message.started"),
    PLAYBACK_STOPPED("■", ChatFormatting.RED, "text.craftgr.message.stopped")
    ;

    private static final Component SPACE = Component.literal(" ");

    private final MutableComponent icon;
    private final MutableComponent body;

    ActionBarMessage(MutableComponent icon, String key) {
        this.icon = icon;
        this.body = Component.translatable(key);
    }

    ActionBarMessage(String icon, ChatFormatting color, String key) {
        this(Component.literal(icon).withStyle(color), key);
    }

    public void show() {
        Minecraft minecraft = CraftGR.getInstance().getMinecraft();

        if (minecraft.player == null) {
            return;
        }

        minecraft.player.displayClientMessage(Component.empty().append(icon).append(SPACE).append(body), true);
    }
}
