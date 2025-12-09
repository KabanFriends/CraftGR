package io.github.kabanfriends.craftgr.song;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.util.ExceptionUtil;
import io.github.kabanfriends.craftgr.util.JsonUtil;
import io.github.kabanfriends.craftgr.util.TitleFixer;
import io.github.kabanfriends.craftgr.util.Http;
import org.apache.logging.log4j.Level;

import java.net.URI;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketSongProvider implements SongProvider {

    private static final int RETRY_INTERVAL = 10;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private WebSocket client;
    private Song currentSong;
    private int clientId;

    public WebSocketSongProvider() {
    }

    @Override
    public void start() {
        WebSocket.Builder builder = Http.standardWebSocket();
        WebSocket.Listener listener = new WebSocket.Listener() {

            @Override
            public void onOpen(WebSocket client) {
                CraftGR.getInstance().log(Level.INFO, "WebSocket client has connected");
                JsonObject json = new JsonObject();
                json.addProperty("message", "grInitialConnection");

                // Request the starting message, and send the initial connection
                client.request(1);
                send(client, json);
            }

            @Override
            public CompletionStage<?> onText(WebSocket client, CharSequence data, boolean last) {
                // Request the next message
                client.request(1);

                String message = data.toString();
                try {
                    JsonObject json = JsonParser.parseString(message).getAsJsonObject();

                    if (json.has("message")) { // Received a message
                        String type = json.get("message").getAsString();

                        if (type.equals("welcome")) { // Get client ID
                            clientId = json.get("id").getAsInt();
                            CraftGR.getInstance().log(Level.INFO, "Client ID received: " + clientId);

                        } else if (type.equals("ping")) { // Response to ping requests
                            JsonObject response = new JsonObject();
                            response.addProperty("message", "pong");
                            response.addProperty("id", clientId);
                            send(client, response);
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

                        CraftGR.getInstance().getSongInfoOverlay().onSongChanged();
                    } else {
                        CraftGR.getInstance().log(Level.WARN, "Received unknown WebSocket message (" + message + "): " + json.toString());
                    }
                } catch (JsonParseException e) {
                    CraftGR.getInstance().log(Level.WARN, "Received invalid WebSocket message (" + message + "): " + ExceptionUtil.getStackTrace(e));
                } catch (Exception e) {
                    CraftGR.getInstance().log(Level.ERROR, "Failed to process WebSocket message: " + ExceptionUtil.getStackTrace(e));
                }
                return null;
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                if (statusCode == WebSocket.NORMAL_CLOSURE) {
                    CraftGR.getInstance().log(Level.INFO, "WebSocket client has disconnected");
                } else {
                    CraftGR.getInstance().log(Level.INFO, "Connection closed unexpectedly, retrying connection (code: " + statusCode + ", reason: " + reason + ")");
                    scheduler.schedule(WebSocketSongProvider.this::start, RETRY_INTERVAL, TimeUnit.SECONDS);
                }
                return null;
            }

            @Override
            public void onError(WebSocket client, Throwable error) {
                CraftGR.getInstance().log(Level.ERROR, "WebSocket error: " + ExceptionUtil.getStackTrace(error));
            }
        };

        this.client = builder.buildAsync(URI.create(ModConfig.get("urlWebSocket")), listener).join();
    }

    @Override
    public void stop() {
        client.sendClose(WebSocket.NORMAL_CLOSURE, "Client closing")
                .thenRun(() -> CraftGR.getInstance().log(Level.INFO, "WebSocket client has disconnected"));
    }

    @Override
    public Song getCurrentSong() {
        return currentSong;
    }

    @Override
    public void verifyCurrentSong() {
    }

    private void send(WebSocket client, JsonObject json) {
        client.sendText(json.toString(), true);
    }
}
