package io.github.kabanfriends.craftgr.audio;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import javazoom.jl.decoder.*;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

//Code based on: https://github.com/PC-Logix/OpenFM/blob/1.12.2/src/main/java/pcl/OpenFM/player/MP3Player.java
public class AudioPlayer {

    private static boolean FORCE_STOP;

    private InputStream stream;
    private Bitstream bitstream;
    private Decoder decoder;
    private IntBuffer buffer;
    private IntBuffer source;
    private float volume = 0.0F;
    public boolean muted = false;
    private boolean playing = false;
    private long started;

    public AudioPlayer(InputStream stream) {
        this.stream = stream;
        this.bitstream = new Bitstream(stream);
        this.decoder = new Decoder();
    }

    protected boolean alError() {
        if (AL10.alGetError() != AL10.AL_NO_ERROR) {
            CraftGR.log(Level.ERROR, String.format("AL10 Error: %d: %s", AL10.alGetError(), AL10.alGetString(AL10.alGetError())));
            return true;
        }
        return false;
    }

    public ProcessResult play() {
        try {
            this.started = 0L;

            this.source = BufferUtils.createIntBuffer(1);
            AL10.alGenSources(this.source);
            if (alError()) {
                close();
                return ProcessResult.AL_ERROR;
            }

            AL10.alSourcei(this.source.get(0), AL10.AL_LOOPING, AL10.AL_FALSE);
            AL10.alSourcef(this.source.get(0), AL10.AL_PITCH, 1.0f);

            AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, this.muted ? 0F : this.volume * (GRConfig.getConfig().volume / 100f) * CraftGR.MC.options.getSoundSourceVolume(SoundSource.MASTER));
            
            if (alError()) {
                close();
                return ProcessResult.AL_ERROR;
            }

            this.playing = true;
            ProcessResult result = ProcessResult.SUCCESS;

            while (this.playing && result == ProcessResult.SUCCESS) {
                AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, this.muted ? 0F : this.volume * (GRConfig.getConfig().volume / 100f) * CraftGR.MC.options.getSoundSourceVolume(SoundSource.MASTER));
                result = decodeFrame();
            }

            close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return ProcessResult.EXCEPTION;
        }
    }

    public void close() throws BitstreamException {
        this.playing = false;

        if (this.source != null) {
            AL10.alSourceStop(this.source.get());
            AL10.alDeleteSources(this.source);
            this.source = null;
        }
        if (this.buffer != null) {
            AL10.alDeleteBuffers(this.buffer);
            this.buffer = null;
        }
    }

    protected ProcessResult decodeFrame() {
        try {
            Header h = this.bitstream.readFrame();

            if (h == null) {
                close();
                return ProcessResult.STOP;
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

            int state = AL10.alGetSourcei(this.source.get(0), AL10.AL_SOURCE_STATE);
            if ((this.playing && state != AL10.AL_PLAYING) || FORCE_STOP) {
                if (FORCE_STOP) FORCE_STOP = false;
                if (this.started == 0L || System.currentTimeMillis() < this.started + 100L) {
                    this.started = System.currentTimeMillis();
                    AL10.alSourcePlay(this.source.get(0));
                } else {
                    close();
                    return ProcessResult.RESTART;
                }
            }

            this.bitstream.closeFrame();

            return ProcessResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ProcessResult.EXCEPTION;
        }
    }

    protected boolean skipFrame() throws JavaLayerException {
        Header h = this.bitstream.readFrame();
        if (h == null) {
            return false;
        }
        this.bitstream.closeFrame();
        return true;
    }

    public void stop() {
        this.playing = false;
        if (this.source != null) {
            AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, 0.0f);
            AL10.alSourceStop(this.source.get());
        }
    }

    public void stopNext() {
        FORCE_STOP = true;
    }

    public void setVolume(float f) {
        this.volume = f;
        if (this.playing && this.source != null) {
            float volume = this.muted ? 0F : f * (GRConfig.getConfig().volume / 100f) * CraftGR.MC.options.getSoundSourceVolume(SoundSource.MASTER);
            AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, volume);
        }
    }

    public float getVolume() {
        return this.volume;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public enum ProcessResult {
        SUCCESS,
        STOP,
        RESTART,
        AL_ERROR,
        EXCEPTION
    }
}
