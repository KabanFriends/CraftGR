package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.RadioStream;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {

    @Inject(method = "reload", at = @At("HEAD"))
    private void craftgr$stopAudio(CallbackInfo ci) {
        RadioStream stream = CraftGR.getInstance().getRadioStream();

        if (stream.getState() == RadioStream.State.PLAYING) {
            stream.disconnect(RadioStream.State.AWAIT_LOADING);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;preload(Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.AFTER), method = "loadLibrary()V")
    private void craftgr$startAudio(CallbackInfo ci) {
        RadioStream stream = CraftGR.getInstance().getRadioStream();
        if (stream.getState() == RadioStream.State.AWAIT_LOADING) {
            stream.start();
        }
    }
}
