package io.github.kabanfriends.craftgr.song;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.util.ExceptionUtil;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public enum SongProviderType {

    JSON_API(JsonAPISongProvider::new),
    WEBSOCKET(WebSocketSongProvider::new),
    ;

    private final Supplier<SongProvider> supplier;

    SongProviderType(Supplier<SongProvider> supplier) {
        this.supplier = supplier;
    }

    public SongProvider createProvider() {
        try {
            return supplier.get();
        } catch (Exception e) {
            CraftGR.getInstance().log(Level.ERROR, "Failed to create song provider: " + ExceptionUtil.getStackTrace(e));
            return null;
        }
    }
}
