package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.AudioPlayer;
import io.github.kabanfriends.craftgr.audio.AudioPlayer.ProcessResult;
import io.github.kabanfriends.craftgr.config.GRConfig;
import javazoom.jl.decoder.BitstreamException;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Level;

import java.io.InputStream;

public class AudioPlayerHandler {

    private static AudioPlayerHandler INSTANCE;
    private static int INIT_STATE;

    public AudioPlayer player;
    public Response response;
    public boolean playing = false;

    public AudioPlayerHandler() {
        INSTANCE = this;
        INIT_STATE = 0;
        initialize();
    }

    public void startPlayback() {
        this.playing = true;

        CraftGR.EXECUTOR.submit(() -> {
            while (true) {
                if (INIT_STATE == 1) {
                    ProcessResult result = this.player.play();

                    if (result == ProcessResult.AL_ERROR || result == ProcessResult.EXCEPTION) {
                        CraftGR.log(Level.ERROR, "Error during audio playback! Restarting in 5 seconds...");

                        try {
                            Thread.sleep(5L * 1000L);
                        } catch (InterruptedException e) {}

                        CraftGR.log(Level.ERROR, "Restarting audio player...");
                    } else if (result == ProcessResult.STOP) {
                        CraftGR.log(Level.INFO, "Playback has stopped!");
                        return;
                    }

                    this.response.close();
                    initialize();
                }else {
                    CraftGR.log(Level.ERROR, "Cannot start audio playback due to an initialization failure! Fix your config and restart the game.");
                    return;
                }
            }
        });
    }

    public void initialize() {
        try {
            Request request = new Request.Builder().url(GRConfig.getConfig().streamURL).build();

            this.response = CraftGR.HTTP_CLIENT.newCall(request).execute();
            InputStream stream = response.body().byteStream();

            AudioPlayer audioPlayer = new AudioPlayer(stream);

            if (INSTANCE != null) {
                audioPlayer.setVolume(1.0f);
            }

            this.player = audioPlayer;

            INIT_STATE = 1;
        }catch (Exception err) {
            CraftGR.log(Level.ERROR, "Error when initializing the audio player:");
            err.printStackTrace();

            INIT_STATE = -1;
        }
    }

    public static boolean isInitialized() {
        return INIT_STATE == 1;
    }

    public static AudioPlayerHandler getInstance() {
        return INSTANCE;
    }
}
