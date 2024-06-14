package io.github.kabanfriends.craftgr;

import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.platform.Platform;
import io.github.kabanfriends.craftgr.render.overlay.impl.SongInfoOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
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

    public static final Component AUDIO_MUTED_ICON;
    public static final Component RECONNECT_ICON;

    static {
        ResourceLocation iconFont = ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "icons");
        AUDIO_MUTED_ICON = Component.literal("M").withStyle(Style.EMPTY.withFont(iconFont));
        RECONNECT_ICON = Component.literal("R").withStyle(Style.EMPTY.withFont(iconFont));
    }

    public static boolean renderSongOverlay;

    private static Platform platform;
    private static CloseableHttpClient httpClient;

    public static void init(Platform platform) {
        CraftGR.platform = platform;

        GRConfig.init();

        CraftGR.httpClient = HttpClients.createSystem();
    }

    public static void lateInit() {
        OverlayHandler.addOverlay(new SongInfoOverlay(MC.getTextureManager()));
        SongHandler.getInstance().initialize();
    }

    public static Platform getPlatform() {
        return platform;
    }

    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

}