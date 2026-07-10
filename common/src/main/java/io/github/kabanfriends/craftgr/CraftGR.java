package io.github.kabanfriends.craftgr;

import io.github.kabanfriends.craftgr.audio.Radio;
import io.github.kabanfriends.craftgr.config.ModConfig;
import io.github.kabanfriends.craftgr.event.ClientEvents;
import io.github.kabanfriends.craftgr.keybind.Keybinds;
import io.github.kabanfriends.craftgr.platform.PlatformAdapter;
import io.github.kabanfriends.craftgr.overlay.SongInfoOverlay;
import io.github.kabanfriends.craftgr.song.EmptySongProvider;
import io.github.kabanfriends.craftgr.song.SongProvider;
import io.github.kabanfriends.craftgr.util.Http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CraftGR {

    private static CraftGR instance;

    private final PlatformAdapter platformAdapter;
    private final ModConfig config;
    private final ExecutorService executor;
    private final ClientEvents events;
    private final Keybinds keybinds;
    private final SongInfoOverlay songInfoOverlay;
    private final Radio radio;

    private SongProvider songProvider = new EmptySongProvider();

    public CraftGR(PlatformAdapter platformAdapter) {
        instance = this;

        this.platformAdapter = platformAdapter;
        this.config = new ModConfig();
        this.config.load();
        this.executor = Executors.newCachedThreadPool();
        this.events = new ClientEvents(this);
        this.keybinds = new Keybinds(this);
        this.songInfoOverlay = new SongInfoOverlay(this);
        this.radio = new Radio(this);

        Http.createClient();
    }

    public PlatformAdapter getPlatformAdapter() {
        return platformAdapter;
    }

    public ExecutorService getThreadExecutor() {
        return executor;
    }

    public SongInfoOverlay getSongInfoOverlay() {
        return songInfoOverlay;
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

    public Radio getRadio() {
        return radio;
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

    public void shutdown() {
        radio.stop(false);
        songProvider.stop();
        executor.shutdownNow();
    }

    public static CraftGR getInstance() {
        return instance;
    }
}