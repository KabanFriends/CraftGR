package io.github.kabanfriends.craftgr.audio;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import io.github.kabanfriends.craftgr.util.ProcessResult;
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

    private InputStream stream;
    private Bitstream bitstream;
    private Decoder decoder;
    private IntBuffer buffer;
    private IntBuffer source;
    private float volume = 1.0F;
    private boolean playing = false;

    public AudioPlayer(InputStream stream) {
        this.stream = stream;
        this.bitstream = new Bitstream(stream);
        this.decoder = new Decoder();
    }

    protected int alError() {
        int error = AL10.alGetError();
        if (error != AL10.AL_NO_ERROR) {
            CraftGR.log(Level.ERROR, String.format("AL10 Error: %d: %s", error, AL10.alGetString(error)));
            return error;
        }
        return 0;
    }

    public ProcessResult play() {
        try {
            this.source = BufferUtils.createIntBuffer(1);
            AL10.alGenSources(this.source);
            alError();

            AL10.alSourcei(this.source.get(0), AL10.AL_LOOPING, AL10.AL_FALSE);
            AL10.alSourcef(this.source.get(0), AL10.AL_PITCH, 1.0f);

            AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, this.volume * (GRConfig.<Integer>getValue("volume") / 100f) * CraftGR.MC.options.getSoundSourceVolume(SoundSource.MASTER));
            alError();

            this.playing = true;
            ProcessResult result = ProcessResult.SUCCESS;

            while (this.playing && result == ProcessResult.SUCCESS) {
                AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, this.volume * (GRConfig.<Integer>getValue("volume") / 100f) * CraftGR.MC.options.getSoundSourceVolume(SoundSource.MASTER));
                result = decodeFrame();
            }

            close();
            return result;
        } catch (Exception e) {
            if (this.playing) {
                e.printStackTrace();
                return ProcessResult.ERROR;
            }
            return ProcessResult.STOP;
        }
    }

    public void close() {
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
            if (this.playing && state != AL10.AL_PLAYING) {
                AL10.alSourcePlay(this.source.get(0));
            }

            this.bitstream.closeFrame();

            return ProcessResult.SUCCESS;
        } catch (Exception e) {
            if (this.playing) {
                e.printStackTrace();
                return ProcessResult.ERROR;
            }
            return ProcessResult.STOP;
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

    public void setVolume(float f) {
        this.volume = f;
        if (this.playing && this.source != null) {
            float volume = f * (GRConfig.<Integer>getValue("volume") / 100f) * CraftGR.MC.options.getSoundSourceVolume(SoundSource.MASTER);
            AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, volume);
        }
    }

    public float getVolume() {
        return this.volume;
    }

    public boolean isPlaying() {
        return this.playing;
    }

}
