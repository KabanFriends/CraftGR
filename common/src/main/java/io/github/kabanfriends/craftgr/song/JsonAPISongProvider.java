package io.github.kabanfriends.craftgr.song;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.util.*;
import io.github.kabanfriends.craftgr.util.Http;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class JsonAPISongProvider implements SongProvider {

    private static final int VERIFY_INTERVAL = 30;
    private static final int RETRY_INTERVAL = 10;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Song currentSong;

    private ScheduledFuture<?> songEndTask;
    private ScheduledFuture<?> songVerifyTask;

    @Override
    public void start() {
        CraftGR.getInstance().getThreadExecutor().submit(() -> verifyCurrentSong(true));
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
    }

    @Override
    public Song getCurrentSong() {
        return currentSong;
    }

    @Override
    public void verifyCurrentSong() {
        verifyCurrentSong(false);
    }

    private void verifyCurrentSong(boolean shouldRetry) {
        try {
            Song song = getSongFromAPI();

            if (currentSong == null || !song.metadata().equals(currentSong.metadata())) {
                startNewSong(song);
            }
        } catch (Exception e) {
            if (shouldRetry) {
                // Stop any scheduled tasks
                cancelIfNotNull(songEndTask);
                cancelIfNotNull(songVerifyTask);

                scheduler.schedule(() -> verifyCurrentSong(true), RETRY_INTERVAL, TimeUnit.SECONDS);
            }

            CraftGR.getInstance().log(Level.ERROR, "Error while fetching the song information" + (shouldRetry ? ", retrying" : "") + ": " + ExceptionUtil.getStackTrace(e));
        }
    }

    private void startNewSong(Song song) {
        this.currentSong = song;

        // Cancel old tasks
        cancelIfNotNull(songEndTask);
        cancelIfNotNull(songVerifyTask);

        // Update the song again when the current song ends
        long remaining = song.metadata().duration() - song.getAPIPlayedTime();
        songEndTask = scheduler.schedule(() -> verifyCurrentSong(true), remaining, TimeUnit.SECONDS);
        // Verify the current song every once in a while
        songVerifyTask = scheduler.scheduleWithFixedDelay(() -> verifyCurrentSong(false), VERIFY_INTERVAL, VERIFY_INTERVAL, TimeUnit.SECONDS);

        CraftGR.getInstance().getSongInfoOverlay().onSongChanged();
    }

    private Song getSongFromAPI() throws IOException {
        JsonElement response = Http.fetchJson(Http.standardRequest()
                .uri(URI.create(ModConfig.get("urlInfoJson")))
                .build())
                .thenApply(HttpResponse::body)
                .join();
        JsonObject json = response.getAsJsonObject();
        JsonObject songInfo = json.getAsJsonObject("SONGINFO");
        JsonObject songTimes = json.getAsJsonObject("SONGTIMES");
        JsonObject songData = json.getAsJsonObject("SONGDATA");
        JsonObject misc = json.getAsJsonObject("MISC");

        long apiDuration = JsonUtil.getValueWithDefault(songTimes, "DURATION", 3L, long.class);
        long apiPlayed = JsonUtil.getValueWithDefault(songTimes, "PLAYED", 0L, long.class);

        String albumArt = JsonUtil.getValueWithDefault(misc, "ALBUMART", null, String.class);

        return new Song(
                new Song.Metadata(
                        TitleFixer.fixJapaneseString(JsonUtil.getValueWithDefault(songInfo, "TITLE", "", String.class)),
                        TitleFixer.fixJapaneseString(JsonUtil.getValueWithDefault(songInfo, "ARTIST", null, String.class)),
                        TitleFixer.fixJapaneseString(JsonUtil.getValueWithDefault(songInfo, "ALBUM", null, String.class)),
                        JsonUtil.getValueWithDefault(songInfo, "YEAR", null, String.class),
                        TitleFixer.fixJapaneseString(JsonUtil.getValueWithDefault(songInfo, "CIRCLE", null, String.class)),
                        apiDuration,
                        JsonUtil.getValueWithDefault(songData, "ALBUMID", 0, int.class),
                        albumArt == null || albumArt.isEmpty() ? null : ModConfig.get("urlAlbumArt") + albumArt,
                        apiPlayed > apiDuration
                ),
                apiPlayed
        );
    }

    private static void cancelIfNotNull(ScheduledFuture<?> task) {
        if (task == null) {
            return;
        }
        task.cancel(false);
    }
}
