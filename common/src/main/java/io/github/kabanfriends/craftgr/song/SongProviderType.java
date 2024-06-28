package io.github.kabanfriends.craftgr.song;

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
        return supplier.get();
    }
}
