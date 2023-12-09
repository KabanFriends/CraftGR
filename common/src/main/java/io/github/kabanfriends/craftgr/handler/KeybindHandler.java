package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.util.AudioPlayerUtil;
import io.github.kabanfriends.craftgr.util.HandlerState;
import io.github.kabanfriends.craftgr.util.MessageUtil;
import net.minecraft.client.KeyMapping;

public class KeybindHandler {

    public static KeyMapping toggleMuteKey;

    public static void tick() {
        if (CraftGR.MC.screen == null) {
            while (toggleMuteKey.consumeClick()) {
                KeybindHandler.togglePlayback();
            }
        }
    }

    private static void togglePlayback() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();
        HandlerState state = handler.getState();

        if (state == HandlerState.ACTIVE) {
            handler.stopPlayback();
            MessageUtil.sendAudioStoppedMessage();
        } else if (state != HandlerState.INITIALIZING && state != HandlerState.RELOADING) {
            MessageUtil.sendConnectingMessage();
            AudioPlayerUtil.startPlaybackAsync();
        }
    }

}
