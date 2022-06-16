package io.github.kabanfriends.craftgr.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.Song;
import io.github.kabanfriends.craftgr.util.InitState;
import io.github.kabanfriends.craftgr.util.ProcessResult;
import io.github.kabanfriends.craftgr.util.TitleFixer;
import okhttp3.Request;
import okhttp3.Response;
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
        Request request = new Request.Builder().url(url).build();

        Response response = CraftGR.getHttpClient().newCall(request).execute();
        InputStream stream = response.body().byteStream();

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

        int albumId = 0;
        JsonElement albumIdElement = songData.get("ALBUMID");
        if (!albumIdElement.isJsonNull()) {
            albumId = albumIdElement.getAsInt();
        }

        Song song = new Song(
                TitleFixer.fixJapaneseString(songInfo.get("TITLE").getAsString()),
                TitleFixer.fixJapaneseString(songInfo.get("ARTIST").getAsString()),
                TitleFixer.fixJapaneseString(songInfo.get("ALBUM").getAsString()),
                songInfo.get("YEAR").getAsString(),
                TitleFixer.fixJapaneseString(songInfo.get("CIRCLE").getAsString()),
                songTimes.get("SONGSTART").getAsLong(),
                songTimes.get("SONGEND").getAsLong(),
                albumId,
                misc.get("ALBUMART").getAsString(),
                misc.get("OFFSETTIME").getAsLong()
        );

        long played = song.offsetTime - song.songStart;
        long duration = song.songEnd - song.songStart;
        this.songStart = System.currentTimeMillis() / 1000L - played;
        this.songEnd = this.songStart + duration;

        if (songData.get("SONGID").getAsInt() == 0 || song.offsetTime >= song.songEnd) {
            song.setIntermission(true);
        }

        if (song.isIntermission()) {
            song.albumArt = "";
            song.title = "";

            this.songEnd = System.currentTimeMillis() / 1000L + 4L;
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
}
