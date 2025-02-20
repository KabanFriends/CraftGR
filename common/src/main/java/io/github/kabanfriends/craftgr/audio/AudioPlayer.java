package io.github.kabanfriends.craftgr.audio;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.util.ExceptionUtil;
import io.github.kabanfriends.craftgr.util.RingBuffer;
import javazoom.jl.decoder.*;
import net.minecraft.Util;
import org.apache.logging.log4j.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

// Code based on: https://github.com/PC-Logix/OpenFM/blob/1.12.2/src/main/java/pcl/OpenFM/player/MP3Player.java
public class AudioPlayer {

    private static final int MAGNITUDE_BUFFER_CAPACITY = 512;

    private final CraftGR craftGR;
    private final Decoder decoder;
    private final Bitstream bitstream;

    private final RingBuffer<FreqSample> freqBuffer;

    private FreqRenderer freqRenderer;
    private IntBuffer buffer;
    private IntBuffer source;

    private float gain = 1.0f;
    private boolean playing = false;

    public AudioPlayer(CraftGR craftGR, InputStream stream) {
        this.craftGR = craftGR;
        this.decoder = new Decoder();
        this.bitstream = new Bitstream(stream);

        this.freqBuffer = new RingBuffer<>(MAGNITUDE_BUFFER_CAPACITY);
    }

    public void play(boolean fadeIn) throws JavaLayerException {
        this.source = BufferUtils.createIntBuffer(1);
        AL10.alGenSources(this.source);
        alError();

        AL10.alSourcei(this.source.get(0), AL10.AL_LOOPING, AL10.AL_FALSE);
        AL10.alSourcef(this.source.get(0), AL10.AL_PITCH, 1.0f);

        if (fadeIn) {
            applyGain(0.0f);
            fadeIn(2000f);
        } else {
            applyGain(1.0f);
        }
        alError();

        this.playing = true;
        while (this.playing) {
            try {
                if (!decodeFrame()) {
                    break;
                }
            } catch (JavaLayerException e) {
                close();
                throw e;
            }
        };

        close();
    }

    public void stop() {
        this.playing = false;
        if (this.source != null) {
            alError();
        }
    }

    public void close() {
        if (this.source != null) {
            AL10.alSourceStop(this.source.get(0));
            AL10.alDeleteSources(this.source);
            alError();
        }
        if (this.buffer != null) {
            AL10.alDeleteBuffers(this.buffer);
            alError();
        }

        try {
            this.bitstream.close();
        } catch (BitstreamException e) {
            craftGR.log(Level.ERROR, "Could not close bitstream: " + ExceptionUtil.getStackTrace(e));
        }
    }

    public void setGain(float gain) {
        this.gain = gain;

        if (this.playing) {
            applyGain(1.0f);
        }
    }

    public FreqRenderer getFreqRenderer() {
        return this.freqRenderer;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    protected FreqSample getFreqSampleNow() {
        int index = AL10.alGetSourcei(this.source.get(0), AL10.AL_BUFFERS_PROCESSED);
        alError();
        if (index >= freqBuffer.size()) {
            return null;
        }
        return freqBuffer.get(index);
    }

    private boolean decodeFrame() throws BitstreamException, DecoderException {
        Header header;
        try {
            header = this.bitstream.readFrame();
        } catch (BitstreamException e) {
            if (e.getErrorCode() == BitstreamErrors.STREAM_ERROR ||
                e.getErrorCode() == BitstreamErrors.UNEXPECTED_EOF ||
                e.getErrorCode() == BitstreamErrors.STREAM_EOF) {

                // If client closed the stream, do not throw errors
                if (!playing) {
                    return false;
                }
            }
            throw e;
        }

        if (header == null) {
            close();
            return false;
        }

        SampleBuffer output = (SampleBuffer) this.decoder.decodeFrame(header, this.bitstream);
        short[] samples = output.getBuffer();

        if (this.freqRenderer == null) {
            this.freqRenderer = new FreqRenderer(this, output.getSampleFrequency(), 30);
        }

        if (this.buffer == null) {
            this.buffer = BufferUtils.createIntBuffer(1);
        } else {
            int processed = AL10.alGetSourcei(this.source.get(0), AL10.AL_BUFFERS_PROCESSED);
            freqBuffer.removeHeadN(processed);
            while (processed > 0) {
                AL10.alSourceUnqueueBuffers(this.source.get(0), this.buffer);
                processed--;
            }
        }

        // For some reason, the original samples array is mutable so we have to copy values before buffering
        short[] capturedSamples = new short[samples.length];
        System.arraycopy(samples, 0, capturedSamples, 0, samples.length);

        freqBuffer.add(new FreqSample(freqRenderer, capturedSamples));

        AL10.alGenBuffers(this.buffer);
        ShortBuffer shortBuffer = BufferUtils.createShortBuffer(output.getBufferLength()).put(samples, 0, output.getBufferLength());
        ShortBuffer data = (ShortBuffer) ((Buffer) shortBuffer).flip();
        AL10.alBufferData(this.buffer.get(0), (output.getChannelCount() > 1) ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, data, output.getSampleFrequency());
        AL10.alSourceQueueBuffers(this.source.get(0), buffer);
        alError();

        int state = AL10.alGetSourcei(this.source.get(0), AL10.AL_SOURCE_STATE);
        if (this.playing && state != AL10.AL_PLAYING) {
            AL10.alSourcePlay(this.source.get(0));
        }
        alError();

        this.bitstream.closeFrame();

        return true;
    }

    private void applyGain(float multiplier) {
        AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, (float) ((Math.exp(multiplier * this.gain) - 1) / (Math.E - 1)));
    }

    private void fadeIn(float fadingDurationMillis) {
        long fadingStart = Util.getMillis();
        craftGR.getThreadExecutor().submit(() -> {
            while (true) {
                float multiplier = Math.min((Util.getMillis() - fadingStart) / fadingDurationMillis, 1.0f);
                applyGain(multiplier);

                if (multiplier >= 1.0f) {
                    break;
                }
            }
        });
    }

    private void alError() {
        int error = AL10.alGetError();
        if (error != AL10.AL_NO_ERROR) {
            craftGR.log(Level.WARN, String.format("AL10 Error: %d: %s", error, AL10.alGetString(error)));
            Thread.dumpStack();
        }
    }
}
