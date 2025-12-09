package io.github.kabanfriends.craftgr.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.ModConfig;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

public class Http {

    private static final HttpResponse.BodyHandler<JsonElement> JSON_BODY_HANDLER = responseInfo -> HttpResponse.BodySubscribers.mapping(
            HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
            JsonParser::parseString
    );

    private static HttpClient httpClient;

    // Private constructor to prevent instantiation
    private Http() {
    }

    public static void createClient() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.of((long) ModConfig.<Integer>get("connectTimeout"), ChronoUnit.MILLIS))
                .build();
    }

    public static <T> CompletableFuture<HttpResponse<T>> fetch(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return httpClient.sendAsync(request, responseBodyHandler);
    }

    public static CompletableFuture<HttpResponse<String>> fetchString(HttpRequest request) {
        return fetch(request, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<JsonElement>> fetchJson(HttpRequest request) {
        return fetch(request, JSON_BODY_HANDLER);
    }

    public static HttpRequest.Builder standardRequest() {
        return HttpRequest.newBuilder()
                .timeout(Duration.of((long) ModConfig.<Integer>get("socketTimeout"), ChronoUnit.MILLIS))
                .header("User-Agent", "Minecraft-CraftGR/" + CraftGR.getInstance().getPlatformAdapter().getModVersion());
    }

    public static WebSocket.Builder standardWebSocket() {
        return httpClient.newWebSocketBuilder()
                .header("User-Agent", "Minecraft-CraftGR/" + CraftGR.getInstance().getPlatformAdapter().getModVersion());
    }
}
