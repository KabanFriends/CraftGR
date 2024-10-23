package io.github.kabanfriends.craftgr.mixin;

import com.mojang.blaze3d.platform.Window;
import io.github.kabanfriends.craftgr.util.ThreadLocals;
import io.github.kabanfriends.craftgr.util.render.UnscaledScreenRectangle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class MixinGuiGraphics {

    @Inject(method = "applyScissor", at = @At("HEAD"))
    private void craftgr$getScissorRectangle(ScreenRectangle rectangle, CallbackInfo ci) {
       if (rectangle instanceof UnscaledScreenRectangle) {
           ThreadLocals.SCISSOR_CURRENT_RECTANGLE.set(rectangle);
       }
    }

    @Redirect(method = "applyScissor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScale()D"))
    private double craftgr$patchScissorScale(Window window) {
        if (ThreadLocals.SCISSOR_CURRENT_RECTANGLE.get() instanceof UnscaledScreenRectangle) {
            ThreadLocals.SCISSOR_CURRENT_RECTANGLE.remove();
            return 1.0;
        }
        return window.getGuiScale();
    }
}
