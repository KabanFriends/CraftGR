package io.github.kabanfriends.craftgr.song;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.util.*;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketSongProvider implements SongProvider {

    private static final Logger LOGGER = Logs.logger();

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
                LOGGER.info("WebSocket client has connected");
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
                        LOGGER.warn("Received unknown WebSocket message '{}': {}", message, json);
                    }
                } catch (JsonParseException e) {
                    LOGGER.warn("Received invalid WebSocket message '{}'", message, e);
                } catch (Exception e) {
                    LOGGER.error("Failed to process WebSocket message", e);
                }
                return null;
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                if (statusCode == WebSocket.NORMAL_CLOSURE) {
                    LOGGER.info("WebSocket client has disconnected by remote");
                } else {
                    LOGGER.info("Connection closed unexpectedly, retrying connection (code: {}, reason: {})", statusCode, reason);
                    scheduler.schedule(WebSocketSongProvider.this::start, RETRY_INTERVAL, TimeUnit.SECONDS);
                }
                return null;
            }

            @Override
            public void onError(WebSocket client, Throwable error) {
                LOGGER.error("WebSocket error", error);
            }
        };

        builder.buildAsync(URI.create(ModConfig.get("urlWebSocket")), listener)
                .whenComplete((webSocket, error) -> {
                    if (error != null) {
                        // The handshake failed (e.g. the server returned a non-101 status such as 502).
                        // onError is never called in this case since no connection was established, so
                        // handle the exceptional completion here to avoid crashing and retry the connection.
                        LOGGER.error("Failed to connect WebSocket, retrying in " + RETRY_INTERVAL + " seconds", error);
                        scheduler.schedule(WebSocketSongProvider.this::start, RETRY_INTERVAL, TimeUnit.SECONDS);
                    } else {
                        this.client = webSocket;
                    }
                });
    }

    @Override
    public void stop() {
        if (client == null) {
            return;
        }
        client.sendClose(WebSocket.NORMAL_CLOSURE, "Client closing")
                .thenRun(() -> LOGGER.info("WebSocket client has disconnected by client"))
                .exceptionally(error -> {
                    LOGGER.error("Failed to close WebSocket connection", error);
                    return null;
                });
    }

    @Override
    public Song getCurrentSong() {
        return currentSong;
    }

    @Override
    public void verifyCurrentSong() {
    }

    private void send(WebSocket client, JsonObject json) {
        client.sendText(json.toString(), true)
                .exceptionally(error -> {
                    LOGGER.error("Failed to send WebSocket message", error);
                    return null;
                });
    }
}
