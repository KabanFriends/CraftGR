package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;

public class AudioPlayerUtil {

    public static void startPlaybackAsync() {
        startPlaybackAsync(1.0f);
    }

    public static void startPlaybackAsync(float baseVolume) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        CraftGR.getThreadExecutor().submit(() -> {
            handler.initialize();
            if (handler.getState() == HandlerState.FAIL) {
                MessageUtil.sendConnectionErrorMessage();
                return;
            }

            if (handler.hasAudioPlayer()) {
                MessageUtil.sendAudioStartedMessage();
                handler.getAudioPlayer().setBaseVolume(baseVolume);
                handler.startPlayback();
            }
        });
    }
}
