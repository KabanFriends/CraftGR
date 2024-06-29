package io.github.kabanfriends.craftgr.song;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.util.ExceptionUtil;
import io.github.kabanfriends.craftgr.util.JsonUtil;
import io.github.kabanfriends.craftgr.util.TitleFixer;
import org.apache.logging.log4j.Level;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketSongProvider extends WebSocketClient implements SongProvider {

    private static final int RETRY_INTERVAL = 10;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Song currentSong;
    private int clientId;

    public WebSocketSongProvider() {
        super(URI.create(ModConfig.get("urlWebSocket")));
    }

    @Override
    public void start() {
        connect();
    }

    @Override
    public void stop() {
        close();
    }

    @Override
    public Song getCurrentSong() {
        return currentSong;
    }

    @Override
    public void verifyCurrentSong() {
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        JsonObject json = new JsonObject();
        json.addProperty("message", "grInitialConnection");
        send(json);
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();

            if (json.has("message")) { // Received a message
                String type = json.get("message").getAsString();

                if (type.equals("welcome")) { // Get client ID
                    clientId = json.get("id").getAsInt();
                    CraftGR.getInstance().log(Level.INFO, "WebSocket client is ready!");

                } else if (type.equals("ping")) { // Response to ping requests
                    JsonObject response = new JsonObject();
                    response.addProperty("message", "pong");
                    response.addProperty("id", clientId);
                    send(response);
                }

            } else if (json.has("songid")) { // Received a song information
                long apiDuration = JsonUtil.getValueWithDefault(json, "duration", 3L, long.class);
                long apiPlayed = JsonUtil.getValueWithDefault(json, "played", 0L, long.class);

                int year = JsonUtil.getValueWithDefault(json, "year", -1, int.class);

                currentSong = new Song(
                        new Song.Metadata(
                                TitleFixer.fixJapaneseString(JsonUtil.getValueWithDefault(json, "title", "", String.class)),
                                TitleFixer.fixJapaneseString(JsonUtil.getValueWithDefault(json, "artist", null, String.class)),
                                TitleFixer.fixJapaneseString(JsonUtil.getValueWithDefault(json, "album", null, String.class)),
                                year == -1 ? null : String.valueOf(year),
                                TitleFixer.fixJapaneseString(JsonUtil.getValueWithDefault(json, "circle", null, String.class)),
                                apiDuration,
                                JsonUtil.getValueWithDefault(json, "albumid", 0, int.class),
                                JsonUtil.getValueWithDefault(json, "albumart", null, String.class),
                                apiPlayed > apiDuration
                        ),
                        apiPlayed
                );

                CraftGR.getInstance().getThreadExecutor().submit(CraftGR.getInstance().getSongInfoOverlay()::onSongChanged);
            }
        } catch (JsonParseException e) {
            CraftGR.getInstance().log(Level.WARN, "Received invalid WebSocket message (" + message + "): " + ExceptionUtil.getStackTrace(e));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        CraftGR.getInstance().log(Level.INFO, "Connection closed by " + (remote ? "remote peer, retrying connection" : "us") + " (code: " + code + ", reason: " + reason + ")");

        if (remote) {
            scheduler.schedule(this::connect, RETRY_INTERVAL, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onError(Exception e) {
        CraftGR.getInstance().log(Level.ERROR, "WebSocket error: " + ExceptionUtil.getStackTrace(e));
    }

    private void send(JsonObject json) {
        send(json.toString());
    }
}
