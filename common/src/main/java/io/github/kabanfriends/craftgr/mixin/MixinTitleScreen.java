package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import io.github.kabanfriends.craftgr.util.AudioPlayerUtil;
import io.github.kabanfriends.craftgr.util.HandlerState;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    private static boolean fading;
    private static long musicFadeStart;

    @Inject(method = "removed", at = @At("HEAD"))
    private void craftgr$onTitleClose(CallbackInfo info) {
        musicFadeStart = 1L;
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        if (handler.getState() == HandlerState.ACTIVE || handler.getState() == HandlerState.READY) {
            if (!handler.getAudioPlayer().isPlaying()) {
                CraftGR.EXECUTOR.submit(handler::startPlayback);
            }
            handler.getAudioPlayer().setBaseVolume(1.0f);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void craftgr$onTitleRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

        //Initialize audio player
        if (handler.getState() == HandlerState.NOT_INITIALIZED) {
            AudioPlayerUtil.startPlaybackAsync(0.0f);
        }

        if (handler.getState() == HandlerState.ACTIVE && handler.hasAudioPlayer() && handler.isPlaying()) {
            //Audio fade in
            if (musicFadeStart == 0L) {
                fading = true;
                musicFadeStart = Util.getMillis();
            }

            if (fading) {
                float value = (float) (Util.getMillis() - musicFadeStart) / 2000.0F;
                handler.getAudioPlayer().setBaseVolume(Mth.clamp(value, 0.0f, 1.0f));

                if (value >= 1.0f) {
                    fading = false;
                }
            }
        }
    }

    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    public void craftgr$onRenderScreen(GuiGraphics graphics, int i, int j, float f, CallbackInfo ci) {
        //Start rendering the song overlay
        if (!CraftGR.renderSongOverlay) {
            CraftGR.renderSongOverlay = true;
        }
    }
}
