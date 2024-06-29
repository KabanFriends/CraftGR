package io.github.kabanfriends.craftgr.audio;

public class AudioPlayerException extends Exception {

    public AudioPlayerException(String message) {
        super(message);
    }

    public AudioPlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AudioPlayerException(Throwable cause) {
        super(cause);
    }
}
