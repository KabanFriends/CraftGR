package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class MixinOptions {

    @Inject(method = "lambda$createSoundSliderOptionInstance$0", at = @At("RETURN"))
    private static void craftgr$applyMasterSoundVolume(SoundSource category, Double value, CallbackInfo ci) {
        if (category == SoundSource.MASTER) {
            CraftGR.getInstance().getRadio().updateVolume();
        }
    }
}
