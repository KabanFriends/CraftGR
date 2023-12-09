package io.github.kabanfriends.craftgr.mixin;

import io.github.kabanfriends.craftgr.CraftGR;
import net.minecraft.util.PngInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(PngInfo.class)
public class MixinPngInfo {

    @Inject(method = "validateHeader", at = @At("HEAD"), cancellable = true)
    private static void craftgr$bypassValidation(ByteBuffer byteBuffer, CallbackInfo ci) {
        if (CraftGR.bypassPngValidation) {
            ci.cancel();
        }
    }
}
