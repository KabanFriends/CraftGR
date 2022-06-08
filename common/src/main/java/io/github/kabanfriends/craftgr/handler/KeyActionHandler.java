package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.util.InitState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class KeyActionHandler {

    public static void togglePlayback() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.hasAudioPlayer()) {
            handler.stopPlayback();

            MutableComponent icon = Component.literal("❌ ");
            icon.withStyle(ChatFormatting.RED);
            MutableComponent message = Component.translatable("text.craftgr.message.stopped");
            message.withStyle(ChatFormatting.WHITE);

            CraftGR.MC.player.displayClientMessage(icon.append(message), true);
        }else {
            InitState state = handler.getInitState();
            if (state == InitState.NOT_INITIALIZED || state == InitState.RELOADING || state == InitState.FAIL) {
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
