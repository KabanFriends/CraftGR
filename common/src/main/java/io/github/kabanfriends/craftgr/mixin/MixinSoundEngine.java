package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.AudioPlayerUtil;
import io.github.kabanfriends.craftgr.util.HandlerState;
import io.github.kabanfriends.craftgr.util.MessageUtil;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {

    @Inject(method = "reload", at = @At("HEAD"))
    private void craftgr$stopAudio(CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.getState() == HandlerState.ACTIVE) {
            handler.stopPlayback(true);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;preload(Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.AFTER), method = "loadLibrary()V")
    private void craftgr$startAudio(CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        // Establish the connection right after the audio is loaded
        if (handler.getState() == HandlerState.NOT_INITIALIZED) {
            AudioPlayerUtil.startPlaybackAsync(0.0f);
        }

        if (handler.getState() == HandlerState.RELOADING) {
            AudioPlayerUtil.startPlaybackAsync();
        }
    }
}
