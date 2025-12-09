package io.github.kabanfriends.craftgr.audio;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.util.*;
import io.github.kabanfriends.craftgr.util.Http;
import javazoom.jl.decoder.JavaLayerException;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Radio {

    private static final int RETRY_INTERVAL = 5;

    private final CraftGR craftGR;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private State state;
    private AudioPlayer audioPlayer;
    private Future<?> playback;
    private boolean hasError;

    public Radio(CraftGR craftGR) {
        this.craftGR = craftGR;

        this.hasError = false;
        this.state = State.AWAIT_LOADING;
    }

    public void start(boolean fadeIn) {
        state = State.STARTING;
        playback = craftGR.getThreadExecutor().submit(() -> handlePlayback(fadeIn));
    }

    public void stop(boolean awaitReload) {
        if (state == State.PLAYING) {
            audioPlayer.stop();
        }
        if (state == State.CONNECTING || state == State.PLAYING) {
            playback.cancel(true);
        }

        hasError = false;
        state = awaitReload ? State.AWAIT_LOADING : State.STOPPED;
    }

    public void toggle() {
        if (state == State.PLAYING) {
            stop(false);
        } else if (state != State.CONNECTING && state != State.AWAIT_LOADING) {
            start(false);
        }
    }

    public void setVolume(int volume) {
        if (state == State.PLAYING) {
            audioPlayer.setGain(volume / 100f);
        }
    }

    private void handlePlayback(boolean fadeIn) {
        craftGR.getThreadExecutor().submit(() -> craftGR.getSongProvider().verifyCurrentSong());

        craftGR.log(Level.INFO, "Connecting to stream server");
        ActionBarMessage.CONNECTING.show();
        state = State.CONNECTING;

        try {
            connect();
        } catch (IOException e) {
            if (e.getCause() instanceof InterruptedException) {
                return;
            }
            craftGR.log(Level.ERROR, "Could not connect to stream server: " + ExceptionUtil.getStackTrace(e));
            state = State.STOPPED;
            hasError = true;
            ActionBarMessage.CONNECTION_ERROR.show();
        }

        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        craftGR.log(Level.INFO, "Audio player starting");
        ActionBarMessage.PLAYBACK_STARTED.show();
        state = State.PLAYING;

        try {
            setVolume(ModConfig.get("volume"));
            audioPlayer.play(fadeIn);

            ActionBarMessage.PLAYBACK_STOPPED.show();
            craftGR.log(Level.INFO, "Audio player stopped");
        } catch (JavaLayerException e) {
            stop(false);

            craftGR.log(Level.ERROR, "Audio player error, restarting in " + RETRY_INTERVAL + " seconds: " + ExceptionUtil.getStackTrace(e));
            state = State.STARTING;
            hasError = true;
            scheduler.schedule(() -> {
                hasError = false;
                ActionBarMessage.RECONNECTING.show();
                start(false);
            }, RETRY_INTERVAL, TimeUnit.SECONDS);
        }
    }

    private void connect() throws IOException {
        Http.fetch(Http.standardRequest()
                .uri(URI.create(ModConfig.get("urlStream")))
                .build(), HttpResponse.BodyHandlers.ofInputStream())
                .thenAccept(response -> {
                    this.audioPlayer = new AudioPlayer(craftGR, response.body());
                })
                .join();
    }

    public State getState() {
        return state;
    }

    public boolean hasError() {
        return hasError;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public enum State {
        AWAIT_LOADING,
        STARTING,
        CONNECTING,
        PLAYING,
        STOPPED
    }
}
