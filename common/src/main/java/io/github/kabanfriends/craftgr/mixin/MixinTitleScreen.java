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
        musicFadeStart = 1L;
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.getState() == HandlerState.ACTIVE || handler.getState() == HandlerState.READY) {
            if (!handler.getAudioPlayer().isPlaying()) {
                handler.startPlayback();
            }
            handler.getAudioPlayer().setVolume(1.0f);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(PoseStack poseStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        //Initialize audio player
        if (handler.getState() == HandlerState.NOT_INITIALIZED) {
            CraftGR.EXECUTOR.submit(() -> {
                handler.initialize();
                if (CraftGR.MC.screen instanceof TitleScreen) {
                    handler.getAudioPlayer().setVolume(0.0f);
                }
                handler.startPlayback();
            });
        }

        if (handler.hasAudioPlayer()) {
            if (handler.getState() == HandlerState.ACTIVE) {
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
