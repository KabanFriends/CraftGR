package io.github.kabanfriends.craftgr.audio;

import io.github.kabanfriends.craftgr.CraftGR;
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

    private final CraftGR craftGR;
    private final Decoder decoder;
    private final Bitstream bitstream;

    private IntBuffer buffer;
    private IntBuffer source;

    private float gain = 1.0f;
    private boolean playing = false;

    public AudioPlayer(CraftGR craftGR, InputStream stream) {
        this.craftGR = craftGR;
        this.decoder = new Decoder();
        this.bitstream = new Bitstream(stream);
    }

    public void play(boolean fadeIn) throws AudioPlayerException {
        try {
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
                if (!decodeFrame()) {
                    break;
                }
            };

            close();
        } catch (Exception e) {
            if (this.playing) {
                throw new AudioPlayerException(e);
            }
        }
    }

    public void stop() {
        this.playing = false;
        if (this.source != null) {
            AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, 0.0f);
            AL10.alSourceStop(this.source.get());
            alError();
        }
    }

    public void close() throws BitstreamException {
        if (this.source != null) {
            int state = AL10.alGetSourcei(this.source.get(0), AL10.AL_SOURCE_STATE);
            if (state != AL10.AL_PLAYING && state != AL10.AL_PAUSED) {
                AL10.alSourcei(this.source.get(0), AL10.AL_BUFFER, 0);
            }
            AL10.alDeleteSources(this.source);
            alError();
        }
        if (this.buffer != null) {
            AL10.alDeleteBuffers(this.buffer);
            alError();
        }
        if (this.bitstream != null) {
            this.bitstream.close();
        }
    }

    public void setGain(float gain) {
        this.gain = gain;

        if (this.playing) {
            applyGain(1.0f);
        }
    }

    public boolean isPlaying() {
        return this.playing;
    }

    private boolean decodeFrame() throws AudioPlayerException {
        try {
            Header h = this.bitstream.readFrame();

            if (h == null) {
                close();
                return false;
            }

            SampleBuffer output = (SampleBuffer) this.decoder.decodeFrame(h, this.bitstream);
            short[] samples = output.getBuffer();

            if (this.buffer == null) {
                this.buffer = BufferUtils.createIntBuffer(1);
            } else {
                int processed = AL10.alGetSourcei(this.source.get(0), AL10.AL_BUFFERS_PROCESSED);
                if (processed > 0) {
                    AL10.alSourceUnqueueBuffers(this.source.get(0), this.buffer);
                }
            }

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
        } catch (Exception e) {
            if (this.playing) {
                throw new AudioPlayerException(e);
            }
            return false;
        }
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

    private int alError() {
        int error = AL10.alGetError();
        if (error != AL10.AL_NO_ERROR) {
            craftGR.log(Level.WARN, String.format("AL10 Error: %d: %s", error, AL10.alGetString(error)));
            Thread.dumpStack();
            return error;
        }
        return 0;
    }
}
