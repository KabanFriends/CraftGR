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

        SongInfoOverlay.getInstance().updateSongTitle();
        SongInfoOverlay.getInstance().updateAlbumArtTexture();
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
        System.out.println("SONG VERIFY");
        try {
            Song song = getSongFromAPI();

            if (!song.equals(currentSong)) {
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
        System.out.println("NEW SONG!");
        this.currentSong = song;

        // Separate handling from current task thread
        CraftGR.getThreadExecutor().submit(() -> {
            // Cancel old tasks
            cancelIfNotNull(songEndTask);
            cancelIfNotNull(songVerifyTask);

            // Update the song again when the current song ends
            long remaining = song.songEnd() - System.currentTimeMillis() / 1000L;
            songEndTask = scheduler.schedule(() -> verifyCurrentSong(true), remaining, TimeUnit.SECONDS);
            // Verify the current song every once in a while
            songVerifyTask = scheduler.scheduleWithFixedDelay(() -> verifyCurrentSong(false), VERIFY_INTERVAL, VERIFY_INTERVAL, TimeUnit.SECONDS);

            SongInfoOverlay.getInstance().updateSongTitle();
            SongInfoOverlay.getInstance().updateAlbumArtTexture();
        });
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

            // Get times from API (depends on remote timestamp)
            long apiSongEnd = getValueWithDefault(songTimes, "SONGEND", System.currentTimeMillis() / 1000L + 3L, long.class);
            long apiSongStart = getValueWithDefault(songTimes, "SONGSTART", 0L, long.class);
            long apiOffsetTime = getValueWithDefault(misc, "OFFSETTIME", 0L, long.class);

            // Make song times independent of remote timestamp
            long played = apiOffsetTime - apiSongStart;
            long duration = apiSongEnd - apiSongStart;

            long songStart = System.currentTimeMillis() / 1000L - played;
            long songEnd = songStart + duration;

            Song song = new Song(
                    TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "TITLE", "", String.class)),
                    TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ARTIST", null, String.class)),
                    TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ALBUM", null, String.class)),
                    getValueWithDefault(songInfo, "YEAR", null, String.class),
                    TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "CIRCLE", null, String.class)),
                    songStart,
                    songEnd,
                    getValueWithDefault(songData, "ALBUMID", 0, int.class),
                    getValueWithDefault(misc, "ALBUMART", null, String.class),
                    apiOffsetTime > apiSongEnd
            );

            return new Song(
                    TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "TITLE", "", String.class)),
                    TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ARTIST", null, String.class)),
                    TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ALBUM", null, String.class)),
                    getValueWithDefault(songInfo, "YEAR", null, String.class),
                    TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "CIRCLE", null, String.class)),
                    songStart,
                    songEnd,
                    getValueWithDefault(songData, "ALBUMID", 0, int.class),
                    getValueWithDefault(misc, "ALBUMART", null, String.class),
                    apiOffsetTime > apiSongEnd
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
        task.cancel(true);
    }
}
