package io.github.kabanfriends.craftgr.song;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import io.github.kabanfriends.craftgr.util.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        CraftGR.getThreadExecutor().submit(() -> verifyCurrentSong(true));
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
        } catch (IOException e) {
            if (shouldRetry) {
                // Stop any scheduled tasks
                cancelIfNotNull(songEndTask);
                cancelIfNotNull(songVerifyTask);

                scheduler.schedule(() -> verifyCurrentSong(true), RETRY_INTERVAL, TimeUnit.SECONDS);
            }

            CraftGR.log(Level.ERROR, "Error while fetching the song information" + (shouldRetry ? ", retrying" : "") + ": " + ExceptionUtil.getStackTrace(e));
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

        SongInfoOverlay.getInstance().updateSongTitle();
        SongInfoOverlay.getInstance().updateAlbumArtTexture();
    }

    private Song getSongFromAPI() throws IOException {
        HttpGet get = HttpUtil.get(GRConfig.getValue("urlInfoJson"));

        try (
                ResponseHolder response = new ResponseHolder(CraftGR.getHttpClient().execute(get));
                InputStream stream = response.getResponse().getEntity().getContent();
                BufferedReader r = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
        ) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }

            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            JsonObject songInfo = json.getAsJsonObject("SONGINFO");
            JsonObject songTimes = json.getAsJsonObject("SONGTIMES");
            JsonObject songData = json.getAsJsonObject("SONGDATA");
            JsonObject misc = json.getAsJsonObject("MISC");

            long apiDuration = getValueWithDefault(songTimes, "DURATION", 3L, long.class);
            long apiPlayed = getValueWithDefault(songTimes, "PLAYED", 0L, long.class);

            return new Song(
                    new Song.Metadata(
                            TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "TITLE", "", String.class)),
                            TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ARTIST", null, String.class)),
                            TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ALBUM", null, String.class)),
                            getValueWithDefault(songInfo, "YEAR", null, String.class),
                            TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "CIRCLE", null, String.class)),
                            apiDuration,
                            getValueWithDefault(songData, "ALBUMID", 0, int.class),
                            getValueWithDefault(misc, "ALBUMART", null, String.class),
                            apiPlayed > apiDuration
                    ),
                    apiPlayed
            );
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValueWithDefault(JsonObject json, String key, T defaultValue, Class<T> clazz) {
        JsonElement element = json.get(key);
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive value = element.getAsJsonPrimitive();
            if (value.isNumber()) {
                Number number = value.getAsNumber();
                if (clazz == byte.class) return (T) Byte.valueOf(number.byteValue());
                if (clazz == double.class) return (T) Double.valueOf(number.doubleValue());
                if (clazz == float.class) return (T) Float.valueOf(number.floatValue());
                if (clazz == long.class) return (T) Long.valueOf(number.longValue());
                if (clazz == int.class) return (T) Integer.valueOf(number.intValue());
                if (clazz == short.class) return (T) Short.valueOf(number.shortValue());
            } else if (value.isString()) {
                return (T) value.getAsString();
            }
            return defaultValue;
        }
        return defaultValue;
    }

    private static void cancelIfNotNull(ScheduledFuture<?> task) {
        if (task == null) {
            return;
        }
        task.cancel(false);
    }
}
