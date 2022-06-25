package io.github.kabanfriends.craftgr;

import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.platform.Platform;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
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

    private static Platform platform;
    private static CloseableHttpClient httpClient;
    private static RequestConfig requestConfig;

    public static void init(Platform platform) {
        AutoConfig.register(GRConfig.class, GsonConfigSerializer::new);

        CraftGR.platform = platform;
        CraftGR.httpClient = HttpClients.createSystem();
        CraftGR.requestConfig = RequestConfig.custom()
                .setConnectTimeout(2000)
                .setSocketTimeout(2000)
                .build();

        OverlayHandler.addOverlay(new SongInfoOverlay());

        SongHandler.getInstance().initialize();
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