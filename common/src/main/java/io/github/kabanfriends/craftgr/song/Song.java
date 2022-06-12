package io.github.kabanfriends.craftgr.song;

public class Song {

    private boolean intermission;

    //SONGINFO
    public String title;
    public String artist;
    public String album;
    public String year;
    public String circle;

    //SONGTIMES
    public long songStart;
    public long songEnd;

    //SONGDATA
    public int albumId;

    //MISC
    public String albumArt;
    public long offsetTime;

    public Song(String title, String artist, String album, String year, String circle, long start, long end, int albumId, String albumArt, long offsetTime) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.circle = circle;

        this.songStart = start;
        this.songEnd = end;

        this.albumId = albumId;

        this.albumArt = albumArt;
        this.offsetTime = offsetTime;
    }

    public void setIntermission(boolean intermission) {
        this.intermission = intermission;
    }

    public boolean isIntermission() {
        return this.intermission;
    }
}
