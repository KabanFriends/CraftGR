package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.util.HandlerState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class KeyActionHandler {

    public static void togglePlayback() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
        HandlerState state = handler.getState();

        if (state == HandlerState.ACTIVE) {
            handler.stopPlayback();

            MutableComponent icon = Component.literal("❌ ");
            icon.withStyle(ChatFormatting.RED);
            MutableComponent message = Component.translatable("text.craftgr.message.stopped");
            message.withStyle(ChatFormatting.WHITE);

            CraftGR.MC.player.displayClientMessage(icon.append(message), true);
        } else {
            if (state != HandlerState.INITIALIZING) {
                CraftGR.EXECUTOR.submit(() -> {
                    handler.initialize();
                    if (handler.hasAudioPlayer()) {
                        handler.getAudioPlayer().setVolume(1.0f);
                        handler.startPlayback();
                    }
                });

                MutableComponent icon = Component.literal("♫ ");
                icon.withStyle(ChatFormatting.GREEN);
                MutableComponent message = Component.translatable("text.craftgr.message.started");
                message.withStyle(ChatFormatting.WHITE);

                CraftGR.MC.player.displayClientMessage(icon.append(message), true);
            }
        }
    }

}
