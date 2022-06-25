package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.AudioPlayer;
import io.github.kabanfriends.craftgr.util.HttpUtil;
import io.github.kabanfriends.craftgr.util.ProcessResult;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.util.InitState;
import io.github.kabanfriends.craftgr.util.ResponseHolder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.logging.log4j.Level;

import java.io.InputStream;

public class AudioPlayerHandler {

    private static final AudioPlayerHandler INSTANCE = new AudioPlayerHandler();

    private InitState initState = InitState.NOT_INITIALIZED;
    private AudioPlayer player;
    private ResponseHolder response;
    private boolean playing = false;

    public void startPlayback() {
        this.playing = true;

        CraftGR.EXECUTOR.submit(() -> {
            while (true) {
                if (initState == InitState.SUCCESS) {
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
                    initialize();
                } else {
                    CraftGR.log(Level.ERROR, "Cannot start audio playback due to an initialization failure!");
                    this.stopPlayback();
                    return;
                }
            }
        });
    }

    public void stopPlayback() {
        stopPlayback(false);
    }

    public void stopPlayback(boolean reloading) {
        CraftGR.log(Level.INFO, "Stopping audio playback...");
        if (player != null) player.stop();
        if (response != null && !response.isClosed()) response.close();

        this.playing = false;
        this.player = null;

        if (reloading) this.initState = InitState.RELOADING;
        else this.initState = InitState.NOT_INITIALIZED;
    }

    public void initialize() {
        CraftGR.log(Level.INFO, "Initializing the audio player...");
        initState = InitState.INITIALIZING;

        try {
            if (response != null) response.close();

            HttpGet get = HttpUtil.get(GRConfig.getConfig().url.streamURL);
            ResponseHolder response = new ResponseHolder(CraftGR.getHttpClient().execute(get));
            this.response = response;

            InputStream stream = response.getResponse().getEntity().getContent();

            AudioPlayer audioPlayer = new AudioPlayer(stream);

            if (player != null) player.stop();
            player = audioPlayer;

            initState = InitState.SUCCESS;
            CraftGR.log(Level.INFO, "Audio player is ready!");
        } catch (Exception err) {
            CraftGR.log(Level.ERROR, "Error when initializing the audio player:");
            err.printStackTrace();

            initState = InitState.FAIL;
        }
    }

    public InitState getInitState() {
        return initState;
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
