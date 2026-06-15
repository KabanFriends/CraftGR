package io.github.kabanfriends.craftgr.audio;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.util.*;
import io.github.kabanfriends.craftgr.util.Http;
import javazoom.jl.decoder.JavaLayerException;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.*;

public class Radio {

    private static final int RETRY_INTERVAL = 5;

    private final CraftGR craftGR;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Object sessionLock = new Object();

    private volatile State state;
    private volatile CompletableFuture<HttpResponse<InputStream>> connection;
    private volatile AudioPlayer audioPlayer;
    private volatile Future<?> playback;
    private volatile boolean hasError;
    private volatile double volume = 1.0;

    private int retries = 0;
    private long sessionId;

    public Radio(CraftGR craftGR) {
        this.craftGR = craftGR;

        this.hasError = false;
        this.state = State.AWAIT_LOADING;
        this.sessionId = 0L;
    }

    public void start(boolean fadeIn) {
        long sessionId;

        synchronized (sessionLock) {
            if (state == State.STARTING || state == State.CONNECTING || state == State.PLAYING) {
                return;
            }

            sessionId = ++this.sessionId;
            hasError = false;
            state = State.STARTING;
        }

        playback = craftGR.getThreadExecutor().submit(() -> handlePlayback(fadeIn, sessionId));
    }

    public void stop(boolean awaitReload) {
        boolean notifyStopped = state == State.STARTING || state == State.CONNECTING || state == State.PLAYING;
        stop(awaitReload, true, notifyStopped);
    }

    private void stop(boolean awaitReload, boolean invalidateSession, boolean notifyStopped) {
        Future<?> playbackToCancel;
        CompletableFuture<HttpResponse<InputStream>> connectionToCancel;
        AudioPlayer audioPlayerToStop;

        synchronized (sessionLock) {
            if (invalidateSession) {
                sessionId++;
            }

            playbackToCancel = playback;
            connectionToCancel = connection;
            audioPlayerToStop = audioPlayer;

            playback = null;
            connection = null;
            audioPlayer = null;
            hasError = false;
            state = awaitReload ? State.AWAIT_LOADING : State.STOPPED;
        }

        if (audioPlayerToStop != null) {
            audioPlayerToStop.stop();
        }
        if (playbackToCancel != null) {
            playbackToCancel.cancel(true);
        }
        if (connectionToCancel != null) {
            connectionToCancel.cancel(true);
        }
        if (notifyStopped) {
            ActionBarMessage.PLAYBACK_STOPPED.show();
        }
    }

    public void toggle() {
        if (state == State.STARTING || state == State.CONNECTING || state == State.PLAYING) {
            stop(false);
        } else if (state != State.AWAIT_LOADING) {
            start(false);
        }
    }

    public void setVolume(double volume) {
        this.volume = volume;
        updateVolume();
    }

    public void updateVolume() {
        if (state == State.PLAYING) {
            audioPlayer.setVolume(volume * Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER));
        }
    }

    private void handlePlayback(boolean fadeIn, long sessionId) {
        if (!isSessionCurrent(sessionId)) {
            return;
        }

        craftGR.getThreadExecutor().submit(() -> craftGR.getSongProvider().verifyCurrentSong());

        if (!isSessionCurrent(sessionId)) {
            return;
        }

        craftGR.log(Level.INFO, "Connecting to stream server");
        if (retries == 0) {
            ActionBarMessage.CONNECTING.show();
        } else {
            ActionBarMessage.RECONNECTING.show();
        }
        state = State.CONNECTING;

        try {
            CompletableFuture<HttpResponse<InputStream>> localConnection = Http.fetch(Http.standardRequest()
                    .uri(URI.create(ModConfig.get("urlStream")))
                    .build(), HttpResponse.BodyHandlers.ofInputStream());

            if (!isSessionCurrent(sessionId)) {
                localConnection.cancel(true);
                return;
            }

            this.connection = localConnection;

            HttpResponse<InputStream> response = localConnection.get();
            if (!isSessionCurrent(sessionId)) {
                try {
                    response.body().close();
                } catch (IOException ignored) {
                }
                localConnection.cancel(true);
                return;
            }

            if (response.statusCode() != 200) {
                craftGR.log(Level.ERROR, "Stream server responded with status code " + response.statusCode());
                hasError = true;
                ActionBarMessage.CONNECTION_ERROR.show();

                // If this is an auto reconnect attempt, schedule another retry
                if (retries > 0) {
                    scheduleRetry(sessionId);
                }
                return;
            }

            this.audioPlayer = new AudioPlayer(craftGR, response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            craftGR.log(Level.INFO, "Stream connection was interrupted");
            return;
        } catch (ExecutionException e) {
            if (!isSessionCurrent(sessionId)) {
                return;
            }

            stop(false, false, false);
            craftGR.log(Level.ERROR, "Could not connect to stream server: " + ExceptionUtil.getStackTrace(e));
            hasError = true;
            ActionBarMessage.CONNECTION_ERROR.show();
            return;
        }

        if (!isSessionCurrent(sessionId)) {
            if (audioPlayer != null) {
                audioPlayer.close();
            }
            return;
        }

        craftGR.log(Level.INFO, "Audio player starting");
        ActionBarMessage.PLAYBACK_STARTED.show();
        state = State.PLAYING;
        retries = 0;

        try {
            setVolume(ModConfig.<Integer>get("volume") / 100.0);
            audioPlayer.play(fadeIn);
            craftGR.log(Level.INFO, "Audio player stopped");
        } catch (JavaLayerException e) {
            if (!isSessionCurrent(sessionId)) {
                return;
            }

            stop(false, false, false);

            if (!Minecraft.getInstance().isRunning()) {
                return;
            }

            craftGR.log(Level.ERROR, "Audio player error, restarting in " + RETRY_INTERVAL + " seconds: " + ExceptionUtil.getStackTrace(e));

            hasError = true;
            scheduleRetry(sessionId);
        }
    }

    private void scheduleRetry(long sessionId) {
        scheduler.schedule(() -> {
            if (!isSessionCurrent(sessionId)) {
                return;
            }

            hasError = false;
            retries++;
            start(false);
        }, RETRY_INTERVAL, TimeUnit.SECONDS);
    }

    private boolean isSessionCurrent(long sessionId) {
        synchronized (sessionLock) {
            return this.sessionId == sessionId;
        }
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
