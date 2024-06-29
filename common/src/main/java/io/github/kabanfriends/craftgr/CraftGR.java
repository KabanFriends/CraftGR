package io.github.kabanfriends.craftgr;

import io.github.kabanfriends.craftgr.audio.RadioStream;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.event.ClientEvents;
import io.github.kabanfriends.craftgr.keybind.Keybinds;
import io.github.kabanfriends.craftgr.platform.Platform;
import io.github.kabanfriends.craftgr.overlay.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.FallbackSongProvider;
import io.github.kabanfriends.craftgr.song.SongProvider;
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

    public static final String MOD_ID = "craftgr";
    public static final String MOD_NAME = "CraftGR";

    public static final Component AUDIO_MUTED_ICON = Component.literal("M").withStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "icons")));
    public static final Component RECONNECT_ICON = Component.literal("R").withStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath(CraftGR.MOD_ID, "icons")));

    private static CraftGR instance;

    private final Minecraft minecraft;
    private final Platform platform;
    private final Logger logger;
    private final ModConfig config;
    private final ExecutorService executor;
    private final CloseableHttpClient httpClient;
    private final ClientEvents events;
    private final Keybinds keybinds;
    private final SongInfoOverlay songInfoOverlay;
    private final RadioStream radioStream;

    private SongProvider songProvider = new FallbackSongProvider();

    public CraftGR(Platform platform) {
        instance = this;

        this.minecraft = Minecraft.getInstance();
        this.platform = platform;
        this.logger = LogManager.getLogger();
        this.config = new ModConfig(this);
        this.executor = Executors.newCachedThreadPool();
        this.httpClient = HttpClients.createDefault();
        this.events = new ClientEvents(this);
        this.keybinds = new Keybinds(this);
        this.songInfoOverlay = new SongInfoOverlay(this);
        this.radioStream = new RadioStream(this);
    }

    public Platform getPlatform() {
        return platform;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public ExecutorService getThreadExecutor() {
        return executor;
    }

    public SongInfoOverlay getSongInfoOverlay() {
        return songInfoOverlay;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public ModConfig getConfig() {
        return config;
    }

    public Keybinds getKeybinds() {
        return keybinds;
    }

    public SongProvider getSongProvider() {
        return songProvider;
    }

    public RadioStream getRadioStream() {
        return radioStream;
    }

    public void setSongProvider(SongProvider newProvider) {
        if (newProvider == null) {
            return;
        }

        if (songProvider != null) {
            songProvider.stop();
        }

        songProvider = newProvider;
        songProvider.start();

        CraftGR.getInstance().getSongInfoOverlay().onSongChanged();
    }

    public ClientEvents clientEvents() {
        return events;
    }

    public void log(Level level, String message) {
        logger.log(level, String.format("[%s] %s", MOD_NAME, message));
    }

    public static CraftGR getInstance() {
        return instance;
    }
}