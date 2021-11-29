package io.github.kabanfriends.craftgr.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.craftgr.CraftGR;
import io.github.kabanfriends.craftgr.handler.AudioPlayerHandler;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.Mth;
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
    private boolean fading;

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
    private void render(PoseStack poseStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
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

            if (fading) {
                //Start music playback
                if (!handler.playing && !handler.player.isPlaying()) {
                    handler.startPlayback();
                }

                //Audio fade in
                if (handler.player.isPlaying()) {
                    if (musicFadeStart == 0L) {
                        musicFadeStart = Util.getMillis();
                    }

                    float value = fading ? (float) (Util.getMillis() - musicFadeStart) / 2000.0F : 0.0F;
                    handler.player.setVolume(Mth.clamp(value, 0.0f, 1.0f));
                }
            }
        }
    }
}
