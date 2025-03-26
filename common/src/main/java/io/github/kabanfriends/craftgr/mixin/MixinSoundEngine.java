package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.audio.Radio;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {

    @Unique
    private boolean craftgr$firstLoad = true;

    @Inject(method = "reload", at = @At("HEAD"))
    private void craftgr$stopAudio(CallbackInfo ci) {
        if (CraftGR.getInstance().getRadio().getState() == Radio.State.PLAYING) {
            CraftGR.getInstance().getRadio().stop(true);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;preload(Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.AFTER), method = "loadLibrary()V")
    private void craftgr$startAudio(CallbackInfo ci) {
        if (CraftGR.getInstance().getRadio().getState() == Radio.State.AWAIT_LOADING) {
            CraftGR.getInstance().getRadio().start(craftgr$firstLoad);
        }
        craftgr$firstLoad = false;
    }
}
