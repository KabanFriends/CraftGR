package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import javazoom.jl.decoder.BitstreamException;
import net.minecraft.client.sounds.SoundEngine;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {

    private static boolean LOADED = false;

    @Inject(method = "reload()V", at = @At("HEAD"))
    public void stopPlayback(CallbackInfo ci) {
        if (AudioPlayerHandler.getInstance().getInitState() == AudioPlayerHandler.InitState.SUCCESS) {
            AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

            try {
                CraftGR.log(Level.INFO, "Closing the audio player");
                AudioPlayerHandler.getInstance().getAudioPlayer().close();
            } catch (BitstreamException e) {
                CraftGR.log(Level.ERROR, "Error when closing the audio player!");
                e.printStackTrace();
            }
        }
    }
}
