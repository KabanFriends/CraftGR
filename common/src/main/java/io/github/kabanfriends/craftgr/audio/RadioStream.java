package io.github.kabanfriends.craftgr.audio;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.util.*;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.apache.http.client.methods.*;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RadioStream {

    private static final int RETRY_INTERVAL = 5;

    private final CraftGR craftGR;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private State state;
    private AudioPlayer player;
    private CloseableHttpResponse response;
    private boolean audioFading;
    private boolean hasError;
    private long audioFadeStart;

    public RadioStream(CraftGR craftGR) {
        this.craftGR = craftGR;

        this.hasError = false;
        this.state = State.AWAIT_LOADING;
        this.player = new AudioPlayer(craftGR);
        this.player.setBaseVolume(0.0f);
    }

    public void tick() {
        if (getState() == State.PLAYING) {
            //Audio fade in
            if (audioFadeStart == 0L) {
                audioFading = true;
                audioFadeStart = Util.getMillis();
            }

            if (audioFading) {
                float value = (float) (Util.getMillis() - audioFadeStart) / 2000.0F;
                getAudioPlayer().setBaseVolume(Mth.clamp(value, 0.0f, 1.0f));

                if (value >= 1.0f) {
                    audioFading = false;
                }
            }
        }
    }

    public void start() {
        state = State.CONNECTING;
        craftGR.getThreadExecutor().submit(this::handlePlayback);
    }

    private void handlePlayback() {
        try {
            craftGR.getThreadExecutor().submit(() -> craftGR.getSongProvider().verifyCurrentSong());

            MessageUtil.sendConnectingMessage();
            connect();

            state = State.PLAYING;
            MessageUtil.sendAudioStartedMessage();
            this.player.play();

            MessageUtil.sendAudioStoppedMessage();
            craftGR.log(Level.INFO, "Audio playback has been stopped!");

        } catch (ConnectionException e) {
            craftGR.log(Level.ERROR, "Error while connecting to the audio stream: " + ExceptionUtil.getStackTrace(e));
            state = State.STOPPED;
            hasError = true;
            MessageUtil.sendConnectionErrorMessage();

        } catch (AudioPlayerException e) {
            craftGR.log(Level.ERROR, "Error during audio playback, reconnecting: " + ExceptionUtil.getStackTrace(e));
            scheduler.schedule(() -> {
                MessageUtil.sendReconnectingMessage();
                handlePlayback();
            }, RETRY_INTERVAL, TimeUnit.SECONDS);
        }
    }

    public void disconnect() {
        disconnect(State.STOPPED);
    }

    public void disconnect(@Nullable State nextState) {
        if (player != null) {
            player.stop();
        }
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                craftGR.log(Level.ERROR, "Error while closing the HTTP response: " + ExceptionUtil.getStackTrace(e));
            }
        }

        player = new AudioPlayer(craftGR);
        hasError = false;
        if (nextState != null) {
            state = nextState;
        }
    }

    private void connect() throws ConnectionException {
        // Close any open streams
        disconnect(null);

        state = State.CONNECTING;
        craftGR.log(Level.INFO, "Connecting to the audio stream");

        try {
            if (response != null) response.close();

            HttpGet get = HttpUtil.get(ModConfig.get("urlStream"));
            CloseableHttpResponse response = craftGR.getHttpClient().execute(get);
            this.response = response;

            InputStream stream = response.getEntity().getContent();

            player.setStream(stream);
        } catch (Exception e) {
            throw new ConnectionException(e);
        }
    }

    public void toggle() {
        if (state == State.PLAYING) {
            disconnect();
        } else if (state != State.CONNECTING && state != State.AWAIT_LOADING) {
            start();
        }
    }

    public State getState() {
        return state;
    }

    public AudioPlayer getAudioPlayer() {
        return player;
    }

    public boolean hasError() {
        return hasError;
    }

    public enum State {
        STOPPED,
        CONNECTING,
        PLAYING,
        AWAIT_LOADING
    }
}
