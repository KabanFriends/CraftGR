package io.github.kabanfriends.craftgr.song;

public class Song {

    //SONGINFO
    public String title;
    public String artist;
    public String album;
    public String year;
    public String circle;

    //SONGDATA
    public int albumId;
    public float rating;

    public Song() {
      this.title = "N/A";
      this.artist = "N/A";
      this.album = "N/A";
      this.year = "N/A";
      this.circle = "N/A";
      this.albumId = 0;
      this.rating = 0f;
    };

    public Song(String title, String artist, String album, String year, String circle, int albumId, float rating) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.circle = circle;
        this.albumId = albumId;
        this.rating = rating;
    }
}
