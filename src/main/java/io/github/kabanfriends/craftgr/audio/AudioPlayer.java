package io.github.kabanfriends.craftgr.audio;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.config.GRConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import org.apache.logging.log4j.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;

//Code based on: https://github.com/PC-Logix/OpenFM/blob/1.12.2/src/main/java/pcl/OpenFM/player/MP3Player.java
public class AudioPlayer {

    private Bitstream bitstream;
    private Decoder decoder;
    private IntBuffer buffer;
    private IntBuffer source;
    private float volume = 0.0F;
    public InputStream ourStream = null;
    private boolean playing = false;

    public AudioPlayer(InputStream stream) {
        this.ourStream = stream;
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

    public boolean play() throws JavaLayerException {
        boolean ret = true;

        this.source = BufferUtils.createIntBuffer(1);
        AL10.alGenSources(this.source);
        if (alError()) {
            close();
            return false;
        }

        AL10.alSourcei(this.source.get(0), AL10.AL_LOOPING, AL10.AL_FALSE);
        AL10.alSourcef(this.source.get(0), AL10.AL_PITCH, 1.0f);
        AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, this.volume * (GRConfig.getConfig().volume/100f) * MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER));

        if (alError()) {
            close();
            return false;
        }

        this.playing = true;

        while ((this.playing) && (ret))
        {
            ret = decodeFrame();
        }

        if (this.playing) {
            while (AL10.alGetSourcei(this.source.get(0), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING) {
                try {
                    AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, this.volume * (GRConfig.getConfig().volume/100f) * MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER));
                    Thread.sleep(1);
                } catch (InterruptedException e) {}
            }
        }

        close();

        return ret;
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

        this.bitstream.close();
    }

    protected boolean decodeFrame() throws JavaLayerException {
        Header h = this.bitstream.readFrame();
        if (h == null)
        {
            return false;
        }

        SampleBuffer output = (SampleBuffer)this.decoder.decodeFrame(h, this.bitstream);
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

        return true;
    }

    protected boolean skipFrame() throws JavaLayerException {
        Header h = this.bitstream.readFrame();
        if (h == null)
        {
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
            AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, f * (GRConfig.getConfig().volume/100f) * MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER));
        }
    }

    public float getVolume() {
        return this.volume;
    }

    public boolean isPlaying() {
        return this.playing;
    }
}
