package io.github.kabanfriends.craftgr.song;

public interface SongProvider {
    
    void start();

    Song getCurrentSong();

    void verifyCurrentSong();
}
