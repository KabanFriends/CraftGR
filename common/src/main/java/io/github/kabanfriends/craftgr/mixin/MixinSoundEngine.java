package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.InitState;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {

    @Inject(method = "reload()V", at = @At("HEAD"))
    public void stopAudio(CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.getInitState() == InitState.SUCCESS) {
            handler.stopPlayback(true);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;preload(Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.AFTER), method = "loadLibrary()V")
    public void startAudio(CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.getInitState() == InitState.RELOADING) {
            CraftGR.EXECUTOR.submit(() -> {
                handler.initialize();
                if (handler.hasAudioPlayer()) {
                    handler.getAudioPlayer().setVolume(1.0f);
                    handler.startPlayback();
                }
            });
        }
    }
}
