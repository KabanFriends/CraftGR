package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.AudioPlayer;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.util.*;
import org.apache.http.client.methods.*;
import org.apache.logging.log4j.Level;

import java.io.InputStream;

public class AudioPlayerHandler {

    private static final AudioPlayerHandler INSTANCE = new AudioPlayerHandler();

    private HandlerState state = HandlerState.NOT_INITIALIZED;
    private AudioPlayer player;
    private ResponseHolder response;
    private boolean playing = false;

    public void startPlayback() {
        if (state == HandlerState.FAIL) {
            CraftGR.log(Level.ERROR, "Cannot start audio playback due to an initialization failure!");
            return;
        }

        this.playing = true;
        state = HandlerState.ACTIVE;

        ProcessResult result = this.player.play();

        switch (result) {
            case ERROR:
                CraftGR.log(Level.ERROR, "Error during audio playback! Restarting in 5 seconds...");

                try {
                    Thread.sleep(5L * 1000L);
                } catch (InterruptedException e) { }
                break;
            case STOP:
                CraftGR.log(Level.INFO, "Audio playback has stopped!");
                return;
        }

        if (response != null && !response.isClosed()) response.close();

        if (state == HandlerState.ACTIVE) {
            MessageUtil.sendReconnectingMessage();
            AudioPlayerUtil.startPlaybackAsync();
        }
    }

    public void stopPlayback() {
        stopPlayback(false);
    }

    public void stopPlayback(boolean reloading) {
        if (player != null) player.stop();
        if (response != null && !response.isClosed()) response.close();

        this.playing = false;
        this.player = null;

        if (reloading) this.state = HandlerState.RELOADING;
        else this.state = HandlerState.STOPPED;
    }

    public void initialize() {
        CraftGR.log(Level.INFO, "Connecting to the audio stream...");
        state = HandlerState.INITIALIZING;

        try {
            if (response != null) response.close();

            HttpGet get = HttpUtil.get(GRConfig.getValue("urlStream"));
            ResponseHolder response = new ResponseHolder(CraftGR.getHttpClient().execute(get));
            this.response = response;

            InputStream stream = response.getResponse().getEntity().getContent();

            AudioPlayer audioPlayer = new AudioPlayer(stream);

            if (player != null) player.stop();
            player = audioPlayer;

            state = HandlerState.READY;
            CraftGR.log(Level.INFO, "Audio player is ready!");
        } catch (Exception err) {
            CraftGR.log(Level.ERROR, "Error while connecting to the audio stream:");
            err.printStackTrace();

            state = HandlerState.FAIL;
        }
    }

    public HandlerState getState() {
        return state;
    }

    public boolean isPlaying() {
        return playing;
    }

    public AudioPlayer getAudioPlayer() {
        return player;
    }

    public boolean hasAudioPlayer() {
        return player != null;
    }

    public static AudioPlayerHandler getInstance() {
        return INSTANCE;
    }
}
