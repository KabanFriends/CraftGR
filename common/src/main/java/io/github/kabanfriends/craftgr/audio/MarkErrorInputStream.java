package io.github.kabanfriends.craftgr.audio;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * An Erroring InputStream when mark limits are exceeded
 * Fixes MP3SPI infinitely reading until it finds a false positive
 */
public class MarkErrorInputStream extends FilterInputStream {

    long pos;
    long marklimit;
    boolean mark_active = false;

    public MarkErrorInputStream(InputStream proxy) {
        super(proxy);
        if (!proxy.markSupported()) {
            throw new IllegalArgumentException("input stream does not support mark");
        }
    }

    private long limit(long n) throws IOException {
        if (mark_active) {
            long avail = (marklimit - pos);
            if (avail <= 0) {
                in.reset();
                throw new IOException("mark limit exceeded");
            } else if (avail < n) {
                return avail;
            }
        }
        return n;
    }

    private void addPos(long n) throws IOException {
        if (n >= 0 && mark_active) {
            pos += n;
        }
    }

    @Override
    public int read() throws IOException {
        limit(1);
        int ret = super.read();
        addPos(1);
        return ret;
    }

    @Override
    public int read(byte[] bts) throws IOException {
        int len = (int) limit(bts.length);
        int ret = super.read(bts, 0, len);
        addPos(ret);
        return ret;
    }

    @Override
    public int read(byte[] bts, int off, int len) throws IOException {
        len = (int) limit(len);
        int ret = super.read(bts, off, len);
        addPos(ret);
        return ret;
    }

    @Override
    public long skip(long ln) throws IOException {
        ln = limit(ln);
        long ret = super.skip(ln);
        addPos(ret);
        return ret;
    }

    @Override
    public synchronized void mark(int readlimit) {
        // prevent mp3spi's insane limit of 4096001.
        if (readlimit == 4096001) {
            readlimit = 4096;
        }
        super.mark(readlimit);
        mark_active = true;
        marklimit = readlimit;
        pos = 0;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        mark_active = false;
    }

    @Override
    public int available() throws IOException {
        if (mark_active && pos >= marklimit)
            return 0; // Allows BufferedInputStream to stop reading.
        return (int) limit(super.available());
    }
}
