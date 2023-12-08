package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.util.AudioPlayerUtil;
import io.github.kabanfriends.craftgr.util.HandlerState;
import io.github.kabanfriends.craftgr.util.MessageUtil;

public class KeyActionHandler {

    public static void togglePlayback() {
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
