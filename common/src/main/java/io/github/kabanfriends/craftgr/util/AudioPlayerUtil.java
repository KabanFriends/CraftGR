package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;

public class AudioPlayerUtil {

    public static void startPlaybackAsync() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        CraftGR.EXECUTOR.submit(() -> {
            handler.initialize();
            if (handler.hasAudioPlayer()) {
                handler.getAudioPlayer().setVolume(1.0f);
                handler.startPlayback();
            }
        });
    }
}
