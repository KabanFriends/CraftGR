package io.github.kabanfriends.craftgr.handler;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.AudioPlayer;
import io.github.kabanfriends.craftgr.config.GRConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Level;

import java.io.InputStream;

public class AudioPlayerHandler {

    private static AudioPlayerHandler INSTANCE;
    private static boolean INIT_FAILED;

    public AudioPlayer player;
    public boolean playing = false;

    public AudioPlayerHandler(AudioPlayer audioPlayer) {
        INSTANCE = this;
        this.player = audioPlayer;
    }

    public static void initialize() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(GRConfig.getConfig().streamURL).build();

            Response response = client.newCall(request).execute();
            InputStream stream = response.body().byteStream();

            AudioPlayer audioPlayer = new AudioPlayer(stream);

            if (INSTANCE == null) {
                new AudioPlayerHandler(audioPlayer);
            }else {
                getInstance().player = audioPlayer;
            }
        }catch (Exception err) {
            CraftGR.log(Level.ERROR, "Error when initializing the audio player:");
            err.printStackTrace();

            INIT_FAILED = true;
        }
    }

    public void startPlayback() {
        this.playing = true;

        CraftGR.EXECUTOR.submit(() -> {
            while (true) {
                if (!INIT_FAILED) {
                    try {
                        CraftGR.log(Level.INFO, "Starting playback...");
                        this.player.play();
                    } catch (Exception err) {
                        CraftGR.log(Level.ERROR, "Error during audio playback! Restarting in 5 seconds...");
                        err.printStackTrace();

                        try {
                            Thread.sleep(5L * 1000L);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                        initialize();
                    }
                }else {
                    CraftGR.log(Level.ERROR, "Cannot start audio playback due to an initialization failure! Fix your config and restart the game.");
                    return;
                }
            }
        });
    }

    public static AudioPlayerHandler getInstance() {
        return INSTANCE;
    }
}
