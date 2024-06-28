package io.github.kabanfriends.craftgr.song;

public class SongProviderManager {

    private static SongProvider provider;

    public static void setProvider(SongProvider songProvider) {
        provider = songProvider;
        songProvider.start();
    }

    public static SongProvider getProvider() {
        if (provider == null) {
            throw new IllegalStateException("Song provider is not set");
        }
        return provider;
    }
}
