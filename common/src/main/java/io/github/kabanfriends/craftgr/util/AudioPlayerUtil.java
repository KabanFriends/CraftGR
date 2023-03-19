package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;

public class AudioPlayerUtil {

    public static void startPlayback() {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        handler.initialize();
        if (handler.hasAudioPlayer()) {
            handler.getAudioPlayer().setVolume(1.0f);
            handler.startPlayback();
        }
    }

    public static void startPlaybackAsync() {
        CraftGR.EXECUTOR.submit(() -> startPlayback());
    }
}
