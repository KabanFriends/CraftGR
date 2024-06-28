package io.github.kabanfriends.craftgr.song;

import java.util.Objects;

public final class Song {

    public Song(
            /* SONGINFO */
            String title,
            String artist,
            String album,
            String year,
            String circle,

            /* SONGTIMES */
            long songStart,
            long songEnd,

            /* SONGDATA */
            int albumId,

            /* MISC */
            String albumArt,

            /* CraftGR */
            boolean intermission
    ) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.circle = circle;
        this.songStart = songStart;
        this.songEnd = songEnd;
        this.albumId = albumId;
        this.albumArt = albumArt;
        this.intermission = intermission;
    }

    protected Song calculateLocalTimes(long offsetTime) {

        return this;
    }

    /*
    public long songStart() {
        long played = apiOffsetTime - apiSongStart;
        long duration = apiSongEnd - apiSongStart;

        long songStart = System.currentTimeMillis() / 1000L - played;
        long songEnd = songStart + duration;
    }
    */

    //<editor-fold desc="Generated">
    private final String title;
    private final String artist;
    private final String album;
    private final String year;
    private final String circle;
    private final long songStart;
    private final long songEnd;
    private final int albumId;
    private final String albumArt;
    private final boolean intermission;

    public String title() {
        return title;
    }

    public String artist() {
        return artist;
    }

    public String album() {
        return album;
    }

    public String year() {
        return year;
    }

    public String circle() {
        return circle;
    }

    public int albumId() {
        return albumId;
    }

    public String albumArt() {
        return albumArt;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Song) obj;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.artist, that.artist) &&
                Objects.equals(this.album, that.album) &&
                Objects.equals(this.year, that.year) &&
                Objects.equals(this.circle, that.circle) &&
                this.songStart == that.songStart &&
                this.songEnd == that.songEnd &&
                this.albumId == that.albumId &&
                Objects.equals(this.albumArt, that.albumArt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist, album, year, circle, songStart, songEnd, albumId, albumArt);
    }

    @Override
    public String toString() {
        return "Song[" +
                "title=" + title + ", " +
                "artist=" + artist + ", " +
                "album=" + album + ", " +
                "year=" + year + ", " +
                "circle=" + circle + ", " +
                "songStart=" + songStart + ", " +
                "songEnd=" + songEnd + ", " +
                "albumId=" + albumId + ", " +
                "albumArt=" + albumArt + ']';
    }
    //</editor-fold>

}
