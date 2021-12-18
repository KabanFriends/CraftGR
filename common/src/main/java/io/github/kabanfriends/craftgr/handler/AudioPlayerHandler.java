package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.AudioPlayer;
import io.github.kabanfriends.craftgr.audio.AudioPlayer.ProcessResult;
import io.github.kabanfriends.craftgr.config.GRConfig;
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
                    ProcessResult result = this.player.play();

                    if (result == ProcessResult.AL_ERROR || result == ProcessResult.EXCEPTION) {
                        CraftGR.log(Level.ERROR, "Error during audio playback! Restarting in 5 seconds...");

                        try {
                            Thread.sleep(5L * 1000L);
                        } catch (InterruptedException e) {
                        }

                        CraftGR.log(Level.ERROR, "Restarting audio player...");
                    } else if (result == ProcessResult.STOP) {
                        CraftGR.log(Level.INFO, "Audio playback has stopped!");
                        return;
                    }

                    response.close();
                    initialize();
                } else {
                    CraftGR.log(Level.ERROR, "Cannot start audio playback due to an initialization failure! Fix your config and restart the game.");
                    return;
                }
            }
        });
    }

    public void stopPlayback() {
        CraftGR.log(Level.INFO, "Stopping audio playback...");
        this.player.stop();
        this.response.close();
        response.close();
    }

    public void initialize() {
        try {
            Request request = new Request.Builder().url(GRConfig.getConfig().url.streamURL).build();

            response = CraftGR.getHttpClient().newCall(request).execute();
            InputStream stream = response.body().byteStream();

            AudioPlayer audioPlayer = new AudioPlayer(stream);

            if (initState == InitState.SUCCESS) {
                audioPlayer.setVolume(1.0f);
            }

            this.player = audioPlayer;

            initState = InitState.SUCCESS;
        } catch (Exception err) {
            CraftGR.log(Level.ERROR, "Error when initializing the audio player:");
            err.printStackTrace();

            initState = InitState.FAIL;
        }
    }

    public boolean isInitialized() {
        return initState == InitState.SUCCESS;
    }

    public boolean isPlaying() {
        return playing;
    }

    public AudioPlayer getAudioPlayer() {
        return player;
    }

    public static AudioPlayerHandler getInstance() {
        return INSTANCE;
    }

    private enum InitState {
        NOT_INITIALIZED,
        SUCCESS,
        FAIL
    }
}
