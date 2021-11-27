package io.github.kabanfriends.craftgr;

import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.OverlayHandler;
import io.github.kabanfriends.craftgr.handler.SongHandler;
import io.github.kabanfriends.craftgr.render.impl.SongInfoOverlay;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CraftGR implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "craftgr";
    public static final String MOD_NAME = "CraftGR";

    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static OkHttpClient HTTP_CLIENT;

    @Override
    public void onInitialize() {
        AutoConfig.register(GRConfig.class, GsonConfigSerializer::new);

        HTTP_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        OverlayHandler.addOverlay(new SongInfoOverlay());

        new SongHandler();
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

}