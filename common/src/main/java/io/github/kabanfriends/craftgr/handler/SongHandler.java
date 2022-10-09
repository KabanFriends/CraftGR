package io.github.kabanfriends.craftgr.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SongHandler {

    private static final SongHandler INSTANCE = new SongHandler();

    private static HandlerState state = HandlerState.NOT_INITIALIZED;

    private Song song;
    private long songStart;
    private long songEnd;

    public void initialize() {
        CraftGR.EXECUTOR.submit(() -> {
            state = HandlerState.INITIALIZING;

            ProcessResult result = prepareNewSong();
            if (result == ProcessResult.ERROR) {
                state = HandlerState.FAIL;
            } else {
                state = HandlerState.ACTIVE;
            }

            this.start();
        });
    }

    private ProcessResult prepareNewSong() {
        Song song;
        try {
            song = getSongFromJson(GRConfig.getValue("urlInfoJson"));
        } catch (Exception e) {
            CraftGR.log(Level.ERROR, "Error while fetching song information!");
            e.printStackTrace();

            return ProcessResult.ERROR;
        }
        this.song = song;

        SongInfoOverlay overlay = SongInfoOverlay.getInstance();
        overlay.createAlbumArtTexture(song);

        if (song.isIntermission()) {
            overlay.setIntermissionSongTitle();
        } else {
            overlay.setSongTitle(song.title);
        }

        return ProcessResult.SUCCESS;
    }

    private void start() {
        while (state == HandlerState.ACTIVE) {
            if (this.song != null) {
                if (System.currentTimeMillis() / 1000L > this.getSongEnd()) {
                    ProcessResult result = prepareNewSong();
                    if (result == ProcessResult.ERROR) {
                        break;
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) { }
        }

        CraftGR.log(Level.ERROR, "Error on preparing the song information! Fetching again in 30 seconds...");
        this.song = null;
        try {
            Thread.sleep(30 * 1000L);
        } catch (InterruptedException e) { }

        this.initialize();
    }

    private Song getSongFromJson(String url) throws IOException {
        HttpGet get = HttpUtil.get(url);
        ResponseHolder response = new ResponseHolder(CraftGR.getHttpClient().execute(get));

        InputStream stream = response.getResponse().getEntity().getContent();

        BufferedReader r = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }

        response.close();

        JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
        JsonObject songInfo = json.getAsJsonObject("SONGINFO");
        JsonObject songTimes = json.getAsJsonObject("SONGTIMES");
        JsonObject songData = json.getAsJsonObject("SONGDATA");
        JsonObject misc = json.getAsJsonObject("MISC");

        Song song = new Song(
                TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "TITLE", "", String.class)),
                TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ARTIST", null, String.class)),
                TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ALBUM", null, String.class)),
                getValueWithDefault(songInfo, "YEAR", null, String.class),
                TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "CIRCLE", null, String.class)),
                getValueWithDefault(songTimes, "SONGSTART", 0L, long.class),
                getValueWithDefault(songTimes, "SONGEND", System.currentTimeMillis() / 1000L + 4L, long.class),
                getValueWithDefault(songData, "ALBUMID", 0, int.class),
                getValueWithDefault(misc, "ALBUMART", null, String.class),
                getValueWithDefault(misc, "OFFSETTIME", 0L, long.class)
        );

        long played = song.offsetTime - song.songStart;
        long duration = song.songEnd - song.songStart;
        this.songStart = System.currentTimeMillis() / 1000L - played;
        this.songEnd = this.songStart + duration;

        if (song.offsetTime >= song.songEnd) {
            song.setIntermission(true);
            song.albumArt = "";
        }

        return song;
    }

    public Song getCurrentSong() {
        return song;
    }

    public long getSongStart() {
        return songStart;
    }

    public long getSongEnd() {
        return songEnd;
    }

    public static SongHandler getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValueWithDefault(JsonObject json, String key, T defaultValue, Class<T> clazz) {
        JsonElement element = json.get(key);
        if (element.isJsonPrimitive()) {
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
}
