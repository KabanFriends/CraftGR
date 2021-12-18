package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class KeyActionHandler {

    public static void togglePlayback() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.hasAudioPlayer()) {
            handler.stopPlayback();

            TextComponent icon = new TextComponent("❌ ");
            icon.withStyle(ChatFormatting.RED);
            TranslatableComponent message = new TranslatableComponent("text.craftgr.message.stopped");
            message.withStyle(ChatFormatting.WHITE);

            CraftGR.MC.player.displayClientMessage(icon.append(message), true);
        }else {
            if (handler.getInitState() == AudioPlayerHandler.InitState.NOT_INITIALIZED) {
                CraftGR.EXECUTOR.submit(() -> {
                    handler.initialize();
                    if (handler.hasAudioPlayer()) {
                        handler.getAudioPlayer().setVolume(1.0f);
                        handler.startPlayback();
                    }
                });

                TextComponent icon = new TextComponent("♫ ");
                icon.withStyle(ChatFormatting.GREEN);
                TranslatableComponent message = new TranslatableComponent("text.craftgr.message.started");
                message.withStyle(ChatFormatting.WHITE);

                CraftGR.MC.player.displayClientMessage(icon.append(message), true);
            }
        }
    }

}
