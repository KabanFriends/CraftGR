package io.github.kabanfriends.craftgr;

import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.platform.Platform;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import net.minecraft.client.Minecraft;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CraftGR {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "craftgr";
    public static final String MOD_NAME = "CraftGR";

    public static final Minecraft MC = Minecraft.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static boolean renderSongOverlay;

    private static Platform platform;
    private static CloseableHttpClient httpClient;
    private static RequestConfig requestConfig;

    private static GRConfig config;

    public static void init(Platform platform) {
        CraftGR.platform = platform;

        getConfig().init();

        CraftGR.httpClient = HttpClients.createSystem();
        CraftGR.requestConfig = RequestConfig.custom()
                .setConnectTimeout(2000)
                .setSocketTimeout(2000)
                .build();
    }

    public static void lateInit() {
        OverlayHandler.addOverlay(new SongInfoOverlay());
        SongHandler.getInstance().initialize();
    }

    public static void setConfig(GRConfig config) {
        CraftGR.config = config;
    }

    public static GRConfig getConfig() {
        return config;
    }

    public static Platform getPlatform() {
        return platform;
    }

    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public static RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

}