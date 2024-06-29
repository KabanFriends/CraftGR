package io.github.kabanfriends.craftgr.song;

public interface SongProvider {
    
    void start();

    void stop();

    Song getCurrentSong();

    void verifyCurrentSong();
}
