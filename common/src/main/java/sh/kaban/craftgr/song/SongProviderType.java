package sh.kaban.craftgr.song;

import sh.kaban.craftgr.util.Logs;
import org.slf4j.Logger;

import java.util.function.Supplier;

public enum SongProviderType {

    JSON_API(JsonAPISongProvider::new),
    WEBSOCKET(WebSocketSongProvider::new),
    ;

    private static final Logger LOGGER = Logs.logger();

    private final Supplier<SongProvider> supplier;

    SongProviderType(Supplier<SongProvider> supplier) {
        this.supplier = supplier;
    }

    public SongProvider createProvider() {
        try {
            return supplier.get();
        } catch (Exception e) {
            LOGGER.error("Failed to create song provider", e);
            return null;
        }
    }
}
