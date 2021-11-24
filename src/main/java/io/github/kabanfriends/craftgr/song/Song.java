package io.github.kabanfriends.craftgr.song;

public class Song {

    public boolean intermission;

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
    public float rating;

    //MISC
    public String albumArt;

    public Song() {
        this.intermission = false;

        this.title = "N/A";
        this.artist = "N/A";
        this.album = "N/A";
        this.year = "N/A";
        this.circle = "N/A";

        this.songStart = 0L;
        this.songEnd = 0L;

        this.albumId = 0;
        this.rating = 0f;

        this.albumArt = "";
    }
}
