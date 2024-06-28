package io.github.kabanfriends.craftgr.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// The purpose of this Mixin is to fix a client bug where the
// batch text rendering in tooltips are never correctly flushed.
@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {

    @Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",shift = At.Shift.AFTER))
    public void craftgr$fixTooltip(Font font, List<ClientTooltipComponent> tooltips, int mouseX, int mouseY, ClientTooltipPositioner positioner, CallbackInfo ci) {
        invokeFlushIfUnmanaged();
    }

    @Invoker("flushIfUnmanaged")
    public abstract void invokeFlushIfUnmanaged();
}
