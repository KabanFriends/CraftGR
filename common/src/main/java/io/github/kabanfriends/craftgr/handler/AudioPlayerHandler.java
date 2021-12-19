package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.AudioPlayer;
import io.github.kabanfriends.craftgr.audio.AudioPlayer.ProcessResult;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.util.InitState;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Level;

import java.io.InputStream;

public class AudioPlayerHandler {

    private static final AudioPlayerHandler INSTANCE = new AudioPlayerHandler();

    private InitState initState = InitState.NOT_INITIALIZED;
    private AudioPlayer player;
    private Response response;
    private boolean playing = false;

    public void startPlayback() {
        this.playing = true;

        CraftGR.EXECUTOR.submit(() -> {
            while (true) {
                if (initState == InitState.SUCCESS) {
                    System.out.println("Starting");
                    ProcessResult result = this.player.play();

                    if (result == ProcessResult.AL_ERROR || result == ProcessResult.EXCEPTION) {
                        CraftGR.log(Level.ERROR, "Error during audio playback! Restarting in 5 seconds...");

                        try {
                            Thread.sleep(5L * 1000L);
                        } catch (InterruptedException e) {
                        }
                    } else if (result == ProcessResult.STOP) {
                        CraftGR.log(Level.INFO, "Audio playback has stopped!");
                        return;
                    }

                    response.close();
                    initialize();
                } else {
                    CraftGR.log(Level.ERROR, "Cannot start audio playback due to an initialization failure! Fix your config and restart the game.");
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
        if (response != null) response.close();

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

            Request request = new Request.Builder().url(GRConfig.getConfig().url.streamURL).build();

            response = CraftGR.getHttpClient().newCall(request).execute();
            InputStream stream = response.body().byteStream();

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
