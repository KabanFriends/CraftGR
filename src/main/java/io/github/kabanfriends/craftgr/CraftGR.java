package io.github.kabanfriends.craftgr;

import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.handler.CommandHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CraftGR implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "craftgr";
    public static final String MOD_NAME = "CraftGR";

    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @Override
    public void onInitialize() {
        AutoConfig.register(GRConfig.class, GsonConfigSerializer::new);
        new CommandHandler();

        new Thread(() -> {
            log(Level.INFO, "CraftGR is starting up!");
            AudioPlayerHandler.initialize();
            log(Level.INFO, "Audio player is ready!");
        }).run();
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}