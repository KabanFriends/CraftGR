package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    private long musicFadeStart;

    @Shadow
    @Final
    private boolean doBackgroundFade;

    @Inject(method = "removed", at = @At("HEAD"))
    private void onClose(CallbackInfo info) {
        if (AudioPlayerHandler.isInitialized()) {
            AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

            if (handler.player.isPlaying()) {
                handler.player.setVolume(1.0f);
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        //Initialize audio player
        if (AudioPlayerHandler.getInstance() == null) {
            CraftGR.EXECUTOR.submit(() -> {
                CraftGR.log(Level.INFO, "CraftGR is starting up!");
                new AudioPlayerHandler();
                CraftGR.log(Level.INFO, "Audio player is ready!");
            });
        }

        if (AudioPlayerHandler.isInitialized()) {
            AudioPlayerHandler handler = AudioPlayerHandler.getInstance();

            if (doBackgroundFade) {
                //Start music playback
                if (!handler.playing && !handler.player.isPlaying()) {
                    handler.startPlayback();
                }

                //Audio fade in
                if (handler.player.isPlaying()) {
                    if (musicFadeStart == 0L) {
                        musicFadeStart = Util.getMeasuringTimeMs();
                    }

                    float value = doBackgroundFade ? (float) (Util.getMeasuringTimeMs() - musicFadeStart) / 2000.0F : 0.0F;
                    handler.player.setVolume(MathHelper.clamp(value, 0.0f, 1.0f));
                }
            }
        }
    }
}
