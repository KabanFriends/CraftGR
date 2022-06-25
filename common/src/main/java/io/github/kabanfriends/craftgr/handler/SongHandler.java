package io.github.kabanfriends.craftgr.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SongHandler {

    private static final SongHandler INSTANCE = new SongHandler();

    private static InitState initState = InitState.NOT_INITIALIZED;

    private boolean destroyed;
    private Song song;
    private long songStart;
    private long songEnd;

    public void initialize() {
        CraftGR.EXECUTOR.submit(() -> {
            ProcessResult result = prepareNewSong();
            if (result == ProcessResult.ERROR) {
                initState = InitState.FAIL;
            } else {
                initState = InitState.SUCCESS;
            }

            this.start();
        });
    }

    private ProcessResult prepareNewSong() {
        Song song;
        try {
            song = getSongFromJson(GRConfig.getConfig().url.infoJsonURL);
        } catch (Exception e) {
            CraftGR.log(Level.ERROR, "Error while fetching song information!");
            e.printStackTrace();

            return ProcessResult.ERROR;
        }
        this.song = song;

        SongInfoOverlay.getInstance().createAlbumArtTexture(song);
        return ProcessResult.SUCCESS;
    }

    private void start() {
        while (initState == InitState.SUCCESS) {
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

        CraftGR.log(Level.ERROR, "Error in displaying song information! Fetching again in 30 seconds...");
        this.song = null;
        try {
            Thread.sleep(30 * 1000L);
        } catch (InterruptedException e) { }

        this.initialize();
    }

    public void destroy() {
        destroyed = true;
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
                TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "TITLE", null, String.class)),
                TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ARTIST", null, String.class)),
                TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "ALBUM", null, String.class)),
                getValueWithDefault(songInfo, "YEAR", null, String.class),
                TitleFixer.fixJapaneseString(getValueWithDefault(songInfo, "CIRCLE", null, String.class)),
                getValueWithDefault(songTimes, "SONGSTART", 0L, Long.class),
                getValueWithDefault(songTimes, "SONGEND", System.currentTimeMillis() / 1000L + 4L, Long.class),
                getValueWithDefault(songData, "ALBUMID", 0, Integer.class),
                getValueWithDefault(misc, "ALBUMART", "", String.class),
                getValueWithDefault(misc, "OFFSETTIME", 0L, Long.class)
        );

        long played = song.offsetTime - song.songStart;
        long duration = song.songEnd - song.songStart;
        this.songStart = System.currentTimeMillis() / 1000L - played;
        this.songEnd = this.songStart + duration;

        if (song.offsetTime >= song.songEnd) {
            song.setIntermission(true);
        }

        return song;
    }

    private <T> T getValueWithDefault(JsonObject json, String key, T defaultValue, Class<T> clazz) {
        JsonElement element = json.get(key);
        if (element.isJsonPrimitive()) {
            JsonPrimitive value = element.getAsJsonPrimitive();
            if (value.isNumber()) {
                if (clazz == Byte.class) {
                    return (T) Byte.valueOf(value.getAsNumber().byteValue());
                } else if (clazz == Double.class) {
                    return (T) Double.valueOf(value.getAsNumber().doubleValue());
                } else if (clazz == Float.class) {
                    return (T) Float.valueOf(value.getAsNumber().floatValue());
                } else if (clazz == Long.class) {
                    return (T) Long.valueOf(value.getAsNumber().longValue());
                } else if (clazz == Integer.class) {
                    return (T) Integer.valueOf(value.getAsNumber().intValue());
                } else if (clazz == Short.class) {
                    return (T) Short.valueOf(value.getAsNumber().shortValue());
                }
            } else if (value.isString()) {
                return (T) value.getAsString();
            }
        }
        return defaultValue;
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
}
