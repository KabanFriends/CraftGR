package io.github.kabanfriends.craftgr.song;

import net.minecraft.Util;

public class Song {

    private final Metadata metadata;
    private final long playedTime;
    private final long localStartTime;

    public Song(Metadata metadata, long playedTime) {
        this.metadata = metadata;
        this.playedTime = playedTime;
        this.localStartTime = currentTime();
    }

    public Metadata metadata() {
        return metadata;
    }

    public long getLocalPlayedTime() {
        long played = getAPIPlayedTime() * 1000L + (currentTime() - localStartTime);
        return Math.min(played, metadata().duration() * 1000L);
    }

    protected long getAPIPlayedTime() {
        return playedTime;
    }

    private static long currentTime() {
        return Util.getMillis();
    }

    /**
     * Song metadata fetched from the Gensokyo Radio's API.
     * Warning: Any time data here are based on the remote server's timestamp.
     */
    public record Metadata(
            /* SONGINFO */
            String title,
            String artist,
            String album,
            String year,
            String circle,

            /* SONGTIMES */
            long duration,

            /* SONGDATA */
            int albumId,

            /* MISC */
            String albumArt,

            /* CraftGR */
            boolean intermission
    ) {}
}
