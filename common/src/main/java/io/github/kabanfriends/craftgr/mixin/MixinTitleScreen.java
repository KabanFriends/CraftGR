package io.github.kabanfriends.craftgr.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.HandlerState;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    private static long musicFadeStart;

    @Inject(method = "removed", at = @At("HEAD"))
    private void onClose(CallbackInfo info) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.getState() == HandlerState.ACTIVE || handler.getState() == HandlerState.READY) {
            if (handler.getAudioPlayer().isPlaying()) {
                handler.getAudioPlayer().setVolume(1.0f);
            }
        }
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        //Initialize audio player
        if (handler.getState() == HandlerState.NOT_INITIALIZED) {
            CraftGR.EXECUTOR.submit(handler::initialize);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(PoseStack poseStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.hasAudioPlayer()) {
            if (handler.getState() == HandlerState.READY) {
                //Start music playback
                if (!handler.isPlaying()) {
                    handler.getAudioPlayer().setVolume(0.0f);
                    handler.startPlayback();
                }
            } else if (handler.getState() == HandlerState.ACTIVE) {
                //Audio fade in
                if (handler.isPlaying()) {
                    if (musicFadeStart == 0L) {
                        musicFadeStart = Util.getMillis();
                    }

                    float value = (float) (Util.getMillis() - musicFadeStart) / 2000.0F;
                    handler.getAudioPlayer().setVolume(Mth.clamp(value, 0.0f, 1.0f));
                }
            }
        }
    }
}
