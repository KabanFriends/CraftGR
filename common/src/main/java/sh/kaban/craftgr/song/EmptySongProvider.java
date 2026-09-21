package sh.kaban.craftgr.song;

public class EmptySongProvider implements SongProvider {

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public Song getCurrentSong() {
        return null;
    }

    @Override
    public void verifyCurrentSong() {
    }
}
