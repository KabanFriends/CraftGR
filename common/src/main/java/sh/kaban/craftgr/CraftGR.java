package sh.kaban.craftgr;

import sh.kaban.craftgr.audio.Radio;
import sh.kaban.craftgr.config.ModConfig;
import sh.kaban.craftgr.event.ClientEvents;
import sh.kaban.craftgr.keybind.Keybinds;
import sh.kaban.craftgr.platform.PlatformAdapter;
import sh.kaban.craftgr.rendering.overlay.SongInfoOverlay;
import sh.kaban.craftgr.song.EmptySongProvider;
import sh.kaban.craftgr.song.SongProvider;
import sh.kaban.craftgr.util.Http;

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