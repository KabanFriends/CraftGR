package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import javazoom.jl.decoder.BitstreamException;
import net.minecraft.client.sound.SoundSystem;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {

    private static boolean LOADED = false;

    @Inject(method = "reloadSounds()V", at = @At("HEAD"))
    public void stopPlayback(CallbackInfo ci) {
        if (AudioPlayerHandler.isInitialized()) {
            AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

            try {
                CraftGR.log(Level.INFO, "Closing the audio player");
                AudioPlayerHandler.getInstance().player.close();
            } catch (BitstreamException e) {
                CraftGR.log(Level.ERROR, "Error when closing the audio player!");
                e.printStackTrace();
            }
        }
    }
}
